package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ItemBuff;
import com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.CardSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

public class CardSelectorBuff extends ItemBuff implements ActionIndicator.Action {
    {
        item = new CardSelector();
    }
    private CardSelector selector() {
        assert item != null;
        return (CardSelector) item;
    }

    @Override
    public void fx( boolean on ) {
        if (on)
            ActionIndicator.setAction(this);
        else
            ActionIndicator.clearAction(this);
    }
    @Override
    public String actionName() {
        return selector().name();
    }
    @Override
    public Image actionIcon() {
        //常驻动作按钮使用 hero_icons 中的卡牌图标，而非物品贴图占位（变色核心）
        return selector().customIcon();
    }

    @Override
    public void doAction() {
        GameScene.show(new WndCardSelector(this));
    }
    public static class WndCardSelector extends Window {

        private static final int WIDTH_P = 120;
        private static final int WIDTH_L = 160;

        private static final int MARGIN  = 2;

        public WndCardSelector( CardSelectorBuff buff ){
            super();
            CardSelector selector = buff.selector();
            int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

            float pos = MARGIN;
            RenderedTextBlock title = PixelScene.renderTextBlock(selector.name(), 9);
            title.hardlight(TITLE_COLOR);
            title.setPos((width-title.width())/2, pos);
            title.maxWidth(width - MARGIN * 2);
            add(title);
            pos = title.bottom() + 3*MARGIN;

            RenderedTextBlock messages = PixelScene.renderTextBlock(selector.desc(), 6);
            messages.setPos(MARGIN, pos);
            messages.maxWidth(width - MARGIN * 2);
            add(messages);
            pos = messages.bottom() + 3*MARGIN;

            for (CardSelector.actionsList a : CardSelector.actionsList.values()) {
                if (a == CardSelector.actionsList.DEBUG && !a.addAction(selector))
                    continue;
                Image ic = a.actionImage(selector);
                RedButton button = new RedButton(a.bodyMessages(selector), 6) {
                    @Override
                    protected void onClick() {
                        super.onClick();
                        hide();
                        a.doAction(selector);
                    }
                };
                button.icon(ic);
                button.leftJustify = true;
                button.multiline = true;
                button.setSize(width, button.reqHeight());
                button.setRect(0, pos, width, button.reqHeight());
                button.enable(a.addAction(selector));
                add(button);
                pos = button.bottom() + MARGIN;
            }

            resize(width, (int)pos);
        }

    }

}
