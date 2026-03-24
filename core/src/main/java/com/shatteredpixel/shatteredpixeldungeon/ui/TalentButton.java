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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GirlsFrontlinePixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoTalent;
import com.shatteredpixel.shatteredpixeldungeon.windows.debugSelectTalent;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

import java.util.LinkedHashMap;

public class TalentButton extends Button {

	public static final int WIDTH = 20;
	public static final int HEIGHT = 26;

	int tier;
	Talent talent;
	int pointsInTalent;
	Mode mode;

	TalentIcon icon;
	Image bg;

	ColorBlock fill;

	public enum Mode {
		INFO,
		UPGRADE,
		METAMORPH_CHOOSE,
		METAMORPH_REPLACE,
        debug_replace
	}

	public TalentButton(int tier, Talent talent, int points, Mode mode){
		super();
		hotArea.blockLevel = PointerArea.NEVER_BLOCK;

		this.tier = tier;
		this.talent = talent;
		this.pointsInTalent = points;
		this.mode = mode;

		bg.frame(20*(talent.maxPoints()-1), 0, WIDTH, HEIGHT);

		icon = new TalentIcon( talent );
		add(icon);
	}

	@Override
	protected void createChildren() {
		super.createChildren();

		fill = new ColorBlock(0, 4, 0xFFFFFF44);
		add(fill);

		bg = new Image(Assets.Interfaces.TALENT_BUTTON);
		add(bg);
	}

	@Override
	protected void layout() {
		width = WIDTH;
		height = HEIGHT;

		super.layout();

		fill.x = x+2;
		fill.y = y + WIDTH - 1;
		fill.size( pointsInTalent/(float)talent.maxPoints() * (WIDTH-4), 5);

		bg.x = x;
		bg.y = y;

		icon.x = x + 2;
		icon.y = y + 2;
		PixelScene.align(icon);
	}

