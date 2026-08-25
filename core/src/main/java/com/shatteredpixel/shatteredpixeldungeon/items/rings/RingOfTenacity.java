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

package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.text.DecimalFormat;
import java.util.HashSet;

public class RingOfTenacity extends Ring {

	{
		icon = ItemSpriteSheet.Icons.RING_TENACITY;
	}

	public String statsInfo() {
		super.statsInfo();
		if (isIdentified()){
			String info = Messages.get(this, "stats", new DecimalFormat("#.##").format(100f * (1f - Math.pow(0.85f, soloBuffedBonus()))));
			info += "\n\n" + Messages.get(this, "element_stats", new DecimalFormat("#.##").format(100f * (1f - Math.pow(0.825f, soloBuffedBonus()))));
            if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)) {
                info = info + "\n\n" + Messages.get(this, "combined_stats", Messages.decimalFormat("#.##", 100.0F * (1.0F - Math.pow(0.85F, combinedBuffedBonus(Dungeon.hero)))));
                info = info + "\n\n" + Messages.get(this, "element_combined_stats", Messages.decimalFormat("#.##", 100.0F * (1.0F - Math.pow(0.825F, combinedBuffedBonus(Dungeon.hero)))));
            }
            return info;
        } else {
			String info = Messages.get(this, "typical_stats", new DecimalFormat("#.##").format(100f * (1f - Math.pow(0.85f, TextGuessingLevel()))));
			info += "\n\n" + Messages.get(this, "element_typical_stats", new DecimalFormat("#.##").format(100f * (1f - Math.pow(0.825f, TextGuessingLevel()))));
			return info;
		}
	}

	@Override
	protected RingBuff buff( ) {
		return new Tenacity();
	}

	public static float damageMultiplier( Char t ){
		//(HT - HP)/HT = heroes current % missing health.
		return (float)Math.pow(0.85, getBuffedBonus( t, Tenacity.class)*((float)(t.HT - t.HP)/t.HT));
	}

	// === 元素抗性 (合并自 RingOfElements) ===

	public static final HashSet<Class> RESISTS = new HashSet<>();
	static {
		RESISTS.add( Burning.class );
		RESISTS.add( Chill.class );
		RESISTS.add( Frost.class );
		RESISTS.add( Ooze.class );
		RESISTS.add( Paralysis.class );
		RESISTS.add( Poison.class );
		RESISTS.add( Corrosion.class );

		RESISTS.add( ToxicGas.class );
		RESISTS.add( Electricity.class );

		RESISTS.addAll( AntiMagic.RESISTS );
	}

	public static float resist( Char target, Class effect ){
		if (getBuffedBonus(target, Tenacity.class) == 0) return 1f;

		for (Class c : RESISTS){
			if (c.isAssignableFrom(effect)){
				return (float)Math.pow(0.825, getBuffedBonus(target, Tenacity.class));
			}
		}

		return 1f;
	}

	public class Tenacity extends RingBuff {
	}
}
