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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

//未来之星专属被动：主武器与副手（HG）武器之间的免费即时切换指示器
public class GunSwap extends EquipmentBuff implements ActionIndicator.Action {

	{
		type = buffType.NEUTRAL;
	}
	private KindOfWeapon weapon() {
		return getEquipment(KindOfWeapon.class);
	}
	@Override
	public void fx( boolean on ) {
		if (on)
			ActionIndicator.setAction(this);
		else
			ActionIndicator.clearAction(this);
	}
	//ActionIndicator.Action 接口实现
	@Override
	public String actionName() {
		return Messages.get(this, "action_name");
	}

	@Override
	public Image actionIcon() {
		if (item != null) {
			return new ItemSprite(item);
		}
		Image icon = new ItemSprite(new Item(){{ image = ItemSpriteSheet.WEAPON_HOLDER; }});
		icon.tint(0x888888);
		return icon;
	}
	//常态占格子
	@Override
	public void doAction() {
		Hero hero = (Hero) target;

		KindOfWeapon weapon = weapon();
		item = hero.belongings.weapon;
		hero.belongings.weapon = weapon;

		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.UNLOCK);

		ActionIndicator.setAction(this); //重建图标（主手已变化）
		Item.updateQuickslot();
		AttackIndicator.updateState();
	}
	@Override
	public int bgColor(){
		return 0xFF99CC;
	}
}
