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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DEL;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class FairyRoom extends SpecialRoom {
    private static final int rate = 4;
    public static final int front = (rate*2+1)*rate+rate-1;
    @Override
    public int minWidth() { return rate*2+1; }
    @Override
    public int minHeight() { return rate*2+1; }
    @Override
    public int maxWidth() { return rate*2+1; }
    @Override
    public int maxHeight() { return rate*2+1; }
    private int itemPos  =   -1;
    public int[] inside = new int[(rate*2+1)*(rate*2+1)];

	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY_SP );

        int pos = level.pointToCell(center());
        int i = 0;
        for (int x = -rate; x <= rate; x++){
            for (int y = -rate; y <= rate; y++){
                Point point = new Point(center().x + x, center().y + y);
                inside[i] = level.pointToCell(point);
                i++;
            }
        }
        Mob del = new DEL();
        del.pos = pos;
        level.mobs.add( del );

        itemPos = PathFinder.NEIGHBOURS4[ Random.Int(4) ] + pos;
        Painter.set(level, itemPos, Terrain.PEDESTAL);

        entrance().set( Door.Type.UNLOCKED );
		
	}

    public int Pos(){
        return itemPos;
    }
    public void placeItem(Item item){
        Dungeon.level.drop(item, itemPos).type = Heap.Type.MISSION_CHEST;
    }
    private static final String ITEM_POS    =   "MISSION_PLACE";
    private static final String ROOM_PLACE  =   "ROOM_PLACE";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ITEM_POS, itemPos);
        bundle.put(ROOM_PLACE, inside);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        itemPos = bundle.getInt(ITEM_POS);
        inside  = bundle.getIntArray(ROOM_PLACE);
    }

}
