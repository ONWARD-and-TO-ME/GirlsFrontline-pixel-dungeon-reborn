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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.ColorItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.LR.GSH18;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;

public enum Icons {

	//title screen icons, variable sizes, spacing for 17x16 标题页图标
	ENTER, //Enter图标，用于进入游戏等
	GOLD, //金币图标，用于显示金币等
	GOLDBAGES, //金币徽章图标，用于显示金币徽章等
	RANKINGS, //排名图标，用于显示排名等
	BADGES, //勋章章图标，用于显示勋章等
	NEWS, //新闻图标，用于显示新闻等
	CHANGES, 	//更新日志图标，用于显示更新日志等
	PREFS, //偏好设置图标，用于显示偏好设置等
	SETTINGS, //设置图标，用于显示设置等
	GIRLPDS, //少前地牢标题图标，用于显示少前地牢标题等
	CHANGESLOG, //变更日志图标，用于显示变更日志等
	SHPX, //像素地牢图标，用于显示像素地牢等

	//rankings and hero select icons, spacing for 16x16 排名和英雄选择图标
	STAIRS, //楼梯图标，用于显示楼梯等
	WARRIOR, //战士图标，用于显示战士等
	MAGE, //法师图标，用于显示法师等
	ROGUE, //盗贼图标，用于显示盗贼等
	HUNTRESS, //猎人图标，用于显示猎人等
	TYPE561, //类型561图标，用于显示类型561等
	GSH18, //GSH18图标，用于显示GSH18等
	HK416, //HK416图标，用于显示HK416等
	dandeline, //丹德莱图标，用于显示丹德莱等


	//grey icons, mainly used for buttons, spacing for 16x16 灰色图标，主要用于按钮
	EXIT,
	DISPLAY, //2 separate images, changes based on orientation 显示，根据方向改变
	DISPLAY_LAND,
	DISPLAY_PORT,
	DATA,
	AUDIO,
	LANGS,
	CONTROLLER,
	STATS,
	CHALLENGE_OFF,
	CHALLENGE_ON,
	RENAME_OFF,
	RENAME_ON,
	LEFTARROW,
	RIGHTARROW,

	//misc icons, mainly used for buttons, spacing for 16x16 until the smaller icons at the end 杂项图标，主要用于按钮
	UNCHECKED, 	//未选中，用于复选框等
	CHECKED, 		//已选中，用于复选框等
    UNLOCK, 		//解锁，用于打开门等
    LOCK, 		//锁定，用于关闭门等
	CLOSE,		//关闭，用于关闭窗口等
	PLUS, 		//加号，用于增加数量等
	ARROW, 		//箭头，用于指向目标等
	INFO, 		//信息图标，用于显示信息等
	WARNING,		//警告图标，用于显示警告等
	BACKPACK_LRG,	//背包图标，用于显示物品等
	TALENT, 		//天赋图标，用于显示天赋等
	MAGNIFY, 		//放大镜，用于放大显示等
	BUFFS, 		//buff图标，用于显示buff等
	ENERGY, 		//能量图标，用于显示能量等
	COIN_SML, 		//金币图标，用于显示金币等
	ENERGY_SML, 	//能量图标，用于显示能量等
	POS_SHOW, 		//位置显示图标，用于显示位置等
	BACKPACK, 	//背包图标，用于显示物品等
	SEED_POUCH, 	//种子图标，用于显示种子等
	SCROLL_HOLDER, 	//书架图标，用于显示书架等
	WAND_HOLSTER, 	//手枪图标，用于显示手枪等
	POTION_BANDOLIER, 	//药水图标，用于显示药水等

	//icons that appear in the game itself, variable spacing 	游戏中的图标，变量间距
	TARGET, //目标图标，用于显示目标等
	SKULL, //骷髅图标，用于显示敌人等
	BUSY, //忙碌图标，用于显示忙碌等
	COMPASS, //指南针图标，用于指向目标等
	SLEEP, //睡眠图标，用于显示睡眠等
	ALERT, //警告图标，用于显示警告等
	LOST, //丢失图标，用于显示丢失等
	DEPTH, //深度图标，用于显示深度等
	DEPTH_CHASM, //深度图标，用于显示深度等
	DEPTH_WATER, //深度图标，用于显示深度等
	DEPTH_GRASS, //深度图标，用于显示深度等
	DEPTH_DARK, //深度图标，用于显示深度等
	DEPTH_LARGE, //深度图标，用于显示深度等
	DEPTH_TRAPS, //深度图标，用于显示深度等
	DEPTH_SECRETS, //深度图标，用于显示深度等
	CHAL_COUNT, //挑战图标，用于显示挑战等

