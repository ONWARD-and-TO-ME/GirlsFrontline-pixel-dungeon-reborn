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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import com.nlf.calendar.Lunar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

public class Pasty extends Food {

	//TODO: implement fun stuff for other holidays
	//TODO: probably should externalize this if I want to add any more festive stuff.
	private enum Holiday{
		NONE,
		EASTER, //TBD
		HWEEN,//2nd week of october though first day of november，公历11月1日
		BREAD,
		ZHONG_QIU//农历八月十五日
	}

	private static Holiday holiday;

	static{
		holiday = Holiday.NONE;

		final Calendar calendar = Calendar.getInstance();

        List<String> festivals = (new Lunar(new Date())).getFestivals();

        // 判断是否是中秋节
        if (festivals.contains("中秋节")) {
        	holiday = Holiday.ZHONG_QIU;
        }

        if (holiday == Holiday.NONE){
			switch(calendar.get(Calendar.MONTH)){
				case Calendar.NOVEMBER:
					if (calendar.get(Calendar.DAY_OF_MONTH) == 1)
						holiday = Holiday.HWEEN;
					break;
				case Calendar.MAY:
					if (calendar.get(Calendar.WEEK_OF_MONTH) >= 4)
						holiday = Holiday.BREAD;
					break;
				case Calendar.JUNE:
					if (calendar.get(Calendar.DAY_OF_MONTH) %2 == 0)
						holiday = Holiday.BREAD;
					break;
			}
		}
	}

	{
		reset();

		energy = Hunger.STARVING;

		bones = true;
	}
	
	@Override
	public void reset() {
		super.reset();
		switch(holiday){
			case NONE:default:
				image = ItemSpriteSheet.PASTY;
				break;
			case BREAD:
				image = ItemSpriteSheet.CINNAMON_ROLL;
				break;
			case HWEEN:
				image = ItemSpriteSheet.PUMPKIN_PIE;
				break;
			case ZHONG_QIU:
				image = ItemSpriteSheet.SALTYMOONCAKE;
				break;
		}
	}
	
	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero);
		
		switch(holiday){
			case NONE:default:
				new Flare( 5, 32 ).color( 0xFF0000, true ).show( curUser.sprite, 2f );
				ScrollOfTerror.terror(null);
				break;
			case HWEEN:
				//heals for 10% max hp
				hero.HP = Math.min(hero.HP + hero.HT/10, hero.HT);
				hero.sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
				break;
		}
	}

	@Override
	public String name() {
		switch(holiday){
			case NONE:default:
				return Messages.get(this, "pasty");
			case BREAD:
				return Messages.get(this, "cinnamonRoll");
			case HWEEN:
				return Messages.get(this, "pie");
			case ZHONG_QIU:
				return Messages.get(this, "salty_moon_cake");
		}
	}

	@Override
	public String info() {
		switch(holiday){
			case NONE:default:
				return Messages.get(this, "pasty_desc");
			case BREAD:
				return Messages.get(this, "cinnamonRoll_desc");
			case HWEEN:
				return Messages.get(this, "pie_desc");
			case ZHONG_QIU:
				return Messages.get(this, "salty_moon_cake_desc");
		}
	}

	public void EatText(){
		switch(holiday){
			case NONE:
				ArrayList<Mob> mobs = new ArrayList<>();
				for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
					if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
						mobs.add(mob);
					}
				}
				if (mobs.isEmpty())
					super.EatText();
				else if (mobs.size() == 1)
					GLog.i( Messages.get(this, "one", mobs.remove(0).name()) );
				else
					GLog.i( Messages.get(this, "many") );
				break;
			case BREAD:
				GLog.i( Messages.get(this, "cinnamon_msg") );
				break;
			default:
				super.EatText();
		}
	}

	@Override
    public int value() {
        return 20*quantity;
    }
}
