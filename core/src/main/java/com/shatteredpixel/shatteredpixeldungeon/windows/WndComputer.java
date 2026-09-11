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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.GirlsFrontlinePixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.custom.seedfinder.SeedFindScene;
import com.shatteredpixel.shatteredpixeldungeon.items.food.WholeCake;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AboutSceneV2;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.SecondTitleScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Game;
import com.watabou.utils.DeviceCompat;

/**
 * 0层电脑地块的主窗口（3×2 九宫格）：
 * 小游戏入口、种子查询器、辅助鉴定、节日蛋糕、关于、主菜单。
 */
public class WndComputer extends WndComputerGrid {

	public WndComputer() {
		super(3, 2);
	}

	@Override
	protected void build() {
		final boolean identifyUnlocked =
				Badges.isUnlocked(Badges.Badge.Identify) || DeviceCompat.isDebug();

		//第一行：小游戏、种子查询器、辅助鉴定
		place(0, 0, new AppButton(Messages.get(this, "mini_games"),
				Icons.get(Icons.DISPLAY), false) {
			@Override
			protected void onLeftClick() {
				GameScene.show(new WndMiniGames());
			}
		});
		place(1, 0, new AppButton(Messages.get(this, "seed_finder"),
				new ItemSprite(ItemSpriteSheet.SEED_SUNGRASS), false) {
			@Override
			protected void onLeftClick() {
				saveAll();
				GirlsFrontlinePixelDungeon.switchNoFade(SeedFindScene.class);
			}
		});
		place(2, 0, new AppButton(Messages.get(this, "auto_identify"),
				Icons.get(Icons.MAGNIFY), !identifyUnlocked) {
			@Override
			protected void onLeftClick() {
				if (identifyUnlocked) {
					GameScene.show(new WndOptions(
							Messages.get(SecondTitleScene.class, "AutoIdentify_title"),
							Messages.get(SecondTitleScene.class, "AutoIdentify_body"),
							Messages.get(SecondTitleScene.class, "AutoIdentify_both"),
							Messages.get(SecondTitleScene.class, "AutoIdentify_yes"),
							Messages.get(SecondTitleScene.class, "AutoIdentify_no")) {
						@Override
						protected void onSelect(int index) {
							super.onSelect(index);
							SPDSettings.AutoIdentify(index == 0 || index == 1);
							SPDSettings.AutoGuessingText(index == 0);
						}
					});
				} else {
					Badges.Badge badge = Badges.Badge.Identify;
					GameScene.show(new WndMessage(
							Messages.titleCase(badge.title()) + "\n\n" + badge.desc()));
				}
			}
		});

		//第二行：节日蛋糕、关于、主菜单
		place(0, 1, new AppButton(Messages.get(this, "cake"),
				new ItemSprite(WholeCake.getCakeImage(SPDSettings.getSpecialDay_CakeStyle())), false) {
			@Override
			protected void onLeftClick() {
				GameScene.show(new SecondTitleScene.WndCake());
			}
		});
		place(1, 1, new AppButton(Messages.get(this, "about"),
				Icons.get(Icons.INFO), false) {
			@Override
			protected void onLeftClick() {
				saveAll();
				GirlsFrontlinePixelDungeon.switchNoFade(AboutSceneV2.class);
			}
		});
		place(2, 1, new AppButton(Messages.get(this, "main_menu"),
				Icons.get(Icons.EXIT), false) {
			@Override
			protected void onLeftClick() {
				saveAll();
				Game.switchScene(TitleScene.class);
			}
		});
	}
}