	@Override
	protected void onClick() {
		super.onClick();

		Window toAdd;
		if (mode == Mode.UPGRADE
				&& Dungeon.hero != null
				&& Dungeon.hero.isAlive()
				&& Dungeon.hero.talentPointsAvailable(tier) > 0
				&& Dungeon.hero.pointsInTalent(talent) < talent.maxPoints()){
			toAdd = new WndInfoTalent(talent, pointsInTalent, new WndInfoTalent.TalentButtonCallback() {

				@Override
				public String prompt() {
					return Messages.titleCase(Messages.get(WndInfoTalent.class, "upgrade"));
				}

				@Override
				public void call() {
					upgradeTalent();
				}
			});
		} else if (mode == Mode.METAMORPH_CHOOSE && Dungeon.hero != null && Dungeon.hero.isAlive()) {
			toAdd = new WndInfoTalent(talent, pointsInTalent, new WndInfoTalent.TalentButtonCallback() {

				@Override
				public String prompt() {
					return Messages.titleCase(Messages.get(ScrollOfMetamorphosis.class, "metamorphose_talent"));
				}

				@Override
				public void call() {
					if (ScrollOfMetamorphosis.WndMetamorphChoose.INSTANCE != null){
						ScrollOfMetamorphosis.WndMetamorphChoose.INSTANCE.hide();
					}
					GameScene.show(new ScrollOfMetamorphosis.WndMetamorphReplace(talent, tier));
				}
			});
		} else if (mode == Mode.METAMORPH_REPLACE && Dungeon.hero != null && Dungeon.hero.isAlive()) {
			toAdd = new WndInfoTalent(talent, pointsInTalent, new WndInfoTalent.TalentButtonCallback() {

				@Override
				public String prompt() {
					return Messages.titleCase(Messages.get(ScrollOfMetamorphosis.class, "metamorphose_talent"));
				}

				@Override
				public void call() {
					Talent replacing = ScrollOfMetamorphosis.WndMetamorphReplace.INSTANCE.replacing;
                    int point = 0;
					for (LinkedHashMap<Talent, Integer> tier : Dungeon.hero.talents){
						if (tier.containsKey(replacing)){
							LinkedHashMap<Talent, Integer> newTier = new LinkedHashMap<>();
							for (Talent t : tier.keySet()){
								if (t == replacing){
                                    //将旧天赋的加点记录
                                    point = tier.get(replacing);
                                    //暂时加入加点为0的天赋，我希望蜕变是获取天赋然后重新加点，从而获得那些在加点过程中获得的收益
									newTier.put(talent, 0);
                                    //被蜕变的是额外天赋，则直接修改额外天赋表，而非记录蜕变关系
                                    //蜕变的替换在生成初始表时，而额外天赋的加入是在生成完成后，时机滞后了，以蜕变逻辑实行，在读档的时候无法复原
                                    if (Dungeon.hero.addTalents.containsKey(replacing)){
                                        final int tierA = Dungeon.hero.addTalents.get(replacing);
                                        Dungeon.hero.addTalents.remove(replacing);
                                        Dungeon.hero.addTalents.put(talent, tierA);
                                    }
                                    //将这个蜕变关系记录到蜕变Map中，以用于在读档时恢复蜕变关系
									else if (!Dungeon.hero.metamorphedTalents.containsValue(replacing)){
                                        //未记录过旧天赋，即旧天赋未被蜕变过时，加入这组蜕变关系
										Dungeon.hero.metamorphedTalents.put(replacing, talent);

									//if what we're replacing is already a value, we need to simplify the data structure
									}
                                    else {
										//以旧天赋为索引寻找蜕变关系时，找到的原始天赋是新天赋，即蜕变回原始天赋时，移除蜕变关系，以简化结构
                                        //A-B-A时，将删除这组蜕变关系，而非保留A-A
										if (Dungeon.hero.metamorphedTalents.get(talent) == replacing){
											Dungeon.hero.metamorphedTalents.remove(talent);

										}
                                        //以旧天赋为索引寻找到的不是原始天赋时，由于读档是以原始天赋为索引，所以要更新蜕变关系
                                        //A-B-C，将更改为A-C
                                        else {
											for (Talent t2 : Dungeon.hero.metamorphedTalents.keySet()){
												if (Dungeon.hero.metamorphedTalents.get(t2) == replacing){
													Dungeon.hero.metamorphedTalents.put(t2, talent);
												}
											}
										}
									}

								}
                                else {
                                    //将未改变的天赋原封不动放到新天赋Map中
									newTier.put(t, tier.get(t));
								}
							}
                            //将新天赋Map替换掉旧的Map
							Dungeon.hero.talents.set(ScrollOfMetamorphosis.WndMetamorphReplace.INSTANCE.tier-1, newTier);
                            if (replacing == Talent.HOLD_FAST){
                                if (Dungeon.hero.belongings.secArmor != null){
                                    final Armor armor = Dungeon.hero.belongings.secArmor;
                                    Dungeon.hero.belongings.secArmor = null;
                                    armor.collect(Dungeon.hero.belongings.backpack);
                                    BrokenSeal.WarriorShield sealBuff = Dungeon.hero.buff(BrokenSeal.WarriorShield.class);
                                    if (sealBuff != null && armor == sealBuff.armor) {
                                        sealBuff.setArmor(null);
                                    }
                                }
                            }
                            //对已在玩家天赋表中的新天赋逐次加点并获得各级收益
                            for(int i = 1; i<=point; i++){
                                Dungeon.hero.talents.get(ScrollOfMetamorphosis.WndMetamorphReplace.INSTANCE.tier-1).put(talent, i);
                                Talent.onTalentUpgraded(Dungeon.hero, talent);
                                //我希望蜕变天赋是对天赋重新升级，令那些在升级时有收益的天赋重新获取一遍收益
                            }
							break;
						}
					}

                    //结束的动画
					ScrollOfMetamorphosis.onMetamorph(replacing, talent);

                    //关闭窗口
					if (ScrollOfMetamorphosis.WndMetamorphReplace.INSTANCE != null){
						ScrollOfMetamorphosis.WndMetamorphReplace.INSTANCE.hide();
					}

				}
			});
		} else if (mode == Mode.debug_replace && Dungeon.hero != null && Dungeon.hero.isAlive()) {
            toAdd = new WndInfoTalent(talent, pointsInTalent, new WndInfoTalent.TalentButtonCallback() {

                @Override
                public String prompt() {
                    return Messages.titleCase(Messages.get(ScrollOfMetamorphosis.class, "metamorphose_talent"));
                }

                @Override
                public void call() {
                    Talent replacing = debugSelectTalent.debugTalent.INSTANCE.replacing;

                    for (LinkedHashMap<Talent, Integer> tier : Dungeon.hero.talents){
                        if (tier.containsKey(replacing)){
                            LinkedHashMap<Talent, Integer> newTier = new LinkedHashMap<>();
                            for (Talent t : tier.keySet()){
                                if (t == replacing){
                                    newTier.put(talent, tier.get(replacing));

                                    if (!Dungeon.hero.metamorphedTalents.containsValue(replacing)){
                                        Dungeon.hero.metamorphedTalents.put(replacing, talent);

                                        //if what we're replacing is already a value, we need to simplify the data structure
                                    } else {
                                        //a->b->a, we can just remove the entry entirely
                                        if (Dungeon.hero.metamorphedTalents.get(talent) == replacing){
                                            Dungeon.hero.metamorphedTalents.remove(talent);

                                            //a->b->c, we need to simplify to a->c
                                        } else {
                                            for (Talent t2 : Dungeon.hero.metamorphedTalents.keySet()){
                                                if (Dungeon.hero.metamorphedTalents.get(t2) == replacing){
                                                    Dungeon.hero.metamorphedTalents.put(t2, talent);
                                                }
                                            }
                                        }
                                    }

                                } else {
                                    newTier.put(t, tier.get(t));
                                }
                            }
                            Dungeon.hero.talents.set(debugSelectTalent.debugTalent.INSTANCE.tier-1, newTier);
                            break;
                        }
                    }

                    if (debugSelectTalent.debugTalent.INSTANCE != null){
                        debugSelectTalent.debugTalent.INSTANCE.hide();
                    }

                }
            });
        } else {
			toAdd = new WndInfoTalent(talent, pointsInTalent, null);
		}

		if (GirlsFrontlinePixelDungeon.scene() instanceof GameScene){
			GameScene.show(toAdd);
		} else {
			GirlsFrontlinePixelDungeon.scene().addToFront(toAdd);
		}
	}

