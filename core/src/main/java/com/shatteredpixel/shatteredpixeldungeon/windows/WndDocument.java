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

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.PointerEvent;
import com.watabou.input.ScrollEvent;
import com.watabou.noosa.Image;
import com.watabou.noosa.ScrollArea;
import com.watabou.noosa.ui.Component;

/**
 * 可滚动的长文本文档窗口（羊皮纸风格），用于展示从 assets 读取的 txt 设计文档。
 * WndStory 不能滚动，长于窗口的正文会被截断，因此这里使用 ScrollPane 承载正文。
 */
public class WndDocument extends Window {

	private static final int WIDTH_P = 120;
	private static final int WIDTH_L = 156;
	private static final int MARGIN = 2;
	private static final int MAX_HEIGHT_P = 160;
	private static final int MAX_HEIGHT_L = 120;

	public WndDocument( Image icon, String title, String text ) {
		super( 0, 0, Chrome.get( Chrome.Type.SCROLL ) );

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		//点击窗口外侧关闭；先于滚动面板添加，使面板内部的拖动/点击优先到达面板。
		//遮罩必须吞掉所有落向背后窗口的事件：ScrollPane 的点击转发不要求"按下"也起始于自身，
		//若在文档面板内按下、在面板外松开，UP 会落到背后的图鉴列表上误触文档按钮；滚轮同理。
		ScrollArea blocker = new ScrollArea( 0, 0, PixelScene.uiCamera.width, PixelScene.uiCamera.height ) {

			private PointerEvent pressed = null;

			@Override
			public boolean onSignal( PointerEvent event ) {
				if (event == null) {
					//拖动轮询：按下起始于遮罩时继续吞掉，防止流向背后窗口
					return pressed != null;
				}
				if (!overlapsScreenPoint( (int)event.current.x, (int)event.current.y )) {
					if (pressed != null && event.type == PointerEvent.Type.UP) {
						pressed = null;
					}
					return false;
				}
				if (event.type == PointerEvent.Type.DOWN) {
					pressed = event;
					return true;
				} else if (event.type == PointerEvent.Type.UP) {
					if (pressed == event) {
						//完整点击发生在面板外（标题/边距/窗口外）：关闭文档
						pressed = null;
						onBackPressed();
					}
					//即使按下起始于文档面板（拖到外面松开），也吞掉这次 UP，防止穿透
					return true;
				}
				//悬停事件同样截获，保持窗口模态
				event.handle();
				return true;
			}

			@Override
			protected void onScroll( ScrollEvent event ) {
				//吞掉滚轮事件，防止背后的图鉴列表随文档一起滚动
			}
		};
		blocker.camera = PixelScene.uiCamera;
		add( blocker );

		float y = MARGIN;
		IconTitle ttl = new IconTitle( icon, title );
		ttl.setRect( MARGIN, y, width - 2*MARGIN, 0 );
		ttl.tfLabel.invert();
		add( ttl );
		y = ttl.bottom() + MARGIN;

		Component content = new Component();
		RenderedTextBlock body = PixelScene.renderTextBlock( text, 6 );
		body.invert();
		body.maxWidth( width - 2*MARGIN - 2 );
		body.setPos( 0, 0 );
		content.add( body );
		content.setSize( width - 2*MARGIN, body.bottom() );

		float paneHeight = Math.min( content.height(), (PixelScene.landscape() ? MAX_HEIGHT_L : MAX_HEIGHT_P) - y );

		//必须先 resize 窗口：ScrollPane 内容相机的屏幕位置在其 layout() 时按窗口相机定位并冻结，
		//若先 setRect 后 resize，内容相机会停留在窗口居中前的旧位置，正文整体偏移出窗口
		resize( width, (int)(y + paneHeight + MARGIN) );

		ScrollPane pane = new ScrollPane( content );
		add( pane );
		pane.setRect( MARGIN, y, width - 2*MARGIN, paneHeight );
	}
}
