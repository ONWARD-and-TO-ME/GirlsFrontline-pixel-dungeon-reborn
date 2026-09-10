package com.shatteredpixel.shatteredpixeldungeon.journal;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.BuffScanner;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Buff 图鉴数据层。参考 {@link Catalog} 的模式，追踪玩家在游戏中见过的 Buff。
 * 数据持久化到 journal.dat 中（与 Catalog/Bestiary/Document 一起）。
 */
public class BuffCatalog {

	private static final String CODE = "buffCatalogSeen";

	// 所有有图标的 Buff 类（过滤掉 icon()==NONE 的基类）
	private static LinkedHashMap<Class<? extends Buff>, Boolean> seen = new LinkedHashMap<>();
	// 缓存每个 Buff 的图标索引（避免反复实例化）
	private static LinkedHashMap<Class<? extends Buff>, Integer> iconCache = new LinkedHashMap<>();
	// 缓存 buffType
	private static LinkedHashMap<Class<? extends Buff>, Buff.buffType> typeCache = new LinkedHashMap<>();

	private static boolean loaded = false;

	// 分类常量
	public static final int POSITIVE = 0;
	public static final int NEGATIVE = 1;
	public static final int NEUTRAL  = 2;

	static {
		init();
	}

	private static void init() {
		ArrayList<Class<? extends Buff>> all = BuffScanner.getAllBuffClasses();
		for (Class<? extends Buff> cl : all) {
			try {
				Buff instance = Reflection.newInstance(cl);
				if (instance == null) continue;
				int icon = instance.icon();
				if (icon == BuffIndicator.NONE) {
					// 无图标的基类/工具类不纳入图鉴
					continue;
				}
				seen.put(cl, false);
				iconCache.put(cl, icon);
				typeCache.put(cl, instance.type);
			} catch (Throwable t) {
				// 跳过无法实例化的类（包括 Error，如 ExceptionInInitializerError）
			}
		}
	}

	public static Collection<Class<? extends Buff>> buffClasses() {
		return seen.keySet();
	}

	public static boolean isSeen(Class<?> buffClass) {
		Boolean s = seen.get(buffClass);
		return s != null && s;
	}

	public static void setSeen(Class<? extends Buff> buffClass) {
		if (seen.containsKey(buffClass) && !seen.get(buffClass)) {
			seen.put(buffClass, true);
			Journal.saveNeeded = true;
		}
	}

	public static int iconIndex(Class<? extends Buff> buffClass) {
		Integer i = iconCache.get(buffClass);
		return i != null ? i : BuffIndicator.NONE;
	}

	public static Buff.buffType buffType(Class<? extends Buff> buffClass) {
		Buff.buffType t = typeCache.get(buffClass);
		return t != null ? t : Buff.buffType.NEUTRAL;
	}

	public static List<Class<? extends Buff>> categoryBuffs(int category) {
		List<Class<? extends Buff>> result = new ArrayList<>();
		for (Class<? extends Buff> cl : seen.keySet()) {
			Buff.buffType t = typeCache.get(cl);
			if (category == POSITIVE && t == Buff.buffType.POSITIVE) result.add(cl);
			else if (category == NEGATIVE && t == Buff.buffType.NEGATIVE) result.add(cl);
			else if (category == NEUTRAL && t == Buff.buffType.NEUTRAL) result.add(cl);
		}
		return result;
	}

	public static int totalBuffs() {
		return seen.size();
	}

	public static int totalSeen() {
		int count = 0;
		for (boolean s : seen.values()) {
			if (s) count++;
		}
		return count;
	}

	public static int categoryTotal(int category) {
		return categoryBuffs(category).size();
	}

	public static int categorySeen(int category) {
		int count = 0;
		for (Class<? extends Buff> cl : categoryBuffs(category)) {
			if (seen.get(cl)) count++;
		}
		return count;
	}

	public static String title(int category) {
		switch (category) {
			case POSITIVE: return Messages.get(BuffCatalog.class, "positive");
			case NEGATIVE: return Messages.get(BuffCatalog.class, "negative");
			default:       return Messages.get(BuffCatalog.class, "neutral");
		}
	}

	// --- 持久化 ---

	public static void store(Bundle bundle) {
		LinkedHashSet<String> seenNames = new LinkedHashSet<>();
		for (Class<? extends Buff> cl : seen.keySet()) {
			if (seen.get(cl)) {
				seenNames.add(cl.getName());
			}
		}
		bundle.put(CODE, seenNames.toArray(new String[0]));
	}

	public static void restore(Bundle bundle) {
		loaded = true;
		if (!bundle.contains(CODE)) return;
		String[] seenNames = bundle.getStringArray(CODE);
		if (seenNames == null) return;
		for (String name : seenNames) {
			for (Class<? extends Buff> cl : seen.keySet()) {
				if (cl.getName().equals(name)) {
					seen.put(cl, true);
					break;
				}
			}
		}
	}
}
