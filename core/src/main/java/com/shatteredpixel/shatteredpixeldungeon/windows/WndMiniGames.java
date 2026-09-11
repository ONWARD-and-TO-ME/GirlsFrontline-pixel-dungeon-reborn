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

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.minigames.BlackJack;
import com.shatteredpixel.shatteredpixeldungeon.minigames.WndBlackJack;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChessScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.MatchThreeScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.SnakeScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Game;

/**
 * 0层电脑窗口中的小游戏二级窗口（3×2 九宫格）：
 * 贪吃蛇、三消游戏、国际象棋、21点，以及返回主窗口的格子。
 * 关闭本窗口即露出其下方的 {@link WndComputer}。
 */
public class WndMiniGames extends WndComputerGrid {

	public WndMiniGames() {
		super(3, 2);
	}

	@Override
	protected void build() {
		//第一行：三个小游戏
		place(0, 0, new AppButton(Messages.get(this, "snake"),
				Icons.get(Icons.DISPLAY), false) {
			@Override
			protected void onLeftClick() {
				saveAll();
				Game.switchScene(SnakeScene.class);
			}
		});
		place(1, 0, new AppButton(Messages.get(this, "match3"),
				Icons.get(Icons.DISPLAY), false) {
			@Override
			protected void onLeftClick() {
				saveAll();
				Game.switchScene(MatchThreeScene.class);
			}
		});
		place(2, 0, new AppButton(Messages.get(this, "chess"),
				Icons.get(Icons.DISPLAY), false) {
			@Override
			protected void onLeftClick() {
				saveAll();
				Game.switchScene(ChessScene.class);
			}
		});

		//第二行：21点、返回主窗口
		place(0, 1, new AppButton(Messages.get(this, "blackjack"),
				Icons.get(Icons.DISPLAY), false) {
			@Override
			protected void onLeftClick() {
				BlackJack.gameStart();
				GameScene.show(new WndBlackJack(false));
			}
		});
		place(2, 1, new AppButton(Messages.get(this, "back"),
				Icons.get(Icons.LEFTARROW), false) {
			@Override
			protected void onLeftClick() {
				hide();
			}
		});
	}
}
