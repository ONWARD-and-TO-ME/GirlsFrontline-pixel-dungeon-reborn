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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PrismaticGuard;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Brimstone;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PrismaticSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class GeminiShield extends Geminis {

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(0, HT/3);
    }

    @Override
    public int attackSkill(Char target) {
        return HT/5;
    }

    @Override
    public int defenseSkill(Char enemy) {
        return HT/3;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(HT/5, HT/4);
    }

    @Override
    public int attackProc( Char enemy, int damage ) {
        if (twin == null)
            twin = (Geminis) Actor.findById(twinID);
        if (enemy instanceof Mob && twin != null) {
            ((Mob)enemy).aggro( twin );
        }
        if(twin != null) {
            Barrier barrier = Buff.affect(twin, Barrier.class);
            barrier.incShield(1);
        }
        return super.attackProc( enemy, damage );
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        if (twin == null)
            twin = (Geminis) Actor.findById(twinID);
        if(twin != null) {
            Barrier barrier = Buff.affect(twin, Barrier.class);
            barrier.incShield(1);
        }
        return super.defenseProc(enemy, damage);
    }

    @Override
    public float speed() {
        return 1.5F;
    }
}
