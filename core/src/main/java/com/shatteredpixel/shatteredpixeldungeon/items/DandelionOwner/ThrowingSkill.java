package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.M4A1;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;

import java.util.ArrayList;

public class ThrowingSkill extends Item {

    {
        unique = true;
        defaultAction = Throwing;
    }
    public int coolDownLeft;
    private static final String Throwing = "Throwing";
    @Override
    public ArrayList<String> actions(Hero hero ){
        ArrayList<String> actions = super.actions(hero);
        actions.add(Throwing);
        return actions;
    }

    @Override
    public void execute( Hero hero, String action ) {
        super.execute(hero, action);
        if (action.equals(Throwing)) {
            if (coolDownLeft > 0)
                GLog.w(Messages.get(this, "CoolDown"));
            else if (!isSnipe())
                GameScene.selectCell(ThrowingSelector);
            else ;
        }
    }
    private static boolean isSnipe(){
        return CardSelector.INSTANCE(Dungeon.hero).hasCard(RareCard.WA2000.NTW_20);
    }
    public static final CellSelector.Listener ThrowingSelector_INSTANCE = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                new ThrowingBomb().cast(Dungeon.hero, target);
                CardSelector selector = CardSelector.INSTANCE(Dungeon.hero);
                if (selector.hasCard(CommonCard.Vector.Beretta_38))
                    new FireBomb(4).cast(Dungeon.hero, target);
                if (selector.hasCard(CommonCard.Vector.Uzi))
                    new FireBomb(4).cast(Dungeon.hero, target);
                if (selector.hasCard(RareCard.Vector.PP_19))
                    new FireBomb(8).cast(Dungeon.hero, target);
                updateQuickslot();
            }
        }
        @Override
        public String prompt() {
            return Messages.get(ThrowingSkill.class, "select_target");
        }
    };
    private final CellSelector.Listener ThrowingSelector = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                ThrowingSelector_INSTANCE.onSelect(target);
                coolDownLeft = 50;
                Dungeon.hero.spendAndNext( Actor.TICK );
                updateQuickslot();
            }
        }
        @Override
        public String prompt() {
            return ThrowingSelector_INSTANCE.prompt();
        }
    };
    protected CoolDownTracker coolDownTracker;
    @Override
    public void Tracker( Char owner ){
        super.Tracker(owner);
        if (coolDownTracker == null){
            coolDownTracker = new CoolDownTracker();
            coolDownTracker.attachTo(owner);
        }
    }
    @Override
    public void stopTrack(){
        super.stopTrack();
        if (coolDownTracker != null){
            coolDownTracker.detach();
            coolDownTracker = null;
        }
    }
    private static final String CoolDownLeft_Throwing = "CoolDownLeft_Throwing";
    @Override
    public void restoreFromBundle( Bundle bundle ){
        super.restoreFromBundle(bundle);
        coolDownLeft    = bundle.getInt(CoolDownLeft_Throwing);
    }
    @Override
    public void storeInBundle( Bundle bundle ){
        super.storeInBundle(bundle);
        bundle.put(CoolDownLeft_Throwing, coolDownLeft);
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }
    @Override
    public boolean isIdentified() {
        return true;
    }
    public class CoolDownTracker extends Buff {
        {
            revivePersists = true;
        }
        @Override
        public boolean act() {
            if (coolDownLeft > 0)
                coolDownLeft--;
            spend(TICK);
            return true;
        }
    }
    protected abstract static class Throwing extends Item{
        {
            image = ItemSpriteSheet.SMOKEUmp45;
        }
        protected boolean outMap(int cell){
            Point p = Dungeon.level.cellToPoint(cell);
            return p.x <= 0 || p.y <= 0 || p.x >= Dungeon.level.width() - 1 || p.y >= Dungeon.level.height() - 1;
        }
        protected int[] throwingPos(){
            return CardSelector.INSTANCE(Dungeon.hero).hasCard(CommonCard.UNIVERSAL.FAMAS) ?
                    PathFinder.NEIGHBOURS25 :
                    PathFinder.NEIGHBOURS9;
        }
        @Override
        public int throwPos( Hero user, int dst){
            return new Ballistica( user.pos, dst, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET ).collisionPos;
        }
        public abstract void explore( int target );
        @Override
        public void onThrow( int cell ){
            explore(cell);
        }
    }
    private static class ThrowingBomb extends Throwing {
        @Override
        public void explore( int target ) {
            ArrayList<Char> list = new ArrayList<>();
            for (int i : throwingPos()){
                int cell = target + i;
                if (outMap(cell))
                    continue;
                Char ch;
                if ((ch = Actor.findChar(cell)) != null)
                    list.add(ch);
            }
            int size = list.size();
            for (Char ch : list){
                if (ch.alignment == Char.Alignment.ALLY || ch instanceof NPC)
                    continue;
                for (int i = 0; i < size; i++)
                    ch.damage(Math.round((1 - (Math.min(5, size - 1)) * 0.1F) * M4A1.damageRoll()), ThrowingSkill.class);
            }
        }
    }
    private static class FireBomb extends Throwing{
        private final int times;
        public FireBomb(int times){
            this.times = times;
        }
        @Override
        public void explore(int cell) {

        }
    }
}
