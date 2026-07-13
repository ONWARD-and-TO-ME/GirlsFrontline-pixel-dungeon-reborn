package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.CardSelector;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class M4A1 extends MeleeWeapon {

	{
		image = ItemSpriteSheet.HK416;

		tier = 1;
		RCH = 2;
        dmgBaseDiffer = -0.8F;
	}
	public static M4A1 INSTANCE( Hero hero ){
		M4A1 m = hero.belongings.getItem(M4A1.class);
		if (m == null)
			m = new M4A1();
		return m;
	}
	public static int damageRoll(){
		M4A1 m = INSTANCE(Dungeon.hero);
		return m.augment.damageFactor(m.damageRoll(null));
	}
	@Override
	public int reach( Char owner ){
		if (owner != null && owner.alignment != Char.Alignment.ALLY)
			return super.reach( owner );
		CardSelector selector = CardSelector.INSTANCE(Dungeon.hero);
		return super.reach( owner );
	}
}
