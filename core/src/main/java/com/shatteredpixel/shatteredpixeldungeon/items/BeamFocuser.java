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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon.Tag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

//隼（超级小爱）配件：集束聚焦器
//仅能贴附在RF（步枪）标签的武器上，使其攻击变为解离法杖式的贯穿激光攻击（见 BeamFocusAttack）
//贴附时像战士纹章一样从背心中选择武器；贴附后永久绑定：无法摘下、武器不会被摧毁或消失、不会从骸骨中开出、配件本身无法出售
public class BeamFocuser extends Item {

	public static final String AC_ATTACH = "ATTACH";

	{
		image = ItemSpriteSheet.BEAM_FOCUSER;

		unique = true;
		bones = false;
		stackable = false;
		cursedKnown = levelKnown = true;

		defaultAction = AC_ATTACH;
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_ATTACH );
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {
		super.execute( hero, action );

		if (action.equals( AC_ATTACH )) {
			curItem = this;
			GameScene.selectItem( weaponSelector );
		}
	}

	protected static WndBag.ItemSelector weaponSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get( BeamFocuser.class, "prompt" );
		}

		@Override
		public Class<? extends Bag> preferredBag() {
			return Belongings.Backpack.class;
		}

		@Override
		public boolean itemSelectable( Item item ) {
			//仅列出RF标签的武器（是否被诅咒在确认时校验）
			return item instanceof Weapon && ((Weapon) item).hasTag( Tag.RF );
		}

		@Override
		public void onSelect( Item item ) {
			if (item == null) {
				return;
			}
			if (!(item instanceof Weapon)) {
				return;
			}

			BeamFocuser focuser = (BeamFocuser) curItem;
			Weapon wep = (Weapon) item;

			//诅咒未知时不能贴附（不揭示诅咒）
			if (!wep.cursedKnown) {
				GLog.w( Messages.get( BeamFocuser.class, "unknown_weapon" ) );
				return;
			}

			//不可贴附在被诅咒的武器上
			if (wep.cursed) {
				GLog.w( Messages.get( BeamFocuser.class, "cursed" ) );
				return;
			}

			//已经贴附过
			if (wep.beamFocused) {
				GLog.w( Messages.get( BeamFocuser.class, "already" ) );
				return;
			}

			wep.beamFocused = true;
			//贴附后武器不会被摧毁和消失，也无法从骸骨中开出
			wep.bones = false;
			wep.keptThoughLostInvent = true;

			focuser.detach( Dungeon.hero.belongings.backpack );

			Dungeon.hero.sprite.operate( Dungeon.hero.pos );
			Sample.INSTANCE.play( Assets.Sounds.UNLOCK );

			GLog.p( Messages.get( BeamFocuser.class, "succeed", wep.name() ) );
			GLog.i( Messages.get( BeamFocuser.class, "attached_hint" ) );
			updateQuickslot();
		}
	};

	@Override
	public int value() {
		//无法被出售（unique且不可堆叠，value为0）
		return 0;
	}
}
