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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GirlsFrontlinePixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

import java.io.IOException;

/**
 * 0层电脑窗口的九宫格基类：类似手机桌面的应用入口网格。
 * 每个格子（{@link AppButton}）的位置与大小都可以由子类自由指定，
 * 整体尺寸根据屏幕可用空间与给定的行列数计算，任何缩放/分辨率下都不会越过屏幕。
 */
public abstract class WndComputerGrid extends Window {

	protected static final int SCREEN_MARGIN = 2;   //窗口（含边框）与屏幕边缘的安全距离
	protected static final int PAD             = 3; //内容区四周内边距
	protected static final int GAP             = 2; //格子之间的间距
	protected static final int MAX_CELL        = 34;//格子期望最大边长
	protected static final int MIN_CELL        = 12;//格子最小边长（极小屏幕兜底）
	protected static final float LABEL_SIZE    = 6; //格子文字大小
	protected static final int LOCKED_COLOR    = 0x888888;

	protected final int cell;

	protected WndComputerGrid(int cols, int rows) {
		//窗口相机坐标系下的可用空间，减去 WINDOW 边框和屏幕安全边距
		int maxW = (int)(Game.width  / (float) PixelScene.cameraZoom)
				- chrome.marginHor() - SCREEN_MARGIN * 2;
		int maxH = (int)(Game.height / (float) PixelScene.cameraZoom)
				- chrome.marginVer() - SCREEN_MARGIN * 2;

		//由屏幕与行列数反推单个格子边长（正方形格子），再反算窗口内容尺寸
		int cellW = (maxW - PAD * 2 - GAP * (cols - 1)) / cols;
		int cellH = (maxH - PAD * 2 - GAP * (rows - 1)) / rows;
		cell = Math.max(MIN_CELL, Math.min(MAX_CELL, Math.min(cellW, cellH)));
		int innerW = PAD * 2 + cell * cols + GAP * (cols - 1);
		int innerH = PAD * 2 + cell * rows + GAP * (rows - 1);

		build();

		resize(innerW, innerH);
	}

	//子类在此创建并放置各自的格子
	protected abstract void build();

	//把一个格子放到网格的指定位置；需要特殊大小/位置时子类可自行 setRect
	protected void place(int col, int row, AppButton btn) {
		add(btn);
		btn.setRect(PAD + col * (cell + GAP), PAD + row * (cell + GAP), cell, cell);
	}

	protected static void saveAll() {
		try {
			Dungeon.saveAll();
		} catch (IOException e) {
			GirlsFrontlinePixelDungeon.reportException(e);
		}
	}

	/**
	 * 网格中的单个应用格子：上方图标、下方文字，边长与位置由外部任意指定。
	 * 可通过 locked 置灰，用于未解锁的入口。
	 */
	protected abstract static class AppButton extends Button {

		private final Image icon;
		private final RenderedTextBlock label;
		private final boolean locked;
		private final float rawW;
		private final float rawH;

		protected AppButton(String text, Image icon, boolean locked) {
			super();
			this.icon = icon;
			this.locked = locked;
			this.rawW = icon.width();
			this.rawH = icon.height();
			add(icon);
			if (locked) {
				icon.brightness(0.4f);
			}

			label = PixelScene.renderTextBlock(text, LABEL_SIZE);
			if (locked) {
				label.hardlight(LOCKED_COLOR);
			}
			add(label);
		}

		@Override
		protected void layout() {
			super.layout();

			label.maxWidth((int) width);

			//图标区域 = 格子高度 - 文字区 - 上下边距
			float iconArea = height - label.height() - 3;
			float scale = Math.min(iconArea / rawH, (width - 4) / rawW);
			icon.scale.set(scale);
			icon.x = x + (width - icon.width()) / 2f;
			icon.y = y + 1;
			PixelScene.align(icon);

			label.setPos(x + (width - label.width()) / 2f,
					y + height - label.height() - 1);
			PixelScene.align(label);
		}

		@Override
		protected void onPointerDown() {
			icon.brightness(locked ? 0.7f : 1.5f);
		}

		@Override
		protected void onPointerUp() {
			icon.brightness(locked ? 0.4f : 1f);
		}

		@Override
		protected void onClick() {
			onLeftClick();
		}

		protected abstract void onLeftClick();
	}
}
