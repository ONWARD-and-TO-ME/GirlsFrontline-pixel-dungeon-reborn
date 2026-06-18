package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.watabou.noosa.ui.Component;

public interface canScrollButton {

    void onClick();

    Component setRect(float x, float y, float width, float height );
    default float bottom(){
        return ((Button) this).bottom();
    }
    default boolean onClick(float x, float y){
        if(!((Button) this).inside(x,y)) return false;
        onClick();

        return true;
    }
}