	//icons that appear in the about screen, variable spacing 	关于屏幕中的图标，变量间距
	LIBGDX, //libgdx图标，用于显示libgdx等
	ALEKS, //Aleks图标，用于显示Aleks等
	WATA, //Wata图标，用于显示Wata等
	CELESTI, //Celeste图标，用于显示Celeste等
	KRISTJAN, //Kristjan图标，用于显示Kristjan等
	CUBE_CODE, //Cube Code图标，用于显示Cube Code等
	PURIGRO, //Purigro图标，用于显示Purigro等
	ARCNOR, //Arcnor图标，用于显示Arcnor等

	/* Made People */
	GIRLPD, //GIRLPD图标，用于显示GIRLPD等
	BAKA, //Baka图标，用于显示Baka等
	LANGLING, //Langling图标，用于显示Langling等

	WOLF, //Wolf图标，用于显示Wolf等
	CATZS, //Catzs图标，用于显示Catzs等
	SEA, //Sea图标，用于显示Sea等
	ONWARD, //Onward图标，用于显示Onward等

	LING, //Ling图标，用于显示Ling等
	SHOWER, //Shower图标，用于显示Shower等
	COLA, //Cola图标，用于显示Cola等

	FTER, //After图标，用于显示After等
	CHOCOSUKI, //Chocosuki图标，用于显示Chocosuki等
	DOGE, //Doge图标，用于显示Doge等

	AWSL, //Awsl图标，用于显示Awsl等
	ALEX, //Alex图标，用于显示Alex等


	SKIP; //Skip图标，用于显示Skip等

	public static Image Notice(ColorItem item){
		Image itemIcon = new Image(Assets.Sprites.ITEM_ICONS);
		itemIcon.scale.set(1.6F);
		itemIcon.frame(ItemSpriteSheet.Icons.film.get(((Item) item).icon));
		return itemIcon;
	}
	public Image get() {
		return get( this );
	}
	
