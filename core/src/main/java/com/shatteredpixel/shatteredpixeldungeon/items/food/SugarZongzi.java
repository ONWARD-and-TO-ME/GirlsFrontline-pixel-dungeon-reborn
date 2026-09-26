package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class SugarZongzi extends Food {
	{
		image = ItemSpriteSheet.SUGARZONGZI;
		energy = Hunger.HUNGRY/2f;
	}

    @Override
    protected void satisfy( Hero hero ){
        if (Dungeon.cur().hero.hasTalent(Talent.GUN_1V2)){
            if (Dungeon.cur().hero.HP < Dungeon.cur().hero.HT) {
                int add = (int) (Dungeon.cur().hero.HT*(0.05F));
                Dungeon.cur().hero.HP = Math.min( Dungeon.cur().hero.HP + add, Dungeon.cur().hero.HT );
                Dungeon.cur().hero.sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
            }
        }
        energy+=50;
        super.satisfy(hero);
        energy = Hunger.HUNGRY/2f;
        //仅在进食前改变提供饱食度，进食后恢复，以实现不对号角生效
    }
    @Override
    protected float eatingTime(){
        if(Dungeon.cur().hero.hasTalent(Talent.Type56Two_FOOD)
                || Dungeon.cur().hero.hasTalent(Talent.Type56_21V2))
            return 0;
        else
            return super.eatingTime();
    }
	@Override
    public int value() {
        return 16 * quantity;
    }
}
