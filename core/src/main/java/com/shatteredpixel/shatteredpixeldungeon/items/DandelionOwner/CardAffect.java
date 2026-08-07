package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.AttackSpeed;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.MoveSpeed;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.Vulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.Weakly;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.M4A1;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class CardAffect {
    public static void fireAllAffect(Char ch){
        if (hasCard(CommonCard.Vector.Type_64))
            Buff.affect(ch, MoveSpeed.SM_Type_64.class).upgrade().setActiveTime(5F);
        if (hasCard(CommonCard.Vector.Cx4))
            Buff.affect(ch, Vulnerability.V_Cx4.class).upgrade().setActiveTime(5F);
        if (hasCard(CommonCard.Vector.MAT_49))
            Buff.affect(ch, Vulnerability.V_MAT_49.class);
    }
    public static int VHS_SA_Affect(Char ch, Weapon weapon, float damage){
        if (hasCard(CommonCard.VHS.Ak5))
            damage += 5;
        if (hasCard(CommonCard.VHS.EM_2)){
            if (weapon instanceof M4A1)
                damage *= 1.5F;
            else
                damage += Math.min(M4A1.INSTANCE().max() * 2, damage * 0.5F);
        }
        if (hasCard(CommonCard.VHS.M82))
            Buff.affect(ch, Weakly.W_M82.class).upgrade().setActiveTime(5F);
        if (hasCard(CommonCard.VHS.MP_446))
            Buff.affect(ch, AttackSpeed.SA_MP_446.class).upgrade().setActiveTime(5F);
        if (hasCard(CommonCard.VHS.P7))
            Buff.affect(ch, Weakly.W_P7.class).setActiveTime(10F);
        if (hasCard(CommonCard.VHS.PM1910)){
            if (Dungeon.hero != null) {
                float add = 0.4F * (Dungeon.hero.HT - Dungeon.hero.HP);
                if (weapon instanceof M4A1)
                    damage += Math.min( M4A1.INSTANCE().max() * 2, add );
                else
                    damage += Math.min( M4A1.INSTANCE().max() * 0.75F, add );
            }
        }
        if (hasCard(CommonCard.VHS.SPP_1))
            Buff.affect(ch, MoveSpeed.SM_SPP_1.class).setActiveTime(10F);
        if (hasCard(CommonCard.VHS.Thunder))
            damage += Math.min(ch.HT * 0.02F, 15);
        if (hasCard(CommonCard.VHS.Spitfire))
            Buff.affect(ch, AttackSpeed.SA_Spitfire.class).setActiveTime(10F);
        return Math.round(damage);
    }
    public static void onThrowing(){
        if (hasCard(CommonCard.UNIVERSAL.PP_19) && Random.Float() < 0.3F){
            GLog.p("已向M4A1填充瞬发投掷技能。");
            M4A1.INSTANCE().throwing_ready = true;
        }
    }
    public static void onIntensify(){
        if (hasCard(CommonCard.UNIVERSAL.HK512) && Random.Float() < 0.3F) {
            GLog.p("已向M4A1填充瞬发强化技能。");
            M4A1.INSTANCE().intensify_ready = true;
        }
        if (hasCard(RareCard.WA2000.K11)){
            GLog.p("已向M4A1填充瞬发投掷技能。");
            M4A1.INSTANCE().throwing_ready = true;
        }
    }
    private static boolean hasCard(Card card){
        return CardSelector.INSTANCE().hasCard(card);
    }

    public static void throwChar(final Char ch, final int centerPos, int power,
                                 boolean closeDoors, boolean collideDmg){
        if (ch.properties().contains(Char.Property.BOSS))
            power /= 2;
        int pos = ch.pos;
        int oppositeAdjacent = pos + (pos - centerPos);

        final Ballistica trajectory = new Ballistica(pos, oppositeAdjacent, Ballistica.MAGIC_BOLT);

        int dist = Math.min(trajectory.dist, power);

        boolean collided = dist == trajectory.dist;

        if (dist == 0
                || ch.rooted
                || ch.properties().contains(Char.Property.IMMOVABLE)) return;

        //large characters cannot be moved into non-open space
        if (Char.hasProp(ch, Char.Property.LARGE)) {
            for (int i = 1; i <= dist; i++) {
                if (!Dungeon.level.openSpace[trajectory.path.get(i)]){
                    dist = i-1;
                    collided = true;
                    break;
                }
            }
        }

        if (Actor.findChar(trajectory.path.get(dist)) != null){
            dist--;
            collided = true;
        }

        if (dist < 0) return;

        final int newPos = trajectory.path.get(dist);

        if (newPos == ch.pos) return;

        final int finalDist = dist;
        final boolean finalCollided = collided && collideDmg;

        int oldPos = ch.pos;
        ch.pos = newPos;
        if (finalCollided && ch.isAlive()) {
            ch.damage(Random.NormalIntRange(finalDist, 2*finalDist), IntensifySkill.class);
            Paralysis.prolong(ch, Paralysis.class, 1 + finalDist/2f);
        }
        if (closeDoors && Dungeon.level.map[oldPos] == Terrain.OPEN_DOOR){
            Door.leave(oldPos);
        }
        ch.moveSprite(oldPos, ch.pos);
    }
}
