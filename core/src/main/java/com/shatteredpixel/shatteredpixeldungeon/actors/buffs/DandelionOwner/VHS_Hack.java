package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.Card;
import com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.CardCalculator;
import com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.CardSelector;
import com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.FinalCard;
import com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.RareCard;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfShroudingFog;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class VHS_Hack extends CounterBuff implements ActionIndicator.Action {
    {
        revivePersists = true;
    }
    @Override
    public boolean act(){
        spend(TICK);
        if (hasCard(FinalCard.VHS.AUG)) {
            boolean notice = false;
            for (Mob m : Dungeon.level.mobs){
                if (m.alignment == Char.Alignment.ENEMY
                        && m.enemySeen
                        && target.fieldOfView[m.pos]) {
                    notice = true;
                    break;
                }
            }
            if (notice)
                charge(1F);
        }
        return true;
    }
    private static boolean hasCard( Card card ){
        return CardSelector.INSTANCE().hasCard(card);
    }
    public void fullCharge(){
        countClear();
        hackLeft++;
    }
    public void charge( float charge ){
        boolean AUG = hasCard(FinalCard.VHS.AUG);
        if (!AUG && (isHacking() || hackLeft > 0))
            return;

        if (count() < CardCalculator.hack_chargeNeed())
            countUp(charge);
        else {
            if (!AUG) {
                //无AUG：充满后清除点数，hackLeft累加addHack()
                countClear();
                hackLeft += addHack();
            } else {
                if (!isHacking()) {
                    //有AUG，非hacking：清除点数→hackLeft累加addHack()→自动消耗一次开启hacking
                    countClear();
                    hackLeft += addHack();
                    hacking = true;
                    hackLeft--;
                } else {
                    //有AUG，正处于hacking：清除点数向hackLeft添加骇入次数，但不超过addHack()
                    if (hackLeft < addHack()) {
                        countClear();
                        hackLeft = Math.min(hackLeft + addHack(), addHack());
                    }
                    //hackLeft已达上限：不清除点数，保留三边累积（hacking + hackLeft已满 + count>=Need）
                }
            }
        }
    }
    private int addHack(){
        int add = 1;
        if (hasCard(RareCard.VHS.PM_06))
            add += 2;
        return add;
    }
    private int hackLeft;
    private boolean hacking = false;
    public boolean isHacking(){
        return hacking;
    }
    public boolean againstEnchant(){
        return hacking && !hasCard(FinalCard.VHS.AUG);
    }
    @Override
    public void fx(boolean on){
        if (on)
            ActionIndicator.setAction(this);
        else
            ActionIndicator.clearAction(this);
    }
    @Override
    public String actionName() {
        return isHacking() ? "取消骇入"
                :"开启骇入";
    }
    @Override
    public Image actionIcon() {
        Image image = isHacking() ? Icons.Notice(new PotionOfShroudingFog())
                : Icons.Notice(new PotionOfInvisibility());

        if (!hacking && hackLeft == 0)
            image.alpha(0.3F);
        return image;
    }
    @Override
    public void doAction() {
        if (!hacking && hackLeft == 0)
            return;
        hacking = !hacking;
        if (hacking)
            hackLeft--;
        else
            hackLeft++;
    }
    private static final String HACK_LEFT = "HACK_LEFT";
    private static final String HACKING = "Hacking";
    @Override
    public void storeInBundle( Bundle bundle ){
        super.storeInBundle(bundle);
        bundle.put(HACKING, hacking);
        bundle.put(HACK_LEFT, hackLeft);
    }
    @Override
    public void restoreFromBundle( Bundle bundle ){
        super.restoreFromBundle(bundle);
        hacking = bundle.getBoolean(HACKING);
        hackLeft = bundle.getInt(HACK_LEFT);
    }
    public static class VHS_Hack_KillingTracker extends FlavourBuff {}
}
