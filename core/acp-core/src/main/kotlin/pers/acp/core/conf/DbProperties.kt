package pers.acp.core.conf

import pers.acp.core.base.BaseProperties
import pers.acp.core.exceptions.EnumValueUndefinedException
import pers.acp.core.log.LogFactory

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
class DbProperties : BaseProperties() {

    /**
     * 获取默认关系型数据库编号
     *
     * @return 数据库编号
     */
    fun getDefaultSQLDbNo(): Int = getProperty("defaultsqldbno").toInt()

    /**
     * 获取数据源名称
     *
     * @param dbno 数据库编号
     * @return 数据源名称
     */
    fun getDbName(dbno: Int): String = getProperty("db.$dbno.dbname")

    /**
     * 获取jdbc连接字符串
     *
     * @param dbNo 数据库编号
     * @return jdbc连接字符串
     */
    fun getJdbcUrlByDbNo(dbNo: Int): String = getProperty("db.$dbNo.jdbcurl")

    /**
     * 获取数据库类型
     *
     * @param dbNo 数据库编号
     * @return 数据库类型
     */
    @Throws(EnumValueUndefinedException::class)
    fun getDbTypeByDbNo(dbNo: Int): String = getProperty("db.$dbNo.dbtype")

    /**
     * 获取数据库连接驱动类名
     *
     * @param dbNo 数据库编号
     * @return 驱动类名
     */
    fun getDriverClass(dbNo: Int): String = getProperty("db.$dbNo.driverclass")

    /**
     * 获取数据库连接用户名
     *
     * @param dbNo 数据库编号
     * @return 用户名
     */
    fun getUsernameByDbNo(dbNo: Int): String = getProperty("db.$dbNo.username")

    /**
     * 获取数据库连接密码
     *
     * @param dbNo 数据库编号
     * @return 密码
     */
    fun getPasswordByDbNo(dbNo: Int): String = getProperty("db.$dbNo.password")

    /**
     * 获取数据库连接池名称
     *
     * @param dbNo 数据库编号
     * @return 连接池名称s
     */
    fun getPoolNameByDbNo(dbNo: Int): String = getProperty("db.$dbNo.poolname")

    companion object {

        private val log = LogFactory.getInstance(DbProperties::class.java)

        /**
         * 获取数据库配置实例
         *
         * @return 数据库配置实例
         */
        @JvmStatic
        fun getInstance(): DbProperties? =
                try {
                    val propertiesFileName = "/db.properties"
                    getInstance(DbProperties::class.java, propertiesFileName) as DbProperties
                } catch (e: Exception) {
                    log.error(e.message, e)
                    null
                }

    }

}
