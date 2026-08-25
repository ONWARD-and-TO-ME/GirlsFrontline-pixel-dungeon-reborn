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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.input.GameAction;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class ActionIndicator extends Tag {

    Image icon;

	private static Action action;
	private static final ArrayList<Action> actions = new ArrayList<>();
	public static ActionIndicator instance;

	public ActionIndicator() {
		super( 0xFFFF4C );

		instance = this;

		setSize( SIZE, SIZE );
		visible = false;
	}
	
	@Override
	public GameAction keyAction() {
		return SPDAction.TAG_ACTION;
	}
	
	@Override
	public void destroy() {
		super.destroy();
		instance = null;
	}
	
	@Override
	protected synchronized void layout() {
		super.layout();
		
		if (icon != null){
			if (!flipped)   icon.x = x + (SIZE - icon.width()) / 2f + 1;
			else            icon.x = x + width - (SIZE + icon.width()) / 2f - 1;
			icon.y = y + (height - icon.height()) / 2f;
			PixelScene.align(icon);
			if (!members.contains(icon))
				add(icon);
		}
	}
	
	private boolean needsLayout = false;
	
	@Override
	public synchronized void update() {
		super.update();

		if (!Dungeon.hero.ready){
			if (icon != null) icon.alpha(0.5f);
		} else {
			if (icon != null) icon.alpha(1f);
		}

		if (!visible && action != null){
			visible = true;
			updateIcon();
			flash();
		} else {
			visible = action != null;
		}
		
		if (needsLayout){
			layout();
			needsLayout = false;
		}
	}

	@Override
	protected void onClick() {
		if (action != null && Dungeon.hero.ready) {
			action.doAction();
		}
	}
	@Override
	public boolean onLongClick(){
		if (actions.size() <= 1)
			return false;
		else{
			if (action == null){
				action = actions.get(0);
				return false;
			}
			int index = actions.indexOf(action);
			if (index == actions.size() - 1)
				index = 0;
			action = actions.get(index);
			return true;
		}
	}
	@Override
	protected String hoverText() {
		String text = (action == null ? null : action.actionName());
		if (text != null){
			return Messages.titleCase(text);
		} else {
			return null;
		}
	}

	public static void setAction(Action action){
		ActionIndicator.action = action;
		if (!actions.contains(action))
			actions.add(action);
		updateIcon();
	}
	public static void clearAction(Action action){
		if (checkAction(action))
			ActionIndicator.action = null;
        actions.remove(action);
	}
	public static boolean checkAction(Action action){
		return ActionIndicator.action == action;
	}
	public static void clearAll(){
		action = null;
		actions.clear();
	}
	public static void updateIcon(){
		if (instance != null){
			synchronized (instance) {
				if (instance.icon != null) {
					instance.icon.killAndErase();
					instance.icon = null;
				}
				if (action != null) {
					instance.icon = action.actionIcon();
					instance.needsLayout = true;
				}
			}
		}
	}

	public interface Action{

		String actionName();

		Image actionIcon();

		void doAction();

	}

}
