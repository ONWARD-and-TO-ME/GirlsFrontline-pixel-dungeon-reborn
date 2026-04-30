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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import static com.shatteredpixel.shatteredpixeldungeon.levels.Level.set;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Grass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.RedBook;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Clipper extends MissileWeapon {
	
	{
		image = ItemSpriteSheet.THROWING_KNIFE;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.2f;
		
		bones = false;
		
		tier = 3;
	}
	
	@Override
	public int max(int lvl) {
		return  5 * tier +                      //15 base
				(tier == 1 ? 2*lvl : tier*lvl); //scaling unchanged
	}

    private static final String AC_CUT = "cut";
    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.add(AC_CUT);
        return actions;
    }
    public void execute( Hero hero, String action ) {
        super.execute(hero, action);
        if (action.equals(AC_CUT))
            GameScene.selectCell(targeter);
    }
    protected CellSelector.Listener targeter = new CellSelector.Listener(){
        @Override
        public void onSelect(Integer cell) {
            if (cell==null || !Dungeon.level.heroFOV[cell]){
                return;
            }
            onSelectA(cell);
        }

        @Override
        public String prompt() {
            return Messages.get(RedBook.class, "prompt");
        }
    };

    protected void onSelectA(Integer cell){
        boolean near = false;
        for (int i: PathFinder.NEIGHBOURS9){
            int c = Dungeon.hero.pos+i;
            if (c==cell){
                near = true;
                break;
            }
        }
        if (!near){
            GLog.n("那里太远了");
            return;
        }
        if (Dungeon.level.map[cell] == Terrain.HIGH_GRASS){
            set(cell, Terrain.GRASS);
            if (Random.Int(3)>Dungeon.hero.pointsInTalentA(Talent.Type56_23V4)) {
                GLog.n("收割失败");
                GameScene.updateMap(cell);
                Dungeon.observe();
                return;
            }
            durability -= 20F;
            if (durability <= 0){
                durability += MAX_DURABILITY;
                detach(Dungeon.hero.belongings.backpack);
                GLog.n("损坏了……");
            }
            float p = Dungeon.hero.pointsInTalentA(Talent.Type56_23V4)*3/4F;
            do {
                Grass grass = new Grass();
                Dungeon.hero.spend(-1);
                if(!grass.doPickUp(Dungeon.hero, cell)){
                    Dungeon.hero.spendAndNext(1);
                    Dungeon.level.drop(grass, cell).sprite.drop(cell);
                }
                p--;
            }while (p > Random.Float());
        }else {
            GLog.n("收割失败");
            return;
        }
        GameScene.updateMap(cell);
        Dungeon.observe();
    }
}
