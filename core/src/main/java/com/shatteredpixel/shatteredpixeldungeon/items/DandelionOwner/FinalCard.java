package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import com.watabou.utils.Random;
import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.addAll;

import java.util.ArrayList;
import java.util.HashMap;

public interface FinalCard extends Card {
    @Override
    default String title(){
        return "Final: " + Card.super.title();
    }
    static Card random( CardSelector selector ){
        ArrayList<Card> list = new ArrayList<>();
        HashMap<FirstCard, FinalCard[]> map = cardMap();
        for (FirstCard card : map.keySet())
            if (selector.hasCard(card))
                addAll(list, map.get(card));
        addAll(list, UNIVERSAL.values());

        for (FinalCard card : selector.FinalCards)
            list.remove(card);
        for (Card card : selector.curCards)
            list.remove(card);

        return Random.element(list);
    }
    static HashMap<FirstCard, FinalCard[]> cardMap(){
        HashMap<FirstCard, FinalCard[]> map = new HashMap<>();
        map.put(FirstCard.HS2000, HS2000.values());
        map.put(FirstCard.Vector, Vector.values());
        map.put(FirstCard.VHS, VHS.values());
        map.put(FirstCard.WA2000, WA2000.values());
        map.put(FirstCard.General_Liu, General_Liu.values());
        return map;
    }
    @Override
    default Class<? extends Card> getCardClass(){
        return FinalCard.class;
    }
    enum HS2000 implements FinalCard{
        CAWS, S_A_T_8, Webley;
        @Override
        public String title(){
            return "HS2000 " + FinalCard.super.title();
        }
    }
    enum Vector implements FinalCard{
        G36, KSVK;
        @Override
        public String title(){
            return "Vector " + FinalCard.super.title();
        }
    }
    enum VHS implements FinalCard{
        AUG, PA_15;
        @Override
        public String title(){
            return "VHS " + FinalCard.super.title();
        }
    }
    enum WA2000 implements FinalCard{
        FAL, Python;
        @Override
        public String title(){
            return "WA2000 " + FinalCard.super.title();
        }
    }
    enum General_Liu implements FinalCard{
        Savage_99, VP1915, VSK_94, Kolibri_Pistole;
        @Override
        public String title(){
            return "General Liu " + FinalCard.super.title();
        }
    }
    enum UNIVERSAL implements FinalCard{
        Kar98k, LTLX7000, MG5;
        @Override
        public String title(){
            return "Universal " + FinalCard.super.title();
        }
    }
}
