package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RingOfKing extends Ring {

	{
		icon = ItemSpriteSheet.Icons.RING_ELEMENTS;
	}

	@Override
	public String statsInfo() {
		int bonus = soloBuffedBonus();
		if (isIdentified()){
			return Messages.get(this, "stats", bonus, bonus);
		} else {
			return Messages.get(this, "typical_stats", 1, 1);
		}
	}

	@Override
	protected RingBuff buff( ) {
		return new KingUpdate();
	}

	public static int updateMultiplier( Char target ){
		return getBuffedBonus( target, KingUpdate.class );
	}

	public class KingUpdate extends RingBuff {
	}
}
