package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.AttackDMG_Add;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.AttackDelay_Add;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.CardAffect;
import com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Dummy_Core;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.M4A1;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PuppetSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public abstract class Puppet extends DirectableAlly {
    {
        spriteClass = PuppetSprite.class;
        intelligentAlly = true;
        alignment = Char.Alignment.ALLY;
        HP = HT = 1;
        state = WANDERING;
    }
    {
        immunities.add( ToxicGas.class );
        immunities.add( CorrosiveGas.class );
        immunities.add( Burning.class );
        immunities.add( AllyBuff.class );
    }
    public float htMul = 1;
    public float attackSpeedMul = 1;
    public float damageMul = 1;
    public Dummy_Core core;
    public Puppet set( Dummy_Core core ){
        this.core = core;
        CardAffect.summonPuppet(this);
        return this;
    }
    public void update(){
        HT = (int) (hero().HT * htMul);
    }
    @Override
    public void die( Object cause ){
        CardAffect.puppetDie(this);
        dropCore();
        super.die(cause);
    }
    public void dropCore(){
        Dungeon.level.drop(core.broken(), pos).seen = true;
        for (int i : PathFinder.NEIGHBOURS9)
            Dungeon.level.mapped[pos + i] = true;
        GameScene.updateFog(pos, 1);
    }
    @Override
    public boolean act(){
        update();
        return super.act();
    }
    private static Hero hero(){
        return Dungeon.hero == null
                ? new Hero()
                : Dungeon.hero;
        //在读档时Dungeon.hero总是优先于Level的读档，所以轮到Level中的Mob的读档时，Dungeon.hero总是非null的。
        //此处加一个判null只是为了图鉴系统处不闪退。
    }
    public int armTier(){
        return hero().tier();
    }
    @Override
    public int damageRoll() {
        float damage;
        Hero hero = hero();
        if (hero.belongings.weapon() != null)
            damage = hero.belongings.weapon().damageRoll(this);
        else
            damage = hero.damageRoll(); //handles ring of force
        return (int) (damage * damageMul);
    }
    @Override
    public int attackSkill( Char target ) {
        return hero().attackSkill(target);
    }
    @Override
    public int defenseSkill(Char enemy) {
        Hero hero = hero();
        if (hero != null) {
            int baseEvasion = 4 + hero.lvl;
            int heroEvasion = hero.defenseSkill(enemy);

            //if the hero has more/less evasion, 50% of it is applied
            return super.defenseSkill(enemy) * (baseEvasion + heroEvasion) / 2;
        } else {
            return 0;
        }
    }
    @Override
    public float attackDelay() {
        float factor = 1F;
        for (AttackDelay_Add a : buffs(AttackDelay_Add.class))
            factor += a.percent();
        return Math.max(hero().attackDelay() / attackSpeedMul * factor, 1/3F);
    }
    @Override
    protected boolean canAttack(Char enemy) {
        Hero hero = hero();
        if (hero.belongings.weapon() != null)
            return hero.belongings.weapon().canReach(this, enemy.pos);
        else
            return M4A1.INSTANCE().canReach(this, enemy.pos) || super.canAttack(enemy);
    }
    @Override
    public int drRoll() {
        Hero hero = hero();
        if (hero.belongings.weapon() != null)
            return Random.NormalIntRange(0, hero.belongings.weapon().defenseFactor(this)/2);
        else
            return 0;
    }
    @Override
    public int attackProc( Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );
        float factor = 1F;
        for (AttackDMG_Add a : buffs(AttackDMG_Add.class))
            factor += a.percent();
        damage *= factor;

        Hero hero = hero();
        if (hero.belongings.weapon() != null)
            damage = hero.belongings.weapon().proc( this, enemy, damage );

        if (!enemy.isAlive() && enemy == hero()){
            //此项的作用是，武器proc导致玩家死亡时，将死亡归因于此
            Dungeon.fail(getClass());
            GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name())) );
        }
        return damage;
    }
    @Override
    public CharSprite sprite() {
        CharSprite s = super.sprite();
        ((PuppetSprite) s).updateArmor( armTier() );
        return s;
    }
    private static final String MulAtHT         = "Mul_T";
    private static final String MulAtAtkSpeed   = "Mul_AS";
    private static final String MulAtDamage     = "Mul_DMG";
    private static final String Summon_Core     = "Summon_Core";
    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put(MulAtHT, htMul);
        bundle.put(MulAtAtkSpeed, attackSpeedMul);
        bundle.put(MulAtDamage, damageMul);
        bundle.put(Summon_Core, core);
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        htMul           = bundle.getFloat(MulAtHT);
        attackSpeedMul  = bundle.getFloat(MulAtAtkSpeed);
        damageMul       = bundle.getFloat(MulAtDamage);
        core            = (Dummy_Core) bundle.get(Summon_Core);
    }
}
