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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.shatteredpixel.shatteredpixeldungeon.items.weapon;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.FocusRay;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

//磁轨加速弹：被贴附的RF武器可切换默认攻击与射线攻击模式
//射线攻击模式下改为解离法杖式的贯穿激光攻击（实现挂接于 Hero.onAttackComplete）
//伤害、攻击速度、攻击距离均直接读取贴附的武器
public class BeamFocusAttack {

	//武器处于诅咒状态时，造成伤害有概率使自己也受到等量伤害
	private static final float CURSE_BACKFIRE_CHANCE = 0.25f;

	//射线模式基础伤害为武器伤害的30%（整十倍率，取整稳定不出小数）
	private static final float BASE_RAY_DMG_MULT = 0.30f;

	//更换弹夹按钮（切换默认攻击模式 ↔ 射线攻击模式）
	public static final String AC_SWITCH_MODE = "SWITCH_MODE";

	//更换弹夹耗时（回合）
	private static final float TIME_TO_SWAP = 3f;

	//射线伤害倍率：基础30%，超级小爱T3天赋「全能战术」+1/+2/+3 提升为45%/60%/80%
	public static float rayDamageMultiplier( Hero hero ) {
		switch (hero.pointsInTalent( Talent.OMNI_TACTICS )) {
			case 1:  return 0.45f;
			case 2:  return 0.60f;
			case 3:  return 0.80f;
			default: return BASE_RAY_DMG_MULT;
		}
	}

	//当前是否处于射线攻击模式（贴附且 rayMode 开启）
	public static boolean isFocusedWeapon( Hero hero ) {
		return hero.belongings.weapon() instanceof Weapon
			&& ((Weapon) hero.belongings.weapon()).beamFocused
			&& ((Weapon) hero.belongings.weapon()).beamRayMode;
	}

	//为贴附了磁轨加速弹的武器追加"更换弹夹"按钮
	public static void addActions( ArrayList<String> actions, Weapon wep ) {
		if (wep.beamFocused) {
			actions.add( AC_SWITCH_MODE );
		}
	}

	//处理"更换弹夹"按钮：切换模式并刷新贴图特效，耗时3回合
	public static void executeAction( Weapon wep, Hero hero, String action ) {
		if (!action.equals( AC_SWITCH_MODE ) || !wep.beamFocused) return;

		wep.beamRayMode = !wep.beamRayMode;

		if (wep.beamRayMode) {
			GLog.p( Messages.get( BeamFocusAttack.class, "switched_to_ray",
					Math.round( rayDamageMultiplier( hero ) * 100 ) ) );
		} else {
			GLog.p( Messages.get( BeamFocusAttack.class, "switched_to_normal" ) );
		}

		//刷新物品贴图（emitter 会随 beamRayMode 变化）
		if (wep.isEquipped( hero )) {
			hero.sprite.operate( hero.pos );
		}
		Sample.INSTANCE.play( Assets.Sounds.UNLOCK );
		wep.updateQuickslot();

		//换弹夹耗时3回合
		hero.spendAndNext( TIME_TO_SWAP );
	}

	//执行一次贯穿激光攻击，返回目标敌人是否被击中
	public static boolean attack( Hero hero, Char enemy ) {

		Weapon wep = (Weapon) hero.belongings.weapon();
		if (wep == null || !wep.beamFocused) {
			return false;
		}

		//解离式弹道：不被任何东西阻挡，贯穿到底
		Ballistica beam = new Ballistica( hero.pos, enemy.pos, Ballistica.WONT_STOP );
		//攻击距离直接读取武器
		int maxDist = Math.min( beam.dist, wep.reachFactor( hero ) );
		int endCell = beam.path.get( maxDist );

		hero.sprite.parent.add( new FocusRay( hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld( endCell ), 0.25f ) );

		boolean terrainAffected = false;

		ArrayList<Char> chars = new ArrayList<>();
		for (int c : beam.subPath( 1, maxDist )) {

			Char ch;
			if ((ch = Actor.findChar( c )) != null) {
				chars.add( ch );
			}

			//解离逻辑：焚烧弹道上的可燃地形
			if (Dungeon.level.flammable[c]) {
				Dungeon.level.destroy( c );
				GameScene.updateMap( c );
				terrainAffected = true;
			}
		}

		if (terrainAffected) {
			Dungeon.observe();
		}

		int totalDealt = 0;
		for (Char ch : chars) {

			//伤害直接读取武器（磁轨加速弹射线模式基础30%，受「全能战术」天赋提升），不进行命中与防御减免判定（解离法杖逻辑）
			int dmg = hero.attackProc( ch, wep.damageRoll( hero ) );
			dmg = Math.round( dmg * rayDamageMultiplier( hero ) );
			dmg = Math.max( dmg, 0 );
			totalDealt += dmg;

			ch.damage( dmg, hero );

			if (dmg > 0) {
				hero.hitSound( Random.Float( 0.87f, 1.15f ) );
				ch.sprite.bloodBurstA( hero.sprite.center(), dmg );
				ch.sprite.flash();
			}
		}

		//贴附后武器被诅咒：造成伤害有概率使自己也受到相等的伤害
		if (wep.cursed && totalDealt > 0 && Random.Float() < CURSE_BACKFIRE_CHANCE) {
			hero.damage( totalDealt, (Item) wep );
			hero.sprite.showStatus( CharSprite.NEGATIVE, Messages.get( BeamFocusAttack.class, "backfire") );
		}

		return chars.contains( enemy );
	}
}
