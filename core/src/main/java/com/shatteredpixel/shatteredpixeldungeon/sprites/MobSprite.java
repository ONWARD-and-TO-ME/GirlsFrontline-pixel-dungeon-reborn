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

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.EmoIcon;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.noosa.tweeners.ScaleTweener;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class MobSprite extends CharSprite {

	private static final float FADE_TIME	= 3f;
	private static final float FALL_TIME	= 1f;

	//位置显示图标，跟随怪物移动
	private EmoIcon posIndicator;

	//是否显示位置显示图标，子类可按状态覆盖（如宝箱怪潜伏时不显示）
	protected boolean showPosIndicator() {
		return true;
	}

	@Override
	public void update() {
		sleeping = ch != null && ch.isAlive() && ((Mob)ch).state == ((Mob)ch).SLEEPING;
		super.update();

		if (SPDSettings.mobPosIndicator() && showPosIndicator() && ch != null && ch.isAlive() && parent != null) {
			if (posIndicator == null || !posIndicator.alive) {
				posIndicator = new EmoIcon.PosShow(this);
			}
			posIndicator.visible = visible;
		} else if (posIndicator != null) {
			posIndicator.killAndErase();
			posIndicator = null;
		}
	}

	@Override
	public void killAndErase() {
		if (posIndicator != null) {
			posIndicator.killAndErase();
			posIndicator = null;
		}
		super.killAndErase();
	}
	
	@Override
	public void onComplete( Animation anim ) {
		
		super.onComplete( anim );
		
		if (anim == die && parent != null) {
			parent.add( new AlphaTweener( this, 0, FADE_TIME ) {
				@Override
				protected void onComplete() {
					MobSprite.this.killAndErase();
				}
			} );
		}
	}
	
	public void fall() {
		
		origin.set( width / 2, height - DungeonTilemap.SIZE / 2 );
		angularSpeed = Random.Int( 2 ) == 0 ? -720 : 720;
		am = 1;

		hideEmo();

		if (health != null){
			health.killAndErase();
		}
		
		parent.add( new ScaleTweener( this, new PointF( 0, 0 ), FALL_TIME ) {
			@Override
			protected void onComplete() {
				MobSprite.this.killAndErase();
				parent.erase( this );
			}
			@Override
			protected void updateValues( float progress ) {
				super.updateValues( progress );
				y += 12 * Game.elapsed;
				am = 1 - progress;
			}
		} );
	}
}
