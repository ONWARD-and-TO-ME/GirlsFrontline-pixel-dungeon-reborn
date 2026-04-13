package com.shatteredpixel.shatteredpixeldungeon.items.fairyitems;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.InventoryScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;

public class Succor extends FairyItems {

    @Override
    public void effect(Hero hero) {
        hero.HP += (int) Math.min(Math.ceil(hero.HT/2F), hero.HT-hero.HP);
        InventoryScroll scroll = new ScrollOfRemoveCurse();
        scroll.anonymize();
        scroll.doRead();
    }

}
