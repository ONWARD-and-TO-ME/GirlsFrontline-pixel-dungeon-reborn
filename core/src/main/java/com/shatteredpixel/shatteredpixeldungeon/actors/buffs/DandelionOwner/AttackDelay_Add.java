package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;

public abstract class AttackDelay_Add extends Increases {
    @Override
    public float increase( float base ) {
        return Math.max(0.333F, base / (1 + percent()));
    }
    public static float otherChance( float base ){
        float chance;
        if (base >= 4)
            chance = 1;
        else if (base >= 2)
            chance = 0.75F;
        else if (base >= 1)
            chance = 0.5F;
        else if (base >= 0.5F)
            chance = 0.25F;
        else
            chance = 0;
        return chance;
    }
    @Override
    public float otherGet( float base ) {
        return base / (1 + percent() * otherChance(base));
    }
    public static class TaBuKe extends AttackDelay_Add {
        {
            percent = 0.8F;
        }
    }
    public static class X95 extends AttackDelay_Add {
        {
            percent = 1F;
        }
    }
    public static class Kolibri_Pistole extends AttackDelay_Add {
        {
            percent = 1F;
        }
    }
}
