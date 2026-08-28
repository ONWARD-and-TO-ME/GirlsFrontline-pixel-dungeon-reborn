package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

//集中展示卡牌产生的四类削弱效果（攻速降低/移速降低/易伤/伤害弱化）。
//各削弱Buff本身不单独展示，统一由本Buff统计“正在生效”与“已挂载（含休眠）”的总数值。
//施加入口：CardAffect.statistics(ch)，在各削弱Buff的施加方法末尾调用。
public class Buff_Statistics extends Buff {
    {
        revivePersists = true;
    }
    public int icon() {
        if (!hasAttackSpeed() && !hasMoveSpeed() && !hasVulner() && !hasWeakly())
            return BuffIndicator.NONE;
        return iconNeedDraw();
    }
    public void tintIcon( Image icon ){
        tintIconNeedDraw(icon);
    }
    @Override
    public String toString() {
        return Messages.get(this, "name");
    }
    @Override
    public String desc() {
        StringBuilder text = new StringBuilder();
        if (hasAttackSpeed())
            text.append(Messages.get(this, "desc_attack_speed", attackSpeedAll(), attackSpeedWorking())).append('\n');
        if (hasMoveSpeed())
            text.append(Messages.get(this, "desc_move_speed", moveSpeedAll(), moveSpeedWorking())).append('\n');
        if (hasVulner())
            text.append(Messages.get(this, "desc_vulnerability", vulnerAll(), vulnerWorking())).append('\n');
        if (hasWeakly())
            text.append(Messages.get(this, "desc_weakly", weaklyAll(), weaklyWorking()));
        return text.toString().trim();
    }

    private int attackSpeedAll(){
        float i = 0;
        for (AttackSpeed speed : target.buffs(AttackSpeed.class))
            i += speed.values();
        S_M82A1 s = target.buff(S_M82A1.class);
        if (s != null)
            i += s.values();
        return (int) i;
    }
    private boolean hasAttackSpeed(){
        return attackSpeedAll() != 0;
    }
    private int attackSpeedWorking(){
        //与All相似，只不过要加上一个working()的判断
        float i = 0;
        for (AttackSpeed speed : target.buffs(AttackSpeed.class))
            if (speed.working())
                i += speed.values();
        S_M82A1 s = target.buff(S_M82A1.class);
        if (s != null && s.working())
            i += s.values();
        return (int) i;
    }
    private int moveSpeedAll(){
        float i = 0;
        for (MoveSpeed speed : target.buffs(MoveSpeed.class))
            i += speed.values();
        //S_M82A1的affectType为ALL，同时影响攻速与移速
        S_M82A1 s = target.buff(S_M82A1.class);
        if (s != null)
            i += s.values();
        return (int) i;
    }
    private boolean hasMoveSpeed(){
        return moveSpeedAll() != 0;
    }
    private int moveSpeedWorking(){
        float i = 0;
        for (MoveSpeed speed : target.buffs(MoveSpeed.class))
            if (speed.working())
                i += speed.values();
        S_M82A1 s = target.buff(S_M82A1.class);
        if (s != null && s.working())
            i += s.values();
        return (int) i;
    }
    private int vulnerAll(){
        float i = 0;
        for (Vulnerability vulner : target.buffs(Vulnerability.class))
            i += vulner.values();
        return (int) i;
    }
    private boolean hasVulner(){
        return vulnerAll() != 0;
    }
    private int vulnerWorking(){
        float i = 0;
        for (Vulnerability vulner : target.buffs(Vulnerability.class))
            if (vulner.working())
                i += vulner.values();
        return (int) i;
    }
    private int weaklyAll(){
        float i = 0;
        for (Weakly weakly : target.buffs(Weakly.class))
            i += weakly.values();
        return (int) i;
    }
    private boolean hasWeakly(){
        return weaklyAll() != 0;
    }
    private int weaklyWorking(){
        float i = 0;
        for (Weakly weakly : target.buffs(Weakly.class))
            if (weakly.working())
                i += weakly.values();
        return (int) i;
    }
}
