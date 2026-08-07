package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.CardSelector;
import com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.FinalCard;
import com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.IntensifySkill;
import com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.ThrowingSkill;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class M4A1 extends MeleeWeapon {

	{
		image = ItemSpriteSheet.HK416;

		tier = 1;
		RCH = 2;
        dmgBaseDiffer = -0.8F;
	}
	@Override
	public float delayFactor( Char owner ) {
		float delay = super.delayFactor(owner);
		return Math.max(0.333F, delay);
	}
	private static final String THROWING_SKILL = "THROWING_SKILL";
	private static final String THROWING_READY = "THROWING_READY";
	private static final String INTENSIFY_READY = "INTENSIFY_READY";
	public boolean throwing_ready;
	public boolean intensify_ready;
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (change() && isEquipped(hero))
			actions.add(THROWING_SKILL);
		if (throwing_ready)
			actions.add(THROWING_READY);
		if (intensify_ready)
			actions.add(INTENSIFY_READY);
		return actions;
	}
	@Override
	public void execute( Hero hero, String action ) {
		super.execute( hero, action );
		if (action.equals(THROWING_SKILL)){
			defaultAction = THROWING_SKILL;
			updateQuickslot();
			ThrowingSkill.INSTANCE(ThrowingSelector(false), SnipeSelector(false));
		}
		else if (action.equals(THROWING_READY))
			ThrowingSkill.INSTANCE(ThrowingSelector(true), SnipeSelector(true));
		else if (action.equals(INTENSIFY_READY)) {
			intensify_ready = false;
			IntensifySkill.INSTANCE(hero);
		}
	}
	private CellSelector.Listener ThrowingSelector(boolean ready) {
		return new CellSelector.Listener() {
			@Override
			public void onSelect(Integer target) {
				if (target != null) {
					if (!ThrowingSkill.Throwing_INSTANCE(target))
						return;
					if (ready) {
						throwing_ready = false;
						updateQuickslot();
						return;
					}
					float delay = delayFactor(Dungeon.hero);
					if (delay * 4 < 1F)
						coolDownLeft = 1;
					else
						coolDownLeft = Math.round(delay * 4);
					Dungeon.hero.spendAndNext(delay * 2);
					updateQuickslot();
				}
			}

			@Override
			public String prompt() {
				return ThrowingSkill.prompt;
			}
		};
	}
	private CellSelector.Listener SnipeSelector(boolean ready) {
		return new CellSelector.Listener() {
			@Override
			public void onSelect(Integer target) {
				if (target != null) {
					if (!ThrowingSkill.Snipe_INSTANCE(target))
						return;
					if (ready) {
						throwing_ready = false;
						updateQuickslot();
						return;
					}

					float delay = delayFactor(Dungeon.hero);
					if (delay * 4 < 1F)
						coolDownLeft = 1;
					else
						coolDownLeft = Math.round(delay * 4);
					Dungeon.hero.spendAndNext(delay * 2);
				}
			}

			@Override
			public String prompt() {
				return ThrowingSkill.prompt;
			}
		};
	}
	public static M4A1 INSTANCE(){
		M4A1 m = Dungeon.hero.belongings.getItem(M4A1.class);
		if (m == null)
			m = new M4A1();
		return m;
	}
	public static float damageRoll(float mul){
		M4A1 m = INSTANCE();
		return m.augment.damageFactor(m.damageRoll(null)) * mul;
	}
	@Override
	public int reach( Char owner ){
		if (change())
			return 0;
		return super.reach( owner );
	}
	private boolean change(){
		return CardSelector.INSTANCE().hasCard(FinalCard.WA2000.FAL);
	}
}
