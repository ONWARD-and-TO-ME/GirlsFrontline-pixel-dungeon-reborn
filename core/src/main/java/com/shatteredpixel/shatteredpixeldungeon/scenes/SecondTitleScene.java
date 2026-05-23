package com.shatteredpixel.shatteredpixeldungeon.scenes;

import static com.shatteredpixel.shatteredpixeldungeon.Chrome.Type.GREY_BUTTON;
import static com.shatteredpixel.shatteredpixeldungeon.Chrome.Type.GREY_BUTTON_TR;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.GirlsFrontlinePixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.custom.seedfinder.SeedFindScene;
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites;
import com.shatteredpixel.shatteredpixeldungeon.effects.Fireball;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.ui.WndTextInput;
import com.shatteredpixel.shatteredpixeldungeon.ui.canScrollRedButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.DeviceCompat;

import java.io.IOException;
import java.util.ArrayList;

public class SecondTitleScene extends PixelScene {
	private static int month = SPDSettings.getSpecialDay_Month();
	private static int day = SPDSettings.getSpecialDay_Day();
	@Override
	public void create() {
		super.create();

		Music.INSTANCE.play(Assets.Music.THEME_1,true);

		uiCamera.visible = false;

		int w = Camera.main.width;
		int h = Camera.main.height;

		Archs archs = new Archs();
		archs.setSize( w, h );
		add( archs );

		Image title = BannerSprites.get( BannerSprites.Type.PIXEL_DUNGEON );
		add( title );

		float topRegion = Math.max(title.height - 6, h*0.45f);

		title.x = (w - title.width()) / 2f;
		title.y = 2 + (topRegion - title.height()) / 2f;

		align(title);

		placeTorch(title.x + 22, title.y + 46);
		placeTorch(title.x + title.width - 22, title.y + 46);

		Image signs = new Image( BannerSprites.get( BannerSprites.Type.PIXEL_DUNGEON_SIGNS ) ) {
			private float time = 0;
			@Override
			public void update() {
				super.update();
				am = Math.max(0f, (float)Math.sin( time += Game.elapsed ));
				if (time >= 1.5f*Math.PI) time = 0;
			}
			@Override
			public void draw() {
				Blending.setLightMode();
				super.draw();
				Blending.setNormalMode();
			}
		};
		signs.x = title.x + (title.width() - signs.width())/2f;
		signs.y = title.y;
		add( signs );

		StyledButton btnZeroLevel;
        btnZeroLevel = new StyledButton(GREY_BUTTON, "返回地表") {
            @Override
            protected void onClick() {
                enterMainGame();
            }
        };
        btnZeroLevel.icon(Icons.get(Icons.ENTER));
        add(btnZeroLevel);

        StyledButton SeedFinder = new StyledButton(GREY_BUTTON, "种子查询器") {
            @Override
            protected void onClick() {
                GirlsFrontlinePixelDungeon.switchNoFade(SeedFindScene.class);
            }
        };
        SeedFinder.icon(new ItemSprite(ItemSpriteSheet.SEED_SUNGRASS));
        add(SeedFinder);

		StyledButton cake = new StyledButton(GREY_BUTTON, "节日蛋糕") {
			@Override
			protected void onClick() {
				GirlsFrontlinePixelDungeon.scene().addToFront(new WndCake());
			}
		};

		cake.icon(new ItemSprite(ItemSpriteSheet.WHOLECAKE));
		add(cake);

		StyledButton AutoIdentify;
		if (Badges.isUnlocked(Badges.Badge.Identify) || DeviceCompat.isDebug()){
			AutoIdentify = new StyledButton(GREY_BUTTON,"辅助鉴定"){
				@Override
				protected void onClick() {
					GirlsFrontlinePixelDungeon.scene().addToFront(
							new WndOptions(Messages.get(SecondTitleScene.class, "AutoIdentify_title"),
									Messages.get(SecondTitleScene.class, "AutoIdentify_body"),
									Messages.get(SecondTitleScene.class, "AutoIdentify_yes"),
									Messages.get(SecondTitleScene.class, "AutoIdentify_no")) {

								protected void onSelect(int index) {
									super.onSelect(index);
                                    SPDSettings.AutoIdentify(index == 0);
								}
							});
				}
			};
		}
		else{
			AutoIdentify = new StyledButton(GREY_BUTTON_TR,"辅助鉴定(未解锁)");
		}
		AutoIdentify.icon(Icons.get(Icons.ENTER));
		add(AutoIdentify);

        StyledButton LastTitle;
        LastTitle = new StyledButton(GREY_BUTTON, "上一页") {
            @Override
            protected void onClick() {
                GirlsFrontlinePixelDungeon.switchNoFade(TitleScene.class);
            }
        };
        LastTitle.icon(Icons.get(Icons.ENTER));
        add(LastTitle);

        StyledButton NextTitle;
        NextTitle = new StyledButton(GREY_BUTTON, "下一页") {
            @Override
            protected void onClick() {
                GirlsFrontlinePixelDungeon.switchNoFade(SecondTitleScene.class);
            }
        };
        NextTitle = new StyledButton(GREY_BUTTON_TR,"下一页(未制作)");
        NextTitle.icon(Icons.get(Icons.ENTER));
        add(NextTitle);

		final int BTN_HEIGHT = 20;
		final int GAP = 2;

		if (landscape()) {
            btnZeroLevel.setRect(title.x - 50, topRegion + GAP, title.width() + 100 - 1, BTN_HEIGHT);
			align(btnZeroLevel);
			AutoIdentify.setRect(btnZeroLevel.left()		,btnZeroLevel.bottom()+GAP		,btnZeroLevel.width()  				,BTN_HEIGHT);
			SeedFinder	.setRect(btnZeroLevel.left()		,AutoIdentify.bottom()+GAP 		,btnZeroLevel.width()/2F-GAP	,BTN_HEIGHT);
			cake		.setRect(SeedFinder.right()+GAP	,AutoIdentify.bottom()+GAP 		,btnZeroLevel.width()/2F-GAP  ,BTN_HEIGHT);
			LastTitle	.setRect(btnZeroLevel.left()		,SeedFinder.bottom()+GAP 		,btnZeroLevel.width()/2F-GAP  ,BTN_HEIGHT);
            NextTitle	.setRect(LastTitle.right()+GAP	,SeedFinder.bottom()+GAP 		,btnZeroLevel.width()/2F-GAP  ,BTN_HEIGHT);
		} else {
            btnZeroLevel.setRect(title.x, topRegion+GAP, title.width(), BTN_HEIGHT);
			align(btnZeroLevel);
			AutoIdentify.setRect(btnZeroLevel.left(),btnZeroLevel.bottom()+GAP	,btnZeroLevel.width()  ,BTN_HEIGHT);
			SeedFinder	.setRect(btnZeroLevel.left(),AutoIdentify.bottom()+GAP	,btnZeroLevel.width()  ,BTN_HEIGHT);
			cake		.setRect(btnZeroLevel.left(),SeedFinder.bottom()+GAP		,btnZeroLevel.width()  ,BTN_HEIGHT);
			LastTitle	.setRect(btnZeroLevel.left(),cake.bottom()+GAP			,btnZeroLevel.width()  ,BTN_HEIGHT);
            NextTitle	.setRect(btnZeroLevel.left(),LastTitle.bottom()+GAP		,btnZeroLevel.width()  ,BTN_HEIGHT);
		}

		fadeIn();
	}

