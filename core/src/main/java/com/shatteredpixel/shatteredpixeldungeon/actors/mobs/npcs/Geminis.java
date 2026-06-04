/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.fairyitems.Gemini;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GhostSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public abstract class Geminis extends NPC {
	
	{
		spriteClass = GhostSprite.class;
		
		HP = HT = 10;
		defenseSkill = 1;
		
		alignment = Alignment.ALLY;
		intelligentAlly = true;
		state = HUNTING;
		
		WANDERING = new Wandering();
		
		//before other mobs
		actPriority = MOB_PRIO + 1;
	}
	
	protected Geminis twin;
	protected int twinID;

    public void twin(Geminis twin){
        this.twin = twin;
        twinID = twin.id();
    }
	@Override
	protected boolean act() {


		if ( twin == null ){
            int times = 5;
            do {
                twin = (Geminis) Actor.findById(twinID);
                times--;
                spend(TICK);
            }while (times > 0 && twin == null);
			if ( twin == null ){
				destroy();
				sprite.die();
				return true;
			}
		}
		
		return super.act();
	}
	
	@Override
	public void die(Object cause) {
        if (twin == null)
            twin = (Geminis) Actor.findById(twinID);
        if (twin != null && twin.isAlive())
            twin.die(this);
        super.die(cause);
	}
	
	private static final String TWIN_ID	= "twin_id";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( TWIN_ID, twinID );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		twinID = bundle.getInt( TWIN_ID );
	}

    @Override
    public void damage(int dmg, Object src) {
        if (twin == null)
            twin = (Geminis) Actor.findById(twinID);
		if (twin != null && !(src instanceof Geminis)) {
			if (src instanceof Mob)
				((Mob) src).aggro(twin);
			else if (WorkingEnemy instanceof Mob && !(WorkingEnemy instanceof Geminis))
				((Mob) WorkingEnemy).aggro(twin);
			dmg /= 2;
			twin.damage(dmg, this, WorkingEnemy);
		}
		WorkingEnemy = null;
        super.damage(dmg, src);
    }

	@Override
	public abstract int damageRoll();
	
	@Override
	public abstract int attackSkill( Char target );
	
	@Override
	public abstract int defenseSkill(Char enemy);
	
	@Override
	public abstract int drRoll();

	@Override
	public abstract float speed();

    @Override
    public int attackProc( Char enemy, int damage ) {
        if (twin == null)
            twin = (Geminis) Actor.findById(twinID);
        if (enemy instanceof Mob && twin != null) {
            ((Mob)enemy).aggro( twin );
        }
        return super.attackProc( enemy, damage );
    }

	{
		immunities.add( ToxicGas.class );
		immunities.add( CorrosiveGas.class );
		immunities.add( Burning.class );
		immunities.add( AllyBuff.class );
	}
	
	private class Wandering extends Mob.Wandering{
		
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (!enemyInFOV && enemy == null){
                if (twin == null && (twin = (Geminis) Actor.findById(twinID)) == null )
                    die(Geminis.this);
                if (twin.enemy != null && twin.enemy.isAlive()) {
                    enemy = twin.enemy;
                    target = twin.target;
                    Geminis.this.spend(0.5F);
                    return true;
                }
                GeminiShield shield;
                GeminiMissile missile;
                if (Geminis.this instanceof GeminiShield){
                    shield = (GeminiShield) Geminis.this;
                    missile= (GeminiMissile) twin;
                }else {
                    shield = (GeminiShield) twin;
                    missile= (GeminiMissile) Geminis.this;
                }
				int s = 0;
				if (missile.buff( Barrier.class ) != null)
					s = missile.buff( Barrier.class ).shielding();
				Buff.affect(Dungeon.hero, Gemini.Contract.class).add(shield.HP , missile.HP + Random.Int(s) / 2);
                Geminis.this.destroy();
                sprite.die();
                twin.destroy();
                twin.sprite.die();
				return true;
			} else {
				return super.act(enemyInFOV, justAlerted);
			}
		}
		
	}
	
}
