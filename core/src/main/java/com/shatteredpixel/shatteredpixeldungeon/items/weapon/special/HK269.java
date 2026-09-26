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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
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
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

/**
 * HK416（寄生榴弹转职）特殊装备：HK269 榴弹发射器
 *
 * 设计文档要求：
 * - 伤害固定5点，不受其他天赋影响
 * - 攻击距离：视野范围内未被墙壁阻挡的一点
 * - 不升级
 * - 充能1/1，冷却结束后自动装填，冷却固定85回合（不受其他天赋影响）
 * - 以目标点为中心3*3范围爆炸，对敌方和中立单位施加4层侵蚀
 * - 爆炸破坏草地、门，不会炸毁木障碍物和物品
 * - 不可从骸骨中继承
 * - 目的：补大榴弹（M320）的空窗期
 */
public class HK269 extends Item {

	public static final String AC_SHOOT = "SHOOT";

	public static final int FIXED_DAMAGE = 5;
	public static final int FIXED_COOLDOWN = 85;
	public static final int EROSION_LAYERS = 4;

	{
		image = ItemSpriteSheet.HK269;

		defaultAction = AC_SHOOT;
		usesTargeting = true;

		unique = true;
		bones = false; //不可从骸骨中继承
	}

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

	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			if (target != null && curUser != null) {
				fire(curUser, target);
			}
		}

		@Override
		public String prompt() {
			return Messages.get(HK269.class, "prompt");
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

		// 爆炸特效
		if (Dungeon.level.heroFOV[target]) {
			CellEmitter.center(target).burst(BlastParticle.FACTORY, 25);
		}
		boolean terrainAffected = false;
		for (int n : PathFinder.cur().NEIGHBOURS9) {
			int c = target + n;
			if (c >= 0 && c < Dungeon.level.length()) {
				if (Dungeon.level.heroFOV[c]) {
					CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
				}
				// 只破坏草地（不破坏木障碍物等）；门的破坏见下
				if (Dungeon.level.map[c] == com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.HIGH_GRASS
						|| Dungeon.level.map[c] == com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.GRASS) {
					Dungeon.level.destroy(c);
					GameScene.updateMap(c);
					terrainAffected = true;
				}
			}
		}
		if (terrainAffected) {
			Dungeon.observe();
		}

		// 范围内每个敌人单独计算
		for (int n : PathFinder.cur().NEIGHBOURS9) {
			int c = target + n;
			if (c < 0 || c >= Dungeon.level.length()) continue;

			Char ch = Actor.findChar(c);
			if (ch == null || !ch.isAlive()) continue;
			// 不会伤害友军和玩家自身
			if (ch == hero || ch.alignment == Char.Alignment.ALLY) continue;

			int finalDmg = FIXED_DAMAGE - ch.drRoll();
			if (finalDmg > 0) {
				ch.damage(finalDmg, hero);
			}

			if (ch.isAlive()) {
				// 对敌方和中立单位施加4层侵蚀
				Erosion e = Buff.affect(ch, Erosion.class);
				e.addStacks(EROSION_LAYERS);

				// T3B 特工：黏雷黏着在受到爆炸伤害后触发
				HK416Talent.triggerStickyAdhesion(hero, ch);
			}
		}

		hero.spendAndNext(1f);

		// 冷却固定85回合，不受其他天赋影响
		Buff.affect(hero, Cooldown.class, FIXED_COOLDOWN);
	}

	/**
	 * HK269 装填冷却（固定85回合，不受其他天赋影响）。
	 */
	public static class Cooldown extends FlavourBuff {
		@Override
		public int icon() {
			return BuffIndicator.NONE;
		}
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 0;
	}
}
