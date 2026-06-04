/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.GirlsFrontlinePixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Image;

public class WndJournalItem extends WndTitledMessage {

    public WndJournalItem(Image image, String title, String trueMessage, String message, boolean isSeen) {
        super(new Image(image), title, trueMessage);
        IconButton showProperty = new IconButton(Icons.INFO.get()){
            protected void onClick() {
                onBackPressed();
                if (GirlsFrontlinePixelDungeon.scene() instanceof GameScene)
                    GameScene.show(new WndItemProperty(new Image(image), title, message, trueMessage, isSeen));
                else
                    GirlsFrontlinePixelDungeon.scene().addToFront(new WndItemProperty(new Image(image), title, message, trueMessage, isSeen));
            }
        };
        showProperty.setRect(width-16, 0 , 16 ,16);
        showProperty.visible = Badges.isUnlocked(Badges.Badge.HAPPY_END) && isSeen;
        showProperty.enable(showProperty.visible);
        add(showProperty);
    }

    private static class WndItemProperty extends WndTitledMessage{
        public WndItemProperty(Image image, String title, String trueMessage, String message, boolean isSeen) {
            super(new Image(image), title, trueMessage);
            IconButton showStory = new IconButton(Icons.CHANGESLOG.get()){
                protected void onClick() {
                    onBackPressed();
                    if (GirlsFrontlinePixelDungeon.scene() instanceof GameScene)
                        GameScene.show(new WndJournalItem(new Image(image), title, message, trueMessage, isSeen));
                    else
                        GirlsFrontlinePixelDungeon.scene().addToFront(new WndJournalItem(new Image(image), title, message, trueMessage, isSeen));
                }
            };
            showStory.setRect(width-16, 0 , 16 ,16);
            add(showStory);
        }
    }
}
