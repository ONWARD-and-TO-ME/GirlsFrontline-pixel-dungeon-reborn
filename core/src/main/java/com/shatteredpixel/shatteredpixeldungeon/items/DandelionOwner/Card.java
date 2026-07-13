package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

public interface Card {
    Enum<? extends Card> getCard();
    default String title(){
        return EnumString(getCard(), ".name");
    }
    default String info(){
        String desc = desc();
        String extra = extra();
        if (extra != null)
            desc += "\n\n" + extra;
        return desc;
    }
    default String desc(){
        return EnumString(getCard(), ".desc");
    }
    default String extra(){
        return null;
    }
    static Hero hero(){
        assert Dungeon.hero != null;
        return Dungeon.hero;
    }
    String extraKey = ".extra";
    static String extraByTime(Enum<? extends Card> card, int mul){
        CardSelector selector = CardSelector.INSTANCE(hero());
        int i = selector.UpgradeTime() / 1000;
        return EnumString(card, extraKey, i * mul);
    }
    static String EnumString(Enum<? extends Card> card, String key, Object... args){
        return Messages.get(Card.class, card.name() + key, args);
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
