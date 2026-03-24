//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff.buffType;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollSWAPSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class GnollSWAP extends Gnoll {
    {
        spriteClass = GnollSWAPSprite.class;
        PASSIVE = new Passive();
        WANDERING = new Wandering();
        state = PASSIVE;
        defenseSkill = 6;
        HP = HT = 24;
        lootChance = 0.0F;
    }

    public int damageRoll() {
        return Random.NormalIntRange(1, 10);
    }

    public int attackSkill(Char target) {
        return 15;
    }

    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 1);
    }

    protected boolean canAttack(Char enemy) {
        if (Dungeon.level.adjacent(pos, enemy.pos)) {
            return true;
        } else {
            if (Dungeon.level.distance(pos, enemy.pos) <= 2) {
                boolean[] passable = BArray.not(Dungeon.level.solid, null);

                for(Char ch : Actor.chars()) {
                    passable[ch.pos] = ch == this;
                }

                PathFinder.buildDistanceMap(enemy.pos, passable, 2);
                if (PathFinder.distance[pos] <= 2) {
                    return true;
                }
            }

            return super.canAttack(enemy);
        }
    }

    public void rollToDropLoot() {
        super.rollToDropLoot();
        if (Dungeon.hero.lvl <= this.maxLvl + 2) {
            ArrayList<Item> items = new ArrayList<>();
            items.add(Generator.randomUsingDefaults());
            items.add(Generator.randomUsingDefaults());
            if (Random.Int(2) == 0) {
                items.add(Generator.randomUsingDefaults());
            }

            for(Item item : items) {
                if(item instanceof Artifact){
                    Generator.removeArtifact(((Artifact)item).getClass());
                    //针对切割者掉落神器/遗物后仍可以刷出相同神器/遗物的bug，这里补充一次清除
                }
                int dropPlace;
                int TryTimes=0;
                int ofs = PathFinder.NEIGHBOURS9[Random.Int(9)];
                do{
                    if(TryTimes<9){
                        TryTimes++;
                        dropPlace=pos+ofs;
                        //尝试9次向NEIGHBOURS9投出
                    }else {
                        dropPlace=pos;
                        //9次尝试均无法投出时，原地放下
                    }
                }while (
                        !(!Dungeon.level.solid[dropPlace] || Dungeon.level.passable[dropPlace])
                        //落点并非固体，或者是固体但是可以通过（门），或者9次尝试均无法投出时，以所得到的落点放下物品
                );
                    Dungeon.level.drop(item, dropPlace).sprite.drop(this.pos);
            }

        }
    }

    public void beckon(int cell) {
        if (this.state != this.PASSIVE) {
            super.beckon(cell);
        } else {
            this.target = cell;
        }

    }

    public String description() {
        String desc = super.description();
        if (this.state == this.PASSIVE) {
            desc = desc + "\n\n" + Messages.get(this, "desc_passive");
        } else {
            desc = desc + "\n\n" + Messages.get(this, "desc_aggro");
        }

        return desc;
    }

    private class Passive extends Mob.Wandering {
        private int seenNotifyCooldown;

        private Passive() {
            seenNotifyCooldown = 0;
        }

        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            for(Buff b : buffs()) {
                if (b.type == buffType.NEGATIVE) {
                    state = WANDERING;
                    return true;
                }
            }

            if (fieldOfView[Dungeon.hero.pos] && Dungeon.level.heroFOV[pos]) {
                if (seenNotifyCooldown <= 0) {
                    GLog.p(Messages.get(GnollSWAP.class, "seen_passive"));
                }

                seenNotifyCooldown = 10;
            } else {
                --seenNotifyCooldown;
            }

            if (enemyInFOV && justAlerted) {
                if (Dungeon.level.heroFOV[pos]) {
                    GLog.w(Messages.get(GnollSWAP.class, "seen_aggro"));
                }

                return noticeEnemy();
            } else {
                return continueWandering();
            }
        }
    }

    private class Wandering extends Mob.Wandering {

        protected boolean noticeEnemy() {
            GLog.w(Messages.get(GnollSWAP.class, "seen_aggro"));
            return super.noticeEnemy();
        }
    }
}
