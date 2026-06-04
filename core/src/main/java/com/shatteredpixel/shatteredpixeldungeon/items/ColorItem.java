package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.watabou.utils.Bundlable;

public interface ColorItem extends Bundlable {
    void setIgnore();
    default void guessType(){
        Dungeon.guessType.add(getClass());
    };
    default boolean isGuess(){
        return Dungeon.guessType.contains(getClass());
    }
    default boolean showGuess(){
        return isGuess() && SPDSettings.isAutoIdentify();
    }
}
