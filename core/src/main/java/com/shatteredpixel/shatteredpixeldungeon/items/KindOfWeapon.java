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

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GunSwap;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

abstract public class KindOfWeapon extends EquipableItem {

	protected static final float TIME_TO_EQUIP = 1f;

	protected String hitSound = Assets.Sounds.HIT;
	protected float hitSoundPitch = 1f;

	//武器枪种标签，供角色天赋等系统按武器类别进行判定
	public enum Tag {
		HG	//手枪（Handgun）
	}

	public boolean hasTag( Tag tag ){
		return false;
	}

	@Override
	public boolean isEquipped( Hero hero ) {
		return hero.belongings.weapon() == this || hero.belongings.secondWep() == this;
	}

	//装备后恢复该武器原有的快捷栏位
	private void restoreQuickslot(){
		int slot = Dungeon.quickslot.getSlot( this );
		if (slot != -1) {
			Dungeon.quickslot.setSlot( slot, this );
			updateQuickslot();
		}
	}

	//未来之星装备 HG 武器时，弹窗选择装备到主手还是副手
	@Override
	public void execute( Hero hero, String action ) {
		if (hero.subClass == HeroSubClass.FUTURE_STAR
				&& action.equals( AC_EQUIP ) && hasTag( Tag.HG )){
			usesTargeting = false;

			String primaryName = Messages.titleCase(hero.belongings.weapon != null
					? hero.belongings.weapon.trueName()
					: Messages.get(KindOfWeapon.class, "empty"));
			String secondaryName = Messages.titleCase(hero.belongings.secondWep != null
					? hero.belongings.secondWep.trueName()
					: Messages.get(KindOfWeapon.class, "empty"));
			if (primaryName.length() > 18) primaryName = primaryName.substring(0, 15) + "...";
			if (secondaryName.length() > 18) secondaryName = secondaryName.substring(0, 15) + "...";
			GameScene.show(new WndOptions(
					new ItemSprite(this),
					Messages.titleCase(name()),
					Messages.get(KindOfWeapon.class, "which_equip_msg"),
					Messages.get(KindOfWeapon.class, "which_equip_primary", primaryName),
					Messages.get(KindOfWeapon.class, "which_equip_secondary", secondaryName)
			){
				@Override
				protected void onSelect(int index) {
					super.onSelect(index);
					if (index == 0 || index == 1){
						if (index == 0) doEquip(hero);
						else            equipSecondary(hero);
						restoreQuickslot();
					}
				}
			});
		} else {
			super.execute( hero, action );
		}
	}
	
	@Override
	public boolean doEquip( Hero hero ) {

        Tracker(hero);
		detachAll( hero.belongings.backpack );
		
		if (hero.belongings.weapon == null || hero.belongings.weapon.doUnequip( hero, true )) {
			
			hero.belongings.weapon = this;
			activate( hero );
			Talent.onItemEquipped(hero, this);
			ActionIndicator.updateIcon();
			updateQuickslot();
			
			cursedKnown = true;
			if (cursed) {
				equipCursed( hero );
				GLog.n( Messages.get(KindOfWeapon.class, "equip_cursed") );
			}
			
			hero.spendAndNext( TIME_TO_EQUIP );
			return true;
			
		} else {

			collect( hero.belongings.backpack );
			return false;
		}
	}

	//装备到副手槽位（未来之星专属，仅限 HG 标签武器）
	public boolean equipSecondary( Hero hero ){
		if (hero.subClass != HeroSubClass.FUTURE_STAR || !hasTag( Tag.HG )){
			return doEquip( hero );
		}

		Tracker(hero);
		detachAll( hero.belongings.backpack );

		if (hero.belongings.secondWep == null || hero.belongings.secondWep.doUnequip( hero, true )) {

			hero.belongings.secondWep = this;
			activate( hero );
			Talent.onItemEquipped(hero, this);
			ActionIndicator.updateIcon();
			updateQuickslot();

			cursedKnown = true;
			if (cursed) {
				equipCursed( hero );
				GLog.n( Messages.get(KindOfWeapon.class, "equip_cursed") );
			}

			//尽快刷出换枪按钮
			GunSwap swap = hero.buff(GunSwap.class);
			if (swap != null) {
				swap.refreshIndicator();
			}

			hero.spendAndNext( TIME_TO_EQUIP );
			return true;

		} else {

			collect( hero.belongings.backpack );
			return false;
		}
	}
    @Override
    public boolean unEquipable(Hero hero){
        // +2级允许取下被诅咒的武器
        return  super.unEquipable(hero) || hero.pointsInTalent(Talent.GSH18_DOCTOR_INTUITION) >= 2;
    }

	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		boolean second = hero.belongings.secondWep == this;

		if (second){
			//先置空腾出容量位，物品才能收回满背包；失败时回滚
			hero.belongings.secondWep = null;
		}

		if (super.doUnequip( hero, collect, single )) {

			if (!second){
				hero.belongings.weapon = null;
			}
			return true;

		} else {

			if (second){
				hero.belongings.secondWep = this;
			}
			return false;

		}
	}

	public int min(){
		return min(buffedLvl());
	}

	public int max(){
		return max(buffedLvl());
	}

	abstract public int min(int lvl);
	abstract public int max(int lvl);

	public int damageRoll( Char owner ) {
        int dmg = Random.NormalIntRange( min(), max() );
        if (owner instanceof Hero) {
            Hero heroA = (Hero)owner;
            Char enemyA = heroA.enemy();
            if (enemyA instanceof Mob && ((Mob) enemyA).surprisedBy(heroA)) {
                if (hero.hasTalent(Talent.Type56Two_Damage)) {
                    int diff = max() - min();
                    dmg = Random.NormalIntRange(min() + Math.round(0.2f * hero.pointsInTalent(Talent.Type56Two_Damage) * diff), max());
                }
            }
        }
		return dmg;
	}
	public float mulByDelay( Char owner ){
		float delay = delayFactor( owner );
		if (delay > 2F)
			return delay/2F;
		if (delay < 0.5F)
			return delay*2F;
		return 1F;
	}
	public float accuracyFactor( Char owner ) {
		return 1f;
	}
	
	public float delayFactor(Char owner ) {
		return 1f;
	}

	public int STRNeed(int extraSTR){
		return extraSTR * (extraSTR + 1) / 2;
	}
	public int reachFactor( Char owner ){
		return 1;
	}
	
	public boolean canReach( Char owner, int target){
		if (Dungeon.level.distance( owner.pos, target ) > reachFactor(owner)){
			return false;
		} else {
			boolean[] passable = BArray.not(Dungeon.level.solid, null);
			for (Char ch : Actor.chars()) {
				if (ch != owner) passable[ch.pos] = false;
			}
			
			PathFinder.buildDistanceMap(target, passable, reachFactor(owner));
			
			return PathFinder.distance[owner.pos] <= reachFactor(owner);
		}
	}

	public int defenseFactor( Char owner ) {
		return 0;
	}
	
	public int proc( Char attacker, Char defender, int damage ) {
		return damage;
	}

	public void hitSound( float pitch ){
		Sample.INSTANCE.play(hitSound, 1, pitch * hitSoundPitch);
	}
	
}
