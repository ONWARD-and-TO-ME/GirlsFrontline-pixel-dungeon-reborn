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

package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Random;

//磁轨加速弹贴附武器后，贴图上闪烁的蓝白色光粒（替代战士纹章的红色 RED_LIGHT）
public class FocusSpark extends Speck {

	//蓝白色调
	private static final Integer[] COLORS = { 0xFFFFFF, 0xDDEEFF, 0xAAD4FF, 0x7FBFFF };

	public static final Emitter.Factory FACTORY = new Emitter.Factory() {
		@Override
		public void emit( Emitter emitter, int index, float x, float y ) {
			FocusSpark p = (FocusSpark) emitter.recycle( FocusSpark.class );
			p.reset( index, x, y );
		}
	};

	public void reset( int index, float x, float y ) {
		//复用 LIGHT 光粒的贴图与运动方式
		reset( index, x, y, LIGHT );
		//染成随机的蓝白色
		hardlight( Random.oneOf( COLORS ) );
	}
}
