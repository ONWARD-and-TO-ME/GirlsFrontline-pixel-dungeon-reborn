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

package com.shatteredpixel.shatteredpixeldungeon;

import com.watabou.noosa.TextureFilm;

/**
 * 徽章贴图索引 V2：为迭代 Badges 系统做准备（暂未被引用）。
 * 贴图文件 interfaces/badge1.png，每格 16*16 像素，每行 16 格，共 17 行。
 * 行语义（自上而下）：
 *   1  角色图标徽章行
 *   2  物品徽章行
 *   3  钻石徽章行
 *   4  击杀徽章行
 *   5  食物徽章行
 *   6  等级徽章行
 *   7  炼药徽章行
 *   8  力量徽章行
 *   9  职业徽章行
 *   10 探索徽章行
 *   11 鉴定徽章行
 *   12 死因徽章行
 *   13 boss徽章行
 *   14 结局徽章行
 *   15 挑战徽章行
 *   16 特殊徽章行
 *   17 徽章背景行
 */
public class BadgeV2 {

	public static final int WIDTH = 16; //每行16格
	public static final int SIZE  = 16; //每格16*16像素

	public static TextureFilm film = new TextureFilm( "interfaces/badge1.png", SIZE, SIZE );

	private static int xy(int x, int y){
		x -= 1; y -= 1;
		return x + WIDTH*y;
	}

	private static void assignItemRect( int item, int width, int height ){
		int x = (item % WIDTH) * SIZE;
		int y = (item / WIDTH) * SIZE;
		film.add( item, x, y, x+width, y+height);
	}

	//第一行 角色图标徽章行
	private static final int HERO_ICONS             =                               xy(1, 1);   //16 slots
	public static final int MASTERY_WARRIOR         = HERO_ICONS+0;
	public static final int MASTERY_MAGE            = HERO_ICONS+1;
	public static final int MASTERY_ROGUE           = HERO_ICONS+2;
	public static final int MASTERY_HUNTRESS        = HERO_ICONS+3;
	public static final int MASTERY_TYPE561         = HERO_ICONS+4;
	public static final int MASTERY_GSH18           = HERO_ICONS+5;
	public static final int MASTERY_HK416           = HERO_ICONS+6;
	public static final int MASTERY_DANDELION       = HERO_ICONS+7;
	//8 free slots

	//第二行 物品徽章行（物品升级等级）
	private static final int ITEM_BADGES            =                               xy(1, 2);   //16 slots
	public static final int ITEM_LEVEL_1            = ITEM_BADGES+0;
	public static final int ITEM_LEVEL_2            = ITEM_BADGES+1;
	public static final int ITEM_LEVEL_3            = ITEM_BADGES+2;
	public static final int ITEM_LEVEL_4            = ITEM_BADGES+3;
	public static final int ITEM_LEVEL_5            = ITEM_BADGES+4;
	//11 free slots

	//第三行 钻石徽章行（稀有/终极成就）
	private static final int DIAMOND_BADGES         =                               xy(1, 3);   //16 slots
	public static final int CRYSTAL_TROPHY          = DIAMOND_BADGES+0;
	//15 free slots

	//第四行 击杀徽章行
	private static final int KILL_BADGES            =                               xy(1, 4);   //16 slots
	public static final int MONSTERS_SLAIN_1        = KILL_BADGES+0;
	public static final int MONSTERS_SLAIN_2        = KILL_BADGES+1;
	public static final int MONSTERS_SLAIN_3        = KILL_BADGES+2;
	public static final int MONSTERS_SLAIN_4        = KILL_BADGES+3;
	public static final int MONSTERS_SLAIN_5        = KILL_BADGES+4;
	public static final int NO_MONSTERS_SLAIN       = KILL_BADGES+5;
	public static final int GRIM_WEAPON             = KILL_BADGES+6;
	public static final int KILL_EXCUTION           = KILL_BADGES+7;
	public static final int KILL_SNAKE              = KILL_BADGES+8;
	public static final int KILL_CALC               = KILL_BADGES+9;
	public static final int KILL_DISPORE            = KILL_BADGES+10;
	public static final int KILL_ELPHELT            = KILL_BADGES+11;
	public static final int ELPHELT_WEAPON          = KILL_BADGES+12;
	//3 free slots

	//第五行 食物徽章行
	private static final int FOOD_BADGES            =                               xy(1, 5);   //16 slots
	public static final int FOOD_EATEN_1            = FOOD_BADGES+0;
	public static final int FOOD_EATEN_2            = FOOD_BADGES+1;
	public static final int FOOD_EATEN_3            = FOOD_BADGES+2;
	public static final int FOOD_EATEN_4            = FOOD_BADGES+3;
	public static final int FOOD_EATEN_5            = FOOD_BADGES+4;
	//11 free slots

