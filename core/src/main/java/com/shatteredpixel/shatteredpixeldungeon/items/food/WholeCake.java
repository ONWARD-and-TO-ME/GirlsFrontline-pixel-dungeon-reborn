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
	// 蛋糕款式：0=WHOLECAKE, 1=WHOLECAKE2, 2=WHOLECAKE3, 3=WHOLECAKE4
	private int cakeStyle;
	public WholeCake(){
		this("某人的蛋糕", "0.5.8v2版本纯粹是为了这个私货而加急制作的。但是令人遗憾的是，没有多少人发现这个私货……\n\n或者说，没有人按照我预设的剧本进行演出……");
	}
	public WholeCake(String body){
		this("节日蛋糕", body);
	}
	public WholeCake(String title, String body){
		this.title = title;
		this.body = body;
	}
	public WholeCake(String title, String body, int cakeStyle){
		this.title = title;
		this.body = body;
		this.cakeStyle = cakeStyle;
		image = getCakeImage(cakeStyle);
	}
	// 根据款式返回对应的贴图常量
	public static int getCakeImage(int style){
		switch (style){
			case 1: return ItemSpriteSheet.WHOLECAKE2;
			case 2: return ItemSpriteSheet.WHOLECAKE3;
			case 3: return ItemSpriteSheet.WHOLECAKE4;
			default: return ItemSpriteSheet.WHOLECAKE;
		}
	}
	// 根据款式返回对应的蛋糕名字
	public static String getCakeName(int style){
		switch (style){
			case 1: return "草莓奶油";
			case 2: return "烘焙可可";
			case 3: return "抹茶青苹果";
			default: return "薄荷巧克力";
		}
	}
	public int getCakeStyle(){
		return cakeStyle;
	}
	private static final String TITLE = "CAKE_TITLE";
	private static final String BODY  = "CAKE_BODY";
	private static final String STYLE = "CAKE_STYLE";
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put(TITLE, title);
		bundle.put(BODY, body);
		bundle.put(STYLE, cakeStyle);
	}
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		title = bundle.getString(TITLE);
		body = bundle.getString(BODY);
		cakeStyle = bundle.getInt(STYLE);
		image = getCakeImage(cakeStyle);
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
