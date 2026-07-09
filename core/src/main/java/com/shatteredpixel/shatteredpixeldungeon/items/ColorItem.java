package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundlable;

public interface ColorItem extends Bundlable {
    void setIgnore();
    default void guessType(String cause) {
        if (Dungeon.guessType.add(getClass()))
            if (cause != null && !cause.isEmpty() && SPDSettings.isAutoIdentify() && SPDSettings.showAutoGuessingText()) {
                GLog.newLine();
                GLog.i(cause);
            }
    }
    default boolean isGuess(){
        return Dungeon.guessType.contains(getClass());
    }
    default boolean showGuess(){
        return isGuess() && SPDSettings.isAutoIdentify();
    }
}
