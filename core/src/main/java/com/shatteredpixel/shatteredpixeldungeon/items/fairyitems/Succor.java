package com.shatteredpixel.shatteredpixeldungeon.items.fairyitems;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;

public class Succor extends FairyItems {

    @Override
    public void effect(Hero hero) {
        hero.HP += (int) Math.min(Math.ceil(hero.HT/2F), hero.HT-hero.HP);
        GameScene.selectItem( itemSelector );
    }

    protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

        @Override
        public String textPrompt() {
            return Messages.get(ScrollOfRemoveCurse.class, "inv_title");
        }

        @Override
        public Class<? extends Bag> preferredBag() {
            return Belongings.Backpack.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return ScrollOfRemoveCurse.unCursable(item);
        }

        @Override
        public void onSelect( Item item ) {

            if (item != null) {

                onItemSelected( item );
                Sample.INSTANCE.play( Assets.Sounds.READ );

            }
        }
    };
    protected void onItemSelected(Item item) {
        new Flare( 6, 32 ).show( curUser.sprite, 2f ) ;

        boolean procced = ScrollOfRemoveCurse.uncurse( curUser, item );

        if (curUser.buff(Degrade.class) != null) {
            Degrade.detach(curUser, Degrade.class);
            procced = true;
        }

        if (procced) {
            GLog.p( Messages.get(ScrollOfRemoveCurse.class, "cleansed") );
        } else {
            GLog.i( Messages.get(ScrollOfRemoveCurse.class, "not_cleansed") );
        }
    }
}
