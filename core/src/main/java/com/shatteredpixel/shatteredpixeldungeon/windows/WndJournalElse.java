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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Pylon;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;
import com.watabou.utils.Reflection;

public class WndJournalElse extends WndTitledMessage {
    public WndJournalElse(CharSprite sprite, String title, String message){
        super(new MobTitle(sprite, title), message);
        PointerArea blocker = new PointerArea( 0, 0, PixelScene.uiCamera.width, PixelScene.uiCamera.height ) {
            @Override
            protected void onClick( PointerEvent event ) {
                onBackPressed();
            }
        };
        blocker.camera = PixelScene.uiCamera;
        add(blocker);
    }
    public WndJournalElse(Mob mob, String title, String message){
        super(new MobTitle(mob, title), message);
        PointerArea blocker = new PointerArea( 0, 0, PixelScene.uiCamera.width, PixelScene.uiCamera.height ) {
            @Override
            protected void onClick( PointerEvent event ) {
                onBackPressed();
            }
        };
        blocker.camera = PixelScene.uiCamera;
        add(blocker);
    }

    public WndJournalElse(Image icon, String title, String message ) {
        super( Instance(icon), title, message);

        PointerArea blocker = new PointerArea( 0, 0, PixelScene.uiCamera.width, PixelScene.uiCamera.height ) {
            @Override
            protected void onClick( PointerEvent event ) {
                onBackPressed();
            }
        };
        blocker.camera = PixelScene.uiCamera;
        add(blocker);

    }
    private static Image Instance(Image icon){
        if (!(icon instanceof CharSprite))
            return icon;
        CharSprite sprite = Reflection.newInstance(((CharSprite) icon).getClass());
        sprite.idle();
        return sprite;
    }
    private static class MobTitle extends Component {

        private static final int GAP	= 2;

        private final CharSprite image;
        private final RenderedTextBlock title;
        public MobTitle(CharSprite sprite, String title){
            this.title = PixelScene.renderTextBlock( title, 9 );
            this.title.hardlight( 0xFFFF44 );
            add( this.title );
            image = Reflection.newInstance(sprite.getClass());
            image.idle();
            add( this.image );
        }

        public MobTitle(Mob mob, String title) {

            this.title = PixelScene.renderTextBlock( title, 9 );
            this.title.hardlight( 0xFFFF44 );
            add( this.title );

            mob = Reflection.newInstance(mob.getClass());

            if (mob instanceof Mimic || mob instanceof Pylon) {
                mob.alignment = Char.Alignment.ENEMY;
            }
            if (mob instanceof WandOfWarding.Ward){
                if (mob instanceof WandOfWarding.Ward.WardSentry){
                    ((WandOfWarding.Ward) mob).upgrade(3);
                    ((WandOfWarding.Ward) mob).upgrade(3);
                    ((WandOfWarding.Ward) mob).upgrade(3);
                    ((WandOfWarding.Ward) mob).upgrade(3);
                } else {
                    ((WandOfWarding.Ward) mob).upgrade(0);
                }
            }
            image = mob.sprite();
            image.idle();
            add( this.image );
        }

        @Override
        protected void layout() {

            image.x = 0;
            image.y = Math.max( 0, (title.height() - image.height())/2 );

            float w = width - image.width() - GAP;

            title.maxWidth((int)w);
            title.setPos(x + image.width()+GAP,
                    image.height() > title.height() ? y +(image.height()-title.height()) / 2 : y);
            height = Math.max( image.height() , title.bottom() );
        }
    }
}
