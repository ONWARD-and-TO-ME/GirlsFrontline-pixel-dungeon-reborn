package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.TierOfTalent;
import com.shatteredpixel.shatteredpixeldungeon.custom.testmode.generator.debugBook;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentsPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

import java.util.LinkedHashMap;

public class WndReplaceTalent extends Window {

    //talents that can only be used by one hero class
    //TODO could some of these be made more generic?
    public static WndReplaceTalent INSTANCE;

    public Talent replacing;
    public int tier;
    LinkedHashMap<Talent, Integer> replaceOptions;

    //for window restoring
    public WndReplaceTalent(){
        super();

        if (INSTANCE != null){
            replacing = INSTANCE.replacing;
            tier = INSTANCE.tier;
            replaceOptions = INSTANCE.replaceOptions;
            INSTANCE = this;
            setup(replacing, tier, replaceOptions);
        } else {
            hide();
        }
    }

    public WndReplaceTalent(Talent replacing, int tier){
        super();

        INSTANCE = this;

        this.replacing = replacing;
        this.tier = tier;

        LinkedHashMap<Talent, Integer> options;
        switch (tier){
            case 1: default: options = TierOfTalent.One; break;
            case 2: options = TierOfTalent.Two; break;
            case 3: options = TierOfTalent.Three; break;
            case 4: options = TierOfTalent.Four; break;
        }
        replaceOptions = options;
        setup(replacing, tier, options);
    }

    private void setup(Talent replacing, int tier, LinkedHashMap<Talent, Integer> replaceOptions){

        float top = 0;

        IconTitle title = new IconTitle( new debugBook());
        title.color( TITLE_COLOR );
        title.setRect(0, 0, 120, 0);
        add(title);

        top = title.bottom() + 2;

        RenderedTextBlock text = PixelScene.renderTextBlock(Messages.get(ScrollOfMetamorphosis.class, "replace_desc"), 6);
        text.maxWidth(120);
        text.setPos(0, top);
        add(text);

        top = text.bottom() + 2;

        TalentsPane.TalentTierPane optionsPane = new TalentsPane.TalentTierPane(replaceOptions, tier, TalentButton.Mode.DEBUG_REPLACE);
        optionsPane.title.text(" ");
        optionsPane.setSize(120, optionsPane.height());
        ScrollPane list = new ScrollPane(optionsPane){
            @Override
            public void onClick( float x, float y ) {
                content.onClickB(x, y);
            }
        };
        add(list);
        list.setRect(-62, -50, 120, Math.min(125, list.height()));
        list.scrollTo(0,0);
        resize(120, (int) Math.min(144, list.height()));
    }

    @Override
    public void hide() {
        super.hide();
        if (INSTANCE == this) {
            INSTANCE = null;
        }
    }
}
