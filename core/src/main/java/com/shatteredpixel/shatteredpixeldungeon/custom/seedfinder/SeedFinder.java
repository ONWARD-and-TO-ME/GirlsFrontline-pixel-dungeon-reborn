package com.shatteredpixel.shatteredpixeldungeon.custom.seedfinder;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ArmoredStatue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GoldenMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost.Quest;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap.Type;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CeremonialCandle;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;

public class SeedFinder implements Runnable {

    @Override
    public void run() {
        Dungeon.resetTest();
        String str;
        if (wantedArr.length == 0)
            str = logSeedItems(DungeonSeed.convertFromText(SeedFindScene.seedCode));
        else
            str = findSeed();
        SeedFindScene.INSTANCE.text = str;
        SeedFindScene.INSTANCE.needUpdate = true;
    }

    public static volatile boolean running;
    public static volatile boolean SeedFinding = false;

    protected final WantedTarget[] wantedArr;
    // Class → 目标下标数组：tryMatch 先查 map 取候选目标，跳过无关物品
    private final HashMap<Class<? extends Item>, int[]> matchIndex;
    // 预筛下标：循环内直接遍历，避免对全量 wantedArr 逐条 isAssignableFrom
    protected final int floor;
    protected final HeroClass heroClass;
    protected SeedFinder(ArrayList<WantedTarget> wanted, int fl, HeroClass cl) {
        wantedArr = wanted.toArray(new WantedTarget[0]);
        matchIndex = buildMatchIndex(wantedArr);
        floor = fl;
        heroClass = cl;
        for (WantedTarget w : wanted) {
            if (Wand.class.isAssignableFrom(w.cls) && w.minLevel >= 3)
                wand = w;
            else if (Ring.class.isAssignableFrom(w.cls) && w.minLevel >= 3)
                ring = w;
        }
    }
    WantedTarget wand;
    WantedTarget ring;

    // 构造时按 cls 分组目标下标，供 tryMatch 做 O(1) 跳查
    private static HashMap<Class<? extends Item>, int[]> buildMatchIndex(WantedTarget[] arr) {
        HashMap<Class<? extends Item>, ArrayList<Integer>> temp = new HashMap<>();
        for (int j = 0; j < arr.length; j++) {
            Class<? extends Item> cls = arr[j].cls;
            ArrayList<Integer> list = temp.get(cls);
            if (list == null) {
                list = new ArrayList<>();
                temp.put(cls, list);
            }
            list.add(j);
        }
        HashMap<Class<? extends Item>, int[]> idx = new HashMap<>();
        for (Map.Entry<Class<? extends Item>, ArrayList<Integer>> e : temp.entrySet()) {
            ArrayList<Integer> list = e.getValue();
            int[] indices = new int[list.size()];
            for (int k = 0; k < list.size(); k++) {
                indices[k] = list.get(k);
            }
            idx.put(e.getKey(), indices);
        }
        return idx;
    }
    public final String findSeed() {
        String result = "NONE";
        SeedFinding = true;
        running = true;

        long seedDigits = DungeonSeed.randomSeed();
        if (seedDigits > 200000) {
            seedDigits -= 100000;
        }

        for (int i = Random.Int(99999); (long) i < DungeonSeed.TOTAL_SEEDS
                && running && SeedFinding; ++i) {
            long currentSeed = seedDigits + i;

            if (SeedFindScene.INSTANCE != null)
                SeedFindScene.INSTANCE.updateCurrentSeed(currentSeed);

            // 10 连复查：命中目标必须落在该种子各楼层生成变体的交集内
            boolean confirmed = true;
            for (int r = 0; r < 10; r++) {
                if (!testSeed(currentSeed)) {
                    confirmed = false;
                    break;
                }
            }
            if (confirmed) {
                result = logSeedItems(currentSeed);
                break;
            }

            if (Thread.currentThread().isInterrupted()) {
                running = false;
                break;
            }
        }
        SeedFinding = false;
        return result;
    }

    // ===== 强力查种（多线程分片） =====
    public static volatile boolean parallelFound = false;
    /** Dungeon 是全局静态状态，多线程操作必须串行化 */
    public static final Object DUNGEON_LOCK = new Object();

    /** 当前搜索线程数，供 SeedFindScene 轮询显示 */
    public static volatile int searchThreadCount = 1;

    public static final AtomicLongArray parallelSeeds = new AtomicLongArray(8);

