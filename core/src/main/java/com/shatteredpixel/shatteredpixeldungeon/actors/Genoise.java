package com.shatteredpixel.shatteredpixeldungeon.actors;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Genoise implements Bundlable {
    protected int target;
    private int minDmg;
    private int maxDmg;

    public Genoise(){
        target = -1;
        minDmg = 0;
        maxDmg = 0;
    }
    public Genoise(int cell, int min, int max) {
        target = cell;
        minDmg = min;
        maxDmg = max;
    }

    private static final String TARGET = "target";
    private static final String MIN_DMG = "minDmg";
    private static final String MAX_DMG = "maxDmg";

    public final int getTarget() { return target; }

    public void explore() {

        Sample.INSTANCE.play( Assets.Sounds.BLAST );

        if (target >= 0 && Dungeon.level.heroFOV[target]) {
            CellEmitter.center( target ).burst( BlastParticle.FACTORY, 30 );
            //中心爆炸粒子效果
        }

        boolean terrainAffected = false;
        for (int n : PathFinder.NEIGHBOURS9) {
            int c = target + n;
            if (c >= 0 && c < Dungeon.level.length()) {
                if (Dungeon.level.heroFOV[c]) {
                    CellEmitter.get( c ).burst( SmokeParticle.FACTORY, 4 );
                    //周围爆炸粒子效果
                }

                if (Dungeon.level.flammable[c]) {
                    Dungeon.level.destroy( c );
                    GameScene.updateMap( c );
                    terrainAffected = true;
                    //更新地形
                }

                //destroys items / triggers bombs caught in the blast.
                Heap heap = Dungeon.level.heaps.get( c );
                if(heap != null)
                    heap.explode();
                //炸普通箱子

                Char ch = Actor.findChar( c );
                if (ch != null) {
                    //those not at the center of the blast take damage less consistently.
                    int dmg = Random.NormalIntRange(minDmg, maxDmg) * (c == target ? 2 : 1) - ch.drRoll();
                    //minDmg为20+彩蛋武器等级，maxDmg为30+2*彩蛋武器等级，所以Random这一项相当于是基础20~30、成长1-2的基础伤害
                    //然后c==target这一项的作用是给中心格伤害翻倍
                    //drRoll的作用是计算怪物自身的防御
                    //综上，蛋糕炸弹的伤害计算就是基础20~30、成长1-2的基础伤害、对中心格伤害翻倍、计算怪物防御
                    if (dmg > 0) {
                        ch.damage( dmg , Dungeon.hero );
                    }

                    if (ch == Dungeon.hero && !ch.isAlive()) {
                        Dungeon.fail( getClass() );
                    }
                }
            }
        }

        if (terrainAffected) {
            Dungeon.observe();
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(TARGET, target);
        bundle.put(MIN_DMG, minDmg);
        bundle.put(MAX_DMG, maxDmg);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        target = bundle.getInt(TARGET);
        minDmg = bundle.getInt(MIN_DMG);
        maxDmg = bundle.getInt(MAX_DMG);
    }
}
