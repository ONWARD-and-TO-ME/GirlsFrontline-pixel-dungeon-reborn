package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.AttackDMG_Add;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.AttackDelay_Add;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.M4A1;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

public class CardCalculator {
    public static float cardAttackProc_NormalAdd(Hero hero, KindOfWeapon wep ){
        int m4Add = 0;
        int add = 0;
        if (hasCard(RareCard.HS2000.DESERT_EAGLE)) {
            int dmg = shieldAttack(Dungeon.hero, 1);
            m4Add += dmg;
            add += GameMath.gate(0.25F, wep.mulByDelay(hero) / 3F, 0.5F) * dmg;
        }
        if (hasCard(FinalCard.HS2000.CAWS)) {
            int dmg = shieldAttack(Dungeon.hero, 3);
            m4Add += dmg;
            add += GameMath.gate(0.25F, wep.mulByDelay(hero) / 3F, 0.5F) * dmg;
        }
        if (wep instanceof M4A1)
            return m4Add;
        else
            return add;
    }
    public static float cardAttackProc_Add( Hero hero, float damage, boolean isM4A1 ){
        float add = 0;
        for (AttackDMG_Add a : hero.buffs(AttackDMG_Add.class))
            if (isM4A1)
                add += a.increase(damage);
            else
                add += a.otherGet(damage);
        //加性叠加
        return damage + add;
    }
    public static float cardAttackProc_Mul( Hero hero, float damage, boolean isM4A1 ){
        float chance;
        float add = 0;
        if (hasCard(CommonCard.HS2000.Sten_II)) {
            chance = 0.667F - (float) hero.HP / hero.HT;
            add += dmgIncrease(damage, chance, isM4A1);
        }
        if (hasCard(CommonCard.UNIVERSAL._9A91)){
            chance = 0.3F;
            add += dmgIncrease(damage, chance, isM4A1);
        }
        if (hasCard(CommonCard.UNIVERSAL.AEK_999)) {
            chance = 0.006F * (hero.HT - hero.HP);
            add += dmgIncrease(damage, chance, isM4A1);
        }
        if (hero.buff(IntensifySkill.Intensify.class) != null &&hasCard(CommonCard.UNIVERSAL.K31)) {
            chance = 1F;
            add += dmgIncrease(damage, chance, isM4A1);
        }
        if (hasCard(RareCard.HS2000.Type_64_Auto)) {
            int shield = 0;
            for (ShieldBuff shieldBuff : hero.buffs(ShieldBuff.class))
                shield += shieldBuff.shielding();
            if (shield > hero.HP) {
                chance = 0.015F * (shield - hero.HP);
                add += dmgIncrease(damage, chance, isM4A1);
            }
        }
        if (hasCard(RareCard.HS2000.AA_12)) {
            chance = 0.5F + 0.01F * (hero.HT - hero.HP);
            add += dmgIncrease(damage, chance, isM4A1);
        }

        chance = everDamageFactor_Add(true);
        add += dmgIncrease(damage, chance, isM4A1);

        return Math.round(damage + add);
    }
    private static float dmgIncrease( float damage, float chance, boolean isM4A1 ){
        if (isM4A1)
            return damage * chance;
        else
            return Math.min(damage * chance, M4A1max(2 * chance));
    }
    public static float everDamageFactor_Add( boolean checkNagant ){
        float mul = 0;
        if (hasFailCard(CommonCard.UNIVERSAL.M1014))
            mul += 1F;
        mul += Card.CardPoint.AttackDamage_Add.point();
        if (checkNagant){
            if (hasCard(CommonCard.UNIVERSAL.Nagant_M1895)
                    && everDelayFactor_Add(false) <= 0)
                mul += 0.25F;
        }
        return mul;
    }
    public static float everDelayFactor_Add( boolean checkNagant ){
        float mul = 0;
        if (hasCard(CommonCard.UNIVERSAL.PK))
            mul += 0.3F;
        mul += Card.CardPoint.AttackDelay_Add.point();
        if (checkNagant){
            if (hasCard(CommonCard.UNIVERSAL.Nagant_M1895)
                    && everDamageFactor_Add(false) <= 0)
                mul += 0.25F;
        }
        return mul;
    }
    public static float cardDelayFactor( Hero hero, float delay, KindOfWeapon wep ){
        float multiplier = 0;
        boolean isM4A1 = wep instanceof M4A1;
        multiplier += everDelayFactor_Add(true);
        if (hero.buff(IntensifySkill.Intensify.class) != null
                && hasCard(CommonCard.UNIVERSAL.Shipka))
            multiplier += 1F;
        if (hasCard(RareCard.General_Liu.CZ75))
            multiplier /= 2;
        if (hasCard(FinalCard.UNIVERSAL.MG5)) {
            if (isM4A1)
                return 0.333F;
            multiplier *= 3;
        }
        if (!isM4A1)
            multiplier *= AttackDelay_Add.otherChance(delay);

        delay /= multiplier + 1;
        return delay;
    }
    public static float onM4A1damageRoll( Hero hero, float damage ){
        M4A1 m = M4A1.INSTANCE();
        //外部因素产生的固定伤害就不加给M4A1了。
        damage = cardAttackProc_Add(hero, damage, true);
        damage = cardAttackProc_Mul(hero, damage, true);
        return damage;
    }
    public static float M4A1damageRoll( float minimax, float maxMul, float lastMul ){
        M4A1 m = M4A1.INSTANCE();
        float min = m.min();
        float max = m.max() * maxMul;
        float dmg = Random.NormalFloat(min + (max - min) * minimax, max * maxMul);
        dmg = m.augment.damageFactor(Math.round(dmg * lastMul));
        return onM4A1damageRoll(Dungeon.hero, dmg);
    }
    public static float M4A1damageRoll( float mul ){
        return M4A1damageRoll(0F, 1F, mul);
    }
    public static float M4A1max( float mul ){
//        M4A1 m = M4A1.INSTANCE();
//        float dmg = m.augment.damageFactor(m.max()) * mul;
//        return onM4A1damageRoll(Dungeon.hero, dmg);
        //暂时还是不吃强化符石好了
        return onM4A1damageRoll(Dungeon.hero, M4A1.INSTANCE().max() * mul);
    }
    public static int shieldAttack( Hero hero, float f ){
        int shield = 0;
        for (ShieldBuff shieldBuff : hero.buffs(ShieldBuff.class))
            shield += shieldBuff.shielding();
        shield *= f;
        if (shield == 0)
            return 0;
        return (int) Math.floor(Math.sqrt(2 * (shield - 1)) + 1);
    }
    public static int fireDamage( boolean auras ){
        float chance = 0.3F;
        if (auras){
            if (hasCard(CommonCard.Vector.KLIN))
                chance += 0.2F;
            if (hasCard(CommonCard.Vector.HONEY_BADGER))
                chance += 0.2F;
            if (hasCard(RareCard.Vector.K2))
                chance += 0.5F;
            if (hasCard(CommonCard.Vector.UKM_2000))
                chance += (float) Math.floor(upgradeTimes() / 1000F) * 5;
        }
        return Math.round(M4A1damageRoll( chance  ));
    }
    public static int critDamage( float baseDmg, boolean isM4A1 ){
        float add = baseDmg * critChance() - baseDmg;
        boolean R93 = Card.CardPoint.R93_HitPoint.point() >= 5;
        if (hasCard(RareCard.WA2000.R93)) {
            if (R93) {
                Card.CardPoint.R93_HitPoint.pointClear();
                add *= 2F;
            }
        }

        if (isM4A1)
            return Math.round(baseDmg + add);

        return Math.round(baseDmg + Math.min(add, M4A1max(maxCritChance(R93))));
    }
    private static float critChance(){
        float chance = 1.5F;
        if (hasCard(CommonCard.UNIVERSAL.Mk12))
            chance += 0.4F;
        if (hasCard(RareCard.WA2000.Px4))
            chance += 1F;
        if (Dungeon.hero.buff(IntensifySkill.Intensify.class) != null && hasCard(CommonCard.UNIVERSAL.C96))
            chance += 1.5F;
        return chance;
    }
    private static float maxCritChance(boolean R93){
        float chance = 2F;
        if (hasCard(CommonCard.UNIVERSAL.Mk12))
            chance += 1F;
        if (hasCard(RareCard.WA2000.Px4))
            chance += 2F;
        if (Dungeon.hero.buff(IntensifySkill.Intensify.class) != null && hasCard(CommonCard.UNIVERSAL.C96))
            chance += 2F;
        if (R93)
            chance *= 2F;
        return chance;
    }
    private static boolean hasCard(Card card){
        return CardSelector.INSTANCE().hasCard(card);
    }
    private static boolean hasFailCard(Card card){
        return CardSelector.INSTANCE().failureCards.contains(card);
    }
    private static int upgradeTimes(){
        return CardSelector.INSTANCE().upgradeTime();
    }
}