    /** 强力查种：单个区间覆盖的种子偏移数量 */
    public static final long SEGMENT_SIZE = 100_000L;
    /** 强力查种：单个区间超过该时长（毫秒）仍未命中，则该线程切换到下一个随机区间 */
    public static final long SEGMENT_TIMEOUT_MS = 5_000L;

    public String findSeedParallel(int threadCount) {
        String result = "NONE";
        SeedFinding = true;
        running = true;
        parallelFound = false;

        // 清零每个线程的当前种子槽位
        searchThreadCount = threadCount;
        for (int t = 0; t < parallelSeeds.length(); t++) parallelSeeds.set(t, -1);

        final long total = DungeonSeed.TOTAL_SEEDS;
        final long startSeed = Random.Long(total);

        final AtomicReference<String> resultRef = new AtomicReference<>();

        Thread[] workers = new Thread[threadCount];
        for (int t = 0; t < threadCount; t++) {
            final int tid = t;

            // 线程 tid 负责 [segStart, segEnd) 连续大段，互不重叠
            long seg = total / threadCount;
            final long segStart = tid * seg;
            final long segEnd = (tid == threadCount - 1) ? total : (tid + 1) * seg;

            workers[t] = new Thread(() -> {
                // 每线程负责段内一个随机区间（种子池）；超时未命中或池测完则完全随机 roll 一次
                long maxSliceStart = segEnd - SEGMENT_SIZE;
                long sliceStart = (maxSliceStart > segStart) ? (segStart + Random.Long(maxSliceStart - segStart + 1)) : segStart;
                long sliceEnd = Math.min(sliceStart + SEGMENT_SIZE, segEnd);
                long sliceStartTime = System.currentTimeMillis();

                long local = sliceStart;

                while (!parallelFound && running && SeedFinding) {
                    if (Thread.currentThread().isInterrupted()) return;

                    long now = System.currentTimeMillis();
                    // 切片超时未命中，或当前切片已测完 → 段内随机跳到下一片
                    if (now - sliceStartTime >= SEGMENT_TIMEOUT_MS || local >= sliceEnd) {
                        if (maxSliceStart <= segStart) return;
                        sliceStart = segStart + Random.Long(maxSliceStart - segStart + 1);
                        sliceEnd = Math.min(sliceStart + SEGMENT_SIZE, segEnd);
                        sliceStartTime = System.currentTimeMillis();
                        local = sliceStart;
                        continue;
                    }

                    final long seedValue = (startSeed + local) % total;
                    parallelSeeds.set(tid, seedValue);

                    // 只写共享状态，UI 由 SeedFindScene.update() 统一轮询
                    synchronized (DUNGEON_LOCK) {
                        if (parallelFound || !running || !SeedFinding) return;

                        // 10 连复查：命中目标必须落在该种子各楼层生成变体的交集内
                        boolean confirmed = true;
                        for (int r = 0; r < 10; r++) {
                            if (!testSeed(seedValue)) {
                                confirmed = false;
                                break;
                            }
                        }
                        if (confirmed) {
                            parallelFound = true;
                            resultRef.set(logSeedItems(seedValue));
                            return;
                        }
                    }

                    local++;
                }
            });
            workers[t].setName("SeedFinder-Worker-" + tid);
            workers[t].setDaemon(true);
        }

        for (Thread w : workers) w.start();
        try {
            for (Thread w : workers) w.join();
        } catch (InterruptedException e) {
            running = false;
            SeedFinding = false;
            for (Thread w : workers) w.interrupt();
            return result;
        }

        running = false;
        SeedFinding = false;
        String r = resultRef.get();
        return r != null ? r : result;
    }

