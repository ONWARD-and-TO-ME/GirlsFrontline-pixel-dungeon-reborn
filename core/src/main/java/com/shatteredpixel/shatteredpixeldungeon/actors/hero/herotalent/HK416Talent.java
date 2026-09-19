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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Erosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.StickyAdhesion;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCleansing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.special.M320;
import com.watabou.utils.Random;

/**
 * HK416 角色天赋池
 *
 * 与 GSH18Talent 相同的组织方式：按生命周期钩子集中实现天赋效果，
 * Talent/Hero 等调用方在各钩子里委托本类的对应静态方法。
 *
 * 会被序列化进存档的 tracker 内部类（HK416AttackChargeTracker、HK416WeakPointTracker、
 * KillMomentumTracker、HK416ShieldComboTracker）遵循项目约定放在 Talent.java 中，
 * 避免类移动导致旧存档无法反序列化。
 */
public final class HK416Talent {

	private HK416Talent() {}

	// ===================== 鉴定速度（T1-2 精锐人型） =====================

	/** 精锐人型：鉴定装备速度 ×1.5 / ×2.5（由 Talent.itemIDSpeedFactor 调用） */
	public static float itemIDSpeedFactor( Hero hero, float factor ){
		switch (hero.pointsInTalent(Talent.HK416_ELITE_TROOPER)){
			case 1: return factor * 1.5f;
			case 2: return factor * 2.5f;
			default: return factor;
		}
	}

	// ===================== 进食（T1-1 额外补给 / T2-1 特殊补给） =====================

	/** 进食后触发（由 Talent.onFoodEaten 调用，foodVal 为回复的饱食度） */
	public static void onFoodEaten( Hero hero, float foodVal ){
		// T1-1 额外补给：按回复饱食度比例减少M320冷却
		// +1：1/30回复饱食度；+2：1/15回复饱食度（仅对M320生效）
		int pts = hero.pointsInTalent(Talent.HK416_EXTRA_SUPPLY);
		if (pts > 0){
			float reduce = foodVal / (pts == 1 ? 30f : 15f);
			M320.Cooldown cd = hero.buff(M320.Cooldown.class);
			if (cd != null){
				if (cd.cooldown() - reduce <= 0){
					cd.detach();
				} else {
					cd.reduce(reduce);
				}
			}
		}

		// T2-1 特殊补给：进食后M320蓝色等级+1/+2（使用后消失）
		if (hero.hasTalent(Talent.HK416_SPECIAL_SUPPLY)){
			M320 m320 = hero.belongings.getItem(M320.class);
			if (m320 != null){
				m320.gainBlueLevel(hero.pointsInTalent(Talent.HK416_SPECIAL_SUPPLY) >= 2 ? 2 : 1);
			}
		}
	}

	// ===================== 喝药水（T2-2 稳态协议） =====================

	/** 喝药水时触发（由 Potion.apply 调用）：非减益药水给予净化buff */
	public static void onPotionQuaffed( Hero hero, Potion potion ){
		if (!hero.hasTalent(Talent.HK416_STEADY_PROTOCOL)){
			return;
		}
		//排除已知的有害药水
		if (isHarmfulPotion(potion)){
			return;
		}
		int duration = hero.pointsInTalent(Talent.HK416_STEADY_PROTOCOL) >= 2 ? 3 : 2;
		PotionOfCleansing.cleanse(hero, duration);
	}

	private static boolean isHarmfulPotion( Potion potion ){
		String name = potion.getClass().getSimpleName();
		switch (name){
			case "PotionOfLiquidFlame":
			case "PotionOfFrost":
			case "PotionOfParalyticGas":
			case "PotionOfToxicGas":
			case "PotionOfCorrosiveGas":
				return true;
			default:
				return false;
		}
	}

	// ===================== 攻击命中（T1-3 弱点看破 / T2-3 致残打击 / T3A 攻击积累） =====================

