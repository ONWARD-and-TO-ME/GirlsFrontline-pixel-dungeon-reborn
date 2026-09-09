/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2018 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.herotalent;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.StarShield;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

/**
 * GSH-18 角色天赋池
 *
 * 把原本散落在 Talent / Hero / Weapon / KindOfWeapon / Armor 中的 GSH18 天赋效果逻辑
 * 按“角色”维度集中到本类，并按生命周期钩子组织。Talent/Hero 等调用方只需在各钩子里
 * 委托本类的对应静态方法，新增 GSH18 天赋也只需改本类（外加 Talent 枚举注册）。
 *
 * 刻意保留在 Talent.java 中、不在本类的内容：
 * - 天赋枚举常量（GSH18_*）与天赋表注册——那是天赋注册表；
 * - 会被序列化进存档的 tracker/冷却 buff 内部类（GSH18EnergizingMealTracker、
 *   StarShieldTracker、IntelligenceAwarenessCooldown、AgileMovement/AgileMovementCooldown、
 *   SiriusHeartTracker）——移动会改变类全名导致旧存档无法反序列化，本类通过 Talent.Xxx 引用。
 */
public final class GSH18Talent {

	private GSH18Talent() {}

	// ===================== 生命周期钩子（供调用方统一委托） =====================

	/** 进食后：疗养一餐、元气一餐 */
	public static void onFoodEaten( Hero hero ){
		mealTreatment(hero);
		if (hero.hasTalent(Talent.GSH18_ENERGIZING_MEAL)){
			// 进食后添加buff，用于跟踪下次攻击必定命中和增加攻击范围
			Buff.affect(hero, Talent.GSH18EnergizingMealTracker.class);
		}
	}

	/** 使用药水后：医护兼容 */
	public static void onPotionUsed( Hero hero, float mul ){
		medicalCompatibility(hero, mul);
	}

	/** 攻击命中后：锁链冲击溅射、双星守护回盾 */
	public static void onAttackProc( Hero hero, Char enemy, int dmg ){
		chainShock(hero, enemy, dmg);
		twinStarGuard(hero);
	}

	/** 攻击命中结算后：消耗元气一餐增益 */
	public static void onAttackHit( Hero hero ){
		Buff tracker = hero.buff(Talent.GSH18EnergizingMealTracker.class);
		if (tracker != null){
			tracker.detach();
		}
	}

	/** 获得护盾时：情报感知、敏捷移动触发 */
	public static void onShielding( Hero hero ){
		intelligenceAwareness(hero);
		agileMovementOnShielding(hero);
	}

	/** 命中乘数：短线作战（攻击相邻敌人时命中 +20%/+45%），不满足返回1 */
	public static float accuracyMultiplier( Hero hero, Char target ){
		if (hero.hasTalent(Talent.GSH18_CLOSE_COMBAT) && Dungeon.level.adjacent(hero.pos, target.pos)) {
			switch (hero.pointsInTalent(Talent.GSH18_CLOSE_COMBAT)){
				case 1: return 1.2f;  // +1级：攻击距离为1的敌人时，命中率增加20%
				case 2: return 1.45f; // +2级：攻击距离为1的敌人时，命中率增加45%
			}
		}
		return 1f;
	}

	/** 元气一餐+1：增益期间下次攻击必定命中 */
	public static boolean guaranteedHit( Hero hero ){
		return hero.hasTalent(Talent.GSH18_ENERGIZING_MEAL)
				&& hero.buff(Talent.GSH18EnergizingMealTracker.class) != null;
	}

	/** 攻击距离加成：漫画之心 + 元气一餐+2 */
	public static int reachBonus( Hero hero, int weaponTier ){
		int bonus = 0;
		// 漫画之心：武器tier不高于天赋点数时攻击距离+1
		if (hero.hasTalent(Talent.GSH18_COMIC_HEART)
				&& weaponTier <= hero.pointsInTalent(Talent.GSH18_COMIC_HEART)){
			bonus++;
		}
		// 元气一餐+2：增益期间攻击范围+1
		if (hero.pointsInTalent(Talent.GSH18_ENERGIZING_MEAL) >= 2
				&& hero.buff(Talent.GSH18EnergizingMealTracker.class) != null){
			bonus++;
		}
		return bonus;
	}

