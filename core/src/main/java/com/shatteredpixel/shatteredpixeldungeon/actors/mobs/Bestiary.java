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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.SmokeBomb;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DEL;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.GeminiMissile;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.GeminiShield;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Noel;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NoelShopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.PrismaticImage;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RatKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.AlarmTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BlazingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ConfusionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CorrosionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CursingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisarmingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DistortionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlashingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlockTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GatewayTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.OozeTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ShockingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.StormTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TeleportationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TenguDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ToxicTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WarpingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WeakeningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.BlandfruitBush;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Dreamfoil;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

public enum Bestiary {

    REGIONAL,
    BOSSES,
    UNIVERSAL,
    RARE,
    QUEST,
    NEUTRAL,
    ALLY,
    TRAP,
    PLANT;

    //tracks whether an entity has been encountered
    private final LinkedHashMap<Class<?>, Boolean> seen = new LinkedHashMap<>();
    //tracks enemy kills, trap activations, plant tramples, or just sets to 1 for seen on allies
    private final LinkedHashMap<Class<?>, Integer> encounterCount = new LinkedHashMap<>();

    //should only be used when initializing
    private void addEntities(Class<?>... classes ){
        for (Class<?> cls : classes){
            seen.put(cls, false);
            encounterCount.put(cls, 0);
        }
    }

    public Collection<Class<?>> entities(){
        return seen.keySet();
    }

    public String title(){
        return Messages.get(Journal.class, name() + ".title");
    }

    public int totalEntities(){
        return seen.size();
    }

    public int totalSeen(){
        int seenTotal = 0;
        for (boolean entitySeen : seen.values()){
            if (entitySeen) seenTotal++;
        }
        return seenTotal;
    }

    static {

        REGIONAL.addEntities(Rat.class, Snake.class, Gnoll.class, Crab.class, Swarm.class,
                Golyat.class, Thief.class, Shaman.RedShaman.class, Shaman.BlueShaman.class, Shaman.PurpleShaman.class, Mg5.class, GolyatFactory.class, GolyatFactory.FactoryGolyat.class,
                Bat.class, Brute.class, Spinner.class, Nemeum.class,
                Ghoul.class, Elemental.FireElemental.class, Elemental.FrostElemental.class, Elemental.ShockElemental.class, Warlock.class, Monk.class, Golem.class,
                Guard.class, Dragun.class, Jupiter.class,
                Cyclops.class, Hydra.class, Succubus.class,
                RipperDemon.class, DemonSpawner.class);

        BOSSES.addEntities(Goo.class,
                Tengu.class, Elphelt.class,
                Pylon.class, DM300.class,
                DwarfKing.class,
                YogDzewa.Larva.class,
                YogFist.BurningFist.class, YogFist.SoiledFist.class,
                YogFist.RottingFist.class, YogFist.RustedFist.class,
                YogFist.BrightFist.class, YogFist.DarkFist.class,
                YogDzewa.class);

        UNIVERSAL.addEntities(Wraith.class, Piranha.class,
                Mimic.class, GoldenMimic.class,
                Statue.class, GuardianTrap.Guardian.class,
                SentryRoom.Sentry.class);

        RARE.addEntities(RatXMAS.class, Albino.class, GnollSWAP.class,
                Bandit.class, GolyatPlus.class,
                SpinnerCat.class,
                Elemental.ChaosElemental.class, Senior.class,
                Typhoon.class,
                CrystalMimic.class, ArmoredStatue.class,
                ArmoredBrute.class);

        QUEST.addEntities(FetidRat.class, GnollTrickster.class, GreatCrab.class,
                Elemental.NewbornFireElemental.class, RotLasher.class, RotHeart.class);

        NEUTRAL.addEntities(Ghost.class, DEL.class, RatKing.class,
                Shopkeeper.class, Wandmaker.class, Noel.class,
                Blacksmith.class, Imp.class,
                NoelShopkeeper.class,
                Sheep.class, Bee.class);

        ALLY.addEntities(MirrorImage.class, PrismaticImage.class,
                DriedRose.GhostHero.class,
                GeminiShield.class, GeminiMissile.class,
                WandOfWarding.Ward.class, WandOfWarding.Ward.WardSentry.class,
                WandOfLivingEarth.EarthGuardian.class,
                ShadowClone.ShadowAlly.class, SmokeBomb.NinjaLog.class, SpiritHawk.HawkAlly.class);

        TRAP.addEntities(WornDartTrap.class, PoisonDartTrap.class, DisintegrationTrap.class, GatewayTrap.class,
                ChillingTrap.class, BurningTrap.class, ShockingTrap.class, AlarmTrap.class, GrippingTrap.class, TeleportationTrap.class, OozeTrap.class,
                FrostTrap.class, BlazingTrap.class, StormTrap.class, GuardianTrap.class, FlashingTrap.class, WarpingTrap.class,
                ConfusionTrap.class, ToxicTrap.class, CorrosionTrap.class,
                FlockTrap.class, SummoningTrap.class, WeakeningTrap.class, CursingTrap.class,
                GeyserTrap.class, ExplosiveTrap.class, RockfallTrap.class,
                DistortionTrap.class, DisarmingTrap.class, GrimTrap.class);

        PLANT.addEntities(Rotberry.class, Sungrass.class, Fadeleaf.class, Icecap.class,
                Firebloom.class, Sorrowmoss.class, Swiftthistle.class, Blindweed.class,
                Stormvine.class, Earthroot.class, Dreamfoil.class, Starflower.class,
                BlandfruitBush.class,
                WandOfRegrowth.Dewcatcher.class, WandOfRegrowth.Seedpod.class, WandOfRegrowth.Lotus.class);

    }

