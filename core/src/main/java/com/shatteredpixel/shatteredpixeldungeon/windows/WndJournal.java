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
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GirlsFrontlinePixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Pylon;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Cypros;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Launcher.Launcher;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.SA.SurpriseAttack;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.TerrainFeaturesTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BadgesGrid;
import com.shatteredpixel.shatteredpixeldungeon.ui.BadgesList;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickRecipe;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingGridPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collection;

public class WndJournal extends WndTabbed {
	
	public static final int WIDTH_P     = 126;
	public static final int HEIGHT_P    = 180;
	
	public static final int WIDTH_L     = 200;
	public static final int HEIGHT_L    = 130;
	
	private static final int ITEM_HEIGHT	= 18;
	public static boolean TitleScene;
	private GuideTab guideTab;
	private AlchemyTab alchemyTab;
	private NotesTab notesTab;
	private CatalogTab catalogTab;
    private BadgesTab badgesTab;
	
	public static int last_index = 0;
	
	public WndJournal(){
		if (Dungeon.hero == null)
			Dungeon.hero = new Hero();
		
		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;
		int height = PixelScene.landscape() ? HEIGHT_L : HEIGHT_P;
		
		resize(width, height);
		
		guideTab = new GuideTab();
		add(guideTab);
		guideTab.setRect(0, 0, width, height);
		guideTab.updateList();
		
		alchemyTab = new AlchemyTab();
		add(alchemyTab);
		alchemyTab.setRect(0, 0, width, height);

		if (!TitleScene) {
			notesTab = new NotesTab();
			add(notesTab);
			notesTab.setRect(0, 0, width, height);
			notesTab.updateList();
		}
		
		catalogTab = new CatalogTab();
		add(catalogTab);
		catalogTab.setRect(0, 0, width, height);
		catalogTab.updateList();

		badgesTab = new BadgesTab();
		add(badgesTab);
		badgesTab.setRect(0, 0, width, height);
		badgesTab.updateList();

		ArrayList<Tab> tabs = new ArrayList<>();
		int page = 0;
		int finalPage = page;
		tabs.add(new IconTab( new ItemSprite(ItemSpriteSheet.GUIDE_PAGE, null) ) {
			protected void select( boolean value ) {
				super.select( value );
				guideTab.active = guideTab.visible = value;
				if (value) last_index = finalPage;
			}
		});
		page++;
		int finalPage1 = page;
		tabs.add(new IconTab( new ItemSprite(ItemSpriteSheet.ALCH_PAGE, null) ) {
					protected void select( boolean value ) {
						super.select( value );
						alchemyTab.active = alchemyTab.visible = value;
						if (value) last_index = finalPage1;
					}
				});
		if (!TitleScene) {
			page++;
			int finalPage2 = page;
			tabs.add(new IconTab(Icons.get(Icons.STAIRS)) {
				protected void select(boolean value) {
					super.select(value);
					notesTab.active = notesTab.visible = value;
					if (value) last_index = finalPage2;
				}
			});
		}
		page++;
		int finalPage3 = page;
		tabs.add(new IconTab( new ItemSprite(ItemSpriteSheet.WEAPON_HOLDER, null) ) {
					protected void select( boolean value ) {
						super.select( value );
						catalogTab.active = catalogTab.visible = value;
						if (value) last_index = finalPage3;
					}
				});
		page++;
		int finalPage4 = page;
		tabs.add(new IconTab( Icons.BADGES.get() ) {
					protected void select( boolean value ) {
						super.select( value );
						badgesTab.active = badgesTab.visible = value;
						if (value) last_index = finalPage4;
					}

					@Override
					protected String hoverText() {
						return Messages.get(badgesTab, "title");
					}
				});

		for (Tab tab : tabs) {
			add( tab );
		}
		
		layoutTabs();
		
		select(last_index);
	}

	@Override
	public void offset(int xOffset, int yOffset) {
		super.offset(xOffset, yOffset);
		guideTab.layout();
		alchemyTab.layout();
		catalogTab.layout();
		if (!TitleScene)
			notesTab.layout();
	}

	private static class ListItem extends Component {
		
		protected RenderedTextBlock label;
		protected BitmapText depth;
		protected ColorBlock line;
		protected Image icon;
		
		public ListItem( Image icon, String text ) {
			this(icon, text, -1);
		}
		
		public ListItem( Image icon, String text, int d ) {
			super();
			
			this.icon.copy(icon);
			
			label.text( text );
			
			if (d >= 0) {
				depth.text(Integer.toString(d));
				depth.measure();
				
				if (d == Dungeon.depth) {
					label.hardlight(TITLE_COLOR);
					depth.hardlight(TITLE_COLOR);
				}
			}
		}
		
