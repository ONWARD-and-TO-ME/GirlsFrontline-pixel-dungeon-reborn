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

package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class WholeCake extends Food {

	{
		image = ItemSpriteSheet.WHOLECAKE;
		energy = Hunger.STARVING;
		unique = true;
	}
	private String title;
	private String body;
	public WholeCake(){
		this("", "");
	}
	public WholeCake(String body){
		this("节日蛋糕", body);
	}
	public WholeCake(String title, String body){
		this.title = title;
		this.body = body;
	}
	private static final String TITLE = "CAKE_TITLE";
	private static final String BODY  = "CAKE_BODY";
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put(TITLE, title);
		bundle.put(BODY, body);
	}
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		title = bundle.getString(TITLE);
		body = bundle.getString(BODY);
	}
	@Override
	public String name(){
		return title;
	}
	@Override
	public String desc(){
		return body;
	}
	@Override
    public int value() {
        return 0;
    }
}
