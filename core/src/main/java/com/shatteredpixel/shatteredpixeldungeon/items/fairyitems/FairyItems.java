package com.shatteredpixel.shatteredpixeldungeon.items.fairyitems;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class FairyItems extends Item {
    {
        stackable = true;
        image = ItemSpriteSheet.SOMETHING;
        unique = true;
        defaultAction = AC_CHOOSE;
    }
    public static FairyItems RandomFairy(boolean useful){
        if (!useful)
            return RandomFairy();
        float random = Random.Float();
        if (Dungeon.hero == null || Dungeon.hero.belongings == null)
            return new Succor();
        int cursed = 0;
        if (Dungeon.hero.belongings.weapon() != null && Dungeon.hero.belongings.weapon().cursed)
            cursed++;
        if (Dungeon.hero.belongings.armor() != null && Dungeon.hero.belongings.armor().cursed)
            cursed++;
        if (Dungeon.hero.belongings.artifact() != null && Dungeon.hero.belongings.artifact().cursed)
            cursed++;
        if (Dungeon.hero.belongings.ring() != null && Dungeon.hero.belongings.ring().cursed)
            cursed++;
        if (Dungeon.hero.belongings.misc() != null && Dungeon.hero.belongings.misc().cursed)
            cursed++;
        if (random * 10 < cursed)
            return new Succor();
        Hunger hunger = Dungeon.hero.buff(Hunger.class);
        if (random *5 < 0.2F && hunger.isStarving()) {
            if (Random.Float() < Dungeon.hero.HP / (float)Dungeon.hero.HT)
                return new Peach();
            else
                return new Letter();
        }
        if (random *2 < 0.5F){
            if (Random.Float() < Dungeon.hero.HP / (float)Dungeon.hero.HT)
                return new Commander();
            else
                return new Gemini();
        }
        return RandomFairy();
    }
    private static FairyItems RandomFairy(){
        switch(Random.Int(5)){
            case 0: return new Commander();
            case 1: return new Gemini();
            case 2: return new Letter();
            case 3: return new Peach();
            case 4: default: return new Succor();
        }
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
            Catalog.setSeen(getClass());
            Catalog.countUse(getClass());
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
    public void effect(Hero hero){}
    @Override
    public int value() {
        return 50 * quantity;
    }
    @Override
    public String desc(){
        String desc = super.desc();
        if (Dungeon.hero != null && inFairyRoom(Dungeon.hero))
            desc += "\n\n" + Messages.get(this, "effect");
        return desc;
    }
}
