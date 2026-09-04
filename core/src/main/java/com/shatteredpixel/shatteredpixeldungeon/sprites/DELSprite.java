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
import com.watabou.noosa.TextureFilm;

public class DELSprite extends MobSprite {

	//deele.png 每帧 24x23：0-1 等待，2 疑惑，3 成功
	private Animation confused;
	private Animation success;

	public DELSprite() {
		super();

		texture( Assets.Sprites.DEELE );

		TextureFilm frames = new TextureFilm( texture, 24, 23);

		idle = new Animation( 1, true );
		idle.frames( frames, 0,0,0,1 );

		run = new Animation( 10, true );
		run.frames( frames, 0, 1 );

		die = new Animation( 20, false );
		die.frames( frames, 0 );

		confused = new Animation( 1, true );
		confused.frames( frames, 2 );

		success = new Animation( 1, true );
		success.frames( frames, 3 );

		play( idle );
	}

	//疑惑表情：单帧定格（如任务尚未完成时）
	public void showConfused() {
		play( confused );
	}

	//成功表情：单帧定格（如任务完成时）
	public void showSuccess() {
		play( success );
	}

	//恢复默认的等待动画
	public void backToWaiting() {
		play( idle );
	}

}
