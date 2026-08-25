package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.AttackDMG_Add;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.AttackDelay_Add;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.HS2000_Shield;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;

public class Puppets {
    public static class Normal extends Puppet{
        @Override
        public int attackProc( Char enemy, int damage ) {
            int dmg = super.attackProc(enemy, damage);
            if (!(enemy instanceof Mob))
                return dmg;
            Mob mob = (Mob) enemy;
            if (mob.enemy instanceof ElitePuppet && !(mob.enemy instanceof Savage_99))
                mob.aggro(this);
            return dmg;
        }
    }
    public static class C93 extends ElitePuppet{
        @Override
        protected boolean canSkill(){
            return enemySeen;
        }
        @Override
        protected float skillCD() {
            return 20;
        }
        @Override
        protected void skill() {
            for (Mob m : Dungeon.level.mobs) {
                if (m.alignment == Alignment.ALLY)
                    Buff.affect(m, HS2000_Shield.class).incShield(5);
                if (m instanceof Puppet)
                    Buff.affect(m, AttackDMG_Add.C93.class, 5F);
            }
            Buff.affect(Dungeon.hero, AttackDMG_Add.C93.class, 5F);
            Buff.affect(Dungeon.hero, HS2000_Shield.class).incShield(5);
        }
    }
    public static class Savage_99 extends ElitePuppet {
        @Override
        protected boolean canSkill() {
            return enemySeen && enemy != null && enemy.alignment != alignment && canAttack(enemy);
        }
        @Override
        protected float skillCD() {
            return 50;
        }
        @Override
        protected void skill() {
            attack(enemy, 5, damageRoll(), Char.INFINITE_ACCURACY);
            spend(5F);
        }
    }
    public static class VP1915 extends ElitePuppet {
        @Override
        protected boolean canSkill() {
            return enemySeen;
        }
        @Override
        protected float skillCD() {
            return 15;
        }
        @Override
        public int attackProc( Char enemy, int damage ) {
            if (enemy instanceof Mob)
                ((Mob) enemy).aggro(this);
            return super.attackProc(enemy, damage);
        }
        @Override
        public void damage( int damage, Object cause ){
            if (buff(VP1915_Shield.class) != null)
                return;
            super.damage(damage, cause);
        }
        @Override
        protected void skill() {
            Buff.affect(this, VP1915_Shield.class, 2F);
        }
        public static class VP1915_Shield extends FlavourBuff {
            public void fx(boolean on) {
                if (on)
                    this.target.sprite.add(CharSprite.State.SHIELDED);
                else
                    this.target.sprite.remove(CharSprite.State.SHIELDED);
            }
        }
    }
    public static class Kolibri_Pistole extends ElitePuppet {
        @Override
        protected boolean canSkill() {
            return enemySeen;
        }
        @Override
        protected float skillCD() {
            return 20;
        }
        @Override
        protected void skill() {
            for (Mob m : Dungeon.level.mobs){
                if (m instanceof Puppet){
                    Buff.affect(m, AttackDMG_Add.Kolibri_Pistole.class, 5F);
                    Buff.affect(m, AttackDelay_Add.Kolibri_Pistole.class, 5F);
                }
            }
        }
    }
}
