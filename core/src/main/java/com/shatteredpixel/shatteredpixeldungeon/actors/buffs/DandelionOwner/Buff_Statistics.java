package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;

public class Buff_Statistics extends Buff {
    //TODO:
    // 计划用这个Buff来统计某个效果的总数值(包括正处于休眠的)、正在生效的数值、下一次衰减的时间(即最快失效或休眠的那个)及其来源
    // 但这需要将所有的Buff.affect及Buff.prlong（如果有），放到CardAffect里边，补上一句附加此统计Buff
    // 这或许是个大工程。尚未开工。
    {
        revivePersists = true;
    }
}
