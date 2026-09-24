package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gun561;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gun561Old;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gun562;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gun562Old;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.ArrayList;

public class Gun562Accessories extends Item{
	public static final String AC_MODIFY="MODIFY";

	{
		image = ItemSpriteSheet.GUN562ACCESSORIES;

		cursedKnown = levelKnown = true;
		unique = true;
		bones = false;

		defaultAction = AC_MODIFY;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions =  super.actions(hero);
		actions.add(AC_MODIFY);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (action.equals(AC_MODIFY)){
			UpgradeGun561(hero);
		}
	}

	private void UpgradeGun561(Hero hero){
		Gun561 gun561 = null;
		Gun561Old gun561Old = null;
		for (Gun561 g : hero.belongings.getAllItems(Gun561.class))
			if (gun561 == null || gun561.trueLevel() < g.trueLevel())
				gun561 = g;
		if (gun561 == null)
			for (Gun561Old g : hero.belongings.getAllItems(Gun561Old.class))
				if (gun561Old == null || gun561Old.trueLevel() < g.trueLevel())
					gun561Old = g;
		if (gun561 != null) {
			Gun562 gun562 = new Gun562();
			gun562.clone(gun561).identify();
			Dungeon.quickslot.replaceSlot(gun561, gun562);
			if (gun561.isEquipped(hero)) {
				hero.belongings.weapon = gun562;
				gun562.activate(hero);
			}
			else
				gun561.detach(hero.belongings.backpack);
			hero.spendAndNext(3f);
			detach(hero.belongings.backpack);
			GLog.i(Messages.get(this,"succeed!"));
		}
		else if (gun561Old != null) {
			Gun562Old gun562Old = new Gun562Old();
			gun562Old.clone(gun561Old).identify();
			Dungeon.quickslot.replaceSlot(gun561Old, gun562Old);
			if (gun561Old.isEquipped(hero)) {
				hero.belongings.weapon = gun562Old;
				gun562Old.activate(hero);
			}
			else
				gun561Old.detach(hero.belongings.backpack);
			hero.spendAndNext(3f);
			detach(hero.belongings.backpack);
			GLog.i(Messages.get(this,"succeed!"));
		}
		else{
			GLog.i(Messages.get(this,"failed"));
		}
	}
}
