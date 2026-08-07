package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;

public abstract class Weakly extends ActiveAbstract.Modifier {
    public static class W_M82 extends Weakly {
        @Override
        public float values() {
            return 50;
        }
    }

    public static class W_P7 extends Weakly {
        @Override
        public float values() {
            return 50;
        }
    }
}
