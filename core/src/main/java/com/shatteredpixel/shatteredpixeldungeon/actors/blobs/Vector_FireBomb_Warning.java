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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ColorFlameFactory;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;

public class Vector_FireBomb_Warning extends Blob {
	@Override
	protected void evolve() {
		
		int cell;

		for (int i = area.left-1; i <= area.right; i++)
			for (int j = area.top-1; j <= area.bottom; j++) {
				cell = i + j * Dungeon.level.width();
				if (cur[cell] > 0)
					volume += off[cell] = cur[cell] - 1;
				else
					off[cell] = 0;
			}
	}
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.pour( new ColorFlameFactory(0xAA4488), 0.03f );
	}
}
