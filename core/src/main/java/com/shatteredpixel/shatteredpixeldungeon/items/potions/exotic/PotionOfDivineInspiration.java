/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.TierOfTalent;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.StatusPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentsPane;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHero;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PotionOfDivineInspiration extends ExoticPotion {
	
	{
		icon = ItemSpriteSheet.Icons.POTION_DIVINE;
		mulOnTalentUsed = 2F;
	}

	@Override
	//need to override drink so that time isn't spent right away
	protected void drink(final Hero hero) {
		Talent.onPotionUsed(hero, 2F);
		curUser = hero;
		curItem = this;

		boolean[] enabled = new boolean[5];
		enabled[1] = enabled[2] = enabled[3] = enabled[4] = true;

		DivineInspirationTracker tracker = hero.buff(DivineInspirationTracker.class);

		if (tracker != null){
			boolean allBoosted = hero.heroClass != HeroClass.Dandelion;
			for (int i = 1; i <= 4; i++){
				if (hero.heroClass == HeroClass.Dandelion) {
					//4层以外的如果出现空天赋说明已经抽完了，所以禁止。仅允许以4层HIGH_EDUCATION给一二三层补天赋点
					if (i != 4 && randomTalent(hero, i - 1) == null)
						enabled[i] = false;
				}
				else if (tracker.getBoosted(i) != 0)
					enabled[i] = false;
				else
					allBoosted = false;
			}

			if (allBoosted){
				GLog.w(Messages.get(this, "no_more_points"));
				return;
			}
		}

		if (!isIdentified())
			curItem.detach(curUser.belongings.backpack);

		GameScene.show(new WndOptions(
				new ItemSprite(this),
				Messages.titleCase(trueName()),
				Messages.get(PotionOfDivineInspiration.class, "select_tier"),
				Messages.titleCase(Messages.get(TalentsPane.class, "tier", 1)),
				Messages.titleCase(Messages.get(TalentsPane.class, "tier", 2)),
				Messages.titleCase(Messages.get(TalentsPane.class, "tier", 3)),
				Messages.titleCase(Messages.get(TalentsPane.class, "tier", 4))
		){
			@Override
			protected boolean enabled(int index) {
				return enabled[index + 1];
			}

			@Override
			protected void onSelect(int index) {
				super.onSelect(index);

				if (index != -1){
					Buff.affect(curUser, DivineInspirationTracker.class).setBoosted(index + 1);

					if (isIdentified())
						curItem.detach(curUser.belongings.backpack);

					if (hero.heroClass == HeroClass.Dandelion && index < 3) {
						addTalent(hero, index);
						addTalent(hero, index);
					}
					identify();
					curUser.busy();
					curUser.sprite.operate(curUser.pos);

					curUser.spendAndNext(1f);

					boolean unspentTalents = false;
					for (int i = 1; i <= Dungeon.hero.talents.size(); i++){
						if (Dungeon.hero.talentPointsAvailable(i) > 0){
							unspentTalents = true;
							break;
						}
					}
					if (unspentTalents){
						StatusPane.talentBlink = 10f;
						WndHero.lastIdx = 1;
					}

					GameScene.showlevelUpStars();

					Sample.INSTANCE.play( Assets.Sounds.DRINK );
					Sample.INSTANCE.playDelayed(Assets.Sounds.LEVELUP, 0.3f, 0.7f, 1.2f);
					Sample.INSTANCE.playDelayed(Assets.Sounds.LEVELUP, 0.6f, 0.7f, 1.2f);
					GLog.p(Messages.get(PotionOfDivineInspiration.class, "bonus"));

					if (!anonymous) {
						Catalog.countUse(PotionOfDivineInspiration.class);
						Talent.onPotionUsed(curUser, mulOnTalentUsed);
					}
				}
			}

			@Override
			public void onBackPressed() {
				//do nothing, prevents accidentally closing
			}
		});

	}
	private static Talent randomTalent( Hero hero, int tier ){
		ArrayList<Talent> talents = new ArrayList<>(Arrays.asList(TierOfTalent.TierTalent(tier)));
		Collections.shuffle(talents);
		for (Talent talent : talents) {
			if (talent != null
					&& !hero.hasTalentB(talent)
					&& !ScrollOfMetamorphosis.WndMetamorphReplace.isIgnoreTalent(talent)
					&& !ScrollOfMetamorphosis.WndMetamorphReplace.hasAgainstTalent(talent))
				return talent;
		}
		return null;
	}
	private static void addTalent( Hero hero, int tier ){
		Talent add = randomTalent(hero, tier);
        if (add == null)
            return;
        hero.talents.get(tier).put(add, 0);
        hero.addTalents.put(add, tier);
    }
	@Override
	public void shatter( int cell ) {
		super.shatter( cell );
		SacrificialFire fire = (SacrificialFire) Dungeon.level.blobs.get( SacrificialFire.class );
		if (fire != null && fire.cur[cell] > 0)
			fire.finish( cell );
	}

	public static class DivineInspirationTracker extends Buff {

		{
			type = buffType.POSITIVE;
			revivePersists = true;
		}

		private int[] boostedTiers = new int[5];

		private static final String BOOSTED_TIERS = "boosted_tiers";
		private static final String New_BOOSTED_TIERS = "Boosted_tiers";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(New_BOOSTED_TIERS, boostedTiers);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			if (bundle.contains(New_BOOSTED_TIERS))
				boostedTiers = bundle.getIntArray(New_BOOSTED_TIERS);
			else {
				boolean[] list = bundle.getBooleanArray(BOOSTED_TIERS);
				for (int i = 0; i < list.length; i++)
					if (list[i])
						boostedTiers[i] = 2;
			}
		}

		public void setBoosted( int tier ){
			boostedTiers[tier] += 2;
		}
		public int getBoosted( int tier ){
			return boostedTiers[tier];
		}

	}
	
}
