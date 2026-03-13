package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.SA;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.watabou.utils.Random;

public abstract class  SurpriseAttack extends MeleeWeapon {

    //SA- 웰로드, 나강리볼버, 구아, 그로자
    //기습시 추가 데미지가 붙는 무기
    public float damageMin;
    {
        dmgBaseMul = 4;
    }

    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero)owner;
            Char enemy = hero.enemy();
            if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
                int diff = max() - min();
                int damage = augment.damageFactor(Random.NormalIntRange(
                        min() + Math.round(diff*damageMin),
                        max()));
                int exStr = hero.STR() - STRReq();
                if (exStr > 0) {
                    damage += Random.IntRange(0, exStr);
                }
                return damage;
            }
        }
        return super.damageRoll(owner);
    }
}
