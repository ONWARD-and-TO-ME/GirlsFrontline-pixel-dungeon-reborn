package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GirlsFrontlinePixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

/*超级小爱专属被动：可随时启停的飞行。
默认开启/降落消耗2回合，飞行时移动速度-20%；视野可穿透高草。
可通过T3天赋「飞升自由」缩短开启耗时并提升飞行移速。
飞行能量：上限200，开启时扣除门槛费（默认20点），飞行中按移动格数消耗（每格1点），
默认关闭后不再自然恢复，只能用炼金能量装填；能量耗尽强制降落。
T3天赋「氘核光束」可降低能量消耗与门槛费、优化炼金装填比，+3时每回合自然恢复0.2%。
飞行状态下饱食度消耗翻倍。
飞升自由 +3 且飞行中 每次移动最多跨 5 格 ，总耗时 = 单格移动耗时（即每格仅需原来 1/5 时间）
 移动动画 精灵从起点滑到终点仅 0.08s （正常单格为 0.1s），形成残影感
 速度线 沿冲刺方向每格喷射冷白色短线，向身后飞散淡出
 遇阻处理 路径上有怪物/不可通行格时冲刺提前结束*/
public class SuperAiFlight extends Buff implements ActionIndicator.Action {

	{
		type = buffType.NEUTRAL;
	}

	private boolean active = false;

	//飞行能量（单位：格）
	public static final float MAX_ENERGY = 200f;
	//开启飞行时扣除的默认门槛能量
	private static final float DEFAULT_TAKEOFF_COST = 20f;
	//炼金能量默认装填比：1点炼金能量 = 5点飞行能量
	public static final int DEFAULT_ALCHEMY_RATIO = 5;
	//氘核光束+3时每回合自然恢复上限的0.2%
	private static final float REGEN_PERCENT = 0.002f;

	private float energy = MAX_ENERGY;
	//上一次结算时的位置，用于按格计算飞行能量消耗
	private int lastPos = -1;

