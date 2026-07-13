package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.GirlsFrontlinePixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.canScrollRedButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndWithCanScrollButton;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class CardSelector extends Item {
    {
        image = ItemSpriteSheet.SOMETHING;
        unique = true;
    }
    public int bugCoolDownLeft;
    public int coolDownLeft;
    public int curCardNum;
    public ArrayList<FirstCard> FirstCards      = new ArrayList<>();
    public ArrayList<CommonCard> CommonCards    = new ArrayList<>();
    public ArrayList<RareCard> RareCards        = new ArrayList<>();
    public ArrayList<FinalCard> FinalCards      = new ArrayList<>();
    public ArrayList<? extends Card> curCards   = new ArrayList<>();
    private static final String SELECT  = "SELECT_CARD";
    private static final String CHECK   = "CHECK_CARD";
    public static CardSelector INSTANCE( Hero hero ){
        CardSelector selector = hero.belongings.getItem(CardSelector.class);
        if (selector == null)
            selector = new CardSelector();
        return selector;
    }
    public int UpgradeTime(){
        return bugCoolDownLeft;
    }
    public boolean hasCard( Card card ){
        if (card instanceof FirstCard)
            return FirstCards.contains(card);
        else if (card instanceof CommonCard)
            return CommonCards.contains(card);
        else if (card instanceof RareCard)
            return RareCards.contains(card);
        else if (card instanceof FinalCard)
            return FinalCards.contains(card);
        return false;
    }
    @Override
    public ArrayList<String> actions( Hero hero ){
        ArrayList<String> actions = super.actions(hero);
        if (curCardNum < 8 && coolDownLeft <= 0)
            actions.add(SELECT);
        if (!FirstCards.isEmpty())
            actions.add(CHECK);
        return actions;
    }
    @Override
    public void execute( Hero hero, String action ) {
        super.execute(hero, action);
        if (action.equals(CHECK))
            checkCards();
        else if (action.equals(SELECT)){
            if (curCards.isEmpty()) {
                switch (curCardNum) {
                    case 0: default:
                        curCards = FirstCard.random(FirstCards); break;
                    case 1: case 2: case 3: case 4:
                        curCards = CommonCard.random(FirstCards, CommonCards); break;
                    case 5: case 6:
                        curCards = RareCard.random(FirstCards, RareCards); break;
                    case 7:
                        curCards = FinalCard.random(FirstCards, FinalCards); break;
                }
            }
            selectCards();
        }
    }
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
    private static WndWithCanScrollButton INSTANCE = null;
    private void checkCards(){
        ArrayList<canScrollRedButton> buttons = new ArrayList<>();
        addCheckCardsBtn(FirstCards, buttons);
        addCheckCardsBtn(CommonCards, buttons);
        addCheckCardsBtn(RareCards, buttons);
        addCheckCardsBtn(FinalCards, buttons);
        INSTANCE = new WndWithCanScrollButton(buttons);
        GirlsFrontlinePixelDungeon.scene().addToFront(INSTANCE);
    }
    private void addCheckCardsBtn(ArrayList<? extends Card> list, ArrayList<canScrollRedButton> buttons){
        for (Card c : list)
            buttons.add(new canScrollRedButton(c.getCard(), c.title()){
                @Override
                public void onClick(){
                    super.onClick();
                    GirlsFrontlinePixelDungeon.scene().addToFront(new WndOptions(c.title(), c.info()));
                }
            });
    }
    private void selectCards(){
        ArrayList<canScrollRedButton> buttons = new ArrayList<>();
        for (Card c : curCards)
            buttons.add(new canScrollRedButton(c.getCard(), c.title()){
                @Override
                public void onClick(){
                    super.onClick();
                    GirlsFrontlinePixelDungeon.scene().addToFront(
                            new WndOptions(c.title(), c.info(),
                                    Messages.get(CardSelector.class, "Entry"),
                                    Messages.get(CardSelector.class, "Cancel")){
                                @Override
                                protected void onSelect( int index ) {
                                    if (index == 0){
                                        if (c instanceof FirstCard)
                                            FirstCards.add((FirstCard) c);
                                        else if (c instanceof CommonCard)
                                            CommonCards.add((CommonCard) c);
                                        else if (c instanceof RareCard)
                                            RareCards.add((RareCard) c);
                                        else if (c instanceof FinalCard)
                                            FinalCards.add((FinalCard) c);
                                        if (curCardNum++ < 8)
                                            coolDownLeft = 1500 + 500 * (curCardNum + bugCoolDownLeft > 33333 ? 8 : 0);
                                        curCards.clear();
                                        if (INSTANCE != null) {
                                            INSTANCE.hide();
                                            INSTANCE = null;
                                        }
                                        hide();
                                    }
                                    else if (index == 1)
                                        hide();
                                }
                        }
                    );
                }
            });
        INSTANCE = new WndWithCanScrollButton(buttons);
        GirlsFrontlinePixelDungeon.scene().addToFront(INSTANCE);
    }
    enum storeString{
        Bug_CoolDown, CoolDownLeft_Card, CurCardNum,
        First, Common, Rare, Final, cur;
        private static final String cardNum = "_CardsNum_";
        private static final String cardName = "Cards_Name";
        private static final String cardClass = "Cards_CLASS_";
        private static final String cardLength = "Cards_Length";
        public void store( Bundle bundle, ArrayList<? extends Card> list ){
            Bundle b = new Bundle();
            for (int i = 0; i < list.size(); i++) {
                assert list.get(i) != null;
                b.put(name() + cardClass + i, list.get(i).getClass());
                b.put(name() + cardNum + i, (Enum<?>) list.get(i));
            }
            b.put( name() + cardLength, list.size() );
            bundle.put( name() + cardName, b );
        }
        @SuppressWarnings("unchecked")
        public <T extends Card> ArrayList<T> restore( Bundle bundle, Class<T> ignore ){
            ArrayList<T> list = new ArrayList<>();
            Bundle b = bundle.getBundle( name() + cardName );
            int size = b.getInt( name() + cardLength );

            for (int i = 0; i < size; i++)
                list.add((T) b.getEnum( name() + cardNum + i, b.getClass(name() + cardClass + i)));

            return list;
        }
    }
    @Override
    public void restoreFromBundle( Bundle bundle ){
        super.restoreFromBundle(bundle);
        bugCoolDownLeft = bundle.getInt(storeString.Bug_CoolDown.name());
        coolDownLeft    = bundle.getInt(storeString.CoolDownLeft_Card.name());
        curCardNum      = bundle.getInt(storeString.CurCardNum.name());
        FirstCards      = storeString.First.restore(bundle, FirstCard.class);
        CommonCards     = storeString.Common.restore(bundle, CommonCard.class);
        RareCards       = storeString.Rare.restore(bundle, RareCard.class);
        FinalCards      = storeString.Final.restore(bundle, FinalCard.class);
        curCards        = storeString.cur.restore(bundle, Card.class);
    }
    @Override
    public void storeInBundle( Bundle bundle ){
        super.storeInBundle(bundle);
        bundle.put(storeString.Bug_CoolDown.name(), bugCoolDownLeft);
        bundle.put(storeString.CoolDownLeft_Card.name(), coolDownLeft);
        bundle.put(storeString.CurCardNum.name(), curCardNum);
        storeString.First.store(bundle, FirstCards);
        storeString.Common.store(bundle, CommonCards);
        storeString.Rare.store(bundle, RareCards);
        storeString.Final.store(bundle, FinalCards);
        storeString.cur.store(bundle, curCards);
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }
    public class CoolDownTracker extends Buff{
        {
            revivePersists = true;
        }
        @Override
        public boolean act() {
            if (coolDownLeft > 0)
                coolDownLeft--;

            if (bugCoolDownLeft++ == 33333) {
                int i = curCards.isEmpty() ? 0 : -1;
                if (curCardNum - i < 8)
                    curCardNum = i;
            }
            spend(TICK);
            return true;
        }
    }
}
