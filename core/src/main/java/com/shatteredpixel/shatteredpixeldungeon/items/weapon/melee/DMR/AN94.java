package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.DMR;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
public class AN94 extends DesignatedMarksmanRifle {
    {
        image = ItemSpriteSheet.AN94;
        hitSound = Assets.Sounds.HIT_CRUSH;
        hitSoundPitch = 0.5f;

        tier = 4;
        RCH = 3;
        dmgUpgradeDiffer = -1;
    }

}