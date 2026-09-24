package com.shatteredpixel.shatteredpixeldungeon.items.artifacts.RedBookSpell;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ActHPtoGetFood;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.RedBook;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;

public class HPtoFood extends BookSpell{

    public static final HPtoFood INSTANCE = new HPtoFood();

    public int icon() {
        return 26;
    }

    {
        chargeUse=0;
        timeUse=0;
    }

    @Override
    public void onCast(RedBook book, Hero hero) {
        HPtoGetFood();
    }
    public static void HPtoGetFood(){
        int dmg = Math.max(15, Math.round(Dungeon.hero.HT * 0.2f));
        Hunger hunger = Dungeon.hero.buff(Hunger.class);
        float food = hunger == null
                ? 120F
                : Math.min(120F, hunger.hunger());
        String info ="";
        info+="你可以主动消耗一些血量换取饱食度，此血量消耗为真实伤害，不受减伤、护盾影响。所获得的饱食度不受饥荒影响。\n" +
                "获得饱食度的持续期间内，玩家的回复不再受到深度睡眠、增压器的正面效果的额外收益\n" +
                "当你食用食物时，超过饱食度上限的部分将会用于减少这个限制的持续时间";
        info+="当前将消耗 _"+dmg+"_ 血量并获得 _"+food+"_ 饱食度";
        GameScene.show(
                new WndOptions(new BuffIcon(new HPtoFood()),
                        Messages.titleCase("锋血转换"),
                        info,
                        "确认",
                        "取消") {
                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            Dungeon.hero.HP -= dmg;
                            if (Dungeon.hero.HP <= 0){
                                Dungeon.hero.die(this);
                                Dungeon.fail(RedBook.class);
                            }
                            else {
                                if (hunger != null){
                                    hunger.satisfy(food);
                                    Buff.affect(Dungeon.hero, ActHPtoGetFood.LockReg.class, food);
                                }
                            }
                        }
                    }
                }
        );
    }

}