	//第六行 等级徽章行（英雄等级）
	private static final int LEVEL_BADGES           =                               xy(1, 6);   //16 slots
	public static final int LEVEL_REACHED_1         = LEVEL_BADGES+0;
	public static final int LEVEL_REACHED_2         = LEVEL_BADGES+1;
	public static final int LEVEL_REACHED_3         = LEVEL_BADGES+2;
	public static final int LEVEL_REACHED_4         = LEVEL_BADGES+3;
	public static final int LEVEL_REACHED_5         = LEVEL_BADGES+4;
	//11 free slots

	//第七行 炼药徽章行
	private static final int ALCHEMY_BADGES         =                               xy(1, 7);   //16 slots
	public static final int ITEMS_CRAFTED_1         = ALCHEMY_BADGES+0;
	public static final int ITEMS_CRAFTED_2         = ALCHEMY_BADGES+1;
	public static final int ITEMS_CRAFTED_3         = ALCHEMY_BADGES+2;
	public static final int ITEMS_CRAFTED_4         = ALCHEMY_BADGES+3;
	public static final int ITEMS_CRAFTED_5         = ALCHEMY_BADGES+4;
	//11 free slots

	//第八行 力量徽章行
	private static final int STRENGTH_BADGES        =                               xy(1, 8);   //16 slots
	public static final int STRENGTH_ATTAINED_1     = STRENGTH_BADGES+0;
	public static final int STRENGTH_ATTAINED_2     = STRENGTH_BADGES+1;
	public static final int STRENGTH_ATTAINED_3     = STRENGTH_BADGES+2;
	public static final int STRENGTH_ATTAINED_4     = STRENGTH_BADGES+3;
	public static final int STRENGTH_ATTAINED_5     = STRENGTH_BADGES+4;
	//11 free slots

	//第九行 职业徽章行（解锁职业+组合技）
	private static final int CLASS_BADGES           =                               xy(1, 9);   //16 slots
	public static final int UNLOCK_WARRIOR          = CLASS_BADGES+0;
	public static final int UNLOCK_MAGE             = CLASS_BADGES+1;
	public static final int UNLOCK_ROGUE            = CLASS_BADGES+2;
	public static final int UNLOCK_HUNTRESS         = CLASS_BADGES+3;
	public static final int UNLOCK_TYPE561          = CLASS_BADGES+4;
	public static final int UNLOCK_GSH18            = CLASS_BADGES+5;
	public static final int UNLOCK_HK416            = CLASS_BADGES+6;
	public static final int UNLOCK_DANDELION        = CLASS_BADGES+7;
	public static final int MASTERY_COMBO           = CLASS_BADGES+8;
	//7 free slots

	//第十行 探索徽章行
	private static final int EXPLORE_BADGES         =                               xy(1, 10);  //16 slots
	public static final int PIRANHAS                = EXPLORE_BADGES+0;
	public static final int BAG_BOUGHT_VELVET_POUCH     = EXPLORE_BADGES+1;
	public static final int BAG_BOUGHT_SCROLL_HOLDER    = EXPLORE_BADGES+2;
	public static final int BAG_BOUGHT_POTION_BANDOLIER = EXPLORE_BADGES+3;
	public static final int BAG_BOUGHT_MAGICAL_HOLSTER  = EXPLORE_BADGES+4;
	public static final int ALL_BAGS_BOUGHT         = EXPLORE_BADGES+5;
	public static final int FOUND_RATMOGRIFY        = EXPLORE_BADGES+6;
	//9 free slots

	//第十一行 鉴定徽章行
	private static final int IDENTIFY_BADGES        =                               xy(1, 11);  //16 slots
	public static final int ALL_POTIONS_IDENTIFIED  = IDENTIFY_BADGES+0;
	public static final int ALL_SCROLLS_IDENTIFIED  = IDENTIFY_BADGES+1;
	public static final int ALL_WEAPONS_IDENTIFIED  = IDENTIFY_BADGES+2;
	public static final int ALL_ARMOR_IDENTIFIED    = IDENTIFY_BADGES+3;
	public static final int ALL_WANDS_IDENTIFIED    = IDENTIFY_BADGES+4;
	public static final int ALL_RINGS_IDENTIFIED    = IDENTIFY_BADGES+5;
	public static final int ALL_ARTIFACTS_IDENTIFIED= IDENTIFY_BADGES+6;
	public static final int ALL_ITEMS_IDENTIFIED    = IDENTIFY_BADGES+7;
	public static final int IDENTIFY                = IDENTIFY_BADGES+8;
	public static final int DEGRADE_EQUIPMENT       = IDENTIFY_BADGES+9;
	//6 free slots

