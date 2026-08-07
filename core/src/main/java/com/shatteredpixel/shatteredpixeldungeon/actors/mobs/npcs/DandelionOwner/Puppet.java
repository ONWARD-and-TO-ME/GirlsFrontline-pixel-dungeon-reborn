package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GhostSprite;
import com.watabou.utils.Bundle;

public abstract class Puppet extends NPC {
    {
        spriteClass = GhostSprite.class;
    }
    private float htMul;
    private float attackSpeedMul;
    private float damageMul;
    public Puppet(){
        this(1, 1, 1);
    }
    public Puppet(float ht, float attackSpeed, float dmg){
        htMul = ht;
        attackSpeedMul = attackSpeed;
        damageMul = dmg;
        update();
    }
    protected void update(){
        Hero hero = Dungeon.hero == null
                ? new Hero()
                : Dungeon.hero;
        //在读档时Dungeon.hero总是优先于Level的读档，所以轮到Level中的Mob的读档时，Dungeon.hero总是非null的。
        //此处加一个判null只是为了图鉴系统处不闪退。
    }
    private static final String MulAtHT         = "Mul_T";
    private static final String MulAtAtkSpeed   = "Mul_AS";
    private static final String MulAtDamage     = "Mul_DMG";
    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put(MulAtHT, htMul );
        bundle.put(MulAtAtkSpeed, attackSpeedMul);
        bundle.put(MulAtDamage, damageMul);
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        htMul           = bundle.getFloat(MulAtHT);
        attackSpeedMul  = bundle.getFloat(MulAtAtkSpeed);
        damageMul       = bundle.getFloat(MulAtDamage);
    }
}