		@Override
		protected void createChildren() {
			label = PixelScene.renderTextBlock( 7 );
			add( label );
			
			icon = new Image();
			add( icon );
			
			depth = new BitmapText( PixelScene.pixelFont);
			add( depth );
			
			line = new ColorBlock( 1, 1, 0xFF222222);
			add(line);
			
		}
		
		@Override
		protected void layout() {
			
			icon.y = y + 1 + (height() - 1 - icon.height()) / 2f;
			icon.x = x + (16 - icon.width())/2f;
			PixelScene.align(icon);
			
			depth.x = icon.x + (icon.width - depth.width()) / 2f;
			depth.y = icon.y + (icon.height - depth.height()) / 2f + 1;
			PixelScene.align(depth);
			
			line.size(width, 1);
			line.x = 0;
			line.y = y;
			
			label.maxWidth((int)(width - 16 - 1));
			label.setPos(17, y + 1 + (height() - label.height()) / 2f);
			PixelScene.align(label);
		}
	}
	
	public static class GuideTab extends Component {
		
		private ScrollPane list;
		private ArrayList<GuideItem> pages = new ArrayList<>();
		
		@Override
		protected void createChildren() {
			list = new ScrollPane( new Component() ){
				@Override
				public void onClick( float x, float y ) {
					int size = pages.size();
					for (int i=0; i < size; i++) {
						if (pages.get( i ).onClick( x, y )) {
							break;
						}
					}
				}
			};
			add( list );
		}
		
		@Override
		protected void layout() {
			super.layout();
			list.setRect( 0, 0, width, height);
		}
		
		public void updateList(){
			Component content = list.content();
			
			float pos = 0;
			
			ColorBlock line = new ColorBlock( width(), 1, 0xFF222222);
			line.y = pos;
			content.add(line);
			
			RenderedTextBlock title = PixelScene.renderTextBlock(Document.ADVENTURERS_GUIDE.title(), 9);
			title.hardlight(TITLE_COLOR);
			title.maxWidth( (int)width() - 2 );
			title.setPos( (width() - title.width())/2f, pos + 1 + ((ITEM_HEIGHT) - title.height())/2f);
			PixelScene.align(title);
			content.add(title);
			
			pos += Math.max(ITEM_HEIGHT, title.height());
			
			for (String page : Document.ADVENTURERS_GUIDE.pageNames()){
				GuideItem item = new GuideItem( page );
				
				item.setRect( 0, pos, width(), ITEM_HEIGHT );
				content.add( item );
				
				pos += item.height();
				pages.add(item);
			}
			
			content.setSize( width(), pos );
			list.setSize( list.width(), list.height() );
		}
		
		private static class GuideItem extends ListItem {
			
			private boolean found = false;
			private String page;
			
			public GuideItem( String page ){
				super( iconForPage(page), Messages.titleCase(Document.ADVENTURERS_GUIDE.pageTitle(page)));
				
				this.page = page;
				found = Document.ADVENTURERS_GUIDE.isPageFound(page);
				
				if (!found) {
					icon.hardlight( 0.5f, 0.5f, 0.5f);
					label.text( Messages.titleCase(Messages.get( this, "missing" )));
					label.hardlight( 0x999999 );
				}
				
			}
			
			public boolean onClick( float x, float y ) {
				if (inside( x, y ) && found) {
					GameScene.show( new WndStory( iconForPage(page),
							Document.ADVENTURERS_GUIDE.pageTitle(page),
							Document.ADVENTURERS_GUIDE.pageBody(page) ));
					Document.ADVENTURERS_GUIDE.readPage(page);
					return true;
				} else {
					return false;
				}
			}
			
		}

		//TODO might just want this to be part of the Document class
		public static Image iconForPage( String page ){
			if (!Document.ADVENTURERS_GUIDE.isPageFound(page)){
				return new ItemSprite( ItemSpriteSheet.GUIDE_PAGE );
			}
			switch (page){
				case Document.GUIDE_INTRO: default:
					return new ItemSprite(ItemSpriteSheet.MASTERY);
				case "Examining":
					return Icons.get(Icons.MAGNIFY);
				case "Surprise_Attacks":
					return new ItemSprite( ItemSpriteSheet.GUA91 );
				case "Identifying":
					return new ItemSprite( new ScrollOfIdentify() );
				case "Food":
					return new ItemSprite( ItemSpriteSheet.PASTY );
				case "Dieing":
					return new ItemSprite( ItemSpriteSheet.TOMB );
				case Document.GUIDE_SEARCHING:
					return Icons.get(Icons.MAGNIFY);
				case "Strength":
					return new ItemSprite( ItemSpriteSheet.GREATAXE );
				case "Upgrades":
					return new ItemSprite( ItemSpriteSheet.RING_EMERALD );
				case "Looting":
					return new ItemSprite( ItemSpriteSheet.CRYSTAL_KEY );
				case "Levelling":
					return Icons.get(Icons.TALENT);
				case "Positioning":
					return new ItemSprite( ItemSpriteSheet.SPIRIT_BOW );
				case "Magic":
					return new ItemSprite( ItemSpriteSheet.WAND_FIREBOLT );
			}
		}

	}
	
