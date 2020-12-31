package pers.acp.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.zaxxer.hikari.HikariDataSource;
import pers.acp.core.conf.DbProperties;
import pers.acp.core.dbcon.ConnectionFactory;
import pers.acp.core.dbcon.DbType;
import pers.acp.core.log.LogFactory;

import java.io.InputStream;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public final class DBConTools {

    private static final LogFactory log = LogFactory.getInstance(DBConTools.class);

    private final ConnectionFactory dbCon;

    /**
     * 默认构造函数
     */
    public DBConTools() {
        dbCon = new ConnectionFactory();
    }

    /**
     * 构造函数
     *
     * @param connectionNo 系统配置的数据源编号
     */
    public DBConTools(int connectionNo) {
        dbCon = new ConnectionFactory(connectionNo);
    }

    /**
     * 初始化数据库工具类
     */
    public static void initTools() {
        int defaultNo = 0;
        DbProperties dbProperties = DbProperties.getInstance();
        if (dbProperties != null) {
            try {
                defaultNo = dbProperties.getDefaultSQLDbNo();
                ConnectionFactory.setDefaultConnectionNo(defaultNo);
                log.info("default database number is [" + ConnectionFactory.getDefaultConnectionNo() + "] [" + dbProperties.getDbName(defaultNo) + "]");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        ConnectionFactory.setDefaultConnectionNo(defaultNo);
    }

    /**
     * 销毁所有连接池
     */
    public static void destroyAllConnections() {
        ConnectionFactory.destroyAllConnections();
    }

    /**
     * 获取数据库连接资源
     *
     * @return 数据库连接资源
     */
    public HikariDataSource getDataSource() {
        return dbCon.getDataSource();
    }

    /**
     * 查询数据
     *
     * @param sql sql语句
     * @return 结果集
     */
    public ResultSet getDataSetBySql(String sql) {
        return dbCon.doQueryForSet(sql);
    }

    /**
     * 查询数据
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 结果集
     */
    public ResultSet getDataSetBySql(String sql, Object[] param) {
        return dbCon.doQueryForSet(sql, param);
    }

    /**
     * 查询数据
     *
     * @param currPage 当前页数
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray SQL信息，用以特殊优化：[0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ResultSet结果集
     */
    public Object[] getDataSetBySql(long currPage, long maxCount, String pKey, String[] sqlArray) {
        return dbCon.doQueryForSet(currPage, maxCount, pKey, sqlArray);
    }

    /**
     * 查询数据
     *
     * @param param    参数
     * @param currPage 当前页数
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray SQL信息，用以特殊优化：[0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ResultSet结果集
     */
    public Object[] getDataSetBySql(Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray) {
        return dbCon.doQueryForSet(param, currPage, maxCount, pKey, sqlArray);
    }

    /**
     * 查询数据
     *
     * @param sql sql语句
     * @return 结果List
     */
    public List<Map<String, Object>> getDataListBySql(String sql) {
        return dbCon.doQueryForList(sql);
    }

    /**
     * 查询数据
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 结果List
     */
    public List<Map<String, Object>> getDataListBySql(String sql, Object[] param) {
        return dbCon.doQueryForList(sql, param);
    }

    /**
     * 查询数据
     *
     * @param currPage 当前页数
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray SQL信息，用以特殊优化：[0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ArrayList结果集
     */
    public Object[] getDataListBySql(long currPage, long maxCount, String pKey, String[] sqlArray) {
        return dbCon.doQueryForList(currPage, maxCount, pKey, sqlArray);
    }

    /**
     * 查询数据
     *
     * @param param    参数
     * @param currPage 当前页数
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray SQL信息，用以特殊优化：[0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ArrayList结果集
     */
    public Object[] getDataListBySql(Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray) {
        return dbCon.doQueryForList(param, currPage, maxCount, pKey, sqlArray);
    }

    /**
     * 查询数据
     *
     * @param sql sql语句
     * @return json对象
     */
    public JsonNode getDataJSONArrayBySql(String sql) {
        return dbCon.doQueryForJSON(sql);
    }

    /**
     * 查询数据
     *
     * @param sql   sql语句
     * @param param 参数
     * @return json对象
     */
    public JsonNode getDataJSONArrayBySql(String sql, Object[] param) {
        return dbCon.doQueryForJSON(sql, param);
    }

    /**
     * 查询数据
     *
     * @param currPage 当前页数
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray SQL信息，用以特殊优化：[0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return JSONObject:{pages:long-总页数,records:long-总记录数,datas:array-结果集}
     */
    public JsonNode getDataJSONArrayBySql(long currPage, long maxCount, String pKey, String[] sqlArray) {
        return dbCon.doQueryForJSON(currPage, maxCount, pKey, sqlArray);
    }

    /**
     * 查询数据
     *
     * @param param    参数
     * @param currPage 当前页数
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray SQL信息，用以特殊优化：[0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return JSONObject:{pages:int-总页数,records:int-总记录数,datas:array-结果集}
     */
    public JsonNode getDataJSONArrayBySql(Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray) {
        return dbCon.doQueryForJSON(param, currPage, maxCount, pKey, sqlArray);
    }

    /**
     * 查询
     *
     * @param sql sql语句
     * @param cls 类
     * @return 结果List
     */
    public <T> List<T> getDataCusObjListBySql(String sql, Class<T> cls) {
        return dbCon.doQueryForCusObjList(sql, cls);
    }

    /**
     * 查询
     *
     * @param sql   sql语句
     * @param param 参数
     * @param cls   类
     * @return 结果List
     */
    public <T> List<T> getDataCusObjListBySql(String sql, Object[] param, Class<T> cls) {
        return dbCon.doQueryForCusObjList(sql, param, cls);
    }

    /**
     * 查询
     *
     * @param currPage 查询的当前页码
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @param cls      类
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ArrayList结果集
     */
    public Object[] getDataCusObjListBySql(long currPage, long maxCount, String pKey, String[] sqlArray, Class<?> cls) {
        return dbCon.doQueryForCusObjList(currPage, maxCount, pKey, sqlArray, cls);
    }

    /**
     * 查询
     *
     * @param param    参数
     * @param currPage 查询的当前页码
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @param cls      类
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ArrayList结果集
     */
    public Object[] getDataCusObjListBySql(Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray, Class<?> cls) {
        return dbCon.doQueryForCusObjList(param, currPage, maxCount, pKey, sqlArray, cls);
    }

    /**
     * 查询LOB数据
     *
     * @param tableName   表名
     * @param whereValues key:字段名称,value:字段值
     * @param lobColName  lob字段名称
     * @return 输入流
     */
    public InputStream getLOB(String tableName, Map<String, Object> whereValues, String lobColName) {
        return dbCon.doQueryLOB(tableName, whereValues, lobColName);
    }

    /**
     * 查询CLOB数据
     *
     * @param tableName   表名
     * @param whereValues key:字段名称,value:字段值
     * @param lobColName  lob字段名称
     * @return 结果字符串
     */
    public String getCLOB(String tableName, Map<String, Object> whereValues, String lobColName) {
        return dbCon.doQueryCLOB(tableName, whereValues, lobColName);
    }

    /**
     * 执行SQL
     *
     * @param sql sql语句
     * @return 成功或失败
     */
    public boolean doUpdate(String sql) {
        return dbCon.doUpdate(sql);
    }

    /**
     * 执行SQL
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 成功或失败
     */
    public boolean doUpdate(String sql, Object[] param) {
        return dbCon.doUpdate(sql, param);
    }

    /**
     * 插入操作，返回自动生成的字段列表
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 自动生成字段列表：KEY=字段名，VALUE=字段值 | 异常：null
     */
    public List<Map<String, Object>> doUpdateWithFill(String sql, Object[] param) {
        return dbCon.doUpdateWithFill(sql, param);
    }

    /**
     * 插入LOB数据
     *
     * @param tableName   表名
     * @param whereValues key:字段名称,value:字段值
     * @param lobColName  lob字段名称
     * @param input       lob数据流
     * @return 成功或失败
     */
    public boolean doInsertLOB(String tableName, Map<String, Object> whereValues, String lobColName, InputStream input) {
        return dbCon.doInsertLOB(tableName, whereValues, lobColName, input);
    }

    /**
     * 更新LOB数据
     *
     * @param tableName   表名
     * @param whereValues key:字段名称,value:字段值
     * @param lobColName  lob字段名称
     * @param input       lob数据流
     * @return 成功或失败
     */
    public boolean doUpdateLOB(String tableName, Map<String, Object> whereValues, String lobColName, InputStream input) {
        return dbCon.doUpdateLOB(tableName, whereValues, lobColName, input);
    }

    /**
     * 开始事务
     */
    public void beginTranslist() {
        dbCon.beginTranslist();
    }

    /**
     * 提交事务
     */
    public void commitTranslist() {
        dbCon.commitTranslist();
    }

    /**
     * 回滚事务
     */
    public void rollBackTranslist() {
        dbCon.rollBackTranslist();
    }

    /**
     * 添加批量SQL语句
     *
     * @param sql sql语句
     */
    public void addBatch(String sql) {
        dbCon.addBatch(sql);
    }

    /**
     * 批量执行SQL
     *
     * @return 成功或失败
     */
    public boolean excuteBatch() {
        return dbCon.excuteBatch();
    }

    /**
     * 获取数据库连接类型
     *
     * @return 链接类型
     */
    public DbType getDbType() {
        return dbCon.getDbType();
    }

    /**
     * 获取数据源编号
     *
     * @return 数据源编号
     */
    public int getDbNo() {
        return dbCon.getDbNo();
    }

    /**
     * 获取数据库驱动类型
     *
     * @return 驱动类型
     */
    public String getDbDriverType() {
        return dbCon.getDbDriverType();
    }

    /**
     * 获取数据库连接对象
     *
     * @return 数据库连接对象
     */
    public ConnectionFactory getDbCon() {
        return dbCon;
    }

    /**
     * 是否自动提交事务
     *
     * @return 是否自动提交
     */
    public boolean isAutoCommit() {
        return dbCon.isAutoCommit();
    }

    /**
     * 获取系统默认数据源编号
     *
     * @return 默认数据源编号
     */
    public static int getDefaultConnectionNo() {
        return ConnectionFactory.getDefaultConnectionNo();
    }

}
