package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import static com.shatteredpixel.shatteredpixeldungeon.items.potions.AlchemicalCatalyst.potionChances;
import static com.shatteredpixel.shatteredpixeldungeon.items.spells.ArcaneCatalyst.scrollChances;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Transmuting;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.AlchemicalCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class Recycle extends InventorySpell {

    {
        image = ItemSpriteSheet.RECYCLE;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return (item instanceof Potion && !(item instanceof Elixir || item instanceof Brew || item instanceof AlchemicalCatalyst)) ||
                item instanceof Scroll ||
                item instanceof Plant.Seed ||
                item instanceof Runestone ||
                item instanceof TippedDart;
    }

    @Override
    protected void onItemSelected(Item item) {
        Item result;
        do {
            if (item instanceof Potion) {
                result = Reflection.newInstance(Random.chances(potionChances));
                if (item instanceof ExoticPotion){
                    result = Reflection.newInstance(ExoticPotion.regToExo.get(result.getClass()));
                }
            } else if (item instanceof Scroll) {
                result = Reflection.newInstance(Random.chances(scrollChances));
                if (item instanceof ExoticScroll){
                    result = Reflection.newInstance(ExoticScroll.regToExo.get(result.getClass()));
                }
            } else if (item instanceof Plant.Seed) {
                result = Generator.random(Generator.Category.SEED);
            } else if (item instanceof Runestone) {
                result = Generator.random(Generator.Category.STONE);
            } else {
                result = TippedDart.randomTipped(1);
            }
        } while (result.getClass() == item.getClass() || Challenges.isItemBlocked(result));

        item.detach(curUser.belongings.backpack);
        GLog.p(Messages.get(this, "recycled", result.name()));
        if (!result.collect()){
            Dungeon.level.drop(result, curUser.pos).sprite.drop();
        }
        Transmuting.show(curUser, item, result);
        curUser.sprite.emitter().start(Speck.factory(Speck.CHANGE), 0.2f, 10);
    }

    @Override
    public int value() {
        //prices of ingredients, divided by output quantity
        return Math.round(quantity * ((50 + 40) / 12f));
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{ScrollOfTransmutation.class, ArcaneCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 8;

            output = Recycle.class;
            outQuantity = 12;
        }

    }
}
