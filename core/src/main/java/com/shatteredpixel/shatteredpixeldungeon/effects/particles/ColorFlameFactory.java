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

package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.Emitter.Factory;
import com.watabou.noosa.particles.PixelParticle;

public class ColorFlameFactory extends Emitter.Factory {
	private final int color;
	public ColorFlameFactory(){
		this(0xEE7722);
	}
	public ColorFlameFactory(int color){
		super();
		this.color = color;
	}
    @Override
    public void emit(Emitter emitter, int index, float x, float y) {
        ((ColorFlame) emitter.recycle(ColorFlame.class)).reset(x, y, color);
    }

    @Override
    public boolean lightMode() {
        return true;
    }

    public static class ColorFlame extends PixelParticle.Shrinking {

        public ColorFlame() {
			this(0xEE7722);
        }
		public ColorFlame(int color){
			super();

			color(color);
			lifespan = 0.6f;

			acc.set(0, -80);
		}

        public void reset(float x, float y, int color) {
            revive();

            this.x = x;
            this.y = y;

			color(color);
            left = lifespan;

            size = 4;
            speed.set(0);
        }

        @Override
        public void update() {
            super.update();
            float p = left / lifespan;
            am = p > 0.8f ? (1 - p) * 5 : 1;
        }
    }
}