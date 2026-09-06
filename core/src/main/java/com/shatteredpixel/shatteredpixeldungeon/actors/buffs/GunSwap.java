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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

//未来之星专属被动：主武器与副手（HG）武器之间的免费即时切换指示器
public class GunSwap extends Buff implements ActionIndicator.Action {

	{
		type = buffType.NEUTRAL;
	}

	//副手存在且当前为未来之星时方可换枪（主手允许为空，即副手升为主手）
	public boolean canSwap() {
		if (!(target instanceof Hero)) return false;
		Hero hero = (Hero) target;
		if (hero.subClass != HeroSubClass.FUTURE_STAR) return false;
		if (hero.belongings.secondWep == null) return false;
		//交换后副手槽 = 原主手武器，副手槽仅限 HG，故原主手须为 HG 或为空
		KindOfWeapon main = hero.belongings.weapon;
		return main == null || main.hasTag( KindOfWeapon.Tag.HG );
	}

	@Override
	public boolean act() {
		if (!(target instanceof Hero) || ((Hero) target).subClass != HeroSubClass.FUTURE_STAR) {
			//职业条件不满足时移除自身（转职不可逆，此为兜底）
			detach();
			return true;
		}

		if (!canSwap()) {
			if (ActionIndicator.checkAction(this)) {
				ActionIndicator.clearAction(this);
			}
		} else if (!ActionIndicator.checkAction(this) && ActionIndicator.actionIsFree()) {
			//仅在指示器空闲时占用，避免与天狼星心脏等其他动作互抢
			ActionIndicator.setAction(this);
		}

		spend(TICK);
		return true;
	}

	//装备/卸下副手后由 KindOfWeapon 调用，尽快刷出换枪按钮
	public void refreshIndicator() {
		if (canSwap() && !ActionIndicator.checkAction(this) && ActionIndicator.actionIsFree()) {
			ActionIndicator.setAction(this);
		}
	}

	@Override
	public void detach() {
		super.detach();
		ActionIndicator.clearAction(this);
	}

	@Override
	public int icon() {
		return BuffIndicator.NONE; //不在buff栏显示
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	//ActionIndicator.Action 接口实现
	@Override
	public String actionName() {
		return Messages.get(this, "action_name");
	}

	@Override
	public Image actionIcon() {
		if (target instanceof Hero && ((Hero) target).belongings.weapon != null) {
			return new ItemSprite(((Hero) target).belongings.weapon);
		}
		Image icon = new ItemSprite(new Item(){{ image = ItemSpriteSheet.WEAPON_HOLDER; }});
		icon.tint(0x888888);
		return icon;
	}

	@Override
	public void doAction() {
		if (!canSwap()) return;

		Hero hero = (Hero) target;
		//防御性校验：交换后副手槽必须仍为 HG（或空）
		KindOfWeapon main = hero.belongings.weapon;
		if (main != null && !main.hasTag( KindOfWeapon.Tag.HG )) return;

		//用裸字段交换：LostInventory 下访问器会返回 null，导致交换失败
		KindOfWeapon temp = hero.belongings.weapon;
		hero.belongings.weapon = hero.belongings.secondWep;
		hero.belongings.secondWep = temp;

		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.UNLOCK);

		ActionIndicator.setAction(this); //重建图标（主手已变化）
		Item.updateQuickslot();
		AttackIndicator.updateState();
	}
}
