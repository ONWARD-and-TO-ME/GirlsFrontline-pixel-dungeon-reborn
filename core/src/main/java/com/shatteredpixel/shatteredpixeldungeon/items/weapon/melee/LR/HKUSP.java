/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2018 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.LR;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/**
 * HK416 初始武器：HK USP 手枪
 *
 * 设计文档要求：
 * - 力量需求：10点（tier1默认力量需求即为10）
 * - 初始伤害：1-6点（tier=1 时 min=tier=1，dmgBaseMul=3 使 max=3*(1+1)=6）
 * - 攻击距离：2格（RCH=2）
 * - 成长：1-2点（minUpgrade=lvl，maxUpgrade=lvl*1*(1+1)=2lvl）
 *
 * 专属贴图暂未绘制，暂时引用 M9 的贴图，后续完善方案会替换。
 */
public class HKUSP extends LongRange {

	{
		image = ItemSpriteSheet.M9; //TODO 专属贴图未绘制，暂时引用M9，后续替换

		tier = 1;
		RCH = 2;
		dmgBaseMul = 3;
		tag = Tag.HG;
	}

}