	/** 攻击命中后触发（由 Talent.onAttackProc 调用），返回调整后的伤害 */
	public static int onAttackProc( Hero hero, Char enemy, int dmg ){
		// T3A 寄生榴弹：每进行一次攻击，下一发榴弹杀伤+2.5%；
		// 攻击有"侵蚀"buff的敌人再额外+1.5%
		if (hero.subClass == HeroSubClass.PARASITIC_GRENADE){
			Talent.HK416AttackChargeTracker tracker = Buff.affect(hero, Talent.HK416AttackChargeTracker.class);
			tracker.countUp(25); //单位为0.1%
			if (enemy.buff(Erosion.class) != null){
				tracker.countUp(15);
			}
		}

		// T1-3 弱点看破：第二次攻击起伤害提升0-2点 / 2点
		if (hero.hasTalent(Talent.HK416_WEAK_POINT)){
			Talent.HK416WeakPointTracker wp = Buff.affect(enemy, Talent.HK416WeakPointTracker.class);
			if (wp.count() >= 1){
				int pts = hero.pointsInTalent(Talent.HK416_WEAK_POINT);
				dmg += pts == 1 ? Random.IntRange(0, 2) : 2;
			}
			wp.countUp(1);
		}

		// T2-3 致残打击：10%/20%概率造成2回合残废（重复触发刷新回合数）
		if (hero.hasTalent(Talent.HK416_CRIPPLING_STRIKE)){
			float chance = hero.pointsInTalent(Talent.HK416_CRIPPLING_STRIKE) == 1 ? 0.10f : 0.20f;
			if (Random.Float() < chance){
				Buff.affect(enemy, Cripple.class, 2f);
			}
		}

		return dmg;
	}

	// ===================== 爆炸命中（T1-4 护盾连携 / T3B 黏弹触发） =====================

	/**
	 * 特殊装备爆炸命中敌人时触发（由 M320.explode 对每个敌人调用）。
	 * T1-4 护盾连携：每命中一个敌人获得2/4点护盾（可叠加，上限20点，随时间衰减）
	 */
	public static void onExplosionHitShield( Hero hero ){
		if (!hero.hasTalent(Talent.HK416_SHIELD_COMBO)){
			return;
		}
		int gain = hero.pointsInTalent(Talent.HK416_SHIELD_COMBO) == 1 ? 2 : 4;

		Talent.HK416ShieldComboTracker tracker = Buff.affect(hero, Talent.HK416ShieldComboTracker.class);
		Barrier barrier = Buff.affect(hero, Barrier.class);

		//上限20点（按天赋积累量计算），且不超过当前护盾总量
		int add = Math.min(gain, 20 - Math.round(tracker.count()));
		if (add > 0 && barrier.shielding() < 20){
			add = Math.min(add, 20 - barrier.shielding());
			if (add > 0){
				barrier.incShield(add);
				tracker.countUp(add);
			}
		}
	}

	/** T3B 特工：黏弹黏着在受到爆炸伤害后触发（由 M320/HK269 爆炸对每个敌人调用） */
	public static void triggerStickyAdhesion( Hero hero, Char ch ){
		StickyAdhesion sticky = ch.buff(StickyAdhesion.class);
		if (sticky != null){
			sticky.trigger(hero);
		}
	}

	// ===================== 击杀（T3-4A 经验积累，由 Char.die 调用） =====================

	/** HK416击杀敌人时调用：积累击杀数（发射榴弹后重置） */
	public static void onKill( Hero hero ){
		if (hero.hasTalent(Talent.HK416_EXPERIENCE)){
			Buff.affect(hero, Talent.KillMomentumTracker.class).countUp(1);
		}
	}

	// ===================== 2.5x ACOG镜（T3-2） =====================

	/** ACOG：精准+5%/+10%/+15%（由 Hero.attackSkill 调用） */
	public static float acogAccuracyMultiplier( Hero hero ){
		int pts = hero.pointsInTalent(Talent.HK416_ACOG);
		return pts > 0 ? 1f + 0.05f * pts : 1f;
	}

	/** ACOG：视野+1/+2/+3格（由 Dungeon.observe / Level FOV 调用） */
	public static int acogVisionBonus( Hero hero ){
		return hero.pointsInTalent(Talent.HK416_ACOG);
	}
}
