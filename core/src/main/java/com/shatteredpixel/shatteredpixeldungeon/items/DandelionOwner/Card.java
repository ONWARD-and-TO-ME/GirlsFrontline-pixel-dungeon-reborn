package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Arrays;

public interface Card {
    static void getAllCard(CardSelector selector){
        FirstCard.getAllCard(selector);
        CommonCard.getAllCard(selector);
        RareCard.getAllCard(selector);
        FinalCard.getAllCard(selector);
    }
    static void random(CardSelector selector){
        Card card;
        for (int i = 0; i < 4; i++)
            if ((card = randomCard(selector)) != null)
                selector.curCards.add(card);
    }
    static Card randomCard(CardSelector selector){
        Card card;
        int curCardNum = selector.curCardNum;
        if (curCardNum < 1 && (card = FirstCard.random(selector)) != null)
            return card;
        if (curCardNum < 5 && (card = CommonCard.random(selector)) != null)
            return card;
        if (curCardNum < 7 && (card = RareCard.random(selector)) != null)
            return card;
        if (curCardNum < 8 && (card = FinalCard.random(selector)) != null)
            return card;
        return null;
    }
    static void addAll(ArrayList<Card> list, Card[] array){
        list.addAll(Arrays.asList(array));
    }
    default Enum<?> getCard(){
        return (Enum<?>) this;
    }
    String title();
    default String cardName(){
        return EnumString(this, ".name");
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
    String fail = "已失效";
    Class<? extends Card> getCardClass();
    default float chance( Hero hero ){
        return 0;
    }
    static String EnumString(Card card, String key, Object... args){
        return Messages.get(card.getCardClass(), card.getCard().name() + key, args);
    }
    enum CardPoint{
        R93_HitPoint,
        AttackDamage_Add, AttackDelay_Add;
        private float point;
        private static final String CardPointBundle = "Card_Point_Bd";
        public void pointUp(){
            point++;
        }
        public void pointDown(){
            point--;
        }
        public void pointUp( float p ){
            point += p;
        }
        public void pointDown( float p ){
            point -= p;
        }
        public void pointClear(){
            point = 0;
        }
        public float point(){
            return point;
        }
        public static void reset(){
            for (CardPoint c : values())
                c.pointClear();
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
                if (b.contains(c.name()))
                    c.point = b.getFloat(c.name());
        }
    }
    default String damageFactor(){
        return EnumString(this, extraKey, CardCalculator.everDamageFactor_Add(true) * 100);
    }
    default String delayFactor(){
        return EnumString(this, extraKey, CardCalculator.everDelayFactor_Add(true) * 100);
    }
    default String critFactor(){
        return EnumString(this, extraKey, CardCalculator.critFactor() * 100);
    }
    default String crit(){
        return EnumString(this, extraKey, CardCalculator.crit() * 100);
    }
    default String normalChance(){
        return EnumString(this, extraKey, Math.round(chance(hero()) * 100));
    }
    static int shield( Hero hero ){
        int shield = 0;
        for (ShieldBuff shieldBuff : hero.buffs(ShieldBuff.class))
            shield += shieldBuff.shielding();
        return shield;
    }
}