	public static class AlchemyTab extends Component {
		
		private RedButton[] pageButtons;
		private static final int NUM_BUTTONS = 10;
		
		private static final int[] spriteIndexes = {10, 12, 7, 9, 11, 8, 3, 13, 14, 15};
		
		public static int currentPageIdx   = -1;
		
		private IconTitle title;
		private RenderedTextBlock body;
		
		private ScrollPane list;
		private ArrayList<QuickRecipe> recipes = new ArrayList<>();
		
		@Override
		protected void createChildren() {
			pageButtons = new RedButton[NUM_BUTTONS];
			for (int i = 0; i < NUM_BUTTONS; i++){
				final int idx = i;
				pageButtons[i] = new RedButton( "" ){
					@Override
					protected void onClick() {
						currentPageIdx = idx;
						updateList();
					}
				};
				if (Document.ALCHEMY_GUIDE.isPageFound(i)) {
					pageButtons[i].icon(new ItemSprite(ItemSpriteSheet.SOMETHING + spriteIndexes[i], null));
				} else {
					pageButtons[i].icon(new ItemSprite(ItemSpriteSheet.SOMETHING, null));
					pageButtons[i].enable(false);
				}
				add( pageButtons[i] );
			}
			
			title = new IconTitle();
			title.icon( new ItemSprite(ItemSpriteSheet.ALCH_PAGE));
			title.visible = false;

			body = PixelScene.renderTextBlock(6);
			
			list = new ScrollPane(new Component());
			add(list);
		}
		
		@Override
		protected void layout() {
			super.layout();
			
			if (PixelScene.landscape()){
				float buttonWidth = width()/pageButtons.length;
				for (int i = 0; i < NUM_BUTTONS; i++) {
					pageButtons[i].setRect(i*buttonWidth, 0, buttonWidth, ITEM_HEIGHT);
					PixelScene.align(pageButtons[i]);
				}
			} else {
				//for first row
				float buttonWidth = width()/5;
				float y = 0;
				float x = 0;
				for (int i = 0; i < NUM_BUTTONS; i++) {
					pageButtons[i].setRect(x, y, buttonWidth, ITEM_HEIGHT);
					PixelScene.align(pageButtons[i]);
					x += buttonWidth;
					if (i == 4){
						y += ITEM_HEIGHT;
						x = 0;
						buttonWidth = width()/5;
					}
				}
			}
			
			list.setRect(0, pageButtons[NUM_BUTTONS-1].bottom() + 1, width,
					height - pageButtons[NUM_BUTTONS-1].bottom() - 1);
			
			updateList();
		}
		
		private void updateList() {

			for (int i = 0; i < NUM_BUTTONS; i++) {
				if (i == currentPageIdx) {
					pageButtons[i].icon().color(TITLE_COLOR);
				} else {
					pageButtons[i].icon().resetColor();
				}
			}
			
			if (currentPageIdx == -1){
				return;
			}
			
			for (QuickRecipe r : recipes){
				if (r != null) {
					r.killAndErase();
					r.destroy();
				}
			}
			recipes.clear();
			
			Component content = list.content();
			
			content.clear();
			
			title.visible = true;
			title.label(Document.ALCHEMY_GUIDE.pageTitle(currentPageIdx));
			title.setRect(0, 0, width(), 10);
			content.add(title);
			
			body.maxWidth((int)width());
			body.text(Document.ALCHEMY_GUIDE.pageBody(currentPageIdx));
			body.setPos(0, title.bottom());
			content.add(body);

			Document.ALCHEMY_GUIDE.readPage(currentPageIdx);

			ArrayList<QuickRecipe> toAdd = QuickRecipe.getRecipes(currentPageIdx);
			
			float left;
			float top = body.bottom()+2;
			int w;
			ArrayList<QuickRecipe> toAddThisRow = new ArrayList<>();
			while (!toAdd.isEmpty()){
				if (toAdd.get(0) == null){
					toAdd.remove(0);
					top += 6;
				}
				
				w = 0;
				while(!toAdd.isEmpty() && toAdd.get(0) != null
						&& w + toAdd.get(0).width() <= width()){
					toAddThisRow.add(toAdd.remove(0));
					w += toAddThisRow.get(0).width();
				}
				
				float spacing = (width() - w)/(toAddThisRow.size() + 1);
				left = spacing;
				while (!toAddThisRow.isEmpty()){
					QuickRecipe r = toAddThisRow.remove(0);
					r.setPos(left, top);
					left += r.width() + spacing;
					if (!toAddThisRow.isEmpty()) {
						ColorBlock spacer = new ColorBlock(1, 16, 0xFF222222);
						spacer.y = top;
						spacer.x = left - spacing / 2 - 0.5f;
						PixelScene.align(spacer);
						content.add(spacer);
					}
					recipes.add(r);
					content.add(r);
				}
				
				if (!toAdd.isEmpty() && toAdd.get(0) == null){
					toAdd.remove(0);
				}
				
				if (!toAdd.isEmpty() && toAdd.get(0) != null) {
					ColorBlock spacer = new ColorBlock(width(), 1, 0xFF222222);
					spacer.y = top + 16;
					spacer.x = 0;
					content.add(spacer);
				}
				top += 17;
				toAddThisRow.clear();
			}
			top -= 1;
			content.setSize(width(), top);
			list.setSize(list.width(), list.height());
			list.scrollTo(0, 0);
		}
	}

