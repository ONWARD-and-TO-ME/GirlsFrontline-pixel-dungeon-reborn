package com.shatteredpixel.shatteredpixeldungeon.items.fairyitems;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public abstract class FairyItems extends Item {
    {
        stackable = true;
        image = ItemSpriteSheet.SOMETHING;
        unique = true;
        defaultAction = AC_CHOOSE;
    }
    private static final String AC_USE  = "USE";
    public static boolean inFairyRoom(Hero hero){
        if (Game.isDebug)
            return true;
        if (!(Dungeon.level instanceof RegularLevel))
            return false;
        for (int i :((RegularLevel) Dungeon.level).fairyRoom){
            if (i == hero.pos)
                return true;
        }
        return false;
    }
    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );

        if (inFairyRoom(hero))
            actions.add(AC_USE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_USE)) {
            hero.sprite.operate( hero.pos );
            hero.busy();
            SpellSprite.show( hero, SpellSprite.FOOD );
            Sample.INSTANCE.play( Assets.Sounds.EAT );
            effect(hero);
            detach(hero.belongings.backpack);
            hero.spendAndNext(Actor.TICK);
        }

    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }
    public abstract void effect(Hero hero);
    @Override
    public int value() {
        return 50 * quantity;
    }
    @Override
    public String desc(){
        String desc = super.desc();
        if (inFairyRoom(Dungeon.hero))
            desc += "\n\n" + Messages.get(this, "effect");
        return desc;
    }
}
