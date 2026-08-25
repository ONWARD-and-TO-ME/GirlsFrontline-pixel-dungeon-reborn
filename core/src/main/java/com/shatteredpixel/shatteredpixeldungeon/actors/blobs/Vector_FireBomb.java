package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner.ThrowingSkill;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class Vector_FireBomb extends Blob {
	protected ArrayList<ThrowingSkill.FireBomb> fireBombs = new ArrayList<>();
	public Vector_FireBomb add(ThrowingSkill.FireBomb fireBomb){
		fireBombs.add(fireBomb);
		return this;
	}
	@Override
	protected float TICK(){
		return 0.5F;
	}
	@Override
	public boolean act() {

		for (ThrowingSkill.FireBomb f : fireBombs.toArray(new ThrowingSkill.FireBomb[0])){
			f.affect();
			if (f.times() < 0.001F)
				fireBombs.remove(f);
		}

		return super.act();
	}
	private static final String AFFECT_FIRE_BOMB = "AFFECT_FIRE_BOMB";
	@Override
	public void storeInBundle( Bundle bundle ){
		super.storeInBundle(bundle);
		bundle.put(AFFECT_FIRE_BOMB, fireBombs);
	}
	@Override
	public void restoreFromBundle( Bundle bundle ){
		super.restoreFromBundle(bundle);
		fireBombs = bundle.getArrayList(AFFECT_FIRE_BOMB, ThrowingSkill.FireBomb.class);
	}
	@Override
	protected void evolve() {
		int cell;
		for (int i = area.left-1; i <= area.right; i++)
			for (int j = area.top-1; j <= area.bottom; j++) {
				cell = i + j * Dungeon.level.width();
				if (cur[cell] > 0)
					volume += off[cell] = cur[cell] - 1;
				else
					off[cell] = 0;
			}
	}
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.pour(SmokeParticle.SPEW, 0.05f);
	}
}