    //some mobs and traps have different internal classes in some cases, so need to convert here
    private static final HashMap<Class<?>, Class<?>> classConversions = new HashMap<>();
    static {
        classConversions.put(CorpseDust.DustWraith.class,      Wraith.class);

        classConversions.put(TenguDartTrap.class,              PoisonDartTrap.class);

        classConversions.put(DwarfKing.DKGhoul.class,          Ghoul.class);
        classConversions.put(DwarfKing.DKWarlock.class,        Warlock.class);
        classConversions.put(DwarfKing.DKMonk.class,           Monk.class);
        classConversions.put(DwarfKing.DKGolem.class,          Golem.class);

        classConversions.put(YogDzewa.YogRipper.class,         RipperDemon.class);
    }

    public static boolean isSeen(Class<?> cls){
        if (DeviceCompat.isDebug() && Dungeon.isChallenged(Challenges.TEST_MODE))
            return true;
        for (Bestiary cat : values()) {
            if (cat.seen.containsKey(cls)) {
                return cat.seen.get(cls);
            }
        }
        return false;
    }

    public static void setSeen(Class<?> cls){
        if(Dungeon.isChallenged(Challenges.TEST_MODE))
            return;
        if (classConversions.containsKey(cls)){
            cls = classConversions.get(cls);
        }
        for (Bestiary cat : values()) {
            if (cat.seen.containsKey(cls) && !cat.seen.get(cls)) {
                cat.seen.put(cls, true);
                Journal.saveNeeded = true;
            }
        }
    }

    public static int encounterCount(Class<?> cls) {
        for (Bestiary cat : values()) {
            if (cat.encounterCount.containsKey(cls)) {
                return cat.encounterCount.get(cls);
            }
        }
        return 0;
    }

    //used primarily when bosses are killed and need to clean up their minions
    public static boolean skipCountingEncounters = false;

    public static void countEncounter(Class<?> cls){
        if(Dungeon.isChallenged(Challenges.TEST_MODE))
            return;
        countEncounters(cls, 1);
    }

    public static void countEncounters(Class<?> cls, int encounters){
        if (skipCountingEncounters){
            return;
        }
        if (classConversions.containsKey(cls)){
            cls = classConversions.get(cls);
        }
        for (Bestiary cat : values()) {
            if (cat.encounterCount.containsKey(cls) && cat.encounterCount.get(cls) != Integer.MAX_VALUE){
                cat.encounterCount.put(cls, cat.encounterCount.get(cls)+encounters);
                if (cat.encounterCount.get(cls) < -1_000_000_000){ //to catch cases of overflow
                    cat.encounterCount.put(cls, Integer.MAX_VALUE);
                }
                Journal.saveNeeded = true;
            }
        }
    }

    private static final String BESTIARY_CLASSES    = "bestiary_classes";
    private static final String BESTIARY_SEEN       = "bestiary_seen";
    private static final String BESTIARY_ENCOUNTERS = "bestiary_encounters";

