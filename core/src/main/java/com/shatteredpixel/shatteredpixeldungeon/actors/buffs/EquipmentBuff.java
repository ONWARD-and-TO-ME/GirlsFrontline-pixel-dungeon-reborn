package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;

public class EquipmentBuff extends ItemBuff {
    public void setEquipment( EquipableItem equipment ) {
        if (item != null)
            item.ownerBuff = null;
        item = equipment;
        if (item != null)
            item.ownerBuff = this;
    }
    public EquipableItem getEquipment() {
        return (EquipableItem) item;
    }
    @SuppressWarnings("unchecked")
    public <T extends EquipableItem> T getEquipment( Class<T> ignored ) {
        return (T) item;
    }
    @Override
    public boolean attachTo(Char target) {
        if (item != null && (target.buff(LostInventory.class) == null || item.keptThoughLostInvent))
            getEquipment().activate(target);
        return super.attachTo(target);
    }
}
