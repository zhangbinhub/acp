package pers.acp.core.dbcon

import pers.acp.core.exceptions.EnumValueUndefinedException

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
enum class DbType(private val namePrefix: String, private val nameSuffix: String, val instanceName: String, val isNoSQL: Boolean) {

    MySQL("`", "`", "MySQLInstance", false),
    Oracle("\"", "\"", "OracleInstance", false),
    MsSQL("[", "]", "MsSQLInstance", false),
    PostgreSQL("\"", "\"", "PostgreSQLInstance", false);

    /**
     * 格式化名称
     *
     * @param name 名称
     * @return 格式化结果
     */
    fun formatName(name: String): String = namePrefix + name + nameSuffix

    companion object {

        private var nameMap: MutableMap<String, DbType> = mutableMapOf()

        init {
            for (type in values()) {
                nameMap[type.name.toUpperCase()] = type
            }
        }

        @JvmStatic
        @Throws(EnumValueUndefinedException::class)
        fun getEnum(name: String): DbType {
            if (nameMap.containsKey(name.toLowerCase())) {
                return nameMap.getValue(name.toLowerCase())
            }
            throw EnumValueUndefinedException(DbType::class.java, name)
        }
    }
}