	private void placeTorch( float x, float y ) {
		Fireball fb = new Fireball();
		fb.setPos( x, y );
		add( fb );
	}

	@Override
	protected void onBackPressed() {
		//Do nothing
	}

    private static void enterMainGame(){
        Dungeon.hero=null;
        ActionIndicator.action  = null;
        GamesInProgress.curSlot = 0;
        GamesInProgress.selectedClass = HeroClass.TYPE561;
        GamesInProgress.Info gameInfo = GamesInProgress.check(GamesInProgress.curSlot);
        if(gameInfo == null){
            InterlevelScene.start();
        }else if(gameInfo.version < Game.versionCode){
            Dungeon.deleteGame(GamesInProgress.curSlot, true);
            InterlevelScene.start();
        }else{
            try{InterlevelScene.restore();}
            catch(IOException e){Game.reportException(e);}
        }
        Game.switchScene(GameScene.class);
    }
	private static class WndCake extends WndOptions {
		public int month;
		public int day;
		public String message;
		public WndCake(){
			this(SPDSettings.getSpecialDay_Month(), SPDSettings.getSpecialDay_Day(), SPDSettings.getSpecialDay_Message());
		}

		public WndCake(int month, int day, String message) {
			super(new ItemSprite(ItemSpriteSheet.WHOLECAKE),
					Messages.titleCase("节日蛋糕"),
					Messages.format("当前节日日期为： %d 月 %d 日", month+1, day)
							+Messages.format("\n还需要完成 %d 次对局以修改节日日期", Math.max(0, SPDSettings.getSpecialDay_PlayTimesNeed()))
							+Messages.format("\n节日文本为：\n" +
							"%s", message),
					2,
					Messages.titleCase("修改月份"),
					Messages.titleCase("修改日期"),
					Messages.titleCase("修改文本"),
					Messages.titleCase("保存"));
			this.month = month;
			this.day = day;
			this.message = message;
		}

