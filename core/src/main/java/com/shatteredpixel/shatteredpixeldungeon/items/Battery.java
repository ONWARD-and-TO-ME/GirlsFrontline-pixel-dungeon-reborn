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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Battery extends Item {

	private static final String TXT_VALUE	= "%+d";

	//通关基础奖励(拾取护符)
	private static final int WIN_REWARD_NORMAL    = 2;
	//使用自定义种子通关：仅1电池(比正常通关少1)
	private static final int WIN_REWARD_SEEDED    = 1;
	//返程额外奖励(持护符回到地面，在通关奖励之上)
	private static final int RETURN_REWARD_NORMAL = 3;
	//使用自定义种子返程：额外1电池(比正常返程少2)
	private static final int RETURN_REWARD_SEEDED = 1;
	//每开启一个挑战，通关/返程各额外获取的电池
	private static final int REWARD_PER_CHALLENGE = 1;
	//开启全部10个挑战时的额外奖励(10挑奖励/10挑返程奖励)
	private static final int ALL_CHALLENGES_COUNT = 10;
	private static final int ALL_CHALLENGES_BONUS = 10;

	//通关(拾取护符)电池奖励
	public static int winReward() {
		int reward = Dungeon.customSeedText.isEmpty() ? WIN_REWARD_NORMAL : WIN_REWARD_SEEDED;
		return addChallengeBonus( reward );
	}

	//返程(持护符回到地面)额外电池奖励
	public static int returnReward() {
		int reward = Dungeon.customSeedText.isEmpty() ? RETURN_REWARD_NORMAL : RETURN_REWARD_SEEDED;
		return addChallengeBonus( reward );
	}

	//挑战加成：每开启一个挑战+1；开启10个挑战再+10
	private static int addChallengeBonus( int reward ) {
		int challengeCount = Challenges.activeChallenges();
		reward += challengeCount * REWARD_PER_CHALLENGE;
		if (challengeCount >= ALL_CHALLENGES_COUNT) {
			reward += ALL_CHALLENGES_BONUS;
		}
		return reward;
	}

	{
		image = ItemSpriteSheet.BATTERY;
		stackable = true;
	}

	public Battery() {
		this( 1 );
	}

	public Battery( int value ) {
		this.quantity = value;
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {

		Catalog.setSeen(getClass());

		//电池为全局资源货币；测试模式不获取、不写入全局（与徽章/排行榜等元进度规则一致）
		if (Dungeon.isChallenged(Challenges.TEST_MODE)) {
			GameScene.pickUp( this, pos );
			hero.spendAndNext( TIME_TO_PICK_UP );
			Sample.INSTANCE.play( Assets.Sounds.ITEM );
			updateQuickslot();
			return true;
		}

		Dungeon.addBattery( quantity );

		GameScene.pickUp( this, pos );
		hero.sprite.showStatus( 0xFFCC33, TXT_VALUE, quantity );
		hero.spendAndNext( TIME_TO_PICK_UP );

		Sample.INSTANCE.play( Assets.Sounds.ITEM );

		updateQuickslot();

		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public Item random() {
		quantity = Random.IntRange( 1, 3 );
		return this;
	}
}
