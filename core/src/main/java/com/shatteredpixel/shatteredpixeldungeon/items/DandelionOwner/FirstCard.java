package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.hero;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionShield;

import java.util.ArrayList;

public enum FirstCard implements Card {
    HS2000, Vector, VHS, WA2000, General_Liu;
    @Override
    public Class<? extends Card> getCardClass(){
        return FirstCard.class;
    }
    public static ArrayList<FirstCard> random( ArrayList<FirstCard> firstCards ){
        ArrayList<FirstCard> cards = new ArrayList<>();

        for (FirstCard c : values()){
            if (firstCards.contains(c) || cards.contains(c))
                continue;
            cards.add(c);
            if (cards.size() >= 4)
                break;
        }

        while (cards.size() < 4)
            cards.add(HS2000);

        return cards;
    }
    public static int HS2000_Shield_Damage(){
        DandelionShield shield = hero().buff(DandelionShield.class);
        if (shield == null || shield.shielding() <= 0)
            return 0;
        return (int)Math.round(Math.sqrt(2*(shield.shielding()-1)) + 1);
    }
    public static int VHS_HitTime(){
        return 5;
    }
}
