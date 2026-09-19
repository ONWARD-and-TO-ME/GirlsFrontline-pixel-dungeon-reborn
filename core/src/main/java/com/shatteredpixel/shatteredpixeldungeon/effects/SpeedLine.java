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

import com.watabou.noosa.Game;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

/**
 * 高速冲刺速度线粒子：沿移动方向拉伸的亮白色短线条，向身后飞散并快速淡出。
 * 用于超级小爱「飞升自由+3」飞行冲刺时的高速感视觉反馈。
 */
public class SpeedLine extends PixelParticle {

	/**
	 * @param angle 移动方向（弧度）。速度线会沿此方向拉伸、并向反方向飞散。
	 */
	public static Emitter.Factory factory( final float angle ) {
		return new Emitter.Factory() {
			@Override
			public void emit( Emitter emitter, int index, float x, float y ) {
				SpeedLine p = (SpeedLine) emitter.recycle( SpeedLine.class );
				p.reset( x, y, angle );
			}
			@Override
			public boolean lightMode() {
				return true;
			}
		};
	}

	public void reset( float x, float y, float angle ) {
		revive();

		this.x = x;
		this.y = y;

		//角度转为角度制（noosa 中 angle 为角度）
		this.angle = (float) (angle * 180 / Math.PI);

		//沿移动方向拉伸的短线条
		float len = Random.Float( 8, 18 );
		size( len, Random.Float( 1, 2.5f ) );

		color( 0xFFFFFF );
		hardlight( 0.7f, 0.85f, 1f ); //偏冷白，带一点飞行能量感
		am = 0.9f;

		//向身后飞散
		speed.polar( angle + (float)Math.PI, Random.Float( 30, 70 ) );
		acc.set( 0 );

		left = lifespan = Random.Float( 0.18f, 0.32f );
	}

	@Override
	public void update() {
		super.update();
		float p = left / lifespan;
		//快速淡出并略微收缩
		am = p * p * 0.9f;
		size( scale.x * (0.6f + 0.4f * p), scale.y );
	}
}
