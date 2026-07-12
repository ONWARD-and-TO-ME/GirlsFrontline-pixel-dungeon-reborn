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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogFist;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;

public class AlchemistSprite extends FistSprite {

	{
		boltType = MagicMissile.RAINBOW;
	}

	public AlchemistSprite() {
		super();

		texture( Assets.ALCHEMIST );

		TextureFilm frames = new TextureFilm( texture, 20, 23 );

		idle = new Animation( 3, true );
		idle.frames( frames, 0, 0, 0, 0, 1, 1, 2, 3, 3, 3 );

		run = new Animation( 6, true );
		run.frames( frames, 4, 5, 6, 7, 8, 9 );

		attack = new Animation( 8, false );
		attack.frames( frames, 10,12 );

		zap = new Animation( 8, false );
		zap.frames( frames, 10, 11, 12, 13, 14, 10, 11, 12, 13, 14 );

		die = new Animation( 6, false );
		die.frames( frames, 15, 15, 15,15, 16, 16, 17,17, 17, 17 );

		play( idle );
	}

	@Override
	protected int texOffset() {
		return 0;
	}

	@Override
	protected Emitter createEmitter() {
		Emitter emitter = emitter();
		emitter.pour( SparkParticle.STATIC, 0.06f );
		return emitter;
	}

	@Override
	public void zap( int cell ) {
		turnTo( ch.pos , cell );
		play( zap );

		((YogFist)ch).onZapComplete();
		parent.add( new Beam.LightRay(center(), DungeonTilemap.raisedTileCenterToWorld(cell)));
	}

	@Override
	public int blood() {
		return 0xFFFFFFFF;
	}

}