		public WndCake(WndCake wndCake) {
			this(wndCake.month, wndCake.day, wndCake.message);
			wndCake.hide();
		}

		@Override
		protected boolean enabled(int index) {
			if (index == 0 || index == 1)
				return SPDSettings.canChangeSpecialDay();
			return true;
		}

		@Override
		protected void onClickButton() {
		}

		@Override
		protected void onSelect(int index) {
			super.onSelect(index);
			if (index != -1) {
				if (index == 0) {
					GirlsFrontlinePixelDungeon.scene().addToFront(new SettingWindow(true));
				} else if (index == 1) {
					GirlsFrontlinePixelDungeon.scene().addToFront(new SettingWindow(false));
				} else if (index == 2) {
					GirlsFrontlinePixelDungeon.scene().addToFront(
							new WndTextInput(
									"请输入节日蛋糕的文案",
									"目前仅限于文案，允许换行，但请留意文本行数，以免过长导致无法修改或者蛋糕在游戏内无法点击按钮。",
									message,
									1000,
									true,
									"确认",
									"返回"
							){
								@Override
								public void onSelect(boolean check, String text) {
									if(check){
										WndCake.this.message = text;
										GirlsFrontlinePixelDungeon.scene().addToFront(new WndCake(WndCake.this));
									}
								}
							}
					);
				} else if (index == 3) {
					hide();
					if (month != SPDSettings.getSpecialDay_Month() || day != SPDSettings.getSpecialDay_Day()) {
						SPDSettings.setSpecialDay_Month(month);
						SPDSettings.setSpecialDay_Day(day);
						SPDSettings.resetSpecialDay_PlayTimes();
					}
					SPDSettings.setSpecialDay_Message(message);
				}
			}
		}

		@Override
		public void onBackPressed() {
			//do nothing, prevents accidentally closing
		}
		private class SettingWindow extends Window {
			private final ArrayList<canScrollRedButton> buttons = new ArrayList<>();
			public SettingWindow(boolean isMonth){

				super();
				resize(120, 144);
				int placed = 0;
				ScrollPane list = new ScrollPane(new Component()) {

					@Override
					public void onClick(float x, float y) {
						int max_size = buttons.size();
						for (int i = 0; i < max_size; ++i) {
							if (buttons.get(i).onClick(x, y))
								break;
						}
					}

				};
				add(list);
				Component content = list.content();
				int start, end;
				if (isMonth){
					start = 0;
					end = 11;
				}
				else {
					start = 1;
					end = 31;
				}
				while (start <= end){
					canScrollRedButton cb = new canScrollRedButton(isMonth? start+1 : start){
						public boolean onClick(float x, float y){
							if(!inside(x,y)) return false;
							onClick();

							return true;
						}

						@Override
						public void onClick(){
							super.onClick();
							hide();
							if (isMonth)
								WndCake.this.month = num - 1;
							else
								WndCake.this.day = num;
							GirlsFrontlinePixelDungeon.scene().addToFront(new WndCake(WndCake.this));
						}

						@Override
						public void layout(){
							super.layout();
							hotArea.width = hotArea.height = 0;
						}
					};
					cb.setRect(0, 18*placed, 120, 16);
					PixelScene.align(cb);
					placed ++;
					content.add(cb);
					buttons.add(cb);
					start++;
				}
				content.setSize(120, buttons.get(buttons.size()-1).bottom());
				list.setSize( list.width(), list.height() );
				list.setRect(0, 0, 120, 144);
				list.scrollTo(0,0);
			}

			@Override
			public void onBackPressed() {
				super.onBackPressed();
			}
		}
	}
}