package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public interface FinalCard extends Card {
    static ArrayList<FinalCard> random(ArrayList<FirstCard> firstCards, ArrayList<FinalCard> curCards){
        assert firstCards != null && !firstCards.isEmpty() && curCards != null;
        ArrayList<FinalCard> list = new ArrayList<>();
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

        ArrayList<FinalCard> cards = new ArrayList<>();
        FinalCard c;
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
    static void addAll(ArrayList<FinalCard> list, FinalCard[] array){
        list.addAll(Arrays.asList(array));
    }
    enum HS2000 implements FinalCard{
        CAWS, S_A_T_8, Webley;
        final Enum<? extends Card> card;
        HS2000(){
            card = this;
        }
        @Override
        public Enum<? extends Card> getCard(){
            return card;
        }
    }
    enum Vector implements FinalCard{
        G36, KSVK;
        final Enum<? extends Card> card;
        Vector(){
            card = this;
        }
        @Override
        public Enum<? extends Card> getCard(){
            return card;
        }
    }
    enum VHS implements FinalCard{
        AUG, PA_15;
        final Enum<? extends Card> card;
        VHS(){
            card = this;
        }
        @Override
        public Enum<? extends Card> getCard(){
            return card;
        }
    }
    enum WA2000 implements FinalCard{
        FAL, Python;
        final Enum<? extends Card> card;
        WA2000(){
            card = this;
        }
        @Override
        public Enum<? extends Card> getCard(){
            return card;
        }
    }
    enum General_Liu implements FinalCard{
        Savage_99, VP1915, VSK_94, Kolibri_Pistole;
        final Enum<? extends Card> card;
        General_Liu(){
            card = this;
        }
        @Override
        public Enum<? extends Card> getCard(){
            return card;
        }
    }
    enum UNIVERSAL implements FinalCard{
        Kar98k, LTLX7000, MG5;
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
