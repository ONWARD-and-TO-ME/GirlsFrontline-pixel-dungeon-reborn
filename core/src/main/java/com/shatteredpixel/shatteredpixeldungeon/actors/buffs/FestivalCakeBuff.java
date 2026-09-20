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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/**
 * 节日蛋糕（WholeCake）食用后获得的全局性永久增益：
 * - 单局内不会被消除（detach 被拦截）；
 * - 十字架复活后依然继承（revivePersists，见 Hero.live()）；
 * - 视野 +1 格（在 Level.updateFieldOfView 中汇总）；
 * - 命中 +20%（在 Hero.attackSkill 中乘算）；
 * - 自带发光效果（复用 CharSprite.State.ILLUMINATED，而非额外挂一个 Light）。
 */
public class FestivalCakeBuff extends Buff {

	{
		type = buffType.POSITIVE;
		announced = true;
		//Hero.live() 复活时会剥离所有 revivePersists == false 的buff
		revivePersists = true;
	}

	//视野加成（格）
	public static final int VISION_BONUS = 1;
	//命中乘数
	public static final float ACCURACY_MULTIPLIER = 1.2f;

	@Override
	public boolean attachTo( Char target ) {
		if (super.attachTo(target)) {
			//视野加成立即生效，刷新一次视野
			if (Dungeon.level != null) {
				Dungeon.observe();
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean act() {
		//永久buff：没有持续时间，每节拍空转即可
		spend(TICK);
		return true;
	}

	@Override
	public int icon() {
		return BuffIndicator.FESTIVAL_CAKE;
	}

	@Override
	public void fx( boolean on ) {
		//发光效果直接包含在本buff内
		if (on) target.sprite.add(CharSprite.State.ILLUMINATED);
		else    target.sprite.remove(CharSprite.State.ILLUMINATED);
	}

	@Override
	public void detach() {
		//全局性buff：单局内无法被任何方式消除（复活保留由 revivePersists 保证）
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}
}
