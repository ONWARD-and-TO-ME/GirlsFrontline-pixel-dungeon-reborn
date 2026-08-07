package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.Core_Calling;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DandelionOwner.Puppet;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public abstract class Dummy_Core extends Item {
    {
        image = ItemSpriteSheet.SOMETHING;
    }
    private static final String AC_FIX = "fix";
    private static final String AC_CALL = "call";
    private int fixTimeNeed;
    private float htMul;
    private float attackSpeedMul;
    private float damageMul;
    public Dummy_Core(){
        this(0);
    }
    public Dummy_Core(int fix){
        fixTimeNeed = fix;
    }
    @Override
    public ArrayList<String> actions( Hero hero ){
        ArrayList<String> actions = super.actions(hero);
        if (fixTimeNeed <= 0)
            actions.add(AC_CALL);
        else
            actions.add(AC_FIX);
        return actions;
    }
    @Override
    public void execute( Hero hero, String action ){
        super.execute(hero, action);
        if (action.equals(AC_FIX)){
            fixTimeNeed--;
            hero.spendAndNext(1F);
        }
        else if (action.equals(AC_CALL)){
            Buff.affect(hero, Core_Calling.class).addCore((Dummy_Core) detach(hero.belongings.backpack));
        }
    }
    public void summon( int pos ){
        Puppet puppet = puppet(htMul, attackSpeedMul, damageMul);
        GameScene.add(puppet, 0);
        ScrollOfTeleportation.appear(puppet, pos);
    }
    protected abstract Puppet puppet(float ht, float attackSpeed, float damage);
    private static final String FIX_TIME_NEED = "FIX_TIME_NEED";
    private static final String MulAtHT         = "Mul_T";
    private static final String MulAtAtkSpeed   = "Mul_AS";
    private static final String MulAtDamage     = "Mul_DMG";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(FIX_TIME_NEED, fixTimeNeed);
        bundle.put(MulAtHT, htMul );
        bundle.put(MulAtAtkSpeed, attackSpeedMul);
        bundle.put(MulAtDamage, damageMul);
    }
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        fixTimeNeed = bundle.getInt(FIX_TIME_NEED);
        htMul           = bundle.getFloat(MulAtHT);
        attackSpeedMul  = bundle.getFloat(MulAtAtkSpeed);
        damageMul       = bundle.getFloat(MulAtDamage);
    }
}
