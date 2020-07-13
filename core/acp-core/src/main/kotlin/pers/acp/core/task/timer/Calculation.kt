package pers.acp.core.task.timer

import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import pers.acp.core.exceptions.TimerException
import pers.acp.core.task.timer.rule.CircleType

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
object Calculation {

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    @JvmField
    var DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

    /**
     * yyyy-MM-dd
     */
    @JvmField
    var DATE_FORMAT = "yyyy-MM-dd"

    /**
     * HH:mm:ss
     */
    private var TIME_FORMAT = "HH:mm:ss"

    /**
     * 根据时间戳获取日期对象
     *
     * @param instant 时间戳
     * @return 日期对象
     */
    fun getCalendar(instant: Long): DateTime = DateTime(instant)

    /**
     * 获取日期对象
     *
     * @param dateTimeStr    日期字符串
     * @param dateTimeFormat 格式字符串
     * @return 日期对象
     */
    fun getCalendar(dateTimeStr: String, dateTimeFormat: String = DATE_FORMAT): DateTime =
            DateTimeFormat.forPattern(dateTimeFormat).parseDateTime(dateTimeStr)

    /**
     * 获取指定日期的后一天
     *
     * @param dateTime 日期对象
     * @return 日期对象
     */
    fun getNextDay(dateTime: DateTime): DateTime = dateTime.plusDays(1)

    /**
     * 获取指定日期前一天
     *
     * @param dateTime 日期对象
     * @return 日期对象
     */
    fun getPrevDay(dateTime: DateTime): DateTime = dateTime.minusDays(1)

    /**
     * 获取指定日期是一周中第几天
     *
     * @param dateTime 日期对象
     * @return 结果（1-7）,其中sunday是7
     */
    fun getWeekNo(dateTime: DateTime): Int = dateTime.dayOfWeek

    /**
     * 获取指定日期日号
     *
     * @param dateTime 日期对象
     * @return 日号（1-31）
     */
    fun getDayNo(dateTime: DateTime): Int = dateTime.dayOfMonth

    /**
     * 获取指定月号
     *
     * @param dateTime 日期对象
     * @return 月号（1-12）
     */
    fun getMonthNo(dateTime: DateTime): Int = dateTime.monthOfYear

    /**
     * 获取指定月所在季度内的月号
     *
     * @param dateTime 日期对象
     * @return 季度内的月号（1, 2, 3）
     */
    fun getMonthNoInQuarter(dateTime: DateTime): Int {
        val month = getMonthNo(dateTime)
        return if (month in 1..12) {
            (month % 3).let {
                if (it == 0) {
                    3
                } else {
                    it
                }
            }
        } else {
            -1
        }
    }

    /**
     * 获取指定月最后一天日号
     *
     * @param dateTime 日期对象
     * @return 日号
     */
    fun getLastDayInMonthNo(dateTime: DateTime): Int = dateTime.dayOfMonth().withMaximumValue().dayOfMonth

    /**
     * 判断当前时间是否是工作日
     *
     * @param dateTime 日期对象
     * @return 是否是工作日
     */
    fun isWeekDay(dateTime: DateTime): Boolean = getWeekNo(dateTime) < 6

    /**
     * 判断当前时间是否是周末
     *
     * @param dateTime 日期对象
     * @return 是否是周末
     */
    fun isWeekend(dateTime: DateTime): Boolean = getWeekNo(dateTime) > 5

    /**
     * 获取定时器参数
     *
     * @param circleType 周期
     * @param rules      规则
     * @return long[]：[0]long-initialDelay开始执行延迟时间,[1]long-period执行间隔时间
     */
    @Throws(TimerException::class)
    fun getTimerParam(circleType: CircleType, rules: String): LongArray {
        val now = DateTime()
        val param = LongArray(2)
        val rule = StringUtils.splitPreserveAllTokens(rules, "|")
        try {
            if (circleType == CircleType.Time) {
                if (rule.size == 2 || rule.size == 1) {
                    if (rule.size == 1) {
                        param[0] = now.toDate().time
                        param[1] = java.lang.Long.valueOf(rule[0])
                    } else {
                        val time = now.toString(DATE_FORMAT) + " " + rule[0]
                        val dateTime = getCalendar(time, DATETIME_FORMAT)
                        param[0] = dateTime.toDate().time
                        param[1] = java.lang.Long.valueOf(rule[1])
                    }
                } else {
                    throw TimerException("circleType is not support（circleType=" + CircleType.Time.name + ",rules=" + rules + "）")
                }
            } else if (circleType == CircleType.Day) {
                if (rule.size == 1) {
                    val nextDay = getNextDay(now)
                    val time = nextDay.toString(DATE_FORMAT) + " " + rule[0]
                    val dateTime = getCalendar(time, DATETIME_FORMAT)
                    param[0] = dateTime.toDate().time
                } else {
                    throw TimerException("circleType is not support（circleType=" + CircleType.Day.name + ",rules=" + rules + "）")
                }
                param[1] = (1000 * 60 * 60 * 24).toLong()
            } else if (circleType == CircleType.Week || circleType == CircleType.Month) {
                if (rule.size == 2) {
                    val nextDay = getNextDay(now)
                    val time = nextDay.toString(DATE_FORMAT) + " " + rule[0]
                    val dateTime = getCalendar(time, DATETIME_FORMAT)
                    param[0] = dateTime.toDate().time
                } else {
                    throw TimerException("circleType is not support（circleType=" + CircleType.Week.name + ",rules=" + rules + "）")
                }
                param[1] = (1000 * 60 * 60 * 24).toLong()
            } else if (circleType == CircleType.Quarter || circleType == CircleType.Year) {
                if (rule.size == 3) {
                    val nextDay = getNextDay(now)
                    val time = nextDay.toString(DATE_FORMAT) + " " + rule[0]
                    val dateTime = getCalendar(time, DATETIME_FORMAT)
                    param[0] = dateTime.toDate().time
                } else {
                    throw TimerException("circleType is not support. circleType=" + CircleType.Week.name + ",rules=" + rules + "）")
                }
                param[1] = (1000 * 60 * 60 * 24).toLong()
            } else {
                throw TimerException("circleType is not support（circleType error）")
            }
            if (param[0] - now.toDate().time > 0) {
                param[0] = param[0] - now.toDate().time
            } else {
                param[0] = 0
            }
            return param
        } catch (e: Exception) {
            throw TimerException("getTimerParam failed:" + e.message)
        }

    }

