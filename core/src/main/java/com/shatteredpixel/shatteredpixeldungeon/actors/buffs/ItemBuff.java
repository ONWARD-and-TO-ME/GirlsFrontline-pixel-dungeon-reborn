package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.utils.Bundle;

public class ItemBuff extends Buff {
    {
        revivePersists = true;
    }
    protected Item item;
    public Item item(){
        return item;
    }
    private static final String ITEM = "ITEM";
    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle(bundle);
        if (item != null)
            bundle.put(ITEM, item);
    }
    @Override
    public void restoreFromBundle( Bundle bundle ){
        super.restoreFromBundle(bundle);
        if (bundle.contains(ITEM)) {
            item = (Item) bundle.get(ITEM);
            item.ownerBuff = this;
        }
    }
    @Override
    public void detach(){
        super.detach();
        if (item != null) {
            item.stopTrack();
            item.ownerBuff = null;
            if (target instanceof Hero
                    && !item.doPickUp((Hero) target))
                Dungeon.level.drop(item, target.pos);
        }
        //这种buff甚至在进入排行榜都不移除，但姑且写一下好了
    }
    @Override
    public boolean attachTo(Char target) {
        if (item != null)
            item.Tracker(target);
        return super.attachTo(target);
    }
}
