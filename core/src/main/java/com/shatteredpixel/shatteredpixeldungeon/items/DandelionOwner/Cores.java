package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Resizing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DandelionOwner.Puppet;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DandelionOwner.Puppets;

public class Cores {
    public static class NormalCore extends Dummy_Core {
        {
            htMul = 0.1F;
            attackSpeedMul = 0.5F;
            damageMul = 0.5F;
            puppetClass = Puppets.Normal.class;
        }
        @Override
        protected void setSize(Puppet puppet) {
            Buff.count(puppet, Resizing.class, 50F);
        }
    }
    public abstract static class EliteCore extends Dummy_Core {
        @Override
        protected void setSize(Puppet puppet) {
            Buff.count(puppet, Resizing.class, 75F);
        }
    }
    public static class C93 extends EliteCore {
        {
            puppetClass = Puppets.C93.class;
        }
    }
    public static class Savage_99 extends EliteCore {
        {
            puppetClass = Puppets.Savage_99.class;
        }
    }
    public static class VP1915 extends EliteCore {
        {
            puppetClass = Puppets.VP1915.class;
        }
    }
    public static class Kolibri_Pistole extends EliteCore {
        {
            puppetClass = Puppets.Kolibri_Pistole.class;
        }
    }
}
