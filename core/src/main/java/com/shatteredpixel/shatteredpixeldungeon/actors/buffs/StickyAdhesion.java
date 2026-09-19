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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

/**
 * HK416（特工转职）专属减益：黏弹黏着
 *
 * 设计文档要求：
 * - 附加上限3层
 * - 可以主动远程触发，或被附加该buff的单位受到爆炸伤害后触发，触发后清空所有层数并造成对应层数伤害
 * - 可以被护甲抵挡
 * - 1层：触发后造成 [10+层数 ~ 10+层数*1.5] 伤害，减少15%命中和15%闪避
 * - 2层：触发后在1层基础上失明4回合
 * - 3层：触发后在1&2层基础上额外造成 [10+层数*2] 伤害
 * - T3-3B 爆炸震慑：触发使敌人减少25%/35%/50%命中与闪避，持续15/20/25回合
 */
public class StickyAdhesion extends Buff {

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	private int stacks = 1;

	private static final String STACKS = "stacks";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(STACKS, stacks);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		stacks = bundle.getInt(STACKS);
	}

	public int stacks() {
		return stacks;
	}

	/** 附着黏弹（上限3层） */
	public static void apply(Char ch, int layers) {
		if (ch == null || !ch.isAlive()) return;
		//不会对友军单位和玩家生效
		if (ch instanceof Hero || ch.alignment == Char.Alignment.ALLY) return;
		StickyAdhesion existing = ch.buff(StickyAdhesion.class);
		if (existing != null) {
			existing.stacks = Math.min(3, existing.stacks + layers);
		} else {
			StickyAdhesion s = Buff.affect(ch, StickyAdhesion.class);
			s.stacks = Math.min(3, layers);
		}
	}

	@Override
	public int icon() {
		return BuffIndicator.NONE;
	}

	@Override
	public String toString() {
		return Messages.get(this, "name", stacks);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", stacks);
	}

	/**
	 * 引爆：受到爆炸伤害或主动触发时调用。
	 * 清空所有层数并造成对应层数伤害与减益。
	 */
	public void trigger(Hero source) {
		int layers = stacks;
		detach();
		if (!target.isAlive()) return;

		//1层基础伤害：[10+层数 ~ 10+层数*1.5]，受护甲抵挡
		int dmg = Random.IntRange(10 + layers, Math.round(10 + layers * 1.5f));
		dmg -= target.drRoll();
		if (dmg > 0) {
			target.damage(dmg, source);
		}
		if (!target.isAlive()) return;

		//触发后减少命中与闪避
		int pts = (source != null && source.isAlive()) ? source.pointsInTalent(Talent.HK416_BLAST_DETER) : 0;
		float percent;
		int duration;
		switch (pts) {
			case 1:  percent = 0.25f; duration = 15; break;
			case 2:  percent = 0.35f; duration = 20; break;
			case 3:  percent = 0.50f; duration = 25; break;
			default: percent = 0.15f; duration = 10; break; //无天赋基础效果
		}
		StickyDeter deter = Buff.affect(target, StickyDeter.class, duration);
		deter.set(percent);

		//2层：失明4回合
		if (layers >= 2) {
			Buff.affect(target, Blindness.class, 4f);
		}
		//3层：额外伤害 [10+层数*2]
		if (layers >= 3) {
			int extra = Math.round(10 + layers * 2f) - target.drRoll();
			if (extra > 0 && target.isAlive()) {
				target.damage(extra, source);
			}
		}
	}

	/**
	 * 黏弹震慑：触发后的命中/闪避降低（由 Char.hit 统一结算）。
	 */
	public static class StickyDeter extends FlavourBuff {

		private float percent = 0.15f;
		private static final String PERCENT = "percent";

		{
			type = buffType.NEGATIVE;
		}

		public void set(float percent) {
			this.percent = Math.max(this.percent, percent);
		}

		public float factor() {
			return 1f - percent;
		}

		@Override
		public int icon() {
			return BuffIndicator.NONE;
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(PERCENT, percent);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			percent = bundle.getFloat(PERCENT);
		}
	}
}
