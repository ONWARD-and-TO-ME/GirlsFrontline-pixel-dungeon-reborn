package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.hero;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.Vector_Fire_Aura;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionShield;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public enum FirstCard implements Card {
    HS2000, Vector, VHS, WA2000, General_Liu;
    @Override
    public String title(){
        return "阵营卡：" + Card.super.title();
    }
    @Override
    public void onSelect(){
        if (this == Vector)
            Buff.affect(hero(), Vector_Fire_Aura.class).resetAction();
    }
    @Override
    public Class<? extends Card> getCardClass(){
        return FirstCard.class;
    }
    public static Card random( CardSelector selector ){
        ArrayList<Card> list = new ArrayList<>(Arrays.asList(values()));
        for (Card card : values())
            if (selector.contain(card))
                list.remove(card);
        return Random.element(list);
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
