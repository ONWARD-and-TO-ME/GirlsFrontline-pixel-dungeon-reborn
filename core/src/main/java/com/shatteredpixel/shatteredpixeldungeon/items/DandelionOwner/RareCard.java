package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.EnumString;
import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.hero;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionShield;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public interface RareCard extends Card {
    static ArrayList<RareCard> random(ArrayList<FirstCard> firstCards, ArrayList<RareCard> curCards){
        assert firstCards != null && !firstCards.isEmpty() && curCards != null;
        ArrayList<RareCard> list = new ArrayList<>();
        if (firstCards.contains(FirstCard.HS2000))
            addAll(list, HS2000.values());
        if (firstCards.contains(FirstCard.Vector))
            addAll(list, Vector.values());
        if (firstCards.contains(FirstCard.VHS))
            addAll(list, VHS.values());
        if (firstCards.contains(FirstCard.WA2000))
            addAll(list, WA2000.values());
        if (firstCards.contains(FirstCard.General_Liu))
            addAll(list, General_Liu.values());
        addAll(list, UNIVERSAL.values());

        ArrayList<RareCard> cards = new ArrayList<>();
        RareCard c;
        int failTimes = 0;
        do {
            c = Random.element(list);
            if (curCards.contains(c) || cards.contains(c) || c == null) {
                failTimes++;
                continue;
            }
            cards.add(c);
        }while ( cards.size() < 4 && failTimes < 1000 );
        return cards;
    }
    static void addAll(ArrayList<RareCard> list, RareCard[] array){
        list.addAll(Arrays.asList(array));
    }
    @Override
    default Class<? extends Card> getCardClass(){
        return RareCard.class;
    }
    enum HS2000 implements RareCard{
        Type_64_Auto, AA_12, KSG, DESERT_EAGLE;
        final Enum<? extends Card> card;
        HS2000(){
            card = this;
        }
        @Override
        public String extra(){
            if (this == Type_64_Auto){
                DandelionShield shield = hero().buff(DandelionShield.class);
                if (shield != null && shield.shielding() > hero().HP)
                    return EnumString(this, extraKey, (shield.shielding() - hero().HP) * 1.5F);
            }
            if (this == AA_12){
                int i = 50 + hero().HT - hero().HP;
                return EnumString(this, extraKey, i);
            }
            if (this == DESERT_EAGLE){
                DandelionShield shield = hero().buff(DandelionShield.class);
                if (shield != null && shield.shielding() > 0)
                    return EnumString(this, extraKey, FirstCard.HS2000_Shield_Damage());
            }
            return null;
        }
        @Override
        public Enum<? extends Card> getCard(){
            return card;
        }
    }
    enum Vector implements RareCard{
        Type_79, AK_74U, HP_35, K2, PP_19;
        final Enum<? extends Card> card;
        Vector(){
            card = this;
        }
        @Override
        public Enum<? extends Card> getCard(){
            return card;
        }
    }
    enum VHS implements RareCard{
        M82A1, MDR, P90, PM_06, RFB, TAC_50, Zas_M21;
        final Enum<? extends Card> card;
        VHS(){
            card = this;
        }
        @Override
        public String extra(){
            if (this == P90)
                return EnumString(this, extraKey, FirstCard.VHS_HitTime());
            return null;
        }
        @Override
        public Enum<? extends Card> getCard(){
            return card;
        }
    }
    enum WA2000 implements RareCard{
        K11, NTW_20, PKP, Px4, R93, MOSIN_NAGANT;
        final Enum<? extends Card> card;
        WA2000(){
            card = this;
        }
        @Override
        public Enum<? extends Card> getCard(){
            return card;
        }
    }
    enum General_Liu implements RareCard{
        C_93, CZ75, M26_ASW, QBU_88, X95, Contender, COLT_SAA, STECHKIN, DiMer;
        final Enum<? extends Card> card;
        General_Liu(){
            card = this;
        }
        @Override
        public Enum<? extends Card> getCard(){
            return card;
        }
    }
    enum UNIVERSAL implements RareCard{
        Type_97_SHOTGUN, FP_6, M1887, DP_12,;
        final Enum<? extends Card> card;
        UNIVERSAL(){
            card = this;
        }
        @Override
        public Enum<? extends Card> getCard(){
            return card;
        }
    }
}