	private static final String ACTIVE = "active";
	private static final String ENERGY = "energy";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(ACTIVE, active);
		bundle.put(ENERGY, energy);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		active = bundle.getBoolean(ACTIVE);
		//旧存档无能量字段时视为满能量
		energy = bundle.contains(ENERGY) ? bundle.getFloat(ENERGY) : MAX_ENERGY;
	}

	@Override
	public boolean attachTo(Char target) {
		if (super.attachTo(target)) {
			if (active) {
				target.flying = true;
			}
			lastPos = target.pos;
			//每回合结算一次能量（按格消耗，不再自然恢复）
			spend(TICK);
			return true;
		}
		return false;
	}

	@Override
	public boolean act() {
		if (active) {
			//按格计算：根据自上次结算以来移动的格数消耗飞行能量（受氘核光束消耗倍率影响）
			int moved = movedTiles();
			if (moved > 0) {
				energy -= moved * drainMultiplier();
				if (energy <= 0f) {
					energy = 0f;
					forceLand();
				}
			}
		}
		//氘核光束+3：获得自行恢复能力，每回合恢复上限的0.2%（飞行/非飞行状态均生效）
		if (hasDeuteriumBeam(3) && energy < MAX_ENERGY) {
			energy = Math.min(MAX_ENERGY, energy + MAX_ENERGY * REGEN_PERCENT);
			if (!active) ActionIndicator.updateIcon();
		}
		//无氘核光束+3时不自然恢复，只能通过炼金能量装填
		lastPos = target != null ? target.pos : -1;
		spend(TICK);
		return true;
	}

	private int deuteriumPoints() {
		return (target instanceof Hero) ? ((Hero) target).pointsInTalent(Talent.DEUTERIUM_BEAM) : 0;
	}

	private boolean hasDeuteriumBeam(int points) {
		return deuteriumPoints() >= points;
	}

	//开启飞行的门槛能量：默认20点，氘核光束+1为15点，+2及以上为10点
	public float takeoffCost() {
		switch (deuteriumPoints()) {
			case 1:  return 15f;
			case 2:
			case 3:  return 10f;
			default: return DEFAULT_TAKEOFF_COST;
		}
	}

	//飞行能量消耗倍率：默认100%，氘核光束+1为90%，+2及以上为80%
	public float drainMultiplier() {
		switch (deuteriumPoints()) {
			case 1:  return 0.9f;
			case 2:
			case 3:  return 0.8f;
			default: return 1f;
		}
	}

	//炼金能量装填比：默认1:5，氘核光束+2及以上提升为1:10
	public int alchemyRatio() {
		return hasDeuteriumBeam(2) ? DEFAULT_ALCHEMY_RATIO * 2 : DEFAULT_ALCHEMY_RATIO;
	}

	//计算自上次结算以来移动的格数（曼哈顿距离，适用于四向移动网格）
	private int movedTiles() {
		if (target == null || lastPos < 0) {
			return 0;
		}
		int cur = target.pos;
		if (cur == lastPos) {
			return 0;
		}
		int w = Dungeon.level.width();
		int dx = Math.abs((cur % w) - (lastPos % w));
		int dy = Math.abs((cur / w) - (lastPos / w));
		return dx + dy;
	}

	@Override
	public void detach() {
		if (active && target != null) {
			target.flying = false;
			if (GirlsFrontlinePixelDungeon.scene() instanceof GameScene && Dungeon.level != null) {
				Dungeon.level.occupyCell(target);
			}
		}
		active = false;
		super.detach();
	}

	@Override
	public void fx(boolean on) {
		if (on) {
			ActionIndicator.setAction(this);
			if (active && target.sprite != null) {
				target.sprite.add(CharSprite.State.LEVITATING);
			}
		} else {
			ActionIndicator.clearAction(this);
			if (target != null && target.sprite != null) {
				target.sprite.remove(CharSprite.State.LEVITATING);
			}
		}
	}

	public boolean isActive() {
		return active;
	}

	//切换飞行状态（默认开启/降落消耗2回合，受「飞升自由」天赋影响可缩短至1回合或免费）
	public void toggle() {
		float cost = takeoffCost();
		//能量不足以支付起飞门槛时不允许起飞
		if (!active && energy <= cost) {
			GLog.w(Messages.get(this, "no_energy", (int) cost));
			return;
		}
		active = !active;
		if (target != null) {
			if (active) {
				target.flying = true;
				//开启飞行的门槛收费
				energy = Math.max(0f, energy - cost);
				if (target.sprite != null) {
					target.sprite.add(CharSprite.State.LEVITATING);
				}
			} else {
				target.flying = false;
				if (target.sprite != null) {
					target.sprite.remove(CharSprite.State.LEVITATING);
				}
				//降落时触发脚下地形（陷阱、水等）
				if (GirlsFrontlinePixelDungeon.scene() instanceof GameScene && Dungeon.level != null) {
					Dungeon.level.occupyCell(target);
				}
			}
		}
		ActionIndicator.updateIcon();
		Sample.INSTANCE.play(Assets.Sounds.MASTERY);

		//消耗开启/降落回合（飞升自由+2及以上免费）
		int turns = turnCost();
		if (turns > 0 && target instanceof Hero) {
			((Hero) target).spendAndNext(turns);
		}
	}

	//能量耗尽时的强制降落（不消耗回合）
	private void forceLand() {
		if (!active) {
			return;
		}
		active = false;
		if (target != null) {
			target.flying = false;
			if (target.sprite != null) {
				target.sprite.remove(CharSprite.State.LEVITATING);
			}
			if (GirlsFrontlinePixelDungeon.scene() instanceof GameScene && Dungeon.level != null) {
				Dungeon.level.occupyCell(target);
			}
		}
		ActionIndicator.updateIcon();
		GLog.w(Messages.get(this, "energy_depleted"));
	}

	//开启/降落所需回合数：默认2回合，飞升自由+1为1回合，+2及以上免费
	public int turnCost() {
		int pts = (target instanceof Hero) ? ((Hero) target).pointsInTalent(Talent.FLIGHT_FREEDOM) : 0;
		switch (pts) {
			case 1:  return 1;
			case 2:
			case 3:  return 0;
			default: return 2;
		}
	}

	//飞行时移动速度乘数：默认-20%，飞升自由+1为+10%，+2为+35%，+3为+80%
	public float speedMultiplier() {
		int pts = (target instanceof Hero) ? ((Hero) target).pointsInTalent(Talent.FLIGHT_FREEDOM) : 0;
		switch (pts) {
			case 1:  return 1.1f;
			case 2:  return 1.35f;
			case 3:  return 1.8f;
			default: return 0.8f;
		}
	}

	//飞升自由+3且飞行激活时，进入高速冲刺状态
	public boolean isDashing() {
		int pts = (target instanceof Hero) ? ((Hero) target).pointsInTalent(Talent.FLIGHT_FREEDOM) : 0;
		return active && pts >= 3;
	}

	//高速冲刺一次性跨越的最大格数
	public int dashDistance() {
		return 5;
	}

	//剩余能量比例（0~1），供动作按钮加载条显示
	public float energyFraction() {
		return Math.max(0f, Math.min(1f, energy / MAX_ENERGY));
	}

	//当前剩余能量（向上取整显示）
	public int energyLeft() {
		return (int) Math.ceil(energy);
	}

	//补满飞行能量所需的炼金能量（向上取整，装填比受「氘核光束」天赋影响）
	public int alchemyCostToFull() {
		return (int) Math.ceil((MAX_ENERGY - energy) / (float) alchemyRatio());
	}

	//使用炼金能量补满飞行能量
	private void refillWithAlchemy() {
		int cost = alchemyCostToFull();
		if (cost <= 0) {
			return;
		}
		if (Dungeon.energy < cost) {
			GLog.w(Messages.get(this, "no_alchemy", cost, Dungeon.energy));
			return;
		}
		Dungeon.energy -= cost;
		energy = MAX_ENERGY;
		ActionIndicator.updateIcon();
		Sample.INSTANCE.play(Assets.Sounds.MASTERY);
		GLog.p(Messages.get(this, "refill_done", cost));
	}

	//起飞/装填选择窗口
	private void showTakeoffWindow(boolean empty) {
		int cost = alchemyCostToFull();
		int ratio = alchemyRatio();
		String msg = Messages.get(this, "refill_msg",
				energyLeft(), (int) MAX_ENERGY, ratio, ratio, cost, Dungeon.energy);
		String refillOpt = Messages.get(this, "opt_refill", cost);

		if (empty) {
			GameScene.show(new WndOptions(
					Messages.get(this, "refill_title"),
					msg,
					refillOpt,
					Messages.get(this, "opt_cancel")) {
				@Override
				protected void onSelect(int index) {
					if (index == 0) {
						refillWithAlchemy();
					}
				}
			});
		} else {
			GameScene.show(new WndOptions(
					Messages.get(this, "refill_title"),
					msg,
					Messages.get(this, "opt_takeoff", turnCost(), (int) takeoffCost(), energyLeft()),
					refillOpt,
					Messages.get(this, "opt_cancel")) {
				@Override
				protected void onSelect(int index) {
					if (index == 0) {
						toggle();
					} else if (index == 1) {
						refillWithAlchemy();
					}
				}
			});
		}
	}

	//ActionIndicator.Action 接口实现
	@Override
	public String actionName() {
		return Messages.get(this, "action_name");
	}

	@Override
	public Image actionIcon() {
		return new HeroIcon(HeroIcon.HEROIC_LEAP);
	}

	@Override
	public void doAction() {
		if (active) {
			//飞行中：点击直接降落
			toggle();
			return;
		}
		if (energy <= takeoffCost()) {
			//能量不足以支付起飞门槛，只能装填
			showTakeoffWindow(true);
		} else if (energy >= MAX_ENERGY) {
			//满能量：直接起飞
			toggle();
		} else {
			//非满能量：起飞或装填
			showTakeoffWindow(false);
		}
	}

	@Override
	public float actionCharge() {
		return energyFraction();
	}

	@Override
	public int bgColor() {
		return 0xFF99CC;
	}
}
