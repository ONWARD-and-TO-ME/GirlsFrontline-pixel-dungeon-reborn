package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.EnumString;
import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.hero;
import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.addAll;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.HS2000_Shield;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;

public interface RareCard extends Card {
    @Override
    default String title(){
        return "Rare：" + cardName();
    }
    static void getAllCard( CardSelector selector ){
        HashMap<FirstCard, RareCard[]> map = cardMap();
        for (FirstCard f : map.keySet())
            for (RareCard card : map.get(f)){
                if (selector.RareCards.contains(card)
                        || selector.curCards.contains(card))
                    continue;
                selector.curCards.add(f);
            }
    }
    static Card random( CardSelector selector ){
        ArrayList<Card> list = new ArrayList<>();
        HashMap<FirstCard, RareCard[]> map = cardMap();
        for (FirstCard card : map.keySet())
            if (selector.contain(card))
                addAll(list, map.get(card));
        addAll(list, UNIVERSAL.values());

        for (RareCard card : selector.RareCards)
            list.remove(card);
        for (Card card : selector.curCards)
            list.remove(card);

        return Random.element(list);
    }
    static HashMap<FirstCard, RareCard[]> cardMap(){
        HashMap<FirstCard, RareCard[]> map = new HashMap<>();
        map.put(FirstCard.HS2000, HS2000.values());
        map.put(FirstCard.Vector, Vector.values());
        map.put(FirstCard.VHS, VHS.values());
        map.put(FirstCard.WA2000, WA2000.values());
        map.put(FirstCard.General_Liu, General_Liu.values());
        return map;
    }
    @Override
    default Class<? extends Card> getCardClass(){
        return RareCard.class;
    }
    enum HS2000 implements RareCard{
        Type_64_Auto, AA_12, KSG, DESERT_EAGLE;
        @Override
        public String title(){
            return FirstCard.HS2000.cardName() + " " + RareCard.super.title();
        }
        @Override
        public String extra(){
            if (this == Type_64_Auto){
                HS2000_Shield shield = hero().buff(HS2000_Shield.class);
                if (shield != null && shield.shielding() > hero().HP)
                    return EnumString(this, extraKey, (shield.shielding() - hero().HP) * 1.5F);
            }
            if (this == AA_12){
                int i = 50 + hero().HT - hero().HP;
                return EnumString(this, extraKey, i);
            }
            if (this == DESERT_EAGLE){
            }
            return null;
        }
    }
    enum Vector implements RareCard{
        Type_79, AK_74U, HP_35, K2, PP_19;
        @Override
        public String title(){
            return FirstCard.Vector.cardName() + " " + RareCard.super.title();
        }
    }
    enum VHS implements RareCard{
        M82A1, MDR, P90, PM_06, RFB, TAC_50, Zas_M21;
        @Override
        public String title(){
            return FirstCard.VHS.cardName() + " " + RareCard.super.title();
        }
        @Override
        public String extra(){
            if (this == P90)
                return EnumString(this, extraKey, FirstCard.VHS_HitTime());
            return null;
        }
    }
    enum WA2000 implements RareCard{
        K11, NTW_20, PKP, Px4, R93, MOSIN_NAGANT;
        @Override
        public String title(){
            return FirstCard.WA2000.cardName() + " " + RareCard.super.title();
        }
    }
    enum General_Liu implements RareCard{
        C_93, CZ75, M26_ASW, QBU_88, X95, Contender, COLT_SAA, STECHKIN;
        @Override
        public void onSelect(){
            if (this == C_93)
                CardAffect.getCore(hero(), new Cores.C93());
            else if (this == CZ75 || this == Contender)
                CardAffect.getCore(hero(), new Cores.NormalCore().quantity(4));
            else if (this == COLT_SAA || this == STECHKIN)
                CardAffect.getCore(hero(), new Cores.NormalCore().quantity(2));
        }
        @Override
        public String title(){
            return FirstCard.General_Liu.cardName() + " " + RareCard.super.title();
        }
    }
    enum UNIVERSAL implements RareCard{
        Type_97_SHOTGUN, FP_6, M1887, DP_12;
        @Override
        public String title(){
            return "Universal " + RareCard.super.title();
        }
    }
}
