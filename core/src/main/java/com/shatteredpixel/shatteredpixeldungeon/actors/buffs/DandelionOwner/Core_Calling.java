package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Dummy_Core;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.LinkedList;

public class Core_Calling extends Buff {
    private LinkedList<Dummy_Core> cores = new LinkedList<>();
    private LinkedList<Integer> callingTimes = new LinkedList<>();
    @Override
    public boolean act(){
        spend(TICK);
        int size = Math.min(cores.size(), callingTimes.size());
        while (cores.size() > size)
            cores.removeLast();
        while (callingTimes.size() > size)
            callingTimes.removeLast();

        for (int i = 0; i < size; i++)
            if (callingTimes.get(i) > 0)
                callingTimes.set(i, callingTimes.get(i) - 1);

        ArrayList<Integer> placeable = new ArrayList<>();
        for (int i = 0; i < PathFinder.NEIGHBOURS25.length; i++) {
            int p = target.pos + PathFinder.NEIGHBOURS25[i];
            if (Actor.findChar(p) == null && Dungeon.level.passable[p])
                placeable.add(p);
        }

        while (!placeable.isEmpty() && !callingTimes.isEmpty() && callingTimes.get(0) == 0){
            int pos = placeable.remove(Random.Int(placeable.size() - 1));
            callingTimes.removeFirst();
            cores.removeFirst().summon(pos);
        }

        return true;
    }
    public void addCore(Dummy_Core core){
        cores.add(core);
        callingTimes.add(5);
    }
}