	public static Image get( Icons type ) {
		Image icon = new Image( Assets.Interfaces.ICONS );
		switch (type) {

			case ENTER:
				icon.frame( icon.texture.uvRectBySize
					( 0, 0, 16, 16 ) );          //开始游戏
				break;
			case GOLD:
				icon.frame( icon.texture.uvRectBySize
					( 144, 0, 15, 12 ) );        //标题页金币
				break;
			case RANKINGS:
				icon.frame( icon.texture.uvRectBySize
					( 16, 0, 16, 15 ) );        //排行榜
				break;
			case BADGES:
				icon.frame( icon.texture.uvRectBySize
					( 32, 0, 16, 16 ) );        //徽章
				break;
			case GOLDBAGES:
				icon.frame( icon.texture.uvRectBySize
					( 48, 0, 16, 16 ) );        //全成就徽章
				break;
			case NEWS:
				icon.frame( icon.texture.uvRectBySize
					( 64, 0, 16, 15 ) );        //新闻
				break;
			case CHANGES:
				icon.frame( icon.texture.uvRectBySize
					( 80, 0, 15, 15 ) );        //更新日志
				break;
			case PREFS:
				icon.frame( icon.texture.uvRectBySize
					( 96, 0, 14, 14 ) );        //偏好设置			
				break;
			case SETTINGS:
				icon.frame( icon.texture.uvRectBySize
					( 112, 0, 12, 12 ) );     //设置(齿轮)
				break;
			case GIRLPDS:
				icon.frame( icon.texture.uvRectBySize
					( 128, 96, 24, 24 ) );       //少前地牢标题图标
				break;
			case SHPX:
				icon.frame( icon.texture.uvRectBySize
					( 160, 160, 16, 16 ) );     //像素地牢图标
				break;
			case CHANGESLOG:
				icon.frame( icon.texture.uvRectBySize
					( 128, 0, 10, 11 ) );       //变更日志(小)
				break;

			case STAIRS:
				icon.frame( icon.texture.uvRectBySize
					( 192, 0, 13, 16 ) );       //楼梯/深度
				break;
			case WARRIOR:
				icon.frame( icon.texture.uvRectBySize
					( 0, 16, 9, 12 ) );         //战士头像
				break;
			case MAGE:
				icon.frame( icon.texture.uvRectBySize
					( 16, 16, 14, 15 ) );       //法师头像
				break;
			case ROGUE:
				icon.frame( icon.texture.uvRectBySize
					( 32, 16, 15, 14 ) );       //盗贼头像
				break;
			case HUNTRESS:
				icon.frame( icon.texture.uvRectBySize
					( 48, 16, 16, 16 ) );       //女猎头像
				break;
			case TYPE561:
				icon.frame( icon.texture.uvRectBySize
					( 64, 16, 12, 16 ) );       //561式头像
				break;
			case GSH18:
				icon.frame( icon.texture.uvRectBySize
					( 80, 16, 15, 12 ) );       //GSH18式头像
				break;
			case HK416:
				icon.frame( icon.texture.uvRectBySize
					( 96, 16, 15, 15 ) );       //HK416式头像
				break;
			case dandeline:
				icon.frame( icon.texture.uvRectBySize
					( 128, 16, 15, 16 ) );       //丹德莱式头像
				break;

			case EXIT:
				icon.frame( icon.texture.uvRectBySize
					( 0, 32, 15, 11 ) );        //退出
				break;
			case DISPLAY:
				if (!PixelScene.landscape()){
					return get(DISPLAY_PORT);                                    //竖屏
				} else {
					return get(DISPLAY_LAND);                                    //横屏
				}
			case DISPLAY_PORT:
				icon.frame( icon.texture.uvRectBySize
					( 16, 32, 12, 16 ) );      //竖屏显示
				break;
			case DISPLAY_LAND:
				icon.frame( icon.texture.uvRectBySize
					( 32, 32, 16, 12 ) );      //横屏显示
				break;
			case DATA:
				icon.frame( icon.texture.uvRectBySize
					( 48, 32, 16, 15 ) );      //数据
				break;
			case AUDIO:
				icon.frame( icon.texture.uvRectBySize
					( 64, 32, 14, 14 ) );      //音频
				break;
			case LANGS:
				icon.frame( icon.texture.uvRectBySize
					( 80, 32, 14, 11 ) );      //语言
				break;
			case CONTROLLER:
				icon.frame( icon.texture.uvRectBySize
					( 96, 32, 16, 12 ) );      //控制器
				break;
			case STATS:
				icon.frame( icon.texture.uvRectBySize
					( 112, 32, 16, 13 ) );     //统计
				break;
			case CHALLENGE_OFF:
				icon.frame( icon.texture.uvRectBySize
					( 128, 32, 14, 12 ) );     //挑战关
				break;
			case CHALLENGE_ON:
				icon.frame( icon.texture.uvRectBySize
					( 144, 32, 14, 12 ) );     //挑战开
				break;
			case RENAME_OFF:
				icon.frame( icon.texture.uvRectBySize
					( 160, 32, 15, 14 ) );     //重命名关
				break;
			case RENAME_ON:
				icon.frame( icon.texture.uvRectBySize
					( 176, 32, 15, 14 ) );     //重命名开
				break;
			case LEFTARROW:
				icon.frame( icon.texture.uvRectBySize
					( 0, 112, 14, 8 ) );        //左箭头
				break;
			case RIGHTARROW:
				icon.frame( icon.texture.uvRectBySize
					( 16, 112, 14, 8 ) );       //右箭头
				break;

            case UNCHECKED:
                icon.frame( icon.texture.uvRectBySize
					( 0, 48, 12, 12 ) );       //未勾选
                break;
            case CHECKED:
                icon.frame( icon.texture.uvRectBySize
					( 16, 48, 12, 12 ) );      //已勾选
                break;
            case UNLOCK:
                icon.frame( icon.texture.uvRectBySize
					( 192, 64, 11, 10 ) );     //解锁
                break;
            case LOCK:
                icon.frame( icon.texture.uvRectBySize
					( 176, 64, 11, 10 ) );     //锁定
                break;
			case CLOSE:
				icon.frame( icon.texture.uvRectBySize
					( 32, 48, 11, 11 ) );      //关闭(X)
				break;
			case PLUS:
				icon.frame( icon.texture.uvRectBySize
					( 48, 48, 11, 11 ) );      //加号(+)
				break;
			case ARROW:
				icon.frame( icon.texture.uvRectBySize
					( 80, 48, 11, 11 ) );      //箭头
				break;
			case INFO:
				icon.frame( icon.texture.uvRectBySize
					( 96, 48, 14, 14 ) );      //信息(i)
				break;
			case WARNING:
				icon.frame( icon.texture.uvRectBySize
					( 112, 48, 14, 14 ) );     //警告(!)
				break;
			case BACKPACK_LRG:
				icon.frame( icon.texture.uvRectBySize
					( 128, 48, 16, 16 ) );     //大背包
				break;
			case TALENT:
				icon.frame( icon.texture.uvRectBySize
					( 144, 48, 13, 13 ) );     //天赋
				break;
			case MAGNIFY:
				icon.frame( icon.texture.uvRectBySize
					( 160, 48, 14, 14 ) );     //放大镜
				break;
			case BUFFS:
				icon.frame( icon.texture.uvRectBySize
					( 176, 48, 16, 15 ) );     //Buff列表
				break;
			case ENERGY:
				icon.frame( icon.texture.uvRectBySize
					( 192, 48, 16, 16 ) );     //能量
				break;
			case COIN_SML:
				icon.frame( icon.texture.uvRectBySize
					( 208, 48, 7, 7 ) );       //小金币	
				break;
			case ENERGY_SML:
				icon.frame( icon.texture.uvRectBySize
					( 208, 56, 8, 7 ) );       //小能量
				break;
			case POS_SHOW:
				icon.frame( icon.texture.uvRectBySize
					( 224, 48, 5, 5 ) );       //位置显示
				break;
			case BACKPACK:
				icon.frame( icon.texture.uvRectBySize
					( 192, 32, 10, 10 ) );     //背包
				break;
			case SCROLL_HOLDER:
				icon.frame( icon.texture.uvRectBySize
					( 202, 32, 10, 10 ) );     //卷轴筒
				break;
			case SEED_POUCH:
				icon.frame( icon.texture.uvRectBySize
					( 212, 32, 10, 10 ) );     //种子袋
				break;
			case WAND_HOLSTER:
				icon.frame( icon.texture.uvRectBySize
					( 222, 32, 10, 10 ) );     //法杖袋
				break;
			case POTION_BANDOLIER:
				icon.frame( icon.texture.uvRectBySize
					( 232, 32, 10, 10 ) );     //药剂带
				break;

			case TARGET:
				icon.frame( icon.texture.uvRectBySize
					( 0, 64, 16, 16 ) );       //目标
				break;
			case SKULL:
				icon.frame( icon.texture.uvRectBySize
					( 16, 64, 8, 8 ) );        //骷髅
				break;
			case BUSY:
				icon.frame( icon.texture.uvRectBySize
					( 24, 64, 8, 8 ) );        //忙碌(加载中)
				break;
			case COMPASS:
				icon.frame( icon.texture.uvRectBySize
					( 16, 72, 7, 5 ) );        //指南针
				break;
			case SLEEP:
				icon.frame( icon.texture.uvRectBySize
					( 32, 64, 9, 8 ) );        //睡眠
				break;
			case ALERT:
				icon.frame( icon.texture.uvRectBySize
					( 32, 72, 8, 8 ) );        //警觉
				break;
			case LOST:
				icon.frame( icon.texture.uvRectBySize
					( 40, 72, 8, 8 ) );        //迷失
				break;
			case DEPTH:
				icon.frame( icon.texture.uvRectBySize
					( 48, 64, 6, 7 ) );        //深度
				break;
			case DEPTH_CHASM:
				icon.frame( icon.texture.uvRectBySize
					( 56, 64, 7, 7 ) );        //深渊
				break;
			case DEPTH_WATER:
				icon.frame( icon.texture.uvRectBySize
					( 64, 64, 7, 7 ) );        //水域
				break;
			case DEPTH_GRASS:
				icon.frame( icon.texture.uvRectBySize
					( 72, 64, 7, 7 ) );        //草地
				break;
			case DEPTH_DARK:
				icon.frame( icon.texture.uvRectBySize
					( 80, 64, 7, 7 ) );        //黑暗
				break;
			case DEPTH_LARGE:
				icon.frame( icon.texture.uvRectBySize
					( 88, 64, 7, 7 ) );        //大房间
				break;
			case DEPTH_TRAPS:
				icon.frame( icon.texture.uvRectBySize
					( 96, 64, 7, 7 ) );        //陷阱
				break;
			case DEPTH_SECRETS:
				icon.frame( icon.texture.uvRectBySize
					( 104, 64, 7, 7 ) );       //秘密
				break;
			case CHAL_COUNT:
				icon.frame( icon.texture.uvRectBySize
					( 48, 72, 7, 7 ) );        //挑战计数
				break;

			case LIBGDX:
				icon.frame( icon.texture.uvRectBySize
					( 160, 144, 16, 13 ) );    //libGDX logo
				break;
			case ALEKS:
				icon.frame( icon.texture.uvRectBySize
					( 176, 144, 16, 13 ) );    //Aleks头像
				break;
			case WATA:
				icon.frame( icon.texture.uvRectBySize
					( 176, 160, 17, 12 ) );    //Watabou头像
				break;

			//large icons are scaled down to match game's size
			case CELESTI:
				icon.frame( icon.texture.uvRectBySize
					( 0, 192, 32, 32 ) );      //Celesti头像(32x32)
				icon.scale.set(PixelScene.align(0.49f));
				break;
			case KRISTJAN:
				icon.frame( icon.texture.uvRectBySize
					( 32, 192, 32, 32 ) );      //Kristjan头像(32x32)	
				icon.scale.set(PixelScene.align(0.49f));
				break;
			case ARCNOR:
				icon.frame( icon.texture.uvRectBySize
					( 64, 192, 32, 32 ) );     //Arcnor头像(32x32)
				icon.scale.set(PixelScene.align(0.49f));
				break;
			case PURIGRO:
				icon.frame( icon.texture.uvRectBySize
					( 96, 192, 32, 32 ) );     //Purigro头像(32x32)
				icon.scale.set(PixelScene.align(0.49f));
				break;
			case CUBE_CODE:
				icon.frame( icon.texture.uvRectBySize
					( 128, 192, 27, 30 ) );    //Cube Code头像
				icon.scale.set(PixelScene.align(0.49f));
				break;

			case SKIP:
				icon.frame( icon.texture.uvRectBySize
					( 48, 96, 24, 14 ) );      //跳过按钮
				break;

			/* Made Group */
			case GIRLPD:
				icon.frame( icon.texture.uvRectBySize
					( 144, 65, 16, 16 ) );     //少前地牢头像
				break;
			case BAKA:
				icon.frame( icon.texture.uvRectBySize
					( 0, 145, 14, 14 ) );      //Baka头像
				break;
			case LANGLING:
				icon.frame( icon.texture.uvRectBySize
					( 0, 161, 14, 14 ) );      //Langling头像
				break;

			case FTER:
				icon.frame( icon.texture.uvRectBySize
					( 96, 145, 14, 14 ) );     //Fter头像
				break;
			case CHOCOSUKI:
				icon.frame( icon.texture.uvRectBySize
					( 112, 145, 14, 14 ) );    //Chocosuki头像
				break;
			case DOGE:
				icon.frame( icon.texture.uvRectBySize
					( 128, 145, 14, 14 ) );    //Doge头像
				break;

			case WOLF:
				icon.frame( icon.texture.uvRectBySize
					( 32, 145, 14, 14 ) );     //Wolf头像
				break;
			case CATZS:
				icon.frame( icon.texture.uvRectBySize
					( 208, 144, 19, 19 ) );    //Catzs头像
				break;
			case SEA:
				icon.frame( icon.texture.uvRectBySize
					( 64, 145, 14, 14 ) );     //Sea头像
				break;
			case ONWARD:
				icon.frame( icon.texture.uvRectBySize
					( 80, 145, 14, 14 ) );     //Onward头像
				break;

			case LING:
				icon.frame( icon.texture.uvRectBySize
					( 32, 160, 14, 14 ) );     //Ling头像
				break;
			case SHOWER:
				icon.frame( icon.texture.uvRectBySize
					( 48, 160, 14, 14 ) );     //Shower头像
				break;
			case COLA:
				icon.frame( icon.texture.uvRectBySize
					( 64, 160, 14, 14 ) );     //Cola头像
				break;

			case AWSL:
				icon.frame( icon.texture.uvRectBySize
					( 96, 160, 14, 14 ) );     //AWSL头像
				break;
			case ALEX:
				icon.frame( icon.texture.uvRectBySize
					( 112, 160, 14, 14 ) );    //Alex头像
				break;
		}
		return icon;
	}
	
	public static Image get( HeroClass cl ) {
		switch (cl) {
		case WARRIOR:
			return get( WARRIOR );
		case MAGE:
			return get( MAGE );
		case ROGUE:
			return get( ROGUE );
		case HUNTRESS:
			return get( HUNTRESS );
		case TYPE561:
		case TYPE561_OLD:
			return get( TYPE561 );
		default:
			return get( WARRIOR );
		case GSH18:
			return get( GSH18 );
		case HK416:
			return get( HK416 );
		//case dandelion:
		//	return get( WARRIOR );
		}
	}

	public static Image get(Level.Feeling feeling){
		switch (feeling){
			case NONE: default:
				return get(DEPTH);
			case CHASM:
				return get(DEPTH_CHASM);
			case WATER:
				return get(DEPTH_WATER);
			case GRASS:
				return get(DEPTH_GRASS);
			case DARK:
				return get(DEPTH_DARK);
			case LARGE:
				return get(DEPTH_LARGE);
			case TRAPS:
				return get(DEPTH_TRAPS);
			case SECRETS:
				return get(DEPTH_SECRETS);
		}
	}
}
