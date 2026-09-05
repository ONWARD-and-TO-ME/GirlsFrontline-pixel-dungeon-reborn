package com.shatteredpixel.shatteredpixeldungeon.items.fairyitems;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DEL;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.FairyRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class FairyItems extends Item {
    {
        stackable = true;
        image = ItemSprite.itemSpriteNeedDraw;
        unique = true;
        defaultAction = AC_CHOOSE;
    }
    public static FairyItems RandomFairy(){
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
        FairyRoom room = ((RegularLevel) Dungeon.level).getRoom(FairyRoom.class);
        if (room == null)
            return false;
        if (room.list().size() == 1) {
            DEL del = null;
            for (Mob m : Dungeon.level.mobs)
                if (m instanceof DEL) {
                    del = (DEL) m;
                    break;
                }
            if (del != null)
                room.rePaint(Dungeon.level, del.pos);
        }
        for (Point point : room.list()) {
            if (hero.pos == Dungeon.level.pointToCell(point))
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
