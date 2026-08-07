package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;

public abstract class MoveSpeed extends CleanSpeed{
    @Override
    final public Char.actionType affectType() {
        return Char.actionType.MOVE;
    }

    public static class SM_AK_74U extends MoveSpeed implements NotDetach {
        @Override
        public float values() {
            return 90;
        }
    }

    public static class SM_LTLX7000 extends MoveSpeed {
        @Override
        public float values() {
            return 50;
        }
    }

    public static class SM_SPP_1 extends MoveSpeed {
        @Override
        public float values() {
            return 40;
        }
    }

    public static class SM_Type_64 extends MoveSpeed implements Upgradable {
        @Override
        public float singleValue() {
            return 6;
        }
        @Override
        public int maxTimes() {
            return 10;
        }
    }

    public static class SM_Type_79 extends MoveSpeed implements FireVenueEffect {
        @Override
        public float values() {
            return 40;
        }
        @Override
        public Char target(){
            return target;
        }
    }

    public static class SM_USAS_12 extends MoveSpeed implements Upgradable {
        @Override
        public float singleValue() {
            return 10;
        }
        @Override
        public int maxTimes() {
            return 5;
        }
    }
}