	@Override
	protected void onPointerDown() {
		icon.brightness( 1.5f );
		bg.brightness( 1.5f );
		Sample.INSTANCE.play( Assets.Sounds.CLICK );
	}

	@Override
	protected void onPointerUp() {
		icon.resetColor();
		bg.resetColor();
	}

	@Override
	protected String hoverText() {
		return Messages.titleCase(talent.title());
	}

	public void enable(boolean value ) {
		active = value;
		icon.alpha( value ? 1.0f : 0.3f );
		bg.alpha( value ? 1.0f : 0.3f );
	}

	public void upgradeTalent(){
		if (Dungeon.hero.talentPointsAvailable(tier) > 0 && parent != null) {
			Dungeon.hero.upgradeTalent(talent);
			float oldWidth = fill.width();
			pointsInTalent++;
			layout();
			Sample.INSTANCE.play(Assets.Sounds.LEVELUP, 0.7f, 1.2f);
			Emitter emitter = (Emitter) parent.recycle(Emitter.class);
			emitter.revive();
			emitter.pos(fill.x + (fill.width() + oldWidth) / 2f, fill.y + fill.height() / 2f);
			emitter.burst(Speck.factory(Speck.STAR), 12);
		}
	}
    public static void DebugTalent(Talent old, Talent replacing, int Tier){
        for (LinkedHashMap<Talent, Integer> tier : Dungeon.hero.talents){
            if (tier.containsKey(replacing)){
                LinkedHashMap<Talent, Integer> newTier = new LinkedHashMap<>();
                for (Talent t : tier.keySet()){
                    if (t == replacing){
                        newTier.put(old, tier.get(replacing));

                        if (!Dungeon.hero.metamorphedTalents.containsValue(replacing)){
                            Dungeon.hero.metamorphedTalents.put(replacing, old);

                            //if what we're replacing is already a value, we need to simplify the data structure
                        } else {
                            //a->b->a, we can just remove the entry entirely
                            if (Dungeon.hero.metamorphedTalents.get(old) == replacing){
                                Dungeon.hero.metamorphedTalents.remove(old);

                                //a->b->c, we need to simplify to a->c
                            } else {
                                for (Talent t2 : Dungeon.hero.metamorphedTalents.keySet()){
                                    if (Dungeon.hero.metamorphedTalents.get(t2) == replacing){
                                        Dungeon.hero.metamorphedTalents.put(t2, old);
                                    }
                                }
                            }
                        }

                    } else {
                        newTier.put(t, tier.get(t));
                    }
                }
                Dungeon.hero.talents.set(Tier-1, newTier);
                break;
            }
        }
    }
}
