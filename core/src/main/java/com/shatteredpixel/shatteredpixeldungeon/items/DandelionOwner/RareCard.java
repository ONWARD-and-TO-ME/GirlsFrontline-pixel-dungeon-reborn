package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.EnumString;
import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.hero;
import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.addAll;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionShield;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;

public interface RareCard extends Card {
    @Override
    default String title(){
        return "大卡：" + Card.super.title();
    }
    static Card random( CardSelector selector ){
        ArrayList<Card> list = new ArrayList<>();
        HashMap<FirstCard, RareCard[]> map = cardMap();
        for (FirstCard card : map.keySet())
            if (selector.hasCard(card))
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
            return "HS2000 " + RareCard.super.title();
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
    }
    enum Vector implements RareCard{
        Type_79, AK_74U, HP_35, K2, PP_19;
        @Override
        public String title(){
            return "Vector " + RareCard.super.title();
        }
    }
    enum VHS implements RareCard{
        M82A1, MDR, P90, PM_06, RFB, TAC_50, Zas_M21;
        @Override
        public String title(){
            return "VHS " + RareCard.super.title();
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
            return "WA2000 " + RareCard.super.title();
        }
    }
    enum General_Liu implements RareCard{
        C_93, CZ75, M26_ASW, QBU_88, X95, Contender, COLT_SAA, STECHKIN, DiMer;
        @Override
        public String title(){
            return "刘氏步枪 " + RareCard.super.title();
        }
    }
    enum UNIVERSAL implements RareCard{
        Type_97_SHOTGUN, FP_6, M1887, DP_12;
        @Override
        public String title(){
            return "通用 " + RareCard.super.title();
        }
    }
}
