package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.AttackDMG_Add;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.AttackDelay_Add;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.AttackSpeed;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.HS2000_Shield;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.MoveSpeed;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.S_M82A1;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.VHS_Hack;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.Vulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.Weakly;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DandelionOwner.Puppet;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.M4A1;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class CardAffect {
    private static float partialShield = 0;
    //这个不需要存档，即便靠跨存档进行操作或者关闭重启，也就多一点或者少一点护盾。
    private static void affectAfterAttack( Hero hero, Char enemy, int baseDMG, KindOfWeapon wep ){
        if (hasCard(FirstCard.HS2000)){
            float s = 1;
            if (hasCard(RareCard.HS2000.KSG))
                s++;
            if (hasCard(FinalCard.HS2000.S_A_T_8))
                s += 2;
            if (!(wep instanceof M4A1))
                s *= wep.mulByDelay(hero);
            partialShield += s;
            int shield = (int) Math.floor(partialShield);
            partialShield -= shield;
            Buff.affect(hero, HS2000_Shield.class).incShield(shield);
        }
        if (hasCard(FirstCard.VHS))
            VHS_Hack_Affect(enemy);
        if (hasCard(RareCard.WA2000.R93))
            Card.CardPoint.R93_HitPoint.pointUp();
        if (hasFailCard(CommonCard.UNIVERSAL.SPAS_12) && Random.Float() < 0.06F)
            throwChar(enemy, hero.pos, 2, false, false);
        if (hasCard(CommonCard.UNIVERSAL.USAS_12))
            Buff.affect(enemy, MoveSpeed.SM_USAS_12.class).upgrade().setActiveTime(5F);

        if (hasCard(RareCard.UNIVERSAL.Type_97_SHOTGUN)){
            if (!Ignore_Type_97_SHOTGUN){
                Ignore_Type_97_SHOTGUN = true;
                ArrayList<Mob> mobs = new ArrayList<>();
                for (Mob m : hero.getVisibleEnemies()) {
                    if (m.alignment == Char.Alignment.ALLY || m instanceof NPC)
                        continue;
                    if (hero.canAttack(m))
                        mobs.add(m);
                }
                if (mobs.isEmpty())
                    if (enemy instanceof Mob)
                        mobs.add((Mob) enemy);
                if (mobs.isEmpty())
                    Ignore_Type_97_SHOTGUN = false;
                else
                    //攻击，Hero先执行attack再spend，attackProc在attack中执行，所以过程中加入Actor会及时结算。
                    //第一段攻击，状态值为false，所以当前段伤害/2，进入此处，将状态值赋值为true，加入一个Actor记录下半段攻击，攻击的伤害为进入CardAffect的原始伤害。
                    addDoubleAttack(hero, Random.element(mobs), 0.5F, baseDMG);
                    //第二段伤害需要落后于第一段伤害
            }
            else
                //第二段攻击，状态值为true，进入此段，不会变成死循环。将状态值赋值为false，为下一次攻击做准备。
                Ignore_Type_97_SHOTGUN = false;
        }
        if (hasCard(FinalCard.UNIVERSAL.Kar98k)){
            if (!Ignore_Kar98k){
                Ignore_Kar98k = true;
                addDoubleAttack(hero, enemy, 1F, baseDMG);
            }
            else
                Ignore_Kar98k = false;
        }
        if (hasFailCard(RareCard.UNIVERSAL.FP_6) && Random.Float() < 0.15F)
            throwChar(enemy, hero.pos, 2, false, false);
    }
    private static void addDoubleAttack( Hero hero, Char enemy, float dmgMulti, int damage ){
        Actor.add(new Actor() {
            @Override
            protected boolean act() {
                hero.attack(enemy, damage, dmgMulti, 0, Char.INFINITE_ACCURACY);
                Actor.remove(this);
                return true;
            }
        });
    }
    private static boolean Ignore_Type_97_SHOTGUN = false;
    private static boolean Ignore_Kar98k = false;
    public static int cardAttackProc( Hero hero, Char enemy, int damage, int baseDMG, KindOfWeapon wep ){
        float dmg = damage;
        if (hasCard(FirstCard.VHS))
            dmg = VHS_Hack_Proc(hero, enemy, dmg, wep);
        dmg += CardCalculator.cardAttackProc_NormalAdd(hero, wep);
        if (!(wep instanceof M4A1)) {
            //此二者已在M4A1damageRoll执行。
            dmg = CardCalculator.cardAttackProc_Add(hero, dmg, false);
            dmg = CardCalculator.cardAttackProc_Mul(hero, dmg, false);
        }
        int d = tryCrit(dmg, wep instanceof M4A1);
        if (hasCard(RareCard.General_Liu.CZ75))
            d /= 2;
        if (hasCard(RareCard.UNIVERSAL.Type_97_SHOTGUN) && !Ignore_Type_97_SHOTGUN)
            d /= 2;
        affectAfterAttack(hero, enemy, baseDMG, wep);
        return d;
    }
    public static void fireAllAffect(Char ch){
        if (hasCard(CommonCard.Vector.Type_64))
            Buff.affect(ch, MoveSpeed.SM_Type_64.class).upgrade().setActiveTime(5F);
        if (hasCard(CommonCard.Vector.Cx4))
            Buff.affect(ch, Vulnerability.V_Cx4.class).upgrade().setActiveTime(5F);
        if (hasCard(CommonCard.Vector.MAT_49))
            Buff.affect(ch, Vulnerability.V_MAT_49.class);

        if (hasCard(RareCard.Vector.Type_79))
            Buff.affect(ch, MoveSpeed.SM_Type_79.class);
        if (hasCard(RareCard.Vector.AK_74U)){
            Buff.affect(ch, MoveSpeed.SM_AK_74U.class).setActiveTime(5F);
            Buff.affect(ch, Vulnerability.V_AK_74U.class).setActiveTime(5F);
        }
        if (hasCard(RareCard.Vector.HP_35))
            Buff.affect(ch, Vulnerability.V_HP_35.class);

        if (hasCard(FinalCard.Vector.KSVK))
            Buff.affect(ch, Vulnerability.V_KSVK.class).upgrade().setActiveTime(5F);
    }
    public static float VHS_Hack_Proc( Hero hero, Char enemy, float damage, KindOfWeapon wep ){
        VHS_Hack hack = Buff.affect(hero, VHS_Hack.class);
        boolean isM4A1 = wep instanceof M4A1;
        float delay = wep.delayFactor(hero);
        if (!hack.isHacking()){
            if (isM4A1)
                hack.charge(1F);
            else
                hack.charge(GameMath.gate(0.5F, delay, 2F));
            return damage;
        }
        float add = 0;
        if (hasCard(CommonCard.VHS.Ak5))
            add += 5;
        if (hasCard(CommonCard.VHS.PM1910)) {
            float mul = isM4A1 ? 2 : 0.75F;
            add += Math.min(0.4F * (hero.HT - hero.HP), CardCalculator.M4A1max(mul));
        }
        if (hasCard(CommonCard.VHS.Thunder))
            add += Math.min(enemy.HT * 0.02F, 15);
        if (hasCard(RareCard.VHS.TAC_50))
            add += Math.min(enemy.HT * 0.05F, 30);

        int dmg = tryCrit(damage * VHS_Hack_Factor() + add, isM4A1);
        if (hasCard(RareCard.VHS.Zas_M21)) {
            float mul = 1F;
            ArrayList<Char> mobs = new ArrayList<>();
            for (int i : PathFinder.NEIGHBOURS25) {
                int cell = i + enemy.pos;
                Char m = Actor.findChar(cell);
                if (m == null || m.alignment == Char.Alignment.ALLY || m instanceof NPC)
                    continue;
                if (m == enemy)
                    continue;
                if (mul > 0.5F)
                    mul -= 0.1F;
                mobs.add(m);
            }
            for (Char ch : mobs) {
                int finalDmg = dmg;
                float finalMul = mul;
                Actor.add(new Actor() {
                    @Override
                    protected boolean act() {
                        ch.damage(Math.round(finalDmg * finalMul), VHS_Hack.class);
                        Actor.remove(this);
                        return true;
                    }
                });
            }
        }
        if (hasCard(RareCard.VHS.MDR)) {
            dmg /= 5;
            for (Mob m : hero.getVisibleEnemies()){
                if (m.alignment == Char.Alignment.ALLY || m instanceof NPC)
                    continue;
                if (m == enemy)
                    continue;
                int finalDmg = dmg;
                Actor.add(new Actor() {
                    @Override
                    protected boolean act() {
                        m.damage(finalDmg, VHS_Hack.class);
                        Actor.remove(this);
                        return true;
                    }
                });
                VHS_Hack_Affect(m);
            }
        }
        if (hasCard(RareCard.VHS.RFB)){
            if (enemy.buff(VHS_Hack.VHS_Hack_KillingTracker.class) == null)
                Actor.add(new Actor() {

                    {
                        actPriority = VFX_PRIO;
                    }

                    @Override
                    protected boolean act() {
                        if (enemy.isAlive()) {
                            hack.fullCharge();
                            Buff.affect(enemy, VHS_Hack.VHS_Hack_KillingTracker.class, 5F);
                        }
                        Actor.remove(this);
                        return true;
                    }
                });
        }
        if (hasCard(FinalCard.VHS.PA_15))
            if (enemy.HP < 0.15F * enemy.HT)
                Actor.add(new Actor() {
                    @Override
                    protected boolean act() {
                        enemy.damage(enemy.HT / 4, VHS_Hack.class);
                        Actor.remove(this);
                        return true;
                    }
                });

        return dmg;
    }
    private static float VHS_Hack_Factor(){
        float factor = 1F;
        if (hasCard(CommonCard.VHS.EM_2))
            factor += 0.5F;
        if (hasCard(CommonCard.VHS.SAR_21))
            factor += (upgradeTimes() / 1000F) * 0.08F;
        return factor;
    }
    public static void VHS_Hack_Affect( Char ch ){
        if (hasCard(CommonCard.VHS.M82))
            Buff.affect(ch, Weakly.W_M82.class).upgrade().setActiveTime(8F);
        if (hasCard(CommonCard.VHS.MP_446))
            Buff.affect(ch, AttackSpeed.SA_MP_446.class).upgrade().setActiveTime(8F);
        if (hasCard(CommonCard.VHS.P7))
            Buff.affect(ch, Weakly.W_P7.class).setActiveTime(15F);
        if (hasCard(CommonCard.VHS.SPP_1))
            Buff.affect(ch, MoveSpeed.SM_SPP_1.class).setActiveTime(15F);
        if (hasCard(CommonCard.VHS.Spitfire))
            Buff.affect(ch, AttackSpeed.SA_Spitfire.class).setActiveTime(15F);

        if (hasCard(RareCard.VHS.M82A1))
            Buff.affect(ch, S_M82A1.class).setActiveTime(5F);
    }
    private static void addThrowing(){
        GLog.p("已向M4A1填充瞬发投掷技能。");
        M4A1.INSTANCE().throwing_ready = true;
    }
    private static void addIntensify(){
        GLog.p("已向M4A1填充瞬发强化技能。");
        M4A1.INSTANCE().intensify_ready = true;
    }
    public static void onThrowing(){
        if (hasCard(CommonCard.UNIVERSAL.PP_19) && Random.Float() < 0.3F){
            GLog.p(Messages.get(CardAffect.class, "throwing_charged"));
            M4A1.INSTANCE().throwing_ready = true;
        }
    }
    public static void onIntensify(){
        if (hasCard(CommonCard.UNIVERSAL.HK512) && Random.Float() < 0.3F)
            addIntensify();
        if (hasCard(RareCard.WA2000.K11))
            addThrowing();
        if (hasCard(CommonCard.UNIVERSAL.PP_19) && Random.Float() < 0.3F)
            addThrowing();
    }
    public static void onMove( Hero hero ){
        if (hasCard(CommonCard.UNIVERSAL.V_PM5)
                && hero.buff(V_PM5_Tracker.class) == null){
            Buff.affect(hero, V_PM5_Tracker.class, 80F);
            Buff.affect(hero, HS2000_Shield.class).incShield(5);
        }
        if (hasCard(FinalCard.UNIVERSAL.Kar98k) && Random.Int(10) == 0)
            Buff.prolong( hero, Cripple.class, 5F);
    }
    public static void afterDamage(Hero hero ){
        if (hasCard(CommonCard.UNIVERSAL.M1014)
                && hero.HP < hero.HT * 0.05F)
            CardSelector.INSTANCE().destroyCard(CommonCard.UNIVERSAL.M1014);
    }
    public static class V_PM5_Tracker extends FlavourBuff{ }
    public static int tryCrit( float baseDmg, boolean M4A1 ){
        return crit()
                ? CardCalculator.critDamage(baseDmg, M4A1)
                : Math.round(baseDmg);
    }
    public static boolean crit(){
        float rate = 0F;
        if (Dungeon.hero.buff(IntensifySkill.Intensify.class) != null)
            rate += 0.1F;
        if (hasCard(FirstCard.WA2000))
            rate += 0.3F;
        if (hasFailCard(CommonCard.UNIVERSAL.Mk48))
            rate += 0.2F;
        if (hasCard(CommonCard.WA2000.SSG3000)
                && Dungeon.hero.buff(IntensifySkill.Intensify.class) != null)
            rate += 0.6F;
        if (hasCard(RareCard.WA2000.PKP))
            rate += 0.5F;
        if (hasCard(RareCard.WA2000.MOSIN_NAGANT))
            rate += (float) Math.floor(upgradeTimes() / 1000F) * 0.02F;
        return Random.Float() < rate;
    }
    public static void getCore( Hero hero, Dummy_Core core ){
        if (!core.doPickUp(hero))
            Dungeon.level.drop(core, hero.pos);
    }
    public static <T extends Puppet> void summonPuppet( T puppet ){
        if (hasCard(CommonCard.General_Liu.Type_80))
            puppet.attackSpeedMul += 0.25F;
        if (hasCard(CommonCard.General_Liu.JERICHO)){
            puppet.htMul -= 0.05F;
            puppet.attackSpeedMul += 0.2F;
            puppet.damageMul += 0.2F;
        }
        if (hasCard(CommonCard.General_Liu.RIBEYROLLES))
            puppet.damageMul += 0.25F;

        if (hasCard(RareCard.General_Liu.CZ75)) {
            puppet.damageMul *= 0.5F;
//            puppet.attackSpeedMul *= 0.5F;
            //使用的玩家武器攻速，这里暂时不做二次扣减。
        }
        if (hasCard(RareCard.General_Liu.Contender)){
            puppet.htMul *= 0.05F;
            puppet.damageMul *= 0.3F;
            puppet.attackSpeedMul *= 0.3F;
        }
    }
    public static <T extends Puppet> void puppetDie( T puppet ){
        if (hasCard(CommonCard.General_Liu.Rex_Zero_1))
            puppet.core.htMul += 0.005F;
        if (hasCard(CommonCard.General_Liu.DEFENDER))
            Buff.affect(Dungeon.hero, AttackDMG_Add.DEFENDER.class, 5F);
        if (hasCard(CommonCard.General_Liu.MONDRAGON)){
            puppet.core.attackSpeedMul += 0.03F;
            puppet.core.damageMul += 0.03F;
        }
        if (hasCard(CommonCard.General_Liu.TaBuKe))
            Buff.affect(Dungeon.hero, AttackDelay_Add.TaBuKe.class, 5F);

        if (hasCard(RareCard.General_Liu.M26_ASW)){
            addThrowing();
            addIntensify();
        }
        if (hasCard(RareCard.General_Liu.X95)){
            for (Mob m : Dungeon.level.mobs)
                if (m instanceof Puppet && m != puppet)
                    if (m.fieldOfView[puppet.pos]){
                        Buff.affect(m, AttackDMG_Add.X95.class, 5F);
                        Buff.affect(m, AttackDelay_Add.X95.class, 5F);
                    }
        }
    }
    public static void halfKilo(){
        if (hasCard(RareCard.General_Liu.QBU_88)){
            for (Mob m : Dungeon.level.mobs)
                if (m instanceof Puppet){
                    Buff.affect(m, AttackDMG_Add.QBU_88.class).upgrade().notDetach();
                    if (m.HP < m.HT)
                        Buff.affect(m, HS2000_Shield.class).incShield(m.HT - m.HP);
                }
        }
    }
    public static void kiloTimes(){
        if (hasCard(CommonCard.UNIVERSAL.FX_05))
            Card.CardPoint.AttackDelay_Add.pointUp(0.04F);
        if (hasCard(CommonCard.UNIVERSAL.Super_SASS))
            Card.CardPoint.AttackDamage_Add.pointUp(0.04F);
    }
    private static int upgradeTimes(){
        return CardSelector.INSTANCE().upgradeTime();
    }
    private static boolean hasCard( Card card ){
        return CardSelector.INSTANCE().hasCard(card);
    }
    private static boolean hasFailCard( Card card ){
        return CardSelector.INSTANCE().failureCards.contains(card);
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
            Actor.add(new Actor() {
                @Override
                protected boolean act() {
                    ch.damage(Random.NormalIntRange(finalDist, 2*finalDist), M4A1.INSTANCE());
                    Actor.remove(this);
                    return true;
                }
            });
            Paralysis.prolong(ch, Paralysis.class, 1 + finalDist/2f);
        }
        if (closeDoors && Dungeon.level.map[oldPos] == Terrain.OPEN_DOOR){
            Door.leave(oldPos);
        }
        ch.moveSprite(oldPos, ch.pos);
    }
}