    protected boolean testSeed(long seed) {
        Dungeon.hero = null;
        GamesInProgress.selectedClass = heroClass;
        Dungeon.init(DungeonSeed.convertToCode(seed));
        boolean[] itemsFound = new boolean[wantedArr.length];
        int foundCount = 0;
        int n = wantedArr.length;
        boolean ghostSeen = false, impSeen = false, wandmakerSeen = false;

        int depth = 1;
        int levelSub = 0;
        while (depth <= floor) {
            Level l = Dungeon.newLevel(depth, levelSub);
            if (depth == 25) {
                if (levelSub == 0)
                    levelSub++;
                else {
                    levelSub = 0;
                    depth++;
                }
            }
            else
                depth++;
            if (l instanceof CityBossLevel)
                ((CityBossLevel) l).spawnShop();

            // 地面物品：遇物即匹配，不建中间表、不 identify
            // level()/enchantment/glyph 均为生成时定型的字段/方法，无需 identify 即可读取
            // values() 直接遍历 IntMap 的 Values 迭代器，省去 valueList() 的数组拷贝+List包装
            for (Heap h : l.heaps.values())
                for (Item item : h.items)
                    if (tryMatch(item, itemsFound) && ++foundCount == n)
                        return true;

            // 怪物掉落：直接取物，不包装 Heap
            for (Mob m : l.mobs) {
                if (m.getClass() == ArmoredStatue.class) {
                    if (tryMatch(((ArmoredStatue) m).armor(), itemsFound) && ++foundCount == n)
                        return true;
                    if (tryMatch(((ArmoredStatue) m).weapon(), itemsFound) && ++foundCount == n)
                        return true;
                }
                else if (m.getClass() == Statue.class) {
                    if (tryMatch(((Statue) m).weapon(), itemsFound) && ++foundCount == n)
                        return true;
                }
                else if (m instanceof Mimic) {
                    for (Item item : ((Mimic) m).items)
                        if (tryMatch(item, itemsFound) && ++foundCount == n)
                            return true;
                }
            }
            if (!ghostSeen && Quest.armor != null) {
                ghostSeen = true;
                if ((tryMatch(Quest.armor, itemsFound)
                        || tryMatch(Quest.weapon, itemsFound)) && ++foundCount == n)
                    return true;
            }
            if (!wandmakerSeen && Wandmaker.Quest.wand1 != null) {
                wandmakerSeen = true;
                Item w1 = Wandmaker.Quest.wand1;
                Item w2 = Wandmaker.Quest.wand2;
                if (wand != null && !wand.matches(w1) && !wand.matches(w2))
                    return false;
                if ((tryMatch(w1, itemsFound) || tryMatch(w2, itemsFound)) && ++foundCount == n)
                    return true;
            }
            if (!impSeen && Imp.Quest.reward != null) {
                impSeen = true;
                if (ring != null && !ring.matches(Imp.Quest.reward))
                    return false;
                if (tryMatch(Imp.Quest.reward, itemsFound) && ++foundCount == n)
                    return true;
            }

        }
        return false;
    }

    private boolean tryMatch(Item item, boolean[] itemsFound) {
        int[] candidates = matchIndex.get(item.getClass());
        if (candidates == null) return false;
        for (int idx : candidates) {
            //只查找这个类所能在的位置
            if (!itemsFound[idx] && wantedArr[idx].matches(item)) {
                itemsFound[idx] = true;
                return true;
            }
        }
        return false;
    }

    private ArrayList<Heap> getMobDrops(Level l) {
        ArrayList<Heap> heaps = new ArrayList<>();
        for (Mob m : l.mobs) {
            if (m instanceof Statue && !(m instanceof ArmoredStatue)) {
                Heap h = new Heap();
                h.items = new LinkedList<>();
                h.items.add(((Statue) m).weapon().identify());
                h.type = Type.HEAP;
                heaps.add(h);
            } else if (m instanceof ArmoredStatue) {
                Heap h = new Heap();
                h.items = new LinkedList<>();
                h.items.add(((ArmoredStatue) m).armor().identify());
                h.items.add(((ArmoredStatue) m).weapon().identify());
                h.type = Type.HEAP;
                heaps.add(h);
            } else if (m instanceof Mimic) {
                Heap h = new Heap();
                h.items = new LinkedList<>();
                for (Item item : ((Mimic) m).items) {
                    h.items.add(item.identify());
                }
                if (m instanceof GoldenMimic) {
                    h.type = Type.GOLDEN_MIMIC;
                } else if (m instanceof CrystalMimic) {
                    h.type = Type.CRYSTAL_MIMIC;
                } else {
                    h.type = Type.MIMIC;
                }
                heaps.add(h);
            }
        }
        return heaps;
    }

