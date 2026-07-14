package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.EnumString;
import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.extraByTime;
import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.hero;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public interface CommonCard extends Card {
    static ArrayList<CommonCard> random( ArrayList<FirstCard> firstCards, ArrayList<CommonCard> curCards){
        assert firstCards != null && !firstCards.isEmpty() && curCards != null;
        ArrayList<CommonCard> list = new ArrayList<>();
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

        ArrayList<CommonCard> cards = new ArrayList<>();
        CommonCard c;
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
    static void addAll(ArrayList<CommonCard> list, CommonCard[] array){
        list.addAll(Arrays.asList(array));
    }
    @Override
    default Class<? extends Card> getCardClass(){
        return CommonCard.class;
    }
    enum HS2000 implements CommonCard{
        Sten_II;
        final Enum<? extends Card> card;
        HS2000(){
            card = this;
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
        @Override
        public Enum<? extends Card> getCard(){
            return card;
        }
    }
    enum Vector implements CommonCard{
        Type_64, Cx4, KLIN, HONEY_BADGER, MAT_49, PP_90, Beretta_38, Uzi, UKM_2000;
        final Enum<? extends Card> card;
        Vector(){
            card = this;
        }
        @Override
        public String extra(){
            if (this == UKM_2000)
                return extraByTime(this, 1);
            return null;
        }
        @Override
        public Enum<? extends Card> getCard(){
            return card;
        }
    }
    enum VHS implements CommonCard{
        Ak5, EM_2, IDW, M82, MP_446, P7, PM1910, SAR_21, SPP_1, Thunder, Spitfire;
        final Enum<? extends Card> card;
        VHS(){
            card = this;
        }
        @Override
        public String extra(){
            if (this == SAR_21)
                return extraByTime(this, 8);
            if (this == IDW)
                return EnumString(this, extraKey, FirstCard.VHS_HitTime());
            return null;
        }
        @Override
        public Enum<? extends Card> getCard(){
            return card;
        }
    }
    enum WA2000 implements CommonCard{
        SSG3000, SV_98;
        final Enum<? extends Card> card;
        WA2000(){
            card = this;
        }
        @Override
        public Enum<? extends Card> getCard(){
            return card;
        }
    }
    enum General_Liu implements CommonCard{
        Type_80, Rex_Zero_1, DEFENDER, JERICHO, RIBEYROLLES, MONDRAGON, TaBuKe;
        final Enum<? extends Card> card;
        General_Liu(){
            card = this;
        }
        @Override
        public String extra(){
            if (this == Rex_Zero_1 && CardPoint.General_Liu_KillingTimes.point() > 0)
                return EnumString(this, extraKey, CardPoint.General_Liu_KillingTimes.point());
            return null;
        }
        @Override
        public Enum<? extends Card> getCard(){
            return card;
        }
    }
    enum UNIVERSAL implements CommonCard{
        Type56_1, _9A91, AEK_999, C96, FAMAS, FX_05, GSh_18, HK512,
        K31, LWMMG, M1014, Mk12, Mk48, PK, PP_19, SPAS_12,
        Super_SASS, USAS_12, V_PM5, Nagant_M1895, Shipka;
        final Enum<? extends Card> card;
        UNIVERSAL(){
            card = this;
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
        @Override
        public Enum<? extends Card> getCard(){
            return card;
        }
    }
}
