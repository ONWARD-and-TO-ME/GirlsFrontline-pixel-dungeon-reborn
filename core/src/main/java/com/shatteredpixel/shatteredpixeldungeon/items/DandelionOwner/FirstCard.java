package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import static com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card.hero;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.VHS_Hack;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner.Vector_Fire_Aura;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public enum FirstCard implements Card {
    HS2000, Vector, VHS, WA2000, General_Liu;
    public static void getAllCard( CardSelector selector ){
        for (FirstCard f : FirstCard.values()) {
            if (selector.FirstCards.contains(f)
                    || selector.curCards.contains(f))
                continue;
            selector.curCards.add(f);
        }
    }
    @Override
    public String title(){
        return "阵营卡：" + cardName();
    }
    @Override
    public void onSelect(){
        if (this == Vector)
            Buff.affect(hero(), Vector_Fire_Aura.class).resetAction();
        if (this == VHS)
            Buff.affect(hero(), VHS_Hack.class);
        if (this == General_Liu)
            CardAffect.getCore(hero(),
                    new Cores.NormalCore());
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
    public static int VHS_HitTime(){
        return 5;
    }
}
