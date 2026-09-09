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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

/**
 * GSH18 T2天赋：锁链冲击
 *
 * 攻击命中后，以目标为中心对周围3x3范围的非友方单位造成基于“本次伤害”的溅射伤害。
 * 主武器命中（{@link Talent#onAttackProc}）与天狼星心脏的附加伤害
 * （{@link com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SiriusHeart#onAttack}）
 * 都会调用 {@link #splash}，因此两条伤害链路都能触发锁链冲击。
 *
 * 从 Talent.java 中单独摘出，避免天赋 dispatcher 文件过于臃肿。
 */
public final class ChainShock {

	private ChainShock() {}

	/**
	 * 触发锁链冲击的溅射判定。
	 *
	 * @param hero  攻击者（需拥有 GSH18_CHAIN_SHOCK 天赋）
	 * @param enemy 被命中的目标，溅射以其所在格为中心
	 * @param dmg   本次对目标造成的伤害，溅射伤害按其比例计算
	 */
	public static void splash( Hero hero, Char enemy, int dmg ){
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
}
