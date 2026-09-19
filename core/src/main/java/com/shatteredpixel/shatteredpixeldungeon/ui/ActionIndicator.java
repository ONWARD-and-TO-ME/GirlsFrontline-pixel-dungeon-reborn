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

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.Color;
import com.watabou.input.GameAction;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;

import java.util.ArrayList;

public class ActionIndicator extends Tag {

	//默认黄色 / 换枪按钮粉色（与星之护盾光环同色 0xFF99CC）
	private static final int COLOR_DEFAULT = Color.YELLOW;

	//动作按钮能量加载条：空槽白色，填充蓝色
	private static final int COLOR_CHARGE_BG   = 0xFFFFFFFF;
	private static final int COLOR_CHARGE_FILL = 0xFF3DA3FF;
	private static final float CHARGE_BAR_HEIGHT = 2f;

    Image icon;
	int bgColor = COLOR_DEFAULT;

	//动作按钮上的能量加载条（可选，由 Action.actionCharge() 驱动）
	private ColorBlock chargeBg;
	private ColorBlock chargeFill;
	private float chargeX, chargeY, chargeW;
	private static Action action;
	private static final ArrayList<Action> actions = new ArrayList<>();
	public static ActionIndicator instance;

	public ActionIndicator() {
		super( COLOR_DEFAULT );

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
	protected NinePatch otherBG;
	@Override
	protected void createChildren() {
		otherBG = Chrome.get( Chrome.Type.TAG );
		otherBG.hardlight( Color.DARK_PURPLE );
		add(otherBG);
		super.createChildren();

		chargeBg = new ColorBlock( 1, 1, COLOR_CHARGE_BG );
		chargeFill = new ColorBlock( 1, 1, COLOR_CHARGE_FILL );
		chargeBg.visible = chargeFill.visible = false;

	}

	@Override
	protected synchronized void layout() {
		super.layout();
		otherBG.x = bg.x;
		otherBG.y = bg.y;
		otherBG.size( width, height );
		otherBG.visible = actions.size() > 1;
		if (otherBG.visible){
			if (!flipped) {
				bg.x -= 3;
				hotArea.x -= 3;
			}
			else {
				bg.x += 3;
				hotArea.x += 3;
			}
			bg.y -= 3;
			hotArea.y -= 3;
			hotArea.height += 3;
			hotArea.width += 3;
		}
		setColor(bgColor);
		if (icon != null){
			if (!flipped) {
				icon.x = bg.x + (SIZE - icon.width()) / 2f + 1;
			}
			else {
				icon.x = bg.x + width - (SIZE + icon.width()) / 2f - 1;
			}
			icon.y = bg.y + (height - icon.height()) / 2f;
			PixelScene.align(icon);
			if (!members.contains(icon))
				add(icon);
		}

		//能量加载条：置于按钮底部居中，位于图标之上渲染
		chargeW = SIZE - 8;
		chargeX = bg.x + (width - chargeW) / 2f;
		chargeY = bg.y + height - 4;
		chargeBg.x = chargeFill.x = chargeX;
		chargeBg.y = chargeFill.y = chargeY;
		chargeBg.size( chargeW, CHARGE_BAR_HEIGHT );
		if (!members.contains(chargeBg)){
			add(chargeBg);
			add(chargeFill);
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

		//更新动作按钮上的能量加载条（-1 表示不显示）
		float charge = (action == null ? -1f : action.actionCharge());
		boolean barVisible = visible && charge >= 0f && chargeW > 0f;
		chargeBg.visible = chargeFill.visible = barVisible;
		if (barVisible){
			if (charge > 1f) charge = 1f;
			//向上取整到最近像素，保证窄条也至少有1像素填充
			float pixelWidth = chargeW;
			Camera cam = camera();
			if (cam != null) pixelWidth *= cam.zoom;
			float fillW = chargeW * (float)Math.ceil( charge * pixelWidth ) / pixelWidth;
			chargeFill.size( fillW, CHARGE_BAR_HEIGHT );
		}

		if (needsLayout){
			layout();
			needsLayout = false;
		}
	}

	@Override
	protected void onClick() {
		if (action != null && Dungeon.hero.ready)
			action.doAction();
	}
	@Override
	public boolean onLongClick(){
		if (actions.size() <= 1)
			return false;
		else{
			if (action == null){
				setAction( actions.get(0) );
				return false;
			}
			int index = actions.indexOf(action);
			if (index == actions.size() - 1)
				index = 0;
			else
				index++;
			setAction( actions.get(index) );
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
		actions.remove(action);
		if (checkAction(action)) {
			if (actions.isEmpty())
				ActionIndicator.action = null;
			else
				ActionIndicator.action = actions.get(0);
		}
		updateIcon();
	}
	public static boolean checkAction(Action action){
		return ActionIndicator.action == action;
	}
	public static void clearAll(){
		action = null;
		actions.clear();
		updateIcon();
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
					instance.bgColor = action.bgColor();
					instance.needsLayout = true;
				}
			}
		}
	}
	public interface Action{

		String actionName();

		Image actionIcon();

		void doAction();
		default int bgColor(){
			return COLOR_DEFAULT;
		}

		/**
		 * 动作按钮上加载条的填充比例。
		 * @return 0~1 显示加载条（空槽白色、填充蓝色）；负数（默认 -1）表示不显示加载条。
		 */
		default float actionCharge(){
			return -1f;
		}

	}

}