    public static void store( Bundle bundle ){

        ArrayList<Class<?>> classes = new ArrayList<>();
        ArrayList<Boolean> seen = new ArrayList<>();
        ArrayList<Integer> encounters = new ArrayList<>();

        for (Bestiary cat : values()) {
            for (Class<?> entity : cat.entities()) {
                if (cat.seen.get(entity) || cat.encounterCount.get(entity) > 0){
                    classes.add(entity);
                    seen.add(cat.seen.get(entity));
                    encounters.add(cat.encounterCount.get(entity));
                }
            }
        }

        Class<?>[] storeCls = new Class[classes.size()];
        boolean[] storeSeen = new boolean[seen.size()];
        int[] storeEncounters = new int[encounters.size()];

        for (int i = 0; i < storeCls.length; i++){
            storeCls[i] = classes.get(i);
            storeSeen[i] = seen.get(i);
            storeEncounters[i] = encounters.get(i);
        }

        bundle.put( BESTIARY_CLASSES, storeCls );
        bundle.put( BESTIARY_SEEN, storeSeen );
        bundle.put( BESTIARY_ENCOUNTERS, storeEncounters );

    }

    public static void restore( Bundle bundle ){

        if (bundle.contains(BESTIARY_CLASSES)
                && bundle.contains(BESTIARY_SEEN)
                && bundle.contains(BESTIARY_ENCOUNTERS)){
            Class<?>[] classes = bundle.getClassArray(BESTIARY_CLASSES);
            boolean[] seen = bundle.getBooleanArray(BESTIARY_SEEN);
            int[] encounters = bundle.getIntArray(BESTIARY_ENCOUNTERS);

            for (int i = 0; i < classes.length; i++){
                for (Bestiary cat : values()){
                    if (cat.seen.containsKey(classes[i])){
                        cat.seen.put(classes[i], seen[i]);
                        cat.encounterCount.put(classes[i], encounters[i]);
                    }
                }
            }
        }

    }

    public static ArrayList<Class<? extends Mob>> getMobRotation( int depth ) {

		ArrayList<Class<? extends Mob>> mobs = getRotation( depth );
		addRareMobs(depth, mobs);
		swapMobAlts(mobs);
		swapRareMobAlts(mobs);
		Random.shuffle(mobs);
		return mobs;
	}

	public static void addRareMobs( int depth, ArrayList<Class<?extends Mob>> rotation ){

		switch (depth){

			// Sewers
			default:
				return;
			case 4:
				if (Random.Float() < 0.025f) rotation.add(Thief.class);
				return;

			// Prison
			case 9:
				if (Random.Float() < 0.025f) rotation.add(Bat.class);
				return;

			// Caves
			case 14:
				if (Random.Float() < 0.025f) rotation.add(Ghoul.class);
				return;

			// City
			case 19:
				if (Random.Float() < 0.025f) rotation.add(Guard.class);
				return;
            case 24:
                if (Random.Float() < 0.025f) rotation.add(Succubus.class);
                return;
		}
	}

	//switches out regular mobs for their alt versions when appropriate
	private static void swapMobAlts(ArrayList<Class<?extends Mob>> rotation){
		for (int i = 0; i < rotation.size(); i++){

            Class<? extends Mob> cl = rotation.get(i);
            int j =Random.Int(100);
			if (j <Dungeon.mobRan) {
				if (cl == Rat.class) {
					cl = Albino.class;
				} else if (cl == Slime.class) {
					cl = CausticSlime.class;
				} else if (cl == Thief.class) {
					cl = Bandit.class;
				} else if (cl == Necromancer.class){
					cl = SpectralNecromancer.class;
				} else if (cl == Brute.class) {
					cl = ArmoredBrute.class;
				} else if (cl == DM200.class) {
					cl = DM201.class;
				} else if (cl == Monk.class) {
					cl = Senior.class;
				} else if (cl == Scorpio.class) {
					cl = Acidic.class;
				} else if (cl == Golyat.class) {
                    cl = GolyatPlus.class;
                }else if (cl == Gnoll.class) {
                    cl = GnollSWAP.class;
                }
			}
//            if (j==1){
//                //抢占本体概率的精英怪变种
//                if (cl == Rat.class&&
//                        (Dungeon.lockXMAS||Dungeon.isXMAS())) {
//                    cl = RatXMAS.class;
//                }
//            }
            rotation.set(i, cl);
		}
	}

