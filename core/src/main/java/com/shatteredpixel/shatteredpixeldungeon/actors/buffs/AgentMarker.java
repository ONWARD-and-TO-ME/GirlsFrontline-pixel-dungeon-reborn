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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.watabou.utils.Bundle;

/**
 * HK416（特工转职）被动：自动给视野内的敌对单位附加一层"黏弹黏着"
 *
 * 设计文档要求：
 * - 自动对视野内的敌对单位附加一层"黏弹黏着"buff
 * - 不会惊醒沉睡的敌人
 * - 不消耗回合
 * - 附加后冷却时间：50回合
 */
public class AgentMarker extends Buff {

	{
		//仅作内部计数，不在buff栏显示
	}

	private float counter = AgentMarkerInterval();

	private static final String COUNTER = "counter";

	private static float AgentMarkerInterval() {
		return 50f;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(COUNTER, counter);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		counter = bundle.getFloat(COUNTER);
	}

	@Override
	public boolean act() {
		//仅特工转职下生效
		if (!(target instanceof Hero) || ((Hero) target).subClass != HeroSubClass.AGENT) {
			detach();
			return true;
		}

		counter -= TICK;
		if (counter <= 0) {
			counter = AgentMarkerInterval();
			markVisibleEnemies((Hero) target);
		}

		spend(TICK);
		return true;
	}

	/** 对视野内的敌对单位附加1层黏弹黏着（不惊醒沉睡的敌人） */
	private void markVisibleEnemies(Hero hero) {
		for (Char ch : Actor.chars()) {
			if (ch == hero || !ch.isAlive()) continue;
			if (ch.alignment != Char.Alignment.ENEMY) continue;
			if (!Dungeon.level.heroFOV[ch.pos]) continue;
			//附加buff不会惊醒沉睡的敌人（仅附着标记，不触发AI警觉）
			StickyAdhesion.apply(ch, 1);
		}
	}
}