    /**
     * 以日为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    fun validateDay(now: DateTime, contrast: DateTime, rule: String): Boolean {
        val rules = StringUtils.splitPreserveAllTokens(rule, "|")
        var isExecute = false
        if (rules.size == 1) {
            // 参照日期后一天
            val afterDay = getNextDay(contrast).toString(DATE_FORMAT) + " " + rules[0]
            // 当前日期和时间
            val nowDay = now.toString(DATETIME_FORMAT)
            if (nowDay >= afterDay) {
                isExecute = true
            }
        }
        return isExecute
    }

    /**
     * 以周为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    fun validateWeek(now: DateTime, contrast: DateTime, rule: String): Boolean {
        val rules = StringUtils.splitPreserveAllTokens(rule, "|")
        var isExecute = false
        if (rules.size == 2) {
            // 参照日期后一天0点
            val afterDay = getNextDay(contrast).toString(DATE_FORMAT) + " 00:00:00"
            // 当前日期和时间
            val nowDay = now.toString(DATETIME_FORMAT)
            // 当前时间
            val nowTime = now.toString(TIME_FORMAT)

            val dayIndex = rules[0].toInt()// 一周中第几天
            val nowIndex = getWeekNo(now)// 当前日期是一周中第几天
            // 比上次发送时间至少晚一天，符合一周中第几天，等于或超过配置的发送时间
            if (nowDay >= afterDay && dayIndex == nowIndex && nowTime >= rules[1]) {
                isExecute = true
            }
        }
        return isExecute
    }

    /**
     * 以月为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    fun validateMonth(now: DateTime, contrast: DateTime, rule: String): Boolean {
        val rules = StringUtils.splitPreserveAllTokens(rule, "|")
        var isExecute = false
        if (rules.size == 2) {
            // 参照日期后一天0点
            val afterDay = getNextDay(contrast).toString(DATE_FORMAT) + " 00:00:00"
            // 当前日期和时间
            val nowDay = now.toString(DATETIME_FORMAT)
            // 当前时间
            val nowTime = now.toString(TIME_FORMAT)

            var dayIndex = rules[0].toInt()// 日号
            val nowIndex = getDayNo(now)// 当前日期是几号
            val maxDay = getLastDayInMonthNo(now)// 当前日期所在月最后一天
            if (maxDay < dayIndex) {
                dayIndex = maxDay
            }
            // 比上次发送时间至少晚一天，符合月中日期，等于或超过配置的发送时间
            if (nowDay >= afterDay && dayIndex == nowIndex && nowTime >= rules[1]) {
                isExecute = true
            }
        }
        return isExecute
    }

    /**
     * 以季度为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    fun validateQuarter(now: DateTime, contrast: DateTime, rule: String): Boolean {
        val rules = StringUtils.splitPreserveAllTokens(rule, "|")
        var isExecute = false
        if (rules.size == 3) {
            // 参照日期后一天0点
            val afterDay = getNextDay(contrast).toString(DATE_FORMAT) + " 00:00:00"

            var dayIndex = rules[1].toInt()// 日号
            val nowIndex = getDayNo(now)// 当前日期是几号
            val maxDay = getLastDayInMonthNo(now)// 当前日期所在月最后一天
            if (maxDay < dayIndex) {
                dayIndex = maxDay
            }
            // 比上次发送时间至少晚一天，符合季度内月号，符合月内日号，等于或超过配置的发送时间
            if (now.toString(DATETIME_FORMAT) >= afterDay && getMonthNoInQuarter(now) == rules[0].toInt() && dayIndex == nowIndex && now.toString(TIME_FORMAT) >= rules[2]) {
                isExecute = true
            }
        }
        return isExecute
    }

    /**
     * 以年为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    fun validateYear(now: DateTime, contrast: DateTime, rule: String): Boolean {
        val rules = StringUtils.splitPreserveAllTokens(rule, "|")
        var isExecute = false
        if (rules.size == 3) {
            // 参照日期后一天0点
            val afterDay = getNextDay(contrast).toString(DATE_FORMAT) + " 00:00:00"

            var dayIndex = rules[1].toInt()// 日号
            val nowIndex = getDayNo(now)// 当前日期是几号
            val maxDay = getLastDayInMonthNo(now)// 当前日期所在月最后一天
            if (maxDay < dayIndex) {
                dayIndex = maxDay
            }
            // 比上次发送时间至少晚一天，符合月号，符合月内日号，等于或超过配置的发送时间
            if (now.toString(DATETIME_FORMAT) >= afterDay && getMonthNo(now) == rules[0].toInt() && dayIndex == nowIndex && now.toString(TIME_FORMAT) >= rules[2]) {
                isExecute = true
            }
        }
        return isExecute
    }

}