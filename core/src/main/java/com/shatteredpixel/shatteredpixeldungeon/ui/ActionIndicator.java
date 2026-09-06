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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GunSwap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.input.GameAction;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class ActionIndicator extends Tag {

	//默认黄色 / 换枪按钮粉色（与星之护盾光环同色 0xFF99CC）
	private static final int COLOR_DEFAULT = 0xFFFF4C;
	private static final int COLOR_GUNSWAP = 0xFF99CC;

    Image icon;

	//主指示器槽位（Combo / Momentum / 换枪 GunSwap 等共用，长按轮换）
	private static Action action;
	private static final ArrayList<Action> actions = new ArrayList<>();
	public static ActionIndicator instance;

	//天狼星心脏专属独立指示器槽位，避免与换枪按钮占位冲突
	private static Action siriusAction;
	private static final ArrayList<Action> siriusActions = new ArrayList<>();
	private static ActionIndicator siriusInstance;

	//当前实例是否为天狼星心脏槽位
	private boolean siriusSlot = false;

	public ActionIndicator() {
		super( 0xFFFF4C );

		//仅首个（主）指示器占用 instance，避免天狼星心脏实例覆盖主指示器引用
		if (instance == null) {
			instance = this;
		}

		setSize( SIZE, SIZE );
		visible = false;
	}

	//标记当前实例为天狼星心脏专用槽位
	public void setSiriusSlot( boolean v ){
		this.siriusSlot = v;
		if (v) {
			siriusInstance = this;
		}
	}

	private Action curAction(){
		return siriusSlot ? siriusAction : action;
	}
	private ArrayList<Action> curActions(){
		return siriusSlot ? siriusActions : actions;
	}
	private void setCurAction( Action a ){
		if (siriusSlot) siriusAction = a;
		else            action = a;
	}

	@Override
	public GameAction keyAction() {
		return SPDAction.TAG_ACTION;
	}
	
	@Override
	public void destroy() {
		super.destroy();
		if (siriusSlot) {
			siriusInstance = null;
		} else if (instance == this) {
			instance = null;
		}
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

		Action cur = curAction();
		if (!visible && cur != null){
			visible = true;
			updateIconForThis();
			flash();
		} else {
			visible = cur != null;
		}
		
		if (needsLayout){
			layout();
			needsLayout = false;
		}
	}

	@Override
	protected void onClick() {
		Action cur = curAction();
		if (cur != null && Dungeon.hero.ready) {
			cur.doAction();
		}
	}
	@Override
	public boolean onLongClick(){
		ArrayList<Action> curList = curActions();
		if (curList.size() <= 1)
			return false;
		else{
			Action cur = curAction();
			if (cur == null){
				setCurAction( curList.get(0) );
				return false;
			}
			int index = curList.indexOf(cur);
			if (index == curList.size() - 1)
				index = 0;
			else
				index++;
			setCurAction( curList.get(index) );
			return true;
		}
	}
	@Override
	protected String hoverText() {
		Action cur = curAction();
		String text = (cur == null ? null : cur.actionName());
		if (text != null){
			return Messages.titleCase(text);
		} else {
			return null;
		}
	}

	//更新当前实例自己的图标
	private void updateIconForThis(){
		synchronized (this) {
			if (icon != null) {
				icon.killAndErase();
				icon = null;
			}
			Action cur = curAction();
			if (cur != null) {
				icon = cur.actionIcon();
				needsLayout = true;
			}
		}
	}

	// ===== 主指示器槽位静态 API（保持原有行为，供 Combo/Momentum/GunSwap 等使用） =====

	public static void setAction(Action action){
		ActionIndicator.action = action;
		if (!actions.contains(action))
			actions.add(action);
		//换枪按钮用粉色（与星之护盾同色），其余技能恢复默认黄色
		if (instance != null) {
			instance.setColor( action instanceof GunSwap ? COLOR_GUNSWAP : COLOR_DEFAULT );
		}
		updateIcon();
	}
	public static void clearAction(Action action){
		if (checkAction(action))
			ActionIndicator.action = null;
        actions.remove(action);
		//清空后恢复默认黄色
		if (instance != null && ActionIndicator.action == null) {
			instance.setColor( COLOR_DEFAULT );
		}
	}
	public static boolean checkAction(Action action){
		return ActionIndicator.action == action;
	}
	//指示器当前是否空闲（未被任何动作占用）
	public static boolean actionIsFree(){
		return action == null;
	}
	public static void clearAll(){
		action = null;
		actions.clear();
	}
	public static void updateIcon(){
		if (instance != null){
			instance.updateIconForThis();
		}
	}

	// ===== 天狼星心脏专属槽位静态 API =====

	public static void setSiriusAction(Action a){
		siriusAction = a;
		if (!siriusActions.contains(a))
			siriusActions.add(a);
		if (siriusInstance != null) {
			siriusInstance.updateIconForThis();
		}
	}
	public static void clearSiriusAction(Action a){
		if (checkSiriusAction(a))
			siriusAction = null;
		siriusActions.remove(a);
	}
	public static boolean checkSiriusAction(Action a){
		return siriusAction == a;
	}
	public static boolean siriusActionIsFree(){
		return siriusAction == null;
	}
	public static void updateSiriusIcon(){
		if (siriusInstance != null) {
			siriusInstance.updateIconForThis();
		}
	}

	public interface Action{

		String actionName();

		Image actionIcon();

		void doAction();

	}

}