    private String logSeedItems(long seed) {
        String seedCode = DungeonSeed.convertToCode(seed);
        SeedFindScene.seedCode = seedCode;
        Dungeon.hero = null;
        GamesInProgress.selectedClass = heroClass;
        Dungeon.init(seedCode);
        HashSet<Class<? extends Item>> blacklist = new HashSet<>(Arrays.asList(Dewdrop.class, IronKey.class, GoldenKey.class, CrystalKey.class, EnergyCrystal.class, CorpseDust.class, Embers.class, CeremonialCandle.class, Pickaxe.class));

        // Phase 1: 遍历所有楼层，收集物品（不 identify），任务奖励在出现层一次性收取并 complete
        ArrayList<FloorData> floorDataList = new ArrayList<>();
        int depth = 1;
        int levelSub = 0;
        SeedFinding = true;
        while (depth <= floor) {
            Level l = Dungeon.newLevel(depth, levelSub);
            if (depth == 25) {
                if (levelSub == 0)
                    levelSub++;
                else {
                    levelSub = 0;
                    depth++;
                }
            }
            else
                depth++;
            if (l instanceof CityBossLevel)
                ((CityBossLevel) l).spawnShop();

            FloorData fd = new FloorData(Dungeon.depth);

            // 地面物品
            for (Heap h : l.heaps.valueList())
                for (Item item : h.items)
                    fd.heapItems.add(new HeapItem(item, h));

            // 怪物掉落
            for (Heap h : getMobDrops(l))
                for (Item item : h.items)
                    fd.heapItems.add(new HeapItem(item, h));

            // 鬼魂任务奖励
            if (Quest.armor != null) {
                ArrayList<Item> rewards = new ArrayList<>();
                rewards.add(Quest.armor);
                rewards.add(Quest.weapon);
                Quest.complete();
                fd.ghostRewards = rewards;
            }
            // 工匠任务奖励（type 在 complete 前捕获）
            if (Wandmaker.Quest.wand1 != null) {
                ArrayList<Item> rewards = new ArrayList<>();
                rewards.add(Wandmaker.Quest.wand1);
                rewards.add(Wandmaker.Quest.wand2);
                fd.wandmakerType = Wandmaker.Quest.type();
                Wandmaker.Quest.complete();
                fd.wandmakerRewards = rewards;
            }
            // 小恶魔任务奖励
            if (Imp.Quest.reward != null) {
                ArrayList<Item> rewards = new ArrayList<>();
                rewards.add(Imp.Quest.reward);
                Imp.Quest.complete();
                fd.impRewards = rewards;
            }

            floorDataList.add(fd);
        }
        SeedFinding = false;

        // Phase 2: 统一 identify 所有收集到的物品
        for (FloorData fd : floorDataList) {
            for (HeapItem hi : fd.heapItems)
                hi.item.identify();

            if (fd.ghostRewards != null)
                for (Item i : fd.ghostRewards) i.identify();

            if (fd.wandmakerRewards != null)
                for (Item i : fd.wandmakerRewards) i.identify();

            if (fd.impRewards != null)
                for (Item i : fd.impRewards) i.identify();
        }

        // Phase 3: 生成文本
        StringBuilder result = new StringBuilder(Messages.get(SeedFinder.class, "seed") + seedCode + " (" + seed + ") " + Messages.get(SeedFinder.class, "items") + ":\n\n");
        for (FloorData fd : floorDataList) {
            result.append("\n_----- ").append((long) fd.depth).append(" ").append(Messages.get(SeedFinder.class, "floor")).append(" -----_\n\n");
            StringBuilder builder = new StringBuilder();
            ArrayList<HeapItem> scrolls = new ArrayList<>();
            ArrayList<HeapItem> potions = new ArrayList<>();
            ArrayList<HeapItem> equipment = new ArrayList<>();
            ArrayList<HeapItem> rings = new ArrayList<>();
            ArrayList<HeapItem> artifacts = new ArrayList<>();
            ArrayList<HeapItem> wands = new ArrayList<>();
            ArrayList<HeapItem> others = new ArrayList<>();
            ArrayList<HeapItem> forSales = new ArrayList<>();

            // 任务奖励（在地面物品之前展示）
            if (fd.ghostRewards != null) {
                this.addTextQuest("[ " + Messages.get(SeedFinder.class, "sad_ghost_reward") + " ]", fd.ghostRewards, builder);
            }
            if (fd.wandmakerRewards != null) {
                builder.append("[ ").append(Messages.get(SeedFinder.class, "wandmaker_need")).append(" ]:\n ");
                switch (fd.wandmakerType) {
                    case 1:
                    default:
                        builder.append(Messages.get(SeedFinder.class, "corpseDust")).append("\n\n");
                        break;
                    case 2:
                        builder.append(Messages.get(SeedFinder.class, "embers")).append("\n\n");
                        break;
                    case 3:
                        builder.append(Messages.get(SeedFinder.class, "rotBerry")).append("\n\n");
                }
                addTextQuest("[ " + Messages.get(SeedFinder.class, "wandmaker_reward") + " ]", fd.wandmakerRewards, builder);
            }
            if (fd.impRewards != null) {
                addTextQuest("[ " + Messages.get(SeedFinder.class, "imp_reward") + " ]", fd.impRewards, builder);
            }

            // 分类地面物品
            int gold = 0;
            for (HeapItem hi : fd.heapItems) {
                Item item = hi.item;
                Heap h = hi.heap;
                if (h.type == Type.FOR_SALE) {
                    forSales.add(hi);
                } else if (!blacklist.contains(item.getClass())) {
                    if (item instanceof Scroll)
                        scrolls.add(hi);
                    else if (item instanceof Potion)
                        potions.add(hi);
                    else if (!(item instanceof MeleeWeapon) && !(item instanceof Armor)) {
                        if (item instanceof Ring)
                            rings.add(hi);
                        else if (item instanceof Artifact)
                            artifacts.add(hi);
                        else if (item instanceof Wand)
                            wands.add(hi);
                        else if (item instanceof Gold)
                            gold += item.quantity();
                        else
                            others.add(hi);
                    } else
                        equipment.add(hi);
                }
            }
            if (gold != 0) {
                Gold goldA = new Gold(gold);
                Heap heapA = new Heap();
                heapA.items = new LinkedList<>();
                heapA.items.add(goldA);
                others.add(new HeapItem(goldA, heapA));
            }
            addTextItems("[ " + Messages.get(SeedFinder.class, "scrolls") + " ]", scrolls, builder);
            addTextItems("[ " + Messages.get(SeedFinder.class, "potions") + " ]", potions, builder);
            addTextItems("[ " + Messages.get(SeedFinder.class, "equipment") + " ]", equipment, builder);
            addTextItems("[ " + Messages.get(SeedFinder.class, "rings") + " ]", rings, builder);
            addTextItems("[ " + Messages.get(SeedFinder.class, "artifacts") + " ]", artifacts, builder);
            addTextItems("[ " + Messages.get(SeedFinder.class, "wands") + " ]", wands, builder);
            addTextItems("[ " + Messages.get(SeedFinder.class, "for_sales") + " ]", forSales, builder);
            addTextItems("[ " + Messages.get(SeedFinder.class, "others") + " ]", others, builder);
            result.append(builder);
        }
        return result.toString();
    }

