package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;

public abstract class Weakly extends ActiveAbstract.Modifier {
    public static class W_M82 extends Weakly implements Upgradable {
        @Override
        public float singleValue() {
            return 20;
        }
        @Override
        public int maxTimes() {
            return 4;
        }
    }

    public static class W_P7 extends Weakly {
        @Override
        public float values() {
            return 50;
        }
    }
}
