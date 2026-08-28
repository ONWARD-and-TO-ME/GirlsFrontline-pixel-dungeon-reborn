package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Vector_FireBomb_Warning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;

public interface ActiveAbstract {
    float values();
    default boolean working(){
        return ((ActiveBuff) this).activeTime > 0;
    }
    interface NotDetach extends ActiveAbstract{}
    interface Upgradable extends NotDetach {
        float singleValue();
        int maxTimes();
        @Override
        default float values(){
            return singleValue() * Math.min(((CounterBuff) this).count(), maxTimes());
        }
    }
    interface FireVenueEffect extends NotDetach{
        @Override
        default boolean working(){
            return workVenue();
        }
        default boolean workVenue(){
            Vector_FireBomb_Warning fire = (Vector_FireBomb_Warning) Dungeon.level.blobs.get( Vector_FireBomb_Warning.class );
            if (fire != null && fire.cur[target().pos] > 0)
                return true;

            Vector_Fire_Aura aura = null;
            if (Dungeon.hero != null)
                aura = Dungeon.hero.buff(Vector_Fire_Aura.class);
            if (aura != null)
                return aura.contain(target().pos);

            return false;
        }
        Char target();
    }
    abstract class Modifier extends ActiveBuff {
        public final float modifier(){
            return values() / 100F;
        }
    }

}
