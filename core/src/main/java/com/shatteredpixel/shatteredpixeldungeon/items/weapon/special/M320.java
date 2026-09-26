/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Erosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.herotalent.HK416Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

/**
 * HK416 特殊装备：M320 榴弹发射器
 *
 * 设计文档要求：
 * - 特殊装备不会被爆炸摧毁
 * - 初始伤害25点；成长：每2.5个角色等级+1（向上取整），上限+10，每级提升15点伤害
 * - 攻击距离：视野范围内未被墙壁阻挡的一点
 * - 充能1/1，冷却结束后自动装填，冷却300回合
 * - 以目标点为中心3*3范围爆炸，范围内每个敌人单独计算
 * - 爆炸破坏草地、门、木障碍物等（不会炸毁升级磁盘/力量药水）
 * - T1-1 额外补给：进食按回复饱食度比例减少冷却（1/30 或 1/15）
 * - T1-4 护盾连携：每命中一个敌人获得2/4点护盾（上限20，随时间衰减）
 * - T2-1 特殊补给：进食后获得蓝色等级+1/+2，使用后消失（每级+15伤害）
 * - T2-5 破片飞溅：命中给予10%/20%易伤3/5回合
 * - T3-1 加快补给：冷却减少30/60/100回合
 * - T3A 寄生榴弹：爆炸对敌方和中立单位施加3层侵蚀；
 *   每次攻击下一发榴弹杀伤+2.5%，攻击带侵蚀的敌人额外+1.5%
 * - T3-4A 经验积累：每击杀+5%/10%/15%榴弹伤害，发射后重置
 */
public class M320 extends Item {

	public static final String AC_SHOOT = "SHOOT";

	{
		image = ItemSpriteSheet.M320;

		defaultAction = AC_SHOOT;
		usesTargeting = true;

		unique = true;
		bones = false;
	}

