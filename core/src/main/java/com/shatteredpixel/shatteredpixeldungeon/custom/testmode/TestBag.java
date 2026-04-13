package com.shatteredpixel.shatteredpixeldungeon.custom.testmode;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.custom.testmode.generator.LazyTest;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class TestBag extends Bag {
    {
        image = ItemSpriteSheet.LOCKED_CHEST;
    }

    @Override
    public boolean canHold( Item item ) {
        if (item instanceof TestItem){
            return super.canHold(item);
        } else {
            return false;
        }
    }

    private static final String AC_LAZY	= "LAZY";
    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.add( AC_LAZY );
        return actions;
    }

    @Override
    public void execute( final Hero hero, String action ) {

        super.execute( hero, action );
        if (action.equals( AC_LAZY )) {
            new LazyTest().collect();
        }
    }
    @Override
    public int value() {
        return 40;
    }

    public int capacity(){
        return 19;
    }
}
