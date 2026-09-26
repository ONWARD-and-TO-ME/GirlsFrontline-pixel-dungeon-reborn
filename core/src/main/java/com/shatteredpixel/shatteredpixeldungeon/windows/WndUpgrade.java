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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.audio.Sample;

public class WndUpgrade extends Window {

	private static final int WIDTH		= 120;
	private static final int GAP		= 2;
	private static final int SLOT_SIZE	= 18;

	private final ScrollOfUpgrade scroll;
	private Item item;

	private RedButton btnUpgrade;

	public WndUpgrade( ScrollOfUpgrade scroll, Item toUpgrade ){

		this.scroll = scroll;
		this.item = toUpgrade;

		IconTitle title = new IconTitle( new ItemSprite(scroll), Messages.titleCase(Messages.get(this, "title")) );
		title.setRect( 0, 0, WIDTH, 0 );
		add( title );

		String mainText = Messages.get(this, "desc")
				+ "\n" + Messages.get(this, "remaining", scroll.quantity());

		RenderedTextBlock message = PixelScene.renderTextBlock( 6 );
		message.text( mainText, WIDTH );
		message.setPos( 0, title.bottom() + GAP );
		add( message );

		// *** 物品当前等级与升级后等级的对比展示 ***

		boolean identified = item.isIdentified();
		int levelFrom = identified ? item.level() : 0;
		int levelTo = levelFrom + 1;

		float slotY = message.bottom() + 2*GAP;
		float center1 = WIDTH*0.30f;
		float center2 = WIDTH*0.70f;

		ColorBlock bg1 = new ColorBlock( SLOT_SIZE, SLOT_SIZE, 0x9953564D );
		bg1.x = center1 - SLOT_SIZE/2f;
		bg1.y = slotY;
		add( bg1 );

		ColorBlock bg2 = new ColorBlock( SLOT_SIZE, SLOT_SIZE, 0x9953564D );
		bg2.x = center2 - SLOT_SIZE/2f;
		bg2.y = slotY;
		add( bg2 );

		if (!identified){
			bg1.hardlight( 2f, 1, 2f );
			bg2.hardlight( 2f, 1, 2f );
		} else if (item.cursed && item.cursedKnown){
			bg1.hardlight( 2f, 0.5f, 1f );
			bg2.hardlight( 2f, 0.5f, 1f );
		}

		ItemSprite i1 = new ItemSprite( item );
		i1.x = center1 - i1.width()/2f;
		i1.y = slotY + (SLOT_SIZE - i1.height())/2f;
		PixelScene.align(i1);
		add( i1 );

		ItemSprite i2 = new ItemSprite( item );
		i2.x = center2 - i2.width()/2f;
		i2.y = i1.y;
		PixelScene.align(i2);
		add( i2 );

		RenderedTextBlock arrow = PixelScene.renderTextBlock( 7 );
		arrow.text( "→" );
		arrow.setPos( (WIDTH - arrow.width())/2f, slotY + (SLOT_SIZE - arrow.height())/2f );
		PixelScene.align(arrow);
		add( arrow );

		RenderedTextBlock t1 = PixelScene.renderTextBlock( 7 );
		RenderedTextBlock t2 = PixelScene.renderTextBlock( 7 );
		if (identified){
			t1.text( levelFrom > 0 ? "+" + levelFrom : "" );
			t1.hardlight( ItemSlot.UPGRADED );
			t2.text( "+" + levelTo );
			t2.hardlight( ItemSlot.UPGRADED );
		} else {
			t1.text( "?" );
			t2.text( "+1?" );
			t2.hardlight( ItemSlot.UPGRADED );
		}
		t1.setPos( bg1.x + SLOT_SIZE - t1.width() - 1, bg1.y + SLOT_SIZE - t1.height() - 1 );
		t2.setPos( bg2.x + SLOT_SIZE - t2.width() - 1, bg2.y + SLOT_SIZE - t2.height() - 1 );
		PixelScene.align(t1);
		PixelScene.align(t2);
		add( t1 );
		add( t2 );

		float bottom = slotY + SLOT_SIZE;

		//未鉴定物品的额外提示
		if (!identified){
			RenderedTextBlock warn = PixelScene.renderTextBlock( 6 );
			warn.text( Messages.get(this, "unided"), WIDTH );
			warn.setPos( 0, bottom + GAP );
			warn.hardlight( ItemSlot.WARNING );
			add( warn );
			bottom = warn.bottom();
		}

		// *** 升级 / 返回 按钮 ***

		btnUpgrade = new RedButton( Messages.get(this, "upgrade") ) {
			@Override
			protected void onClick() {
				super.onClick();

				scroll.playReadAnimation();
				item = scroll.upgradeItem( item );
				Sample.INSTANCE.play( Assets.Sounds.READ );
				scroll.detach( Dungeon.cur().hero.belongings.backpack );

				hide();

				//背包里还有升级磁盘且物品仍可升级：再次弹出窗口，实现连续升级
				ScrollOfUpgrade more = Dungeon.cur().hero.belongings.getItem( ScrollOfUpgrade.class );
				if (more != null && more.quantity() > 0 && item.isUpgradable()){
					GameScene.show( new WndUpgrade( more, item ) );
				}
			}
		};
		btnUpgrade.icon( new ItemSprite(scroll) );
		btnUpgrade.setRect( 0, bottom + 2*GAP, WIDTH/2f - 1, 16 );
		add( btnUpgrade );

		RedButton btnBack = new RedButton( Messages.get(this, "back") ) {
			@Override
			protected void onClick() {
				super.onClick();
				hide();
				//不消耗磁盘，返回物品选择界面
				scroll.reShowSelector();
			}
		};
		btnBack.icon( Icons.get(Icons.EXIT) );
		btnBack.setRect( WIDTH/2f + 1, bottom + 2*GAP, WIDTH/2f - 1, 16 );
		add( btnBack );

		//读盘动画（约1回合）结束前禁止连续点击
		btnUpgrade.enable( Dungeon.cur().hero.ready );

		resize( WIDTH, (int)btnBack.bottom() );
	}

	@Override
	public synchronized void update() {
		super.update();
		if (!btnUpgrade.active && Dungeon.cur().hero.ready){
			btnUpgrade.enable( true );
		}
	}

	@Override
	public void onBackPressed() {
		hide();
		scroll.reShowSelector();
	}
}