	private static class NotesTab extends Component {

		private ScrollPane list;

		@Override
		protected void createChildren() {
			list = new ScrollPane( new Component() );
			add( list );
		}

		@Override
		protected void layout() {
			super.layout();
			list.setRect( 0, 0, width, height);
		}

		private void updateList(){
			Component content = list.content();

			float pos = 0;

			//Keys
			ArrayList<Notes.KeyRecord> keys = Notes.getRecords(Notes.KeyRecord.class);
			if (!keys.isEmpty()){
				ColorBlock line = new ColorBlock( width(), 1, 0xFF222222);
				line.y = pos;
				content.add(line);

				RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "keys"), 9);
				title.hardlight(TITLE_COLOR);
				title.maxWidth( (int)width() - 2 );
				title.setPos( (width() - title.width())/2f, pos + 1 + ((ITEM_HEIGHT) - title.height())/2f);
				PixelScene.align(title);
				content.add(title);

				pos += Math.max(ITEM_HEIGHT, title.height());
			}
			for(Notes.Record rec : keys){
				ListItem item = new ListItem( Icons.get(Icons.STAIRS),
						Messages.titleCase(rec.desc()), rec.depth() );
				item.setRect( 0, pos, width(), ITEM_HEIGHT );
				content.add( item );

				pos += item.height();
			}

