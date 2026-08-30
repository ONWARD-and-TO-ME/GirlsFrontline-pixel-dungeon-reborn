package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.addAll;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;

public interface CommonCard extends Card {
    // 子枚举覆写保持不变（阵营前缀 + 稀有度前缀 + 本地化卡名）
    @Override
    default String title(){
        return "Common：" + cardName();
    }
    static void getAllCard( CardSelector selector ){
        HashMap<FirstCard, CommonCard[]> map = cardMap();
        for (FirstCard f : map.keySet())
            if (selector.hasCard(f))
                for (CommonCard card : map.get(f)) {
                    if (selector.CommonCards.contains(card)
                            || selector.curCards.contains(card))
                        continue;
                    selector.curCards.add(card);
                }
        for (CommonCard card : UNIVERSAL.values()){
            if (selector.CommonCards.contains(card)
                    || selector.curCards.contains(card))
                continue;
            selector.curCards.add(card);
        }
    }
    static Card random( CardSelector selector, boolean signal ){
        ArrayList<Card> list = new ArrayList<>();
        HashMap<FirstCard, CommonCard[]> map = cardMap();
        for (FirstCard card : map.keySet())
            if (selector.contain(card))
                addAll(list, map.get(card));
        if (!signal)
            addAll(list, UNIVERSAL.values());

        for (CommonCard card : selector.CommonCards)
            list.remove(card);
        for (Card card : selector.curCards)
            list.remove(card);

        return Random.element(list);
    }
    static HashMap<FirstCard, CommonCard[]> cardMap(){
        HashMap<FirstCard, CommonCard[]> map = new HashMap<>();
        map.put(FirstCard.HS2000, HS2000.values());
        map.put(FirstCard.Vector, Vector.values());
        map.put(FirstCard.VHS, VHS.values());
        map.put(FirstCard.WA2000, WA2000.values());
        map.put(FirstCard.General_Liu, General_Liu.values());
        return map;
    }
    @Override
    default Class<? extends Card> getCardClass(){
        return CommonCard.class;
    }
    enum HS2000 implements CommonCard{
        Sten_II;
        @Override
        public String title(){
            return FirstCard.HS2000.cardName() + " " + CommonCard.super.title();
        }
        @Override
        public String extra(){
            if (this == Sten_II)
                return normalChance();
            return null;
        }
        @Override
        public float chance( Hero hero) {
            switch (this) {
                case Sten_II:
                    return 0.667F - (float) hero.HP / hero.HT;
            }
            return 0F;
        }
    }
    enum Vector implements CommonCard{
        Type_64, Cx4, KLIN, HONEY_BADGER, MAT_49, PP_90, Beretta_38, Uzi, UKM_2000;
        @Override
        public String title(){
            return FirstCard.Vector.cardName() + " " + CommonCard.super.title();
        }
    }
    enum VHS implements CommonCard{
        Ak5, EM_2, IDW, M82, MP_446, P7, PM1910, SAR_21, SPP_1, Thunder, Spitfire;
        @Override
        public String title(){
            return FirstCard.VHS.cardName() + " " + CommonCard.super.title();
        }
        @Override
        public String extra(){
            if (this == PM1910)
                return normalChance();
            return null;
        }
        @Override
        public float chance( Hero hero ){
            switch (this) {
                case PM1910:
                    return 0.4F * (hero.HT - hero.HP);
            }
            return 0F;
        }
    }
    enum WA2000 implements CommonCard{
        SSG3000, SV_98;
        @Override
        public String title(){
            return FirstCard.WA2000.cardName() + " " + CommonCard.super.title();
        }
    }
    enum General_Liu implements CommonCard{
        Type_80, Rex_Zero_1, DEFENDER, JERICHO, RIBEYROLLES, MONDRAGON, TaBuKe;
        @Override
        public String title(){
            return FirstCard.General_Liu.cardName() + " " + CommonCard.super.title();
        }
    }
    enum UNIVERSAL implements CommonCard{
        Type56_1, _9A91, AEK_999, C96, FAMAS, FX_05, GSh_18, HK512,
        K31, LWMMG, M1014, Mk12, Mk48, PK, PP_19, SPAS_12,
        Super_SASS, USAS_12, V_PM5, Nagant_M1895, Shipka;
        @Override
        public String title(){
            return "Universal " + CommonCard.super.title();
        }
        @Override
        public String extra(){
            switch (this){
                case _9A91:
                case Super_SASS:
                    //伤害
                    return damageFactor();
                case FX_05:
                case PK:
                    //攻速
                    return delayFactor();
                case Mk12:
                    //爆伤比例
                    return critFactor();
                case Mk48:
                    //暴击率
                    return crit();
                case AEK_999:
                    return normalChance();
                case M1014:
                    if (CardSelector.INSTANCE().failureCards.contains(this))
                        return failText();
            }

            return null;
        }
        @Override
        public float chance( Hero hero ){
            switch (this){
                case _9A91:
                    return 0.3F;
                case AEK_999:
                    return 0.006F * (hero.HT - hero.HP);
                case K31:
                    return 1F;
            }
            return 0F;
        }
    }
}
