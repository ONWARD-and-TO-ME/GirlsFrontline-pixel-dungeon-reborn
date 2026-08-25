package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.CardCalculator;

public abstract class AttackDMG_Add extends Increases {
    protected float otherChance;
    @Override
    public float increase( float base ){
        return base * percent();
    }
    @Override
    public float otherGet( float base ){
        return Math.min(CardCalculator.M4A1max(otherChance), increase(base));
    }
    public static class SV_98 extends AttackDMG_Add {
        {
            percent = 1.00F;
            otherChance = 3F;
        }
    }
    public static class DEFENDER extends AttackDMG_Add {
        {
            percent = 0.80F;
            otherChance = 3F;
        }
    }
    public static class C93 extends AttackDMG_Add {
        {
            percent = 0.25F;
            otherChance = 1F;
        }
    }
    public static class QBU_88 extends AttackDMG_Add{
        {
            percent = 0.3F;
            maxLvl = 5;
//            otherChance = 1F;
            //只生效于傀儡，就不需要otherChance了
        }
    }
    public static class X95 extends AttackDMG_Add {
        {
            percent = 1F;
        }
    }
    public static class Kolibri_Pistole extends AttackDMG_Add {
        {
            percent = 1F;
        }
    }
}