	// 特殊补给（T2-1）获得的蓝色等级，使用后消失
	private int blueLevel = 0;

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_SHOOT);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (action.equals(AC_SHOOT)) {
			if (hero.buff(Cooldown.class) != null) {
				GLog.w(Messages.get(this, "not_ready"));
				return;
			}
			curUser = hero;
			curItem = this;
			GameScene.selectCell(shooter);
		}
	}

	// ===================== 伤害与成长 =====================

	/** 基础加成等级：每2.5个角色等级+1（向上取整），上限+10 */
	public int growthLevel(Hero hero) {
		return Math.min(10, (int) Math.ceil(hero.lvl / 2.5f));
	}

	/** T2-1 特殊补给：进食后获得蓝色等级（上限2，使用后消失） */
	public void gainBlueLevel(int amount) {
		blueLevel = Math.min(2, blueLevel + amount);
	}

	public int blueLevel() {
		return blueLevel;
	}

	/** 当前榴弹伤害：25 + 成长等级*15 + 蓝色等级*15，再乘以攻击积累与击杀积累加成 */
	public int currentDamage(Hero hero) {
		int dmg = 25 + growthLevel(hero) * 15;

		// T2-1 特殊补给：蓝色等级，每级+15，使用后消失
		dmg += blueLevel * 15;

		float multiplier = 1f;

		// T3A 寄生榴弹：攻击积累（2.5%/次，侵蚀目标额外1.5%/次），发射后重置
		Talent.HK416AttackChargeTracker charge = hero.buff(Talent.HK416AttackChargeTracker.class);
		if (charge != null) {
			multiplier += charge.count() / 1000f; //内部以0.1%为单位积累
		}

		// T3-4A 经验积累：击杀积累（5%/10%/15%每次），发射后重置
		Talent.KillMomentumTracker killTracker = hero.buff(Talent.KillMomentumTracker.class);
		if (killTracker != null) {
			int pts = hero.pointsInTalent(Talent.HK416_EXPERIENCE);
			if (pts > 0) {
				multiplier += pts * 0.05f * killTracker.count();
			}
		}

		dmg = Math.round(dmg * multiplier);
		return Math.max(1, dmg);
	}

	// ===================== 冷却 =====================

	/** 冷却回合数：300 - T3-1 加快补给（30/60/100） */
	public int cooldownTurns(Hero hero) {
		int reduce = 0;
		switch (hero.pointsInTalent(Talent.HK416_FAST_SUPPLY)) {
			case 1: reduce = 30; break;
			case 2: reduce = 60; break;
			case 3: reduce = 100; break;
		}
		return Math.max(0, 300 - reduce);
	}

	// ===================== 发射 =====================

	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			if (target != null && curUser != null) {
				fire(curUser, target);
			}
		}

		@Override
		public String prompt() {
			return Messages.get(M320.class, "prompt");
		}
	};

	private void fire(Hero hero, int target) {
		// 攻击距离：视野范围内未被墙壁阻挡的一点
		if (!Dungeon.level.heroFOV[target]) {
			GLog.w(Messages.get(this, "out_of_sight"));
			return;
		}
		Ballistica shot = new Ballistica(hero.pos, target, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
		if (shot.collisionPos != target) {
			GLog.w(Messages.get(this, "blocked"));
			return;
		}

		int dmg = currentDamage(hero);
		blueLevel = 0;

		// 重置积累
		Talent.HK416AttackChargeTracker charge = hero.buff(Talent.HK416AttackChargeTracker.class);
		if (charge != null) charge.countClear();
		Talent.KillMomentumTracker killTracker = hero.buff(Talent.KillMomentumTracker.class);
		if (killTracker != null) killTracker.countClear();

		explode(hero, target, dmg);

		hero.spendAndNext(1f);

		// 进入冷却
		Buff.affect(hero, Cooldown.class, cooldownTurns(hero));
	}

	/**
	 * 榴弹爆炸：以目标点为中心3*3范围
	 */
	public static void explode(Hero hero, int cell, int dmg) {
		// 爆炸特效
		if (Dungeon.level.heroFOV[cell]) {
			CellEmitter.center(cell).burst(BlastParticle.FACTORY, 30);
		}
		boolean terrainAffected = false;
		for (int n : PathFinder.cur().NEIGHBOURS9) {
			int c = cell + n;
			if (c >= 0 && c < Dungeon.level.length()) {
				if (Dungeon.level.heroFOV[c]) {
					CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
				}
				// 破坏草地、门、木障碍物等可燃地形
				if (Dungeon.level.flammable[c]) {
					Dungeon.level.destroy(c);
					GameScene.updateMap(c);
					terrainAffected = true;
				}
			}
		}
		if (terrainAffected) {
			Dungeon.observe();
		}

		// 范围内每个敌人单独计算伤害
		for (int n : PathFinder.cur().NEIGHBOURS9) {
			int c = cell + n;
			if (c < 0 || c >= Dungeon.level.length()) continue;

			Char ch = Actor.findChar(c);
			if (ch == null || !ch.isAlive()) continue;
			// 不会伤害友军和玩家自身
			if (ch == hero || ch.alignment == Char.Alignment.ALLY) continue;

			int finalDmg = dmg - ch.drRoll();
			if (finalDmg > 0) {
				ch.damage(finalDmg, hero);
			}

			if (ch.isAlive()) {
				// T1-4 护盾连携：每命中一个敌人获得2/4点护盾（上限20，随时间衰减）
				HK416Talent.onExplosionHitShield(hero);

				// T2-5 破片飞溅：命中给予易伤（本分支易伤为33%增伤），3/5回合
				if (hero.hasTalent(Talent.HK416_SHRAPNEL)) {
					int pts = hero.pointsInTalent(Talent.HK416_SHRAPNEL);
					int dur = pts == 1 ? 3 : 5;
					Buff.affect(ch, Vulnerable.class, dur);
				}

				// T3A 寄生榴弹：对敌方和中立单位施加3层侵蚀
				if (hero.subClass == HeroSubClass.PARASITIC_GRENADE) {
					Erosion e = Buff.affect(ch, Erosion.class);
					e.addStacks(3);
				}

				// T3B 特工：黏雷黏着在受到爆炸伤害后触发
				HK416Talent.triggerStickyAdhesion(hero, ch);
			}
		}
	}

	// ===================== 冷却buff =====================

	/**
	 * M320 装填冷却。拥有该buff时表示未装填完成；
	 * 冷却自然结束时M320即装填完毕（可用与否直接检测该buff）。
	 * T1-1 额外补给会在进食时缩短剩余冷却。
	 */
	public static class Cooldown extends FlavourBuff {
		@Override
		public int icon() {
			return BuffIndicator.NONE;
		}

		/** 缩短剩余冷却（额外补给天赋使用） */
		public void reduce(float amount) {
			spend(-Math.min(amount, cooldown()));
		}
	}

	// ===================== 存档 =====================

	private static final String BLUE_LEVEL = "blue_level";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(BLUE_LEVEL, blueLevel);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		blueLevel = bundle.getInt(BLUE_LEVEL);
	}

	// ===================== 描述 =====================

	@Override
	public String info() {
		String info = super.info();
		Hero hero = Dungeon.cur().hero;
		if (hero != null) {
			info += "\n\n" + Messages.get(this, "stats", 25 + growthLevel(hero) * 15, cooldownTurns(hero));
			if (hero.buff(Cooldown.class) != null) {
				info += " " + Messages.get(this, "cooldown_left", Math.round(hero.buff(Cooldown.class).cooldown()));
			}
		}
		return info;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}
}
