package com.shatteredpixel.shatteredpixeldungeon.utils;
import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import java.util.Calendar;

/**
 * 农历节日工具类，基于6Tail的农历Java库实现
 * 功能：计算中国传统节日，使地牢能够自动根据农历日期调整节日状态
 *
 * 已实现的节日：
 * - 春节、元宵节、端午节、七夕、中秋节、重阳节（农历）
 * - 清明节、国庆节（公历）
 *
 * 使用前需在core级gradle中导入依赖：
 * implementation 'cn.6tail:lunar:1.7.4'
 *
 * 示例用法：在RegularLevel.java中调用 Gregorian.checkLunarDates();
 *
 * @see <a href="https://mvnrepository.com/artifact/cn.6tail/lunar">Lunar Maven</a>
 * @see <a href="https://github.com/6tail/lunar-java">Lunar Github</a>
 * @since 2024.1.9 加入NTP验证系统时间
 */
public class Gregorian {
    // 事件结束时间戳（毫秒）
    private static long eventEndTime = 0;

    public static void LunarCheckDate() {
        Calendar calendar = Calendar.getInstance();
        Solar solarDate = Solar.fromDate(calendar.getTime());
        Lunar lunarDate = solarDate.getLunar();
        int gregorianMonth = calendar.get(Calendar.MONTH) + 1; // 转换为1-12月
        int gregorianDay = calendar.get(Calendar.DAY_OF_MONTH);
        eventEndTime = 0;

        //先按公历重置节日状态，避免跨天后残留旧节日
        Holidays.checkSolarHolidays();

        //公历传统节日
        checkChinaBirthday(gregorianMonth, gregorianDay);
        checkQingming(gregorianMonth, gregorianDay);

        //农历传统节日，与公历节日重叠时优先（如中秋遇上国庆）
        checkTraditionalFestivals(lunarDate);
    }

    /**
     * 检查主要农历传统节日
     * 注：lunar-java 中闰月返回负数月份，传统节日不按闰月计算
     */
    private static void checkTraditionalFestivals(Lunar lunar) {
        int month = lunar.getMonth();
        int day = lunar.getDay();

        if (month == 1 && day >= 1 && day <= 7) {
            Holidays.holiday = Holidays.Holiday.SPRING_FESTIVAL;
        } else if (month == 1 && day == 15) {
            Holidays.holiday = Holidays.Holiday.LANTERN_FESTIVAL;
        } else if (month == 5 && day == 5) {
            Holidays.holiday = Holidays.Holiday.DRAGON_BOAT;
        } else if (month == 7 && day == 7) {
            Holidays.holiday = Holidays.Holiday.QIXI;
        } else if (month == 8 && day >= 15 && day < 22) {
            Holidays.holiday = Holidays.Holiday.midAutumnFestival;
        } else if (month == 9 && day == 9) {
            Holidays.holiday = Holidays.Holiday.DOUBLE_NINTH;
        }
    }

    /**
     * 检查是否为清明节期间（公历4月4日至4月6日，节气日期在此间浮动）
     */
    private static void checkQingming(int month, int day) {
        if (month == 4 && day >= 4 && day <= 6) {
            Holidays.holiday = Holidays.Holiday.QINGMING;
        }
    }

    /**
     * 检查是否为国庆节期间（阳历10月1日至10月7日）
     */
    private static void checkChinaBirthday(int month, int day) {
        if (month == 10 && day >= 1 && day <= 7) {
            Holidays.holiday = Holidays.Holiday.NATIONAL_DAY;
            //eventEndTime = calculateSolarEventEndTime(2025, 10, 6);//eventEndTime那一行先注释 下次加游戏内显示的时候有用
        }
    }
}