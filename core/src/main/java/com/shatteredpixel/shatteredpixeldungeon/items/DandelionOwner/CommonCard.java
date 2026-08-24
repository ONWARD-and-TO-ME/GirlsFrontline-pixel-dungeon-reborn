package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.EnumString;
import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.extraByTime;
import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.hero;
import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.addAll;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;

public interface CommonCard extends Card {
    @Override
    default String title(){
        return "小卡：" + Card.super.title();
    }
    // 子枚举覆写保持不变（阵营前缀 + 稀有度前缀 + 本地化卡名）
    static Card random( CardSelector selector ){
        ArrayList<Card> list = new ArrayList<>();
        HashMap<FirstCard, CommonCard[]> map = cardMap();
        for (FirstCard card : map.keySet())
            if (selector.hasCard(card))
                addAll(list, map.get(card));
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
            return "HS2000 " + CommonCard.super.title();
        }
        @Override
        public String extra(){
            if (this == Sten_II){
                Hero hero = hero();
                int i = 67 - (100 * hero.HP) / hero.HT;
                return EnumString(this, extraKey, i);
            }
            return null;
        }
    }
    enum Vector implements CommonCard{
        Type_64, Cx4, KLIN, HONEY_BADGER, MAT_49, PP_90, Beretta_38, Uzi, UKM_2000;
        @Override
        public String title(){
            return "Vector " + CommonCard.super.title();
        }
        @Override
        public String extra(){
            if (this == UKM_2000)
                return extraByTime(this, 1);
            return null;
        }
    }
    enum VHS implements CommonCard{
        Ak5, EM_2, IDW, M82, MP_446, P7, PM1910, SAR_21, SPP_1, Thunder, Spitfire;
        @Override
        public String title(){
            return "VHS " + CommonCard.super.title();
        }
        @Override
        public String extra(){
            if (this == SAR_21)
                return extraByTime(this, 8);
            if (this == IDW)
                return EnumString(this, extraKey, FirstCard.VHS_HitTime());
            return null;
        }
    }
    enum WA2000 implements CommonCard{
        SSG3000, SV_98;
        @Override
        public String title(){
            return "WA2000 " + CommonCard.super.title();
        }
    }
    enum General_Liu implements CommonCard{
        Type_80, Rex_Zero_1, DEFENDER, JERICHO, RIBEYROLLES, MONDRAGON, TaBuKe;
        @Override
        public String title(){
            return "刘氏步枪 " + CommonCard.super.title();
        }
        @Override
        public String extra(){
            if (this == Rex_Zero_1 && CardPoint.General_Liu_KillingTimes.point() > 0)
                return EnumString(this, extraKey, CardPoint.General_Liu_KillingTimes.point());
            return null;
        }
    }
    enum UNIVERSAL implements CommonCard{
        Type56_1, _9A91, AEK_999, C96, FAMAS, FX_05, GSh_18, HK512,
        K31, LWMMG, M1014, Mk12, Mk48, PK, PP_19, SPAS_12,
        Super_SASS, USAS_12, V_PM5, Nagant_M1895, Shipka;
        @Override
        public String title(){
            return "通用 " + CommonCard.super.title();
        }
        @Override
        public String extra(){
            if (this == AEK_999) {
                int i = hero().HT - hero().HP;
                return EnumString(this, extraKey, i * 0.6F);
            }
            if (this == FX_05 || this == Super_SASS)
                return extraByTime(this, 4);
            return null;
        }
    }
}