    private void addTextItems(String caption, ArrayList<HeapItem> items, StringBuilder builder) {
        if (!items.isEmpty()) {
            builder.append(caption).append(":\n");
            for (HeapItem item : items) {
                Item i = item.item;
                Heap h = item.heap;
                if (!(i instanceof Armor && ((Armor) i).hasCurseGlyph()
                        || i instanceof Weapon && ((Weapon) i).hasCurseEnchant()) && i.cursed)
                    builder.append("- ").append(Messages.get(SeedFinder.class, "cursed")).append(i);
                else
                    builder.append("- ").append(i);
                if (h.type != Type.HEAP) {
                    String heap = h.toString();
                    if (h.type == Type.FOR_SALE)
                        heap = Shopkeeper.sellPrice(h.peek()) + "钻石";
                    builder.append("(").append(heap).append(")");
                } else if (h.room != Heap.Room.NONE) {
                    String room = Heap.RoomName(Type.NONE, h.room);
                    builder.append("(").append(room).append(")");
                }
                builder.append("\n");
            }
            builder.append("\n");
        }
    }

    private void addTextQuest(String caption, ArrayList<Item> items, StringBuilder builder) {
        if (!items.isEmpty()) {
            builder.append(caption).append(":\n");
            for (Item i : items)
                if (i.cursed)
                    builder.append("- ").append(Messages.get(SeedFinder.class, "cursed")).append(i).append("\n");
                else
                    builder.append("- ").append(i).append("\n");
            builder.append("\n");
        }
    }

    // 单层数据载体：Phase 1 收集、Phase 2 identify、Phase 3 展示
    private static final class FloorData {
        final int depth;
        final ArrayList<HeapItem> heapItems = new ArrayList<>();
        ArrayList<Item> ghostRewards = null;
        ArrayList<Item> wandmakerRewards = null;
        int wandmakerType = 0;
        ArrayList<Item> impRewards = null;

        FloorData(int depth) {
            this.depth = depth;
        }
    }

    public static class HeapItem {
        public Item item;
        public Heap heap;

        public HeapItem(Item item, Heap heap) {
            this.item = item;
            this.heap = heap;
        }
    }
}
