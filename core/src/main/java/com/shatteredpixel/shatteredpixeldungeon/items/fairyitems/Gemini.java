package com.shatteredpixel.shatteredpixeldungeon.items.fairyitems;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.GeminiMissile;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.GeminiShield;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Geminis;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Gemini extends FairyItems {

    @Override
    public void effect(Hero hero) {
        Buff.affect(Dungeon.hero, Gemini.Contract.class).add();
    }

    public static class Contract extends Buff{
        {
            type = buffType.POSITIVE;
        }
        private ArrayList<Integer> shield = new ArrayList<>();
        private ArrayList<Integer> missile= new ArrayList<>();

        public Contract add(){
            add(maxHP(2), maxHP(0.75F));
            return this;
        }
        public Contract add(int shield, int missile){
            this.shield.add(shield);
            this.missile.add(missile);
            return this;
        }
        private static final String SHIELD = "SHIELD";
        private static final String MISSILE= "MISSILE";
        @Override
        public void storeInBundle( Bundle bundle ) {
            super.storeInBundle(bundle);
            int[] shield_HP = new int[shield.size()];
            for (int i = 0; i < shield.size(); i++){
                shield_HP[i] = shield.get(i);
            }
            bundle.put(SHIELD, shield_HP);
            int[] missile_HP = new int[missile.size()];
            for (int i = 0; i < missile.size(); i++){
                missile_HP[i] = missile.get(i);
            }
            bundle.put(MISSILE, missile_HP);

        }

        @Override
        public void restoreFromBundle( Bundle bundle ) {
            super.restoreFromBundle( bundle );
            shield = bundle.getIntArrayList(SHIELD);
            missile= bundle.getIntArrayList(MISSILE);
        }
        @Override
        public boolean act() {

            Hero hero = (Hero)target;

            Mob closest = null;
            int v = hero.visibleEnemies();
            for (int i=0; i < v; i++) {
                Mob mob = hero.visibleEnemy( i );
                if ( mob.isAlive() && mob.state != mob.PASSIVE && mob.state != mob.WANDERING && mob.state != mob.SLEEPING && !hero.mindVisionEnemies.contains(mob)
                        && (closest == null || Dungeon.level.distance(hero.pos, mob.pos) < Dungeon.level.distance(hero.pos, closest.pos))) {
                    closest = mob;
                }
            }

            ArrayList<Integer> placeable = new ArrayList<>();
            if (closest != null && Dungeon.level.distance(hero.pos, closest.pos) < 5){
                //spawn guardian
                for (int i = 0; i < PathFinder.NEIGHBOURS25.length; i++) {
                    int p = hero.pos + PathFinder.NEIGHBOURS25[i];
                    if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
                        placeable.add(p);
                    }
                }
            }
            while (placeable.size()>=2 && !shield.isEmpty() && !missile.isEmpty()){
                Geminis shield  = new GeminiShield();
                Geminis missile = new GeminiMissile();
                shield.twin(missile);
                missile.twin(shield);
                shield.HT = maxHP(2);
                missile.HT= maxHP(0.75F);
                shield.HP = this.shield.remove(0);
                missile.HP= this.missile.remove(0);

                shield.state = shield.HUNTING;
                shield.enemy = closest;
                GameScene.add(shield, 1);
                ScrollOfTeleportation.appear(shield, placeable.remove(Random.Int(placeable.size()-1)));
                missile.enemy = closest;
                missile.state = missile.HUNTING;
                GameScene.add(missile, 0);
                ScrollOfTeleportation.appear(missile, placeable.remove(Random.Int(placeable.size()-1)));

            }

            if (missile.isEmpty() || shield.isEmpty())
                detach();
            spend(TICK);

            return true;
        }
        public int maxHP(Geminis geminis){
            if (geminis instanceof GeminiShield)
                return maxHP(2);
            else
                return maxHP(0.75F);
        }
        public int maxHP(float mul){
            return maxHP((Hero)target, mul);
        }

        public static int maxHP( Hero hero, float mul ){
            return 10 + Math.round(hero.lvl * mul);
        }
        @Override
        public int icon() {
            return BuffIndicator.ARMOR;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1f, 1f, 2f);
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            String info = "";
            for (int i = 0; i < shield.size(); i++){
                if (i > missile.size())
                    break;
                info +="\n";
                info += Messages.get(GeminiShield.class, "name") + Messages.format("：%d", shield.get(i));
                info +="\n";
                info += Messages.get(GeminiMissile.class, "name") + Messages.format("：%d", missile.get(i));
            }
            return Messages.get(this, "desc", info);
        }

    }
}
