package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.custom.testmode.generator.debugBook;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentsPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class WndSelectTalent extends Window {

    public static WndSelectTalent INSTANCE;

    TalentsPane pane;

    public WndSelectTalent(){
        super();

        INSTANCE = this;

        float top = 0;

        IconTitle title = new IconTitle( new debugBook());
        title.color( TITLE_COLOR );
        title.setRect(0, 0, 120, 0);
        add(title);

        top = title.bottom() + 2;

        RenderedTextBlock text = PixelScene.renderTextBlock(Messages.get(ScrollOfMetamorphosis.class, "choose_desc"), 6);
        text.maxWidth(120);
        text.setPos(0, top);
        add(text);

        top = text.bottom() + 2;

        ArrayList<LinkedHashMap<Talent, Integer>> talents = new ArrayList<>(Dungeon.hero.talents);

        pane = new TalentsPane(TalentButton.Mode.DEBUG_CHOOSE, talents);
        add(pane);
        pane.setPos(0, top);
        pane.setSize(120, pane.content().height());
        resize((int)pane.width(), (int)pane.bottom());
        pane.setPos(0, top);
    }

    @Override
    public void hide() {
        super.hide();
        INSTANCE = null;
    }

    @Override
    public void offset(int xOffset, int yOffset) {
        super.offset(xOffset, yOffset);
        pane.setPos(pane.left(), pane.top()); //triggers layout
    }
}
