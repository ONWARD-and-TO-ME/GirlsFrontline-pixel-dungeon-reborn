package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

import java.util.Collections;
import java.util.LinkedList;

public class DandelionShield extends ShieldBuff {
	
	{
		type = buffType.POSITIVE;
	}
	private final LinkedList<Integer> list = new LinkedList<>(Collections.nCopies(61, 0));
	private int curShield = 61;
	@Override
	public void incShield(int amt) {
		if(target instanceof Hero)
			Talent.onShielding((Hero)target);

		if (target != null) target.needsShieldUpdate = true;

		if (curShield > 60)
			curShield = 60;
		list.set(60, list.get(60) + amt);
	}

	@Override
	public boolean act() {
		list.removeFirst();
		list.addLast(0);
		curShield--;
		if (curShield < 0)
			curShield = 0;
		
		if (shielding() <= 0)
			detach();
		
		spend( TICK );
		
		return true;
	}
	public int absorbDamage( int dmg ){
		while (curShield <= 60){
			if (list.get(curShield) > dmg){
				list.set(curShield, list.get(curShield) - dmg);
				dmg = 0;
				break;
			}
			else {
				dmg -= list.get(curShield);
				list.set(curShield, 0);
				curShield++;
			}
		}
		if (curShield >= 61)
			detach();

		if (target != null) target.needsShieldUpdate = true;
		return dmg;
	}
	@Override
	public int shielding(){
		int shielding = 0;
		for (int i : list)
			shielding += i;
		return shielding;
	}
	private int nextRemove(){
		return list.get(0);
	}
	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.SHIELDED);
		else target.sprite.remove(CharSprite.State.SHIELDED);
	}
	
	@Override
	public int icon() {
		return BuffIndicator.ARMOR;
	}
	
	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0.5f, 1f, 2f);
	}

	@Override
	public String iconTextDisplay() {
		return Integer.toString(shielding());
	}
	
	@Override
	public String toString() {
		return Messages.get(this, "name");
	}
	
	@Override
	public String desc() {
		return Messages.get(this, "desc", shielding(), nextRemove());
	}
	private static final String curShieldNum	= "curShieldNum";
	private static final String AllShielding	= "AllShielding";
	private static final String SecondShielding	= "Shield_Num_";
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		Bundle b = new Bundle();
		for (int i = 0; i < 61; i++)
			b.put(SecondShielding + i, list.get(i));
		bundle.put(AllShielding, b);
		bundle.put(curShieldNum, curShield);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		Bundle b = bundle.getBundle(AllShielding);
		for (int i = 0; i < 61; i++)
			list.set(i, b.getInt(SecondShielding + i));
		curShield = bundle.contains(curShieldNum) ? bundle.getInt(curShieldNum) : 61;
	}
}
