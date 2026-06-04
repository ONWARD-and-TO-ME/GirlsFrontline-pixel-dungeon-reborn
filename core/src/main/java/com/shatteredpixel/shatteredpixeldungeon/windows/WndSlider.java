package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.OptionSlider;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

import java.util.ArrayList;

public class WndSlider extends Window {
    public int num;
    public WndSlider(String title, int times){
        this(title, times, 0);
    }
    public WndSlider(String title, int times, int value){
        super();

        num = value;

        RenderedTextBlock textBlock = PixelScene.renderTextBlock(9);
        textBlock.text(title);
        textBlock.setPos(0, 2);
        PixelScene.align(textBlock);
        add(textBlock);

        final int GAP = 2;
        final int WIDTH = 120;
        ArrayList<OptionSlider> buttons = new ArrayList<>();
        float pos = textBlock.bottom() + GAP;
        for (int i = 0; i < times; i++){
            String s = "x" + (int) Math.pow(10, i);
            OptionSlider Slider = new OptionSlider(s, "0", "9", 0, 9) {
                @Override
                protected void onChange() {
                    num = 0;
                    for (OptionSlider s: buttons){
                        num += s.getSelectedValue() * (int) Math.pow(10, buttons.indexOf(s));
                    }
                }
            };
            Slider.setSelectedValue(value % 10);
            value /= 10;
            Slider.setRect(0, pos, WIDTH, 24);
            add(Slider);
            buttons.add(Slider);
            pos = Slider.bottom() + GAP;
        }
        resize(WIDTH, (int) buttons.get(buttons.size()-1).bottom());
    }
}
