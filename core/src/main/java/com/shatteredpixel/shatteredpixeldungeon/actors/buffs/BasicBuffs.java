package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class BasicBuffs extends FlavourBuff {

    {
        this.type = buffType.POSITIVE;
    }

    protected float R = 0;
    protected float G = 0;
    protected float B = 0;
    public int icon() {
        return BuffIndicator.INVISIBLE;
    }
    public void tintIcon(Image icon) {
        icon.hardlight(R, G, B);
    }
    protected float percent;
    public float percent(){
        return percent;
    }
    private static final String PERCENT      = "PERCENT";
    @Override
    public String desc(){
        int percent = (int) ((percent()-1)*100);
        return Messages.get(this, "desc", percent, (int) (visualcooldown()));
    }

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle(bundle);
        bundle.put(PERCENT, percent);
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle(bundle);
        percent = bundle.getFloat(PERCENT);
    }
    public static class Increase extends BasicBuffs{
        {
            R = 0.4F;
            G = 0.5F;
            B = 0.6F;
        }
        public Increase set(float percent){
            if (this.percent < percent)
                this.percent = percent;
            return this;
        }
    }
    public static class Velocity extends BasicBuffs{
        {
            R = 0.7F;
            G = 0.5F;
            B = 0.6F;
        }
        public Velocity set(float percent){
            if (this.percent < percent)
                this.percent = percent;
            return this;
        }
    }
    public static class Accuracy extends BasicBuffs{
        {
            R = 0.4F;
            G = 0.2F;
            B = 0.6F;
        }
        public Accuracy set(float percent){
            if (this.percent < percent)
                this.percent = percent;
            return this;
        }
    }
    public static class Evasion extends BasicBuffs{
        {
            R = 0.4F;
            G = 0.5F;
            B = 0;
        }
        public Evasion set(float percent){
            if (this.percent < percent)
                this.percent = percent;
            return this;
        }
    }
    public static class Reduce extends BasicBuffs{
        {
            R = 1F;
            G = 0.2F;
            B = 0.3F;
        }
        public Reduce set(float percent){
            this.percent = percent;
            return this;
        }
        @Override
        public String desc() {
            return Messages.get(this, "desc", (int) (100 - 100 * percent()), (int) (visualcooldown()));
        }
    }
}
