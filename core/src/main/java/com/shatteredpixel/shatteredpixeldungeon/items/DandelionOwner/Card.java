package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Arrays;

public interface Card {
    static void random(CardSelector selector){
        Card card;
        for (int i = 0; i < 4; i++)
            if ((card = randomCard(selector)) != null)
                selector.curCards.add(card);
    }
    static Card randomCard(CardSelector selector){
        Card card;
        if (selector.curCardNum < 1 && (card = FirstCard.random(selector)) != null)
            return card;
        if (selector.curCardNum < 5 && (card = CommonCard.random(selector)) != null)
            return card;
        if (selector.curCardNum < 7 && (card = RareCard.random(selector)) != null)
            return card;
        if (selector.curCardNum < 8 && (card = FinalCard.random(selector)) != null)
            return card;
        return null;
    }
    static void addAll(ArrayList<Card> list, Card[] array){
        list.addAll(Arrays.asList(array));
    }
    @SuppressWarnings("unchecked")
    default Enum<? extends Card> getCard(){
        return (Enum<? extends Card>) this;
    }
    default String title(){
        String name = EnumString(this, ".name");
        if (name.contains("NO TEXT FOUND")) {
            name = ((Enum<?>) this).name();
        }
        return name;
    }
    default String info(){
        String desc = desc();
        String extra = extra();
        if (extra != null)
            desc += "\n\n" + extra;
        return desc;
    }
    default void onSelect(){ }
    default String desc(){
        return EnumString(this, ".desc");
    }
    default String extra(){
        return null;
    }
    static Hero hero(){
        return Dungeon.hero;
    }
    String extraKey = ".extra";
    static String extraByTime(Card card, int mul){
        CardSelector selector = CardSelector.INSTANCE();
        int i = selector.UpgradeTime() / 1000;
        return EnumString(card, extraKey, i * mul);
    }
    Class<? extends Card> getCardClass();
    static String EnumString(Card card, String key, Object... args){
        return Messages.get(card.getCardClass(), card.getCard().name() + key, args);
    }
    enum CardPoint{
        General_Liu_KillingTimes;
        private int point;
        private static final String CardPointBundle = "Card_Point_Bd";
        public void pointUp(){
            point++;
        }
        public int point(){
            return point;
        }
        public static void reset(){
            for (CardPoint c : values())
                c.point = 0;
        }
        public static void store( Bundle bundle ){
            Bundle b = new Bundle();
            for (CardPoint c : CardPoint.values())
                b.put(c.name(), c.point);
            bundle.put(CardPointBundle, b);
        }
        public static void restore( Bundle bundle ){
            Bundle b = bundle.contains(CardPointBundle) ? bundle.getBundle(CardPointBundle) : new Bundle();
            for (CardPoint c : CardPoint.values())
                if(b.contains(c.name()))
                    c.point = b.getInt(c.name());
        }
    }
}
