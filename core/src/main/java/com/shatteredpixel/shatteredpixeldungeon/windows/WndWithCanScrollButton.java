package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.ui.canScrollButton;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

public class WndWithCanScrollButton extends Window {

    public WndWithCanScrollButton(ArrayList<? extends canScrollButton> buttons){
        this(buttons, 1);
    }
    public WndWithCanScrollButton(ArrayList<? extends canScrollButton> buttons, int buttonsPerRow){
        this(buttons, buttonsPerRow, new ArrayList<>(), 1);
    }
    public WndWithCanScrollButton(ArrayList<? extends canScrollButton> buttons, ArrayList<Button> buttonsB){
        this(buttons, 1, buttonsB, 1);
    }
    public WndWithCanScrollButton(ArrayList<? extends canScrollButton> buttons, int buttonsPerRow, ArrayList<Button> buttonsB, int buttonsBPerRow){
        super();
        final int GAP = 2;
        final int btnHeight = 16;
        int HEIGHT = 144;
        int lastBtnRow = 0;
        lastBtnRow += (int) Math.ceil(buttons.size() / (float) buttonsPerRow);
        lastBtnRow += (int) Math.ceil(buttonsB.size() / (float) buttonsBPerRow);
        HEIGHT = Math.min(HEIGHT, lastBtnRow * (btnHeight + GAP));
        resize(120, HEIGHT);
        ScrollPane list = new ScrollPane(new Component()) {

            @Override
            public void onClick(float x, float y) {
                int max_size = buttons.size();
                for (int i = 0; i < max_size; ++i) {
                    if (buttons.get(i).onClick(x, y))
                        break;
                }
            }
        };
        add(list);
        Component content = list.content();
        int width = 120 / buttonsPerRow - (buttonsPerRow - 1) * GAP;
        int col = 0;
        int row = 0;
        for (canScrollButton btn : buttons){

            btn.setRect(col * width, (btnHeight + GAP)*row, width, btnHeight);
            PixelScene.align((Button) btn);
            col++;
            if (col == buttonsPerRow) {
                col = 0;
                row++;
            }
            content.add((Button) btn);
        }
        content.setSize(120, ((Button) buttons.get(buttons.size()-1)).bottom());
        list.setSize( list.width(), list.height() );
        list.setRect(0, 0, 120, HEIGHT - Math.round(buttonsB.size() / (float) buttonsBPerRow) * (btnHeight + GAP));
        list.scrollTo(0,0);
        width = 120 / buttonsBPerRow - (buttonsBPerRow - 1) * GAP;
        if (col != 0){
            col = 0;
            row++;
        }
        for (Button btn : buttonsB){

            btn.setRect(col * width, (btnHeight + GAP)*row, width, btnHeight);
            PixelScene.align(btn);
            col++;
            if (col == buttonsPerRow) {
                col = 0;
                row++;
            }
            content.add(btn);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
