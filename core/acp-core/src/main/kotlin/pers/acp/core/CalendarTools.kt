package pers.acp.core

import org.joda.time.DateTime
import pers.acp.core.task.timer.Calculation

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
object CalendarTools {

    /**
     * 根据时间戳获取日期对象
     *
     * @param instant 时间戳
     * @return 日期对象
     */
    @JvmStatic
    fun getCalendar(instant: Long): DateTime = Calculation.getCalendar(instant)

    /**
     * 获取日期对象
     *
     * @param dateTimeStr    日期字符串
     * @param dateTimeFormat 格式字符串
     * @return 日期对象
     */
    @JvmStatic
    @JvmOverloads
    fun getCalendar(dateTimeStr: String, dateTimeFormat: String = Calculation.DATE_FORMAT): DateTime = Calculation.getCalendar(dateTimeStr, dateTimeFormat)

    /**
     * 获取指定日期的后一天
     *
     * @param dateTime 日期对象
     * @return 日期对象
     */
    @JvmStatic
    fun getNextDay(dateTime: DateTime): DateTime = Calculation.getNextDay(dateTime)

    /**
     * 获取指定日期前一天
     *
     * @param dateTime 日期对象
     * @return 日期对象
     */
    @JvmStatic
    fun getPrevDay(dateTime: DateTime): DateTime = Calculation.getPrevDay(dateTime)

    /**
     * 获取指定日期是一周中第几天
     *
     * @param dateTime 日期对象
     * @return 结果（1-7）,其中sunday是7
     */
    @JvmStatic
    fun getWeekNo(dateTime: DateTime): Int = Calculation.getWeekNo(dateTime)

    /**
     * 获取指定日期日号
     *
     * @param dateTime 日期对象
     * @return 日号（1-31）
     */
    @JvmStatic
    fun getDayNo(dateTime: DateTime): Int = Calculation.getDayNo(dateTime)

    /**
     * 获取指定月号
     *
     * @param dateTime 日期对象
     * @return 月号（1-12）
     */
    @JvmStatic
    fun getMonthNo(dateTime: DateTime): Int = Calculation.getMonthNo(dateTime)

    /**
     * 获取指定月所在季度内的月号
     *
     * @param dateTime 日期对象
     * @return 季度内的月号（1, 2, 3）
     */
    @JvmStatic
    fun getMonthNoInQuarter(dateTime: DateTime): Int = Calculation.getMonthNoInQuarter(dateTime)

    /**
     * 获取指定月最后一天日号
     *
     * @param dateTime 日期对象
     * @return 日号
     */
    @JvmStatic
    fun getLastDayInMonthNo(dateTime: DateTime): Int = Calculation.getLastDayInMonthNo(dateTime)

    /**
     * 判断当前时间是否是工作日
     *
     * @param dateTime 日期对象
     * @return 是否是工作日
     */
    @JvmStatic
    fun isWeekDay(dateTime: DateTime): Boolean = Calculation.isWeekDay(dateTime)

    /**
     * 判断当前时间是否是周末
     *
     * @param dateTime 日期对象
     * @return 是否是周末
     */
    @JvmStatic
    fun isWeekend(dateTime: DateTime): Boolean = Calculation.isWeekend(dateTime)

    /**
     * 以日为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    @JvmStatic
    fun validateDay(now: DateTime, contrast: DateTime, rule: String): Boolean = Calculation.validateDay(now, contrast, rule)

    /**
     * 以周为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    @JvmStatic
    fun validateWeek(now: DateTime, contrast: DateTime, rule: String): Boolean = Calculation.validateWeek(now, contrast, rule)

    /**
     * 以月为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    @JvmStatic
    fun validateMonth(now: DateTime, contrast: DateTime, rule: String): Boolean = Calculation.validateMonth(now, contrast, rule)

    /**
     * 以季度为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    @JvmStatic
    fun validateQuarter(now: DateTime, contrast: DateTime, rule: String): Boolean = Calculation.validateQuarter(now, contrast, rule)

    /**
     * 以年为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    @JvmStatic
    fun validateYear(now: DateTime, contrast: DateTime, rule: String): Boolean = Calculation.validateYear(now, contrast, rule)

}