	private static void swapRareMobAlts(ArrayList<Class<?extends Mob>> rotation) {
		for (int i = 0; i < rotation.size(); i++){
			Class<? extends Mob> cl = rotation.get(i);
			if (Random.Int( 500 ) == 0) {
				if (cl == Spinner.class) {
					cl = SpinnerCat.class;
				}
				rotation.set(i, cl);
			}
			if (Random.Int( 100 ) == 0) {
				if (cl == Hydra.class) {
					cl = Typhoon.class;
				}
				rotation.set(i, cl);
			}
		}
	}
    public static ArrayList<Class<? extends Mob>> NumMob(Class mob, int num){
        //输入想要加入的怪的class，以及权重数量，即可返回对应数量的怪的列表，将其addAll到getRotation的列表就等效于原本逐个填写了
        ArrayList<Class<? extends Mob>> List = new ArrayList<>();
        for(int i=0 ; i< num ; i++){
            List.add(mob);
        }
        return List;
    }


	public static ArrayList<Class<? extends Mob>> getRotation(int floor) {
        ArrayList<Class<? extends Mob>> List = new ArrayList<>();
		switch ( floor ) {
			default:
                List.addAll(NumMob(Rat.class,1));
				return List;
			case 1:
                List.addAll(NumMob(Rat.class,6));
                List.addAll(NumMob(Snake.class,2));
				return List;
            case 2:
                List.addAll(NumMob(Rat.class,3));
                List.addAll(NumMob(Gnoll.class,3));
                return List;
			case 3:
                List.addAll(NumMob(Rat.class,2));
                List.addAll(NumMob(Gnoll.class,4));
                List.addAll(NumMob(Crab.class,1));
                List.addAll(NumMob(Swarm.class,1));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Rat.class, Rat.class,
//						Gnoll.class, Gnoll.class, Gnoll.class, Gnoll.class,
//						Crab.class, Swarm.class));
			case 4: case 5:
                List.addAll(NumMob(Rat.class,1));
                List.addAll(NumMob(Gnoll.class,2));
                List.addAll(NumMob(Crab.class,3));
                List.addAll(NumMob(Swarm.class,1));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Rat.class,
//						Gnoll.class, Gnoll.class,
//						Crab.class, Crab.class, Crab.class,
//						Swarm.class));
			case 6:
                List.addAll(NumMob(Golyat.class,3));
                List.addAll(NumMob(Thief.class,1));
                List.addAll(NumMob(Swarm.class,1));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Golyat.class, Golyat.class, Golyat.class,
//						Thief.class,
//						Swarm.class));
			case 7:
                List.addAll(NumMob(Golyat.class,3));
                List.addAll(NumMob(Thief.class,1));
                List.addAll(NumMob(Shaman.random(),1));
                List.addAll(NumMob(Mg5.class,1));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Golyat.class, Golyat.class, Golyat.class,
//						Thief.class,
//						Shaman.random(),
//						Mg5.class));
			case 8:
                List.addAll(NumMob(Golyat.class,2));
                List.addAll(NumMob(Thief.class,1));
                List.addAll(NumMob(Shaman.random(),2));
                List.addAll(NumMob(Mg5.class,2));
                List.addAll(NumMob(GolyatFactory.class,1));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Golyat.class, Golyat.class, Golyat.class,
//						Thief.class,
//						Shaman.random(), Shaman.random(),
//						Mg5.class, Mg5.class));
			case 9: case 10:
                List.addAll(NumMob(Golyat.class,1));
                List.addAll(NumMob(Thief.class,1));
                List.addAll(NumMob(Shaman.random(),2));
                List.addAll(NumMob(Mg5.class,2));
                List.addAll(NumMob(GolyatFactory.class,2));
                return List;
//                return List;
//				return new ArrayList<>(Arrays.asList(
//						Golyat.class, Golyat.class, Golyat.class,
//						Thief.class,
//						Shaman.random(), Shaman.random(),
//						Mg5.class, Mg5.class, Mg5.class));
			case 11:
                List.addAll(NumMob(Bat.class,5));
                List.addAll(NumMob(Brute.class,1));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Bat.class, Bat.class, Bat.class, Bat.class, Bat.class,
//						Brute.class));
			case 12:
                List.addAll(NumMob(Bat.class,4));
                List.addAll(NumMob(Brute.class,4));
                List.addAll(NumMob(Spinner.class,2));
                List.addAll(NumMob(Nemeum.class,1));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Bat.class, Bat.class, Bat.class, Bat.class,
//						Brute.class, Brute.class, Brute.class, Brute.class,
//						Spinner.class,Spinner.class,
//						Nemeum.class));
			case 13:
                List.addAll(NumMob(Bat.class,2));
                List.addAll(NumMob(Brute.class,3));
                List.addAll(NumMob(Spinner.class,2));
                List.addAll(NumMob(Nemeum.class,2));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Bat.class, Bat.class,
//						Brute.class, Brute.class, Brute.class,
//						Spinner.class,Spinner.class,
//						Nemeum.class, Nemeum.class));
			case 14: case 15:
                List.addAll(NumMob(Bat.class,1));
                List.addAll(NumMob(Brute.class,3));
                List.addAll(NumMob(Spinner.class,2));
                List.addAll(NumMob(Nemeum.class,3));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Bat.class,
//						Brute.class, Brute.class, Brute.class,
//						Spinner.class,Spinner.class,
//						Nemeum.class,Nemeum.class,Nemeum.class));
			case 16:
                List.addAll(NumMob(Elemental.random(),3));
                List.addAll(NumMob(Ghoul.class,2));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Elemental.random(),Elemental.random(),Elemental.random(),
//						Ghoul.class,Ghoul.class));
			case 17:
                List.addAll(NumMob(Elemental.random(),2));
                List.addAll(NumMob(Ghoul.class,3));
                List.addAll(NumMob(Monk.class,2));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Elemental.random(), Elemental.random(),
//						Ghoul.class, Ghoul.class, Ghoul.class,
//						Monk.class, Monk.class));
			case 18:
                List.addAll(NumMob(Monk.class,3));
                List.addAll(NumMob(Warlock.class,2));
                List.addAll(NumMob(Golem.class,1));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Monk.class, Monk.class, Monk.class,
//						Warlock.class, Warlock.class,
//						Golem.class));
			case 19: case 20:
                List.addAll(NumMob(Monk.class,1));
                List.addAll(NumMob(Warlock.class,3));
                List.addAll(NumMob(Golem.class,2));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Monk.class,
//						Warlock.class, Warlock.class,Warlock.class,
//						Golem.class,Golem.class));
			case 21:
                List.addAll(NumMob(Guard.class,1));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Guard.class, Guard.class));
			case 22:
                List.addAll(NumMob(Guard.class,3));
                List.addAll(NumMob(Dragun.class,1));
                List.addAll(NumMob(Jupiter.class,1));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Dragun.class,
//                        Jupiter.class,
//                        Guard.class, Guard.class, Guard.class));
			case 23:
                List.addAll(NumMob(Guard.class,4));
                List.addAll(NumMob(Dragun.class,2));
                List.addAll(NumMob(Jupiter.class,1));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Dragun.class, Dragun.class,
//                        Jupiter.class,
//						Guard.class, Guard.class, Guard.class, Guard.class));
			case 24: case 25:
                List.addAll(NumMob(Guard.class,4));
                List.addAll(NumMob(Dragun.class,2));
                List.addAll(NumMob(Jupiter.class,1));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Dragun.class, Dragun.class,
//                        Jupiter.class,
//						Guard.class, Guard.class, Guard.class, Guard.class));
			case 26:
                List.addAll(NumMob(Cyclops.class,4));
                List.addAll(NumMob(Succubus.class,2));
                List.addAll(NumMob(Jupiter.class,1));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Cyclops.class, Cyclops.class,Cyclops.class, Cyclops.class,
//						Succubus.class,Succubus.class,
//						Jupiter.class));
			case 27:
                List.addAll(NumMob(Cyclops.class,3));
                List.addAll(NumMob(Succubus.class,2));
                List.addAll(NumMob(Jupiter.class,2));
                List.addAll(NumMob(Hydra.class,1));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Cyclops.class, Cyclops.class,Cyclops.class,
//						Succubus.class,Succubus.class,
//						Jupiter.class, Jupiter.class,
//						Hydra.class));
			case 28:
                List.addAll(NumMob(Cyclops.class,2));
                List.addAll(NumMob(Succubus.class,1));
                List.addAll(NumMob(Jupiter.class,1));
                List.addAll(NumMob(Hydra.class,6));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Cyclops.class, Cyclops.class,
//						Succubus.class,
//                        Jupiter.class,
//						Hydra.class, Hydra.class, Hydra.class,
//						Hydra.class, Hydra.class, Hydra.class));
			case 29: case 30:
                List.addAll(NumMob(Cyclops.class,1));
                List.addAll(NumMob(Hydra.class,9));
                return List;
//				return new ArrayList<>(Arrays.asList(
//						Cyclops.class,
//						Hydra.class, Hydra.class, Hydra.class,
//						Hydra.class, Hydra.class, Hydra.class,
//						Hydra.class, Hydra.class, Hydra.class));
		}
	}
}