	//第十二行 死因徽章行
	private static final int DEATH_BADGES           =                               xy(1, 12);  //16 slots
	public static final int DEATH_FROM_FIRE         = DEATH_BADGES+0;
	public static final int DEATH_FROM_POISON       = DEATH_BADGES+1;
	public static final int DEATH_FROM_GAS          = DEATH_BADGES+2;
	public static final int DEATH_FROM_HUNGER       = DEATH_BADGES+3;
	public static final int DEATH_FROM_FALLING      = DEATH_BADGES+4;
	public static final int DEATH_FROM_GLYPH        = DEATH_BADGES+5;
	//10 free slots

	//第十三行 boss徽章行
	//TODO: BOSS_SLAIN_3_* 子职业系列（共9个）超出本行容量，待分配到其他行
	private static final int BOSS_BADGES            =                               xy(1, 13);  //16 slots
	public static final int BOSS_SLAIN_1            = BOSS_BADGES+0;
	public static final int BOSS_SLAIN_2            = BOSS_BADGES+1;
	public static final int BOSS_SLAIN_3            = BOSS_BADGES+2;
	public static final int BOSS_SLAIN_4            = BOSS_BADGES+3;
	public static final int BOSS_SLAIN_1_WARRIOR    = BOSS_BADGES+4;
	public static final int BOSS_SLAIN_1_MAGE       = BOSS_BADGES+5;
	public static final int BOSS_SLAIN_1_ROGUE      = BOSS_BADGES+6;
	public static final int BOSS_SLAIN_1_HUNTRESS   = BOSS_BADGES+7;
	public static final int BOSS_SLAIN_1_TYPE561    = BOSS_BADGES+8;
	public static final int BOSS_SLAIN_1_GSH18      = BOSS_BADGES+9;
	public static final int BOSS_SLAIN_1_HK416      = BOSS_BADGES+10;
	public static final int BOSS_SLAIN_1_ALL_CLASSES= BOSS_BADGES+11;
	//4 free slots

	//第十四行 结局徽章行
	private static final int ENDING_BADGES          =                               xy(1, 14);  //16 slots
	public static final int VICTORY                 = ENDING_BADGES+0;
	public static final int VICTORY_WARRIOR         = ENDING_BADGES+1;
	public static final int VICTORY_MAGE            = ENDING_BADGES+2;
	public static final int VICTORY_ROGUE           = ENDING_BADGES+3;
	public static final int VICTORY_HUNTRESS        = ENDING_BADGES+4;
	public static final int VICTORY_TYPE561         = ENDING_BADGES+5;
	public static final int VICTORY_GSH18           = ENDING_BADGES+6;
	public static final int VICTORY_HK416           = ENDING_BADGES+7;
	public static final int VICTORY_ALL_CLASSES     = ENDING_BADGES+8;
	public static final int HAPPY_END               = ENDING_BADGES+9;
	public static final int YASD                    = ENDING_BADGES+10;
	//5 free slots

	//第十五行 挑战徽章行
	private static final int CHALLENGE_BADGES       =                               xy(1, 15);  //16 slots
	public static final int CHAMPION_1              = CHALLENGE_BADGES+0;
	public static final int CHAMPION_2              = CHALLENGE_BADGES+1;
	public static final int CHAMPION_3              = CHALLENGE_BADGES+2;
	//13 free slots

	//第十六行 特殊徽章行（游戏局数+金币收集+节日）
	private static final int SPECIAL_BADGES         =                               xy(1, 16);  //16 slots
	public static final int GAMES_PLAYED_1          = SPECIAL_BADGES+0;
	public static final int GAMES_PLAYED_2          = SPECIAL_BADGES+1;
	public static final int GAMES_PLAYED_3          = SPECIAL_BADGES+2;
	public static final int GAMES_PLAYED_4          = SPECIAL_BADGES+3;
	public static final int GAMES_PLAYED_5          = SPECIAL_BADGES+4;
	public static final int GOLD_COLLECTED_1        = SPECIAL_BADGES+5;
	public static final int GOLD_COLLECTED_2        = SPECIAL_BADGES+6;
	public static final int GOLD_COLLECTED_3        = SPECIAL_BADGES+7;
	public static final int GOLD_COLLECTED_4        = SPECIAL_BADGES+8;
	public static final int GOLD_COLLECTED_5        = SPECIAL_BADGES+9;
	public static final int XMAS_GIFT               = SPECIAL_BADGES+10;
	//5 free slots

	//第十七行 徽章背景行（各品质底框，供徽章图标叠加使用）
	private static final int BADGE_BACKGROUNDS      =                               xy(1, 17);  //16 slots
	//16 free slots（背景框按迭代需要再命名）

	static{
		//全部17*16个格子均为16*16像素
		for (int i = 0; i < WIDTH*17; i++)
			assignItemRect(i, SIZE, SIZE);
	}
}