	/** 敏捷移动：本次受击是否完全闪避（调用方应在返回true时给予无限闪避） */
	public static boolean tryEvade( Hero hero ){
		if (hero.buff(Talent.AgileMovement.class) == null){
			return false;
		}
		int level = hero.pointsInTalent(Talent.GSH18_AGILE_MOVEMENT);
		float chance = 0.15f;
		if (level >= 3){
			chance = 0.4f;
		} else if (level == 2){
			chance = 0.25f;
		}
		return Random.Float() < chance;
	}

	/** 移速乘数：后勤支援（拥有星之护盾时 +10%/+20%），不满足返回1 */
	public static float speedMultiplier( Hero hero ){
		if (hero.hasTalent(Talent.GSH18_LOGISTICS_SUPPORT)){
			StarShield starShield = hero.buff(StarShield.class);
			if (starShield != null && starShield.shielding() > 0){
				return 1f + 0.1f * hero.pointsInTalent(Talent.GSH18_LOGISTICS_SUPPORT);
			}
		}
		return 1f;
	}

	/** 医生直觉+2：允许取下被诅咒的武器 */
	public static boolean canUnequipWeapon( Hero hero ){
		return hero.pointsInTalent(Talent.GSH18_DOCTOR_INTUITION) >= 2;
	}

	/** 医生直觉+1：允许取下被诅咒的防具 */
	public static boolean canUnequipArmor( Hero hero ){
		return hero.pointsInTalent(Talent.GSH18_DOCTOR_INTUITION) >= 1;
	}

	// ===================== 各天赋具体实现 =====================

	/** T1 疗养一餐：+1 进食回2点生命；+2 进食额外获得2点星之护盾 */
	private static void mealTreatment( Hero hero ){
		if (!hero.hasTalent(Talent.GSH18_MEAL_TREATMENT)){
			return;
		}
		// +1:进食恢复2点生命
		if (hero.pointsInTalent(Talent.GSH18_MEAL_TREATMENT) >= 1) {
			hero.HP = Math.min(hero.HP + 2, hero.HT);
			if (hero.sprite != null) {
				Emitter e = hero.sprite.emitter();
				if (e != null) e.burst(Speck.factory(Speck.HEALING), 2);
			}
		}
		// +2:进食获得2点星之护盾值
		if (hero.pointsInTalent(Talent.GSH18_MEAL_TREATMENT) >= 2) {
			StarShield starShield = hero.buff(StarShield.class);
			if (starShield == null) {
				// 如果角色还没有星之护盾buff，创建一个新的
				starShield = Buff.affect(hero, StarShield.class);
			}
			starShield.incShield(2);
			if (hero.sprite != null) {
				hero.sprite.centerEmitter().burst(MagicMissile.WardParticle.FACTORY, 2);
			}
		}
	}

	/**
	 * T2 锁链冲击：以目标为中心对周围3x3范围的非友方单位造成基于本次伤害的溅射。
	 * +1溅射10%、+2溅射20%；+2时有20%概率把相邻可移动单位击退1格。
	 * 主武器命中（onAttackProc）与天狼星心脏附加伤害（SiriusHeart.onAttack）都会调用。
	 */
	public static void chainShock( Hero hero, Char enemy, int dmg ){
		if (hero == null || enemy == null || dmg <= 0){
			return;
		}
		if (!hero.hasTalent(Talent.GSH18_CHAIN_SHOCK)){
			return;
		}

		int points = hero.pointsInTalent(Talent.GSH18_CHAIN_SHOCK);
		float damageMultiplier = points == 1 ? 0.1f : 0.2f; // +1为10%，+2为20%
		int splashDamage = Math.round(dmg * damageMultiplier);
		if (splashDamage <= 0) return;

		// 获取目标周围3x3范围的所有格子
		for (int i : PathFinder.NEIGHBOURS9) {
			int cell = enemy.pos + i;
			if (Dungeon.level.insideMap(cell) && cell != enemy.pos) { // 排除目标自身
				Char ch = Actor.findChar(cell);
				if (ch != null && ch.alignment != Char.Alignment.ALLY && ch.isAlive()) {
					// 对范围内非友方单位造成伤害
					ch.damage(splashDamage, hero);
					ch.sprite.flash();

					// +2级时，有20%概率对临近可移动单位造成1格击退
					if (points >= 2 && Dungeon.level.adjacent(enemy.pos, cell) && !ch.properties().contains(Char.Property.IMMOVABLE)) {
						// 20%概率触发击退
						if (Random.Float() < 0.2f) {
							int pushDir = ch.pos - enemy.pos;
							Ballistica path = new Ballistica(ch.pos, ch.pos + pushDir, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);

							// 如果路径有效且长度足够
							if (path.path.size() > 1) {
								int newPos = path.path.get(1);
								// 检查目标位置是否可行走且没有其他角色
								if (Dungeon.level.passable[newPos] && Actor.findChar(newPos) == null) {
									// 执行击退
									ch.pos = newPos;
									ch.sprite.move(ch.pos - pushDir, ch.pos);
								}
							}
						}
					}
				}
			}
		}
	}

