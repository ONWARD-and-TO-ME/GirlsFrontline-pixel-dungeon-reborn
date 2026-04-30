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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.FairyRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlacksmithSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndDEL;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class DEL extends NPC {
	
	{
		spriteClass = BlacksmithSprite.class;

		properties.add(Property.IMMOVABLE);
	}
    public int WorkLoad;
    public void WorkLoadUsed(int num){
        WorkLoad -= num;
    }
    public boolean add;
    private static final String WORKLOAD = "WORKLOAD";
    private static final String ADD = "ADD_SIGHT";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(WORKLOAD, WorkLoad);
        bundle.put(ADD, add);
    }
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        WorkLoad = bundle.getInt(WORKLOAD);
        add      = bundle.getBoolean(ADD);
    }
	private boolean seenBefore = false;
	@Override
	protected boolean act() {
		if (!seenBefore && Dungeon.level.heroFOV[pos]){
            if (!add) {
                add = true;
                WorkLoad += 20;
                Notes.add(Notes.Landmark.DEL);
                if (Dungeon.level instanceof RegularLevel){
                    for (Room room : ((RegularLevel) Dungeon.level).rooms()){
                        if (room instanceof FairyRoom) {
                            ((RegularLevel) Dungeon.level).fairyRoom = ((FairyRoom) room).inside;
                            for (int i : ((FairyRoom) room).inside)
                                Dungeon.level.sharedVision[i] = true;
                        }
                    }
                }
            }
            Mission mission = Buff.affect(Dungeon.hero, Mission.class);
            if (mission.noMission()) {
                if (WorkLoad > 0)
                    yellGood(Messages.get(this, "greetings", Dungeon.hero.name()));
                else
                    yellGood(Messages.get(this, "greetingsB", Dungeon.hero.name()));
            }
            else if (mission.finish()) {
                yellGood( Messages.get(this, "finish", Dungeon.hero.name()) );
            }
            else
                yellNormal( Messages.get(this, "working", Dungeon.hero.name() ) );
            seenBefore = true;
		}
		return super.act();
	}
	
	@Override
	public boolean interact(Char c) {
		
		sprite.turnTo( pos, c.pos );

		if (c != Dungeon.hero){
			return true;
		}

        if (WorkLoad > 0)
            Game.runOnRenderThread(new Callback() {
                @Override
                public void call() {
                    selectMission();
                }
            });
        else
            yellNormal(Messages.get(this, "noWorkLoad"));
		return true;
	}
    private void selectMission(){

        String[] options;
        if (Dungeon.hero.hasTalent(Talent.Type56_23V4) && Dungeon.hero.heroClass != HeroClass.TYPE561)
            options = new String[5];
        else
            options = new String[4];
        for (int i = 0; i < options.length; i++){
            options[i] = getMissionName(i);
        }

        boolean[] enable = new boolean[5];
        for (Item i : Dungeon.hero.belongings){
            if (!i.isEquipped(Dungeon.hero) && (!i.isIdentified() || i.cursedKnown && i.cursed) && !(i instanceof CorpseDust) && WorkLoad >= 1)
                enable[0] = true;
            if (i.isUpgradable() && i.level() > 0 && i.levelKnown  && i.overLoad == Item.OverLoad.NONE &&
                    ( i.isEquipped(Dungeon.hero) && ((EquipableItem) i).unEquipable(Dungeon.hero) ||
                            !i.isEquipped(Dungeon.hero) && !(i instanceof BrokenSeal) ) && WorkLoad >= getMissionWorkLoad(1))
                enable[1] = true;
            if (i.isEquipped(Dungeon.hero) && i.cursed && WorkLoad >= getMissionWorkLoad(2))
                enable[2] = true;
            if (i instanceof MissileWeapon && WorkLoad >= getMissionWorkLoad(3))
                enable[3] = true;
        }
        enable[4] = WorkLoad >= getMissionWorkLoad(4);
        GameScene.show(new WndOptions(DEL.this.sprite(),
                Messages.titleCase(DEL.this.name()),
                Messages.get(DEL.class, "body", DEL.this.WorkLoad),
                options) {
            protected void onSelect(int index) {
                super.onSelect(index);
                if (index != -1) {
                    GameScene.show(new WndDEL(DEL.this, index));
                }
            }
            @Override
            protected boolean enabled(int index) {
                return enable[index];
            }
        });
    }

    public static String getMissionName(int mission){
        switch (mission){
            case 0: default:return "减毒";
            case 1:return "过载";
            case 2:return "脱下红底装备";
            case 3:return "仿制投掷武器";
            case 4:return "制作剪刀";
        }
    }
    public static int getMissionWorkLoad(int mission){
        switch (mission){
            case 0: default:return 4;
            case 1:return 6;
            case 2:return 12;
            case 3: case 4:return 1;
        }
    }
    public static int getMissionGold(int mission){
        switch (mission){
            case 0: default: return  (int) (100 * (Statistics.deepestFloor + Dungeon.depth*2)/3F);
            case 1: return  (int) (80 * (Statistics.deepestFloor + Dungeon.depth*2)/3F);
            case 2: return  100*(Statistics.deepestFloor - Dungeon.depth + 1);
            case 3: return 20;
            case 4: return 100;

        }
    }
    public static int getMissionTimes(int mission){
        switch (mission){
            case 0:return 600;
            case 2:return 150;
            case 1:case 3: case 4:return 20;
            default:return 100;
        }
    }
    public static String getMissionBody(int mission){
        switch (mission){
            case 0:return Messages.get(DEL.class, "notice_removeCurse", getMissionWorkLoad(mission), getMissionGold(mission), getMissionTimes(mission), 1, 200, 50);
            case 1:return Messages.get(DEL.class, "notice_overLoad", getMissionWorkLoad(mission), getMissionGold(mission), getMissionTimes(mission));
            case 2:return Messages.get(DEL.class, "notice_unEquip", getMissionWorkLoad(mission), getMissionGold(mission), getMissionTimes(mission));
            case 3:return Messages.get(DEL.class, "notice_newInstance", getMissionWorkLoad(mission), getMissionGold(mission), getMissionTimes(mission));
            case 4:return Messages.get(DEL.class, "notice_clipper", getMissionWorkLoad(mission), getMissionGold(mission), getMissionTimes(mission), 15);
            default:return "404 NOT FOUND!!!";
        }
    }

	@Override
	public int defenseSkill( Char enemy ) {
		return INFINITE_EVASION;
	}
	
	@Override
	public void damage( int dmg, Object src ) {
	}

	@Override
	public void add( Buff buff ) {
	}

	@Override
	public boolean reset() {
		return true;
	}

    public static class Mission extends Buff{
        {
            revivePersists = true;
            type = buffType.POSITIVE;
        }
        private ArrayList<Item> items = new ArrayList<>();
        private ArrayList<Integer> times = new ArrayList<>();
        private ArrayList<Item> drops = new ArrayList<>();
        @Override
        public boolean act(){
            spend(TICK);
            if (items == null)
                items = new ArrayList<>();
            if (drops == null)
                drops = new ArrayList<>();
            if (finish()) {
                new DEL().yellGood( Messages.get(DEL.class, "finish", Dungeon.hero.name() ) );
            }
            if (items.isEmpty() && drops.isEmpty()) {
                spend(TICK);
                if (items.isEmpty() && drops.isEmpty()) {
                    detach();
                    return true;
                }
            }
            else if (!items.isEmpty())
                times.set(0, times.get(0)-1);
            while (!items.isEmpty() && times.get(0) <= 0) {
                times.remove(0);
                drops.add(items.remove(0));
            }
            return true;
        }
        public void addMission(int time, Item item){
            addMission(time, new Item[]{item});
        }
        public void addMission(int time, Item... item){
            times.add(time);
            items.add(item[0]);
            for (int i  = 1; i < item.length; i++){
                times.add(0);
                items.add(item[i]);
            }
        }
        public boolean noMission(){
            return items.isEmpty() && drops.isEmpty();
        }
        public boolean finish(){
            if (!(Dungeon.level instanceof RegularLevel))
                return false;
            if (drops == null || drops.isEmpty())
                return false;
            for (Room room : ((RegularLevel) Dungeon.level).rooms()) {
                if (room instanceof FairyRoom && ((FairyRoom) room).Pos() != -1) {
                    while (!drops.isEmpty()){
                        ((FairyRoom) room).placeItem(drops.remove(0));
                    }
                    return true;
                }
            }
            return false;
        }
        public int icon() {
            return BuffIndicator.INVISIBLE;
        }
        public String desc(){
            String desc = "";
            int i = 0;
            for (Item item : items){
                desc += "\n" + item.name();
                int time = times.get(i);
                if (time != 0)
                    desc += time;
                i++;
            }
            for (Item item : drops){
                desc += "\n" + item.name();
            }
            return desc;
        }
        private static final String ITEM	= "item";
        private static final String TIME    = "time";
        private static final String DROP    = "drop";

        @Override
        public void storeInBundle( Bundle bundle ) {
            super.storeInBundle( bundle );
            bundle.put(ITEM, items);
            int[] time = new int[times.size()];
            for (int i = 0 ; i < times.size() ; i++)
                time[i] = times.get(i);
            bundle.put(TIME, time);
            bundle.put(DROP, drops);
        }

        @Override
        public void restoreFromBundle( Bundle bundle ) {
            super.restoreFromBundle( bundle );
            items = bundle.getBundlableArrayList(ITEM, Item.class);
            times = bundle.getIntArrayList(TIME);
            drops = bundle.getBundlableArrayList(DROP, Item.class);
        }
    }
    public static class RemovingCurse extends FlavourBuff {
        {
            revivePersists = true;
            type = buffType.NEUTRAL;
        }
        private int equip;
        private int pos = -1;
        public int icon() {
            return BuffIndicator.INVISIBLE;
        }
        @Override
        public String toString() {
            return "强行解除剩余时间";
        }
        @Override
        public String desc(){
            return String.valueOf(visualcooldown());
        }
        public void Remember(int equip, int pos){
            this.equip = equip;
            this.pos = pos;
        }
        @Override
        public boolean act(){
            if (pos == -1)
                pos = target.pos;
            else if (target.pos != pos || visualcooldown() < 4)
                detach();
            spend(TICK);
            return true;
        }
        public void detach() {
            if (visualcooldown() > 5)
                GLog.w("fail");
            else {
                EquipableItem item;
                switch (equip){
                    case 0: default:item = Dungeon.hero.belongings.weapon;break;
                    case 1:item = Dungeon.hero.belongings.armor;break;
                    case 2:item = Dungeon.hero.belongings.artifact;break;
                    case 3:item = Dungeon.hero.belongings.misc;break;
                    case 4:item = Dungeon.hero.belongings.ring;break;
                    case 5:item = Dungeon.hero.belongings.secArmor;break;
                }
                item.setUnEquipable();
                item.doDrop((Hero) target);
            }
            super.detach();
        }
        private static final String Equip = "EQUIP";
        private static final String Hero_Pos = "Hero_Pos";
        @Override
        public void storeInBundle( Bundle bundle ) {
            super.storeInBundle( bundle );
            bundle.put(Equip, equip);
            bundle.put(Hero_Pos, pos);
        }
        @Override
        public void restoreFromBundle( Bundle bundle ) {
            super.restoreFromBundle( bundle );
            equip = bundle.getInt(Equip);
            pos = bundle.getInt(Hero_Pos);
        }
    }
}