			//Landmarks
			ArrayList<Notes.LandmarkRecord> landmarks = Notes.getRecords(Notes.LandmarkRecord.class);
			if (!landmarks.isEmpty()){
				ColorBlock line = new ColorBlock( width(), 1, 0xFF222222);
				line.y = pos;
				content.add(line);

				RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "landmarks"), 9);
				title.hardlight(TITLE_COLOR);
				title.maxWidth( (int)width() - 2 );
				title.setPos( (width() - title.width())/2f, pos + 1 + ((ITEM_HEIGHT) - title.height())/2f);
				PixelScene.align(title);
				content.add(title);

				pos += Math.max(ITEM_HEIGHT, title.height());
			}
			for (Notes.Record rec : landmarks) {
				ListItem item = new ListItem( Icons.get(Icons.STAIRS),
						Messages.titleCase(rec.desc()), rec.depth() );
				item.setRect( 0, pos, width(), ITEM_HEIGHT );
				content.add( item );

				pos += item.height();
			}

			content.setSize( width(), pos );
			list.setSize( list.width(), list.height() );
		}

	}

	public static class CatalogTab extends Component{

		private RedButton[] itemButtons;
		private static final int NUM_BUTTONS = 3;

		public static int currentItemIdx   = 0;
		private static float[] scrollPositions = new float[NUM_BUTTONS];

		//sprite locations
		private static final int EQUIP_IDX = 0;
		private static final int CONSUM_IDX = 1;
		private static final int BESTIARY_IDX = 2;
		private static final int LORE_IDX = 3;

		private ScrollingGridPane grid;

		@Override
		protected void createChildren() {
			itemButtons = new RedButton[NUM_BUTTONS];
			for (int i = 0; i < NUM_BUTTONS; i++){
				final int idx = i;
				itemButtons[i] = new RedButton( "" ){
					@Override
					protected void onClick() {
						currentItemIdx = idx;
						updateList();
					}
				};
				add( itemButtons[i] );
			}
			itemButtons[EQUIP_IDX].icon(new ItemSprite(ItemSpriteSheet.WEAPON_HOLDER));
			itemButtons[CONSUM_IDX].icon(new ItemSprite(ItemSpriteSheet.POTION_HOLDER));
			itemButtons[BESTIARY_IDX].icon(new ItemSprite(ItemSpriteSheet.CATA_HOLDER));
//			itemButtons[LORE_IDX].icon(new ItemSprite(ItemSpriteSheet.SPELL_HOLDER));

			grid = new ScrollingGridPane(){
				@Override
				public synchronized void update() {
					super.update();
					scrollPositions[currentItemIdx] = content.camera.scroll.y;
				}
			};
			add( grid );
		}

		@Override
		protected void layout() {
			super.layout();

			int perRow = NUM_BUTTONS;
			float buttonWidth = width()/perRow;

			for (int i = 0; i < NUM_BUTTONS; i++) {
				itemButtons[i].setRect(x +(i%perRow) * (buttonWidth),
						y + (i/perRow) * (ITEM_HEIGHT ),
						buttonWidth, ITEM_HEIGHT);
				PixelScene.align(itemButtons[i]);
			}

			grid.setRect(x,
					itemButtons[NUM_BUTTONS-1].bottom() + 1,
					width,
					height - itemButtons[NUM_BUTTONS-1].height() - 1);
		}

		public void updateList() {

			grid.clear();

			for (int i = 0; i < NUM_BUTTONS; i++){
				if (i == currentItemIdx){
					itemButtons[i].icon().color(TITLE_COLOR);
				} else {
					itemButtons[i].icon().resetColor();
				}
			}

			grid.scrollTo( 0, 0 );

			if (currentItemIdx == EQUIP_IDX) {
				int totalItems = 0;
				int totalSeen = 0;
				for (Catalog catalog : Catalog.equipmentCatalogs){
					totalItems += catalog.totalItems();
					totalSeen += catalog.totalSeen();
				}
				grid.addHeader("_" + Messages.get(this, "title_equipment") + "_ (" + totalSeen + "/" + totalItems + ")", 9, true);

				for (Catalog catalog : Catalog.equipmentCatalogs){
					grid.addHeader("_" + Messages.titleCase(catalog.title()) + "_ (" + catalog.totalSeen() + "/" + catalog.totalItems() + "):");
					addGridItems(grid, catalog.items());
				}

			} else if (currentItemIdx == CONSUM_IDX){
				int totalItems = 0;
				int totalSeen = 0;
				for (Catalog catalog : Catalog.consumableCatalogs){
					totalItems += catalog.totalItems();
					totalSeen += catalog.totalSeen();
				}
				grid.addHeader("_" + Messages.get(this, "title_consumables") + "_ (" + totalSeen + "/" + totalItems + ")", 9, true);

				if (TitleScene){
					Potion.initColors();
					Scroll.initLabels();
					Ring.initGems();
				}
				for (Catalog catalog : Catalog.consumableCatalogs){
					grid.addHeader("_" + Messages.titleCase(catalog.title()) + "_ (" + catalog.totalSeen() + "/" + catalog.totalItems() + "):");
					addGridItems(grid, catalog.items());
				}

			} else if (currentItemIdx == BESTIARY_IDX){
				int totalItems = 0;
				int totalSeen = 0;
				for (Bestiary bestiary : Bestiary.values()){
					totalItems += bestiary.totalEntities();
					totalSeen += bestiary.totalSeen();
				}
				grid.addHeader("_" + Messages.get(this, "title_bestiary") + "_ (" + totalSeen + "/" + totalItems + ")", 9, true);

				for (Bestiary bestiary : Bestiary.values()){
					grid.addHeader("_" + Messages.titleCase(bestiary.title()) + "_ (" + bestiary.totalSeen() + "/" + bestiary.totalEntities() + "):");
					addGridEntities(grid, bestiary.entities());
				}

			}

			grid.setRect(x, itemButtons[NUM_BUTTONS-1].bottom() + 1, width,
					height - itemButtons[NUM_BUTTONS-1].height() - 1);

			grid.scrollTo(0, scrollPositions[currentItemIdx]);
		}

		private static String property(MeleeWeapon weapon){
			String info = "";
			if (weapon instanceof Cypros){
				info+=Messages.format("该武器阶数_%d_。\n", weapon.tier);
				((Cypros) weapon).setMode(Cypros.Mode.MAGNUM, false);
				((Cypros) weapon).setMode(Cypros.Mode.TRAVAILLER, false);
				info+=Messages.format("_特拉维小姐_:基础面板属性_%.1f_~_%.1f_，成长_%.1f_~_%.1f_，攻击距离_%d_，攻击延迟/后摇_%.3f_，精度乘数_%.3f_。基础防御_0_~_%d_，防御成长_0_~_%d_。",
						((Cypros) weapon).minBaseDmg(0), ((Cypros) weapon).maxBaseDmg(0),
						weapon.minUpgrade(1), ((Cypros) weapon).maxUpgrade(0, 1),
						weapon.RCH, weapon.DLY, weapon.ACC, weapon.DEF, weapon.DEFUPGRADE);
				info+="\n\n";
				((Cypros) weapon).setMode(Cypros.Mode.CONFIRE, false);
				info+=Messages.format("_康菲尔小姐_:基础面板属性_%.1f_~_%.1f_，成长_%.1f_~_%.1f_，攻击距离_%d_，攻击延迟/后摇_%.3f_，精度乘数_%.3f_。在伏击时的伤害区间改写为当前的区间的_%.1f_%%~_100_%%。",
						((Cypros) weapon).minBaseDmg(1), ((Cypros) weapon).maxBaseDmg(1),
						weapon.minUpgrade(1), ((Cypros) weapon).maxUpgrade(1, 1),
						weapon.RCH, weapon.DLY, weapon.ACC, ((Cypros) weapon).surpriseMultiplier*100);
				info+="\n\n";
				((Cypros) weapon).setMode(Cypros.Mode.MAGNUM, false);
				info+=Messages.format("_马格南婚礼_:基础面板属性_%.1f_~_%.1f_，成长_%.1f_~_%.1f_，攻击距离_%d_，攻击延迟/后摇_%.3f_，精度乘数_%.3f_。在伏击时的伤害区间改写为当前的区间的_%.1f_%%~_100_%%。",
						((Cypros) weapon).minBaseDmg(2), ((Cypros) weapon).maxBaseDmg(2),
						weapon.minUpgrade(1), ((Cypros) weapon).maxUpgrade(2, 1),
						weapon.RCH, weapon.DLY, weapon.ACC, ((Cypros) weapon).surpriseMultiplier*100);
			}
			else {
				info += Messages.format("该武器阶数_%d_，基础面板属性_%.1f_~_%.1f_，成长_%.1f_~_%.1f_，攻击距离_%d_，攻击延迟/后摇_%.3f_，精度乘数_%.3f_。",
						weapon.tier,
						weapon.minBaseDmg(), weapon.maxBaseDmg(),
						weapon.minUpgrade(1), weapon.maxUpgrade(1),
						weapon.RCH, weapon.DLY, weapon.ACC);
				if (weapon.DEF > 0)
					info += Messages.format("基础防御_0_~_%d_。", weapon.DEF);
				if (weapon.DEFUPGRADE > 0)
					info += Messages.format("防御成长_0_~_%d_。", weapon.DEFUPGRADE);
				if (weapon instanceof Launcher)
					info += "\n该类武器对_.50_有伤害加成。";
				else if (weapon instanceof SurpriseAttack)
					info += Messages.format("\n该类武器在伏击时的伤害区间改写为当前的区间的_%.1f_%%~_100_%%。", ((SurpriseAttack) weapon).damageMin * 100);
			}
			return info;
		}
		private static void addGridItems( ScrollingGridPane grid, Collection<Class<?>> classes) {
			for (Class<?> itemClass : classes) {

				boolean seen = Catalog.isSeen(itemClass);;
				ItemSprite sprite = null;
				Image secondIcon = null;
				String title = "";
				String desc = "";
				String property = "";

				if (Item.class.isAssignableFrom(itemClass)) {

					Item item = (Item) Reflection.newInstance(itemClass);

					if (item instanceof MeleeWeapon)
						property = property((MeleeWeapon) item);
					if (seen) {
						if (item instanceof Ring) {
							((Ring) item).anonymize();
						} else if (item instanceof Potion) {
							((Potion) item).anonymize();
						} else if (item instanceof Scroll) {
							((Scroll) item).anonymize();
						}
					}

					sprite = new ItemSprite(item.image, seen ? item.glowing() : null);
					if (!seen)  {
						if (item instanceof ExoticPotion){
							sprite.frame(ItemSpriteSheet.POTION_CRIMSON);
						}
						sprite.lightness(0);
						title = "???";
						desc = Messages.get(CatalogTab.class, "not_seen_item");
						desc += "\n\n" + Messages.get(item, "discover_hint");
					} else {
						title = Messages.titleCase( item.name() );
						//some items don't include direct stats, generally when they're not applicable
						if (item instanceof ClassArmor || item instanceof SpiritBow){
							desc += item.desc();
						} else {
							desc += item.info();
						}

						if (Catalog.useCount(itemClass) > 0) {
							if (item.isUpgradable() || item instanceof Artifact) {
								desc += "\n\n" + Messages.get(CatalogTab.class, "upgrade_count", Catalog.useCount(itemClass));
							} else if (item instanceof Gold) {
								desc += "\n\n" + Messages.get(CatalogTab.class, "gold_count", Catalog.useCount(itemClass));
							} else if (item instanceof EnergyCrystal) {
								desc += "\n\n" + Messages.get(CatalogTab.class, "energy_count", Catalog.useCount(itemClass));
							} else {
								desc += "\n\n" + Messages.get(CatalogTab.class, "use_count", Catalog.useCount(itemClass));
							}
						}

						//mage's staff normally has 2 pixels extra at the top for particle effects, we chop that off here
						if (item instanceof MagesStaff){
							RectF frame = sprite.frame();
							frame.top += frame.height()/8f;
							sprite.frame(frame);
						}

						if (item.icon != -1) {
							secondIcon = new Image(Assets.Sprites.ITEM_ICONS);
							secondIcon.frame(ItemSpriteSheet.Icons.film.get(item.icon));
						}
					}

				} else if (Weapon.Enchantment.class.isAssignableFrom(itemClass)){

					Weapon.Enchantment ench = (Weapon.Enchantment) Reflection.newInstance(itemClass);

					if (seen){
						sprite = new ItemSprite(ItemSpriteSheet.GREATAXE, ench.glowing());
						title = Messages.titleCase(ench.name());
						desc = ench.desc();
					} else {
						sprite = new ItemSprite(ItemSpriteSheet.GREATAXE);
						sprite.lightness(0f);
						title = "???";
						desc = Messages.get(CatalogTab.class, "not_seen_enchantment");
						desc += "\n\n" + Messages.get(ench, "discover_hint");
					}

				} else if (Armor.Glyph.class.isAssignableFrom(itemClass)){

					Armor.Glyph glyph = (Armor.Glyph) Reflection.newInstance(itemClass);

					if (seen){
						sprite = new ItemSprite(ItemSpriteSheet.ARMOR_CLOTH, glyph.glowing());
						title = Messages.titleCase(glyph.name());
						desc = glyph.desc();
					} else {
						sprite = new ItemSprite(ItemSpriteSheet.ARMOR_CLOTH);
						sprite.lightness(0f);
						title = "???";
						desc = Messages.get(CatalogTab.class, "not_seen_glyph");
						desc += "\n\n" + Messages.get(glyph, "discover_hint");
					}

				}

				String finalTitle = title;
				String finalDesc = desc;
				String finalProperty = property;
				ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(sprite) {
					@Override
					public boolean onClick(float x, float y) {
						if (inside(x, y)) {
							Image sprite = new Image(icon);
							if (!finalProperty.isEmpty()) {
								if (GirlsFrontlinePixelDungeon.scene() instanceof GameScene) {
									GameScene.show(new WndJournalItem(sprite, finalTitle, finalDesc, finalProperty, seen));
								} else {
									GirlsFrontlinePixelDungeon.scene().addToFront(new WndJournalItem(sprite, finalTitle, finalDesc, finalProperty, seen));
								}
							}
							else {
								if (GirlsFrontlinePixelDungeon.scene() instanceof GameScene){
									GameScene.show(new WndJournalElse(sprite, finalTitle, finalDesc));
								} else {
									GirlsFrontlinePixelDungeon.scene().addToFront(new WndJournalElse(sprite, finalTitle, finalDesc));
								}
							}
							return true;
						} else {
							return false;
						}
					}
				};
				if (secondIcon != null){
					gridItem.addSecondIcon(secondIcon);
				}
				if (!seen) {
					gridItem.hardLightBG(2f, 1f, 2f);
				}
				grid.addItem(gridItem);
			}
		}

		private static void addGridEntities(ScrollingGridPane grid, Collection<Class<?>> classes) {
			for (Class<?> entityCls : classes){

				boolean seen = Bestiary.isSeen(entityCls);
				Mob mob = null;
				Image icon = null;
				String title = null;
				String desc = null;
				if (Mob.class.isAssignableFrom(entityCls)) {

					mob = (Mob) Reflection.newInstance(entityCls);

					if (mob instanceof Mimic || mob instanceof Pylon) {
						mob.alignment = Char.Alignment.ENEMY;
					}
					if (mob instanceof WandOfWarding.Ward){
						if (mob instanceof WandOfWarding.Ward.WardSentry){
							((WandOfWarding.Ward) mob).upgrade(3);
							((WandOfWarding.Ward) mob).upgrade(3);
							((WandOfWarding.Ward) mob).upgrade(3);
							((WandOfWarding.Ward) mob).upgrade(3);
						} else {
							((WandOfWarding.Ward) mob).upgrade(0);
						}
					}


					CharSprite sprite = mob.sprite();
					sprite.idle();

					icon = sprite;
					if (seen) {
						title = Messages.titleCase(mob.name());
						desc = mob.description();
						if (Bestiary.encounterCount(entityCls) > 1){
							desc += "\n\n" + Messages.get(CatalogTab.class, "enemy_count", Bestiary.encounterCount(entityCls));
						}
					} else {
						icon.lightness(0f);
						title = "???";
						if (mob instanceof WandOfRegrowth.Lotus){
							desc = Messages.get(CatalogTab.class, "not_seen_plant");
						} else if (mob.alignment == Char.Alignment.ENEMY){
							desc = Messages.get(CatalogTab.class, "not_seen_enemy");
						} else {
							desc = Messages.get(CatalogTab.class, "not_seen_ally");
						}
						desc += "\n\n" + Messages.get(mob, "discover_hint");
					}

				} else if (Trap.class.isAssignableFrom(entityCls)){

					Trap trap = (Trap) Reflection.newInstance(entityCls);
					icon = TerrainFeaturesTilemap.getTrapVisual(trap);

					if (seen) {
						title = Messages.titleCase(trap.name());
						desc = trap.desc();
						if (Bestiary.encounterCount(entityCls) > 1){
							desc += "\n\n" + Messages.get(CatalogTab.class, "trap_count", Bestiary.encounterCount(entityCls));
						}
					} else {
						icon.lightness(0f);
						title = "???";
						desc = Messages.get(CatalogTab.class, "not_seen_trap");
						desc += "\n\n" + Messages.get(trap, "discover_hint");
					}

				} else if (Plant.class.isAssignableFrom(entityCls)){

					Plant plant = (Plant) Reflection.newInstance(entityCls);
					icon = TerrainFeaturesTilemap.getPlantVisual(plant);

					if (seen) {
						title = Messages.titleCase(plant.name());
						desc = plant.desc();
						if (Bestiary.encounterCount(entityCls) > 1){
							desc += "\n\n" + Messages.get(CatalogTab.class, "plant_count", Bestiary.encounterCount(entityCls));
						}
					} else {
						icon.lightness(0f);
						title = "???";
						desc = Messages.get(CatalogTab.class, "not_seen_plant");
						desc += "\n\n" + Messages.get(plant, "discover_hint");
					}

				}

				//we have to clip the bounds of the sprite if it's too large
				if (icon.width() >= 17 || icon.height() >= 17) {
					float size = Math.max(icon.width(), icon.height());
					icon.scale.set(16/size);
				}
				Mob finalMob = mob;
				String finalTitle = title;
				String finalDesc = desc;
                ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(icon) {
					@Override
					public boolean onClick(float x, float y) {
						if (inside(x, y)) {
							if (seen && finalMob != null){
								if (GirlsFrontlinePixelDungeon.scene() instanceof GameScene) {
									GameScene.show(new WndJournalElse(finalMob, finalTitle, finalDesc));
								} else {
									GirlsFrontlinePixelDungeon.scene().addToFront(new WndJournalElse(finalMob, finalTitle, finalDesc));
								}
							} else {

								Image image = new Image(icon);
								if (GirlsFrontlinePixelDungeon.scene() instanceof GameScene) {
									GameScene.show(new WndJournalElse(image, finalTitle, finalDesc));
								} else {
									GirlsFrontlinePixelDungeon.scene().addToFront(new WndJournalElse(image, finalTitle, finalDesc));
								}
							}

							return true;
						} else {
							return false;
						}
					}
				};
				if (!seen) {
					gridItem.hardLightBG(2f, 1f, 2f);
				}
				grid.addItem(gridItem);
			}
		};
	}

	public static class BadgesTab extends Component {

		private RedButton btnLocal;
		private RedButton btnGlobal;

		private RenderedTextBlock title;

		private Component badgesLocal;
		private Component badgesGlobal;

		public static boolean global = false;

		@Override
		protected void createChildren() {

			if (!TitleScene) {
				btnLocal = new RedButton(Messages.get(this, "this_run")) {
					@Override
					protected void onClick() {
						super.onClick();
						global = false;
						updateList();
					}
				};
				btnLocal.icon(Icons.BADGES.get());
				add(btnLocal);

				btnGlobal = new RedButton(Messages.get(this, "overall")) {
					@Override
					protected void onClick() {
						super.onClick();
						global = true;
						updateList();
					}
				};
				btnGlobal.icon(Icons.BADGES.get());
				add(btnGlobal);

				if (Badges.filterReplacedBadges(false).size() <= 8) {
					badgesLocal = new BadgesList(false);
				} else {
					badgesLocal = new BadgesGrid(false);
				}
				add(badgesLocal);
			} else {
				title = PixelScene.renderTextBlock(Messages.get(this, "title_main_menu"), 9);
				title.hardlight(Window.TITLE_COLOR);
				add(title);
			}

			badgesGlobal = new BadgesGrid(true);
			add( badgesGlobal );
		}

		@Override
		protected void layout() {
			super.layout();

			if (btnLocal != null) {
				btnLocal.setRect(x, y, width / 2, 18);
				btnGlobal.setRect(x + width / 2, y, width / 2, 18);

				badgesLocal.setRect(x, y + 20, width, height-20);
				badgesGlobal.setRect( x, y + 20, width, height-20);
			} else {
				title.setPos( x + (width - title.width())/2, y + (12-title.height())/2);

				badgesGlobal.setRect( x, y + 14, width, height-14);
			}
		}

		private void updateList(){
			if (btnLocal != null) {
				badgesLocal.visible = badgesLocal.active = !global;
				badgesGlobal.visible = badgesGlobal.active = global;

				btnLocal.textColor(global ? Window.WHITE : Window.TITLE_COLOR);
				btnGlobal.textColor(global ? Window.TITLE_COLOR : Window.WHITE);
			} else {
				badgesGlobal.visible = badgesGlobal.active = true;
			}
		}

	}
}
