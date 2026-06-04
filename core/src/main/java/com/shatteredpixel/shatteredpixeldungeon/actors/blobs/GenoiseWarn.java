/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2018 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;



import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Genoise;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ElpheltSprite;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class GenoiseWarn extends Blob {

	//cosmetic blob, used to warn noobs that goo's pump up should, in fact, be avoided.

	{
		//this one needs to act after the Goo
		actPriority = MOB_PRIO - 1;
	}
	protected ArrayList<Genoise> genoises = new ArrayList<>();
	protected ArrayList<Float> fuseTimes = new ArrayList<>();
	public static Genoise genoise = null;
	public static float fuseTime = -100F;
	@Override
	public void set(){
		if (genoise != null && fuseTime != -100F){
			if (genoises == null)
				genoises = new ArrayList<>();
			if (fuseTimes == null)
				fuseTimes = new ArrayList<>();
			genoises.add(genoise);
			fuseTimes.add(fuseTime);
			genoise = null;
			fuseTime = -100F;
		}
	}
	@Override
    public boolean act() {
		super.act();
		while (genoises.size() < fuseTimes.size()){
			fuseTimes.remove(fuseTimes.size() - 1);
		}
		while (fuseTimes.size() < genoises.size()){
			genoises.remove(genoises.size() - 1);
		}
		int i = 0;
		while (i < fuseTimes.size()){
			fuseTimes.set(i, fuseTimes.get(i)-1);
			if (fuseTimes.get(i) < 0) {
				genoises.remove(i).explore();
				fuseTimes.remove(i);
				continue;
			}
			i++;
		}
		return true;
	}

	@Override
	protected void evolve() {

		int cell;

		for (int i = area.left; i < area.right; i++){
			for (int j = area.top; j < area.bottom; j++){
				cell = i + j*Dungeon.level.width();
				off[cell] = cur[cell] > 0 ? cur[cell] - 1 : 0;

				if (off[cell] > 0) {
					volume += off[cell];
				}
			}
		}

	}

	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.pour(ElpheltSprite.GenoiseParticle.FACTORY, 0.03f );
	}

	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
	private final static String GENOISE = "GENOISES";
	private final static String FUSE_TIME = "FUSE_TIME";
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(GENOISE))
			genoises = bundle.getBundlableArrayList(GENOISE, Genoise.class);
		else
			genoises = new ArrayList<>();
		fuseTimes = new ArrayList<>();
		if (bundle.contains(FUSE_TIME)){
			for (float i : bundle.getFloatArray(FUSE_TIME)) {
				fuseTimes.add(i);
			}
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(GENOISE, genoises);
		float[] fuse = new float[fuseTimes.size()];
		for (int i = 0; i < fuseTimes.size(); i++)
			fuse[i] = fuseTimes.get(i);
		bundle.put(FUSE_TIME, fuse);
	}
}