	/** T1 双星守护：攻击命中回复星之护盾，每回合上限 +1为5点、+2为10点（非GSH18减半） */
	private static void twinStarGuard( Hero hero ){
		if (!hero.hasTalent(Talent.GSH18_STAR_SHIELD)){
			return;
		}
		Talent.StarShieldTracker tracker = Buff.affect(hero, Talent.StarShieldTracker.class);
		int shieldPerHit = hero.pointsInTalent(Talent.GSH18_STAR_SHIELD); // +1回1点，+2回2点

		// 如果是GSH18，正常上限；否则，上限减半
		int maxPerTurn = 5 * hero.pointsInTalent(Talent.GSH18_STAR_SHIELD); // +1每回合最多5点，+2每回合最多10点
		if (hero.heroClass != HeroClass.GSH18){
			maxPerTurn /= 2; // 非GSH18角色上限减半
		}

		if (tracker.count() < maxPerTurn) {
			Buff.affect(hero, StarShield.class).incShield(shieldPerHit);
			tracker.countUp(shieldPerHit);
			if (hero.sprite != null){
				hero.sprite.centerEmitter().burst(MagicMissile.WardParticle.FACTORY, 2);
			}
		}
	}

	/** T2 医护兼容：使用增益治疗药水（mul==1.25）时，把治疗量的20%/50%转化为星之护盾 */
	private static void medicalCompatibility( Hero hero, float mul ){
		if (mul == 1.25F && hero.hasTalent(Talent.GSH18_MEDICAL_COMPATIBILITY)){
			StarShield starShield = Buff.affect(hero, StarShield.class);
			// 计算应回复的护盾层数：治疗药水恢复量的20%/50%
			int healAmount = PotionOfHealing.getHealAmount(hero.HT);
			float shieldPercent = -0.1f + 0.3f * hero.pointsInTalent(Talent.GSH18_MEDICAL_COMPATIBILITY);
			int shieldToAdd = Math.round(healAmount * shieldPercent);
			starShield.incShield(shieldToAdd);
			if (hero.sprite != null) {
				hero.sprite.centerEmitter().burst(MagicMissile.WardParticle.FACTORY, 2);
			}
		}
	}

	/** T3 情报感知：获得护盾时触发（冷却100/75/50回合），给予1回合心灵视野（距离2/5/8格） */
	private static void intelligenceAwareness( Hero hero ){
		if (!hero.hasTalent(Talent.GSH18_INTELLIGENCE_AWARENESS)){
			return;
		}
		Talent.IntelligenceAwarenessCooldown cooldownBuff = hero.buff(Talent.IntelligenceAwarenessCooldown.class);
		if (cooldownBuff == null){
			float cooldownTurns = 125.0f - 25.0f * hero.pointsInTalent(Talent.GSH18_INTELLIGENCE_AWARENESS);
			Buff.affect(hero, Talent.IntelligenceAwarenessCooldown.class, cooldownTurns);
			int distance = -1 + 3 * hero.pointsInTalent(Talent.GSH18_INTELLIGENCE_AWARENESS);
			Buff.affect(hero, MindVision.class, 1.0f).distance = distance;
		}
	}

	/** T3 敏捷移动：获得护盾时触发（50回合冷却），挂1回合闪避增益 */
	private static void agileMovementOnShielding( Hero hero ){
		if (!hero.hasTalent(Talent.GSH18_AGILE_MOVEMENT)){
			return;
		}
		Talent.AgileMovementCooldown cooldownBuff = hero.buff(Talent.AgileMovementCooldown.class);
		if (cooldownBuff == null){
			Buff.affect(hero, Talent.AgileMovementCooldown.class, 50.0f);
			Buff.affect(hero, Talent.AgileMovement.class, 1.0f);
		}
	}
}
