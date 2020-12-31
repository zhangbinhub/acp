package pers.acp.core.dbcon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zaxxer.hikari.HikariDataSource;

import pers.acp.core.conf.DbProperties;
import pers.acp.core.dbcon.instance.BaseDBInstance;
import pers.acp.core.exceptions.ConfigException;
import pers.acp.core.exceptions.EnumValueUndefinedException;
import pers.acp.core.log.LogFactory;
import pers.acp.core.tools.CommonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ConnectionFactory {

    private static final LogFactory log = LogFactory.getInstance(ConnectionFactory.class);

    /**
     * 系统默认数据源编号
     */
    private static int DEFAULT_CONNECTION_NO = 0;

    private final int connectionNo;

    private DbType dbType;

    private static final ConcurrentHashMap<String, HikariDataSource> dsMap = new ConcurrentHashMap<>();

    private HikariDataSource ds = null;

    private Connection connection = null;

    private Statement stmt = null;

    private PreparedStatement pstmt = null;

    /**
     * 构造函数
     */
    public ConnectionFactory() {
        this(DEFAULT_CONNECTION_NO);
    }

    /**
     * 构造函数
     */
    public ConnectionFactory(int no) {
        connectionNo = no;
        try {
            initConnection();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 初始化数据库链接
     */
    private void initConnection() throws ConfigException, EnumValueUndefinedException {
        DbProperties dbProperties = DbProperties.getInstance();
        if (dbProperties != null) {
            dbType = DbType.getEnum(dbProperties.getDbTypeByDbNo(connectionNo));
            if (!dbType.isNoSQL()) {
                HikariDataSource dataSource = new HikariDataSource();
                dataSource.setJdbcUrl(dbProperties.getJdbcUrlByDbNo(connectionNo));
                dataSource.setUsername(dbProperties.getUsernameByDbNo(connectionNo));
                dataSource.setPassword(dbProperties.getPasswordByDbNo(connectionNo));
                dataSource.setPoolName(dbProperties.getPoolNameByDbNo(connectionNo));
                dataSource.setDriverClassName(dbProperties.getDriverClass(connectionNo));
                String poolName = "Hikari";
                if (!CommonUtils.INSTANCE.isNullStr(dataSource.getPoolName())) {
                    poolName = dataSource.getPoolName();
                }
                if (dsMap.containsKey(poolName)) {
                    ds = dsMap.get(poolName);
                } else {
                    ds = dataSource;
                    dsMap.put(poolName, ds);
                }
            }
        } else {
            throw new ConfigException("load database config failed,don't find connectionNo=" + connectionNo);
        }
    }

    /**
     * 销毁所有连接池
     */
    public static void destroyAllConnections() {
        try {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            dsMap.forEach((key, value) -> value.close());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 获取数据库连接资源
     *
     * @return 数据库连接资源
     */
    public HikariDataSource getDataSource() {
        return this.ds;
    }

    /**
     * 获取数据库连接
     */
    private void getConnection() throws SQLException {
        if (connection == null) {
            connection = ds.getConnection();
            stmt = connection.createStatement();
            connection.setAutoCommit(true);
        }
    }

    /**
     * 生成数据库操作工厂
     *
     * @return 数据库操作工厂
     */
    private BaseDBInstance produceDBInstance() throws Exception {
        String name = dbType.getInstanceName();
        String className = ConnectionFactory.class.getCanonicalName();
        Class<?> cls = Class.forName(className.substring(0, className.lastIndexOf(".")) + ".factory." + name);
        return (BaseDBInstance) cls.getDeclaredConstructor().newInstance();
    }

    /**
     * 查询结果转换为List
     *
     * @param rs 结果集
     * @return 结果List
     */
    private List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        if (rs == null) {
            return new ArrayList<>();
        }
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> rowData;
        while (rs.next()) {
            rowData = new Hashtable<>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                if (CommonUtils.INSTANCE.isNullStr(rs.getString(i))) {
                    rowData.put(md.getColumnLabel(i).toUpperCase(), "");
                } else {
                    rowData.put(md.getColumnLabel(i).toUpperCase(), rs.getObject(i));
                }
            }
            list.add(rowData);
        }
        rs.close();
        return list;
    }

    /**
     * 查询结果转换为JSONArray
     *
     * @param rs 结果集
     * @return 结果json对象
     */
    private JsonNode resultSetToJSON(ResultSet rs) throws SQLException {
        ObjectMapper mapper = new ObjectMapper();
        if (rs == null) {
            return mapper.createArrayNode();
        }
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        ArrayNode json = mapper.createArrayNode();
        while (rs.next()) {
            ObjectNode rowData = mapper.createObjectNode();
            for (int i = 1; i <= columnCount; i++) {
                if (CommonUtils.INSTANCE.isNullStr(rs.getString(i))) {
                    rowData.put(md.getColumnLabel(i).toUpperCase(), "");
                } else {
                    rowData.put(md.getColumnLabel(i).toUpperCase(), rs.getString(i));
                }
            }
            json.add(rowData);
        }
        rs.close();
        return json;
    }

    /**
     * 查询结果转换为List
     *
     * @param rs  结果集
     * @param cls 自定义类
     * @return 结果List
     */
    private <T> List<T> resultSetToCusObjList(ResultSet rs, Class<T> cls) throws SQLException, InstantiationException, IllegalAccessException, IOException, NoSuchMethodException, InvocationTargetException {
        if (rs == null) {
            return new ArrayList<>();
        }
        ArrayList<T> list = new ArrayList<>();
        while (rs.next()) {
            list.add(rowToCusObj(rs, cls));
        }
        rs.close();
        return list;
    }

    /**
     * 结果转为自定义对象
     *
     * @param rs   数据集
     * @param clas 自定义类
     * @return 自定义对象实例
     */
    private <T> T rowToCusObj(ResultSet rs, Class<T> clas) throws SQLException, IllegalAccessException, InstantiationException, IOException, NoSuchMethodException, InvocationTargetException {
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        Field[] fields = clas.getDeclaredFields();
        T rowData = clas.getDeclaredConstructor().newInstance();
        for (int i = 1; i <= columnCount; i++) {
            String colName = md.getColumnLabel(i);
            for (Field field : fields) {
                field.setAccessible(true);
                if (colName.equalsIgnoreCase(field.getName())) {
                    switch (md.getColumnType(i)) {
                        case Types.BLOB:
                            Blob blob = rs.getBlob(i);
                            if (blob != null) {
                                field.set(rowData, blob.getBinaryStream());
                            }
                            break;
                        case Types.CLOB:
                            Clob clob = rs.getClob(i);
                            field.set(rowData, clobToStr(clob));
                            break;
                        case Types.REAL:
                        case Types.FLOAT:
                        case Types.DOUBLE:
                            field.set(rowData, BigDecimal.valueOf(rs.getDouble(i)));
                            break;
                        default:
                            field.set(rowData, rs.getObject(i));
                    }
                }
            }
        }
        return rowData;
    }

    /**
     * CLOB转为字符串
     *
     * @param clob clob对象
     * @return 字符串
     */
    public static String clobToStr(Clob clob) throws IOException, SQLException {
        if (clob != null) {
            BufferedReader br = new BufferedReader(clob.getCharacterStream());
            StringBuilder buffer = new StringBuilder();
            String str = br.readLine();
            while (!CommonUtils.INSTANCE.isNullStr(str)) {
                buffer.append(str);
                str = br.readLine();
            }
            return buffer.toString();
        } else {
            return null;
        }
    }

    /**
     * 执行查询
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 结果集
     */
    private ResultSet doQuery(String sql, Object[] param) throws SQLException {
        getConnection();
        stmt.clearBatch();
        log.debug("sql=" + sql);
        pstmt = connection.prepareStatement(sql);
        if (param != null) {
            for (int i = 0; i < param.length; i++) {
                pstmt.setObject(i + 1, param[i]);
            }
        }
        return pstmt.executeQuery();
    }

    /**
     * 执行语句
     *
     * @param sql   语句
     * @param param 参数
     * @return 结果
     */
    private ResultSet doExecute(String sql, Object[] param) throws SQLException {
        getConnection();
        stmt.clearBatch();
        log.debug("sql=" + sql);
        pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        if (param != null) {
            for (int i = 0; i < param.length; i++) {
                pstmt.setObject(i + 1, param[i]);
            }
        }
        pstmt.executeUpdate();
        return pstmt.getGeneratedKeys();
    }

    /**
     * 查询
     *
     * @param sql sql语句
     * @return 结果集
     */
    private ResultSet doQueryBase(String sql) throws SQLException {
        return doQuery(sql, null);
    }

    /**
     * 查询
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 结果集
     */
    private ResultSet doQueryBase(String sql, Object[] param) throws SQLException {
        return doQuery(sql, param);
    }

    /**
     * 查询
     *
     * @param param    参数
     * @param currPage 查询的当前页码
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ResultSet结果集
     */
    private Object[] doQueryBase(Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray) throws Exception {
        Object[] result = new Object[3];
        StringBuilder searchNumberStr = new StringBuilder();
        if (sqlArray != null && sqlArray.length == 4) {
            searchNumberStr.append("select count(").append(pKey).append(") as CNM from ").append(sqlArray[1]).append(" ").append(sqlArray[2]);
        } else {
            log.error("query Exception: sqlArray is null or length is not 4");
            result[0] = 1;
            result[1] = 0;
            result[2] = null;
            return result;
        }
        ResultSet rs_t = doQuery(searchNumberStr.toString(), param);
        rs_t.next();
        long totlerecord = rs_t.getInt("CNM");
        long totlepage;
        if (currPage <= 0 || maxCount <= 0) {
            totlepage = 1;
            currPage = 1;
            maxCount = totlerecord;
        } else {
            totlepage = totlerecord / maxCount;
            if (totlerecord % maxCount != 0) {
                totlepage++;
            }
        }
        rs_t.close();
        BaseDBInstance dbInstance = produceDBInstance();
        result[0] = totlepage;
        result[1] = totlerecord;
        result[2] = dbInstance.doQueryPage(dbType, connection, param, currPage, maxCount, pKey, sqlArray);
        pstmt = dbInstance.getPstmt();
        return result;
    }

    /**
     * 查询
     *
     * @param sql sql语句
     * @return 结果集
     */
    public ResultSet doQueryForSet(String sql) {
        ResultSet result;
        try {
            result = doQueryBase(sql);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception, sql:" + sql);
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return null;
        }
    }

    /**
     * 查询
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 结果集
     */
    public ResultSet doQueryForSet(String sql, Object[] param) {
        ResultSet result;
        try {
            result = doQueryBase(sql, param);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception, sql:" + sql);
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return null;
        }
    }

    /**
     * 查询
     *
     * @param currPage 查询的当前页码
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ResultSet结果集
     */
    public Object[] doQueryForSet(long currPage, long maxCount, String pKey, String[] sqlArray) {
        return doQueryForSet(null, currPage, maxCount, pKey, sqlArray);
    }

    /**
     * 查询
     *
     * @param param    参数
     * @param currPage 查询的当前页码
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ResultSet结果集
     */
    public Object[] doQueryForSet(Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray) {
        Object[] result = new Object[3];
        try {
            Object[] tmpr = doQueryBase(param, currPage, maxCount, pKey, sqlArray);
            result[0] = tmpr[0];
            result[1] = tmpr[1];
            result[2] = tmpr[2];
            if (connection.getAutoCommit()) {
                this.close();
            }
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            result[0] = 1;
            result[1] = 0;
            result[2] = null;
        }
        return result;
    }

    /**
     * 查询
     *
     * @param sql sql语句
     * @return 结果List
     */
    public List<Map<String, Object>> doQueryForList(String sql) {
        List<Map<String, Object>> result;
        try {
            ResultSet rs = doQueryBase(sql);
            result = resultSetToList(rs);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception, sql:" + sql);
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return new ArrayList<>();
        }
    }

    /**
     * 查询
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 结果List
     */
    public List<Map<String, Object>> doQueryForList(String sql, Object[] param) {
        List<Map<String, Object>> result;
        try {
            ResultSet rs = doQueryBase(sql, param);
            result = resultSetToList(rs);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception, sql:" + sql);
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return new ArrayList<>();
        }
    }

    /**
     * 查询
     *
     * @param currPage 查询的当前页码
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ArrayList结果集
     */
    public Object[] doQueryForList(long currPage, long maxCount, String pKey, String[] sqlArray) {
        return doQueryForList(null, currPage, maxCount, pKey, sqlArray);
    }

    /**
     * 查询
     *
     * @param param    参数
     * @param currPage 查询的当前页码
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ArrayList结果集
     */
    public Object[] doQueryForList(Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray) {
        Object[] result = new Object[3];
        try {
            Object[] tmpr = doQueryBase(param, currPage, maxCount, pKey, sqlArray);
            result[0] = tmpr[0];
            result[1] = tmpr[1];
            result[2] = resultSetToList((ResultSet) tmpr[2]);
            if (connection.getAutoCommit()) {
                this.close();
            }
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            result[0] = 1;
            result[1] = 0;
            result[2] = new ArrayList<Map<String, Object>>();
        }
        return result;
    }

    /**
     * 查询
     *
     * @param sql sql语句
     * @return json对象
     */
    public JsonNode doQueryForJSON(String sql) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode result;
        try {
            ResultSet rs = doQueryBase(sql);
            result = resultSetToJSON(rs);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception, sql:" + sql);
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return mapper.createArrayNode();
        }
    }

    /**
     * 查询
     *
     * @param sql   sql语句
     * @param param 参数
     * @return json对象
     */
    public JsonNode doQueryForJSON(String sql, Object[] param) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode result;
        try {
            ResultSet rs = doQueryBase(sql, param);
            result = resultSetToJSON(rs);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception, sql:" + sql);
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return mapper.createArrayNode();
        }
    }

    /**
     * 查询
     *
     * @param currPage 查询的当前页码
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return JSONObject:{pages:int-总页数,records:int-总记录数,datas:array-结果集}
     */
    public JsonNode doQueryForJSON(long currPage, long maxCount, String pKey, String[] sqlArray) {
        return doQueryForJSON(null, currPage, maxCount, pKey, sqlArray);
    }

    /**
     * 查询
     *
     * @param param    参数
     * @param currPage 查询的当前页码
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return JSONObject:{pages:int-总页数,records:int-总记录数,datas:array-结果集}
     */
    public JsonNode doQueryForJSON(Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();
        try {
            Object[] tmpr = doQueryBase(param, currPage, maxCount, pKey, sqlArray);
            result.put("pages", Long.valueOf(String.valueOf(tmpr[0])));
            result.put("records", Long.valueOf(String.valueOf(tmpr[1])));
            result.set("datas", resultSetToJSON((ResultSet) tmpr[2]));
            if (connection.getAutoCommit()) {
                this.close();
            }
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            result.put("pages", 1);
            result.put("records", 0);
            result.set("datas", mapper.createArrayNode());
        }
        return result;
    }

    /**
     * 查询
     *
     * @param sql sql语句
     * @param cls 类
     * @return 结果List
     */
    public <T> List<T> doQueryForCusObjList(String sql, Class<T> cls) {
        List<T> result;
        try {
            ResultSet rs = doQueryBase(sql);
            result = resultSetToCusObjList(rs, cls);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return new ArrayList<>();
        }
    }

    /**
     * 查询
     *
     * @param sql   sql语句
     * @param param 参数
     * @param cls   类
     * @return 结果List
     */
    public <T> List<T> doQueryForCusObjList(String sql, Object[] param, Class<T> cls) {
        List<T> result;
        try {
            ResultSet rs = doQueryBase(sql, param);
            result = resultSetToCusObjList(rs, cls);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return new ArrayList<>();
        }
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
    public Object[] doQueryForCusObjList(long currPage, long maxCount, String pKey, String[] sqlArray, Class<?> cls) {
        return doQueryForCusObjList(null, currPage, maxCount, pKey, sqlArray, cls);
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
    public Object[] doQueryForCusObjList(Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray, Class<?> cls) {
        Object[] result = new Object[3];
        try {
            Object[] tmpr = doQueryBase(param, currPage, maxCount, pKey, sqlArray);
            result[0] = tmpr[0];
            result[1] = tmpr[1];
            result[2] = resultSetToCusObjList((ResultSet) tmpr[2], cls);
            if (connection.getAutoCommit()) {
                this.close();
            }
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            result[0] = 1;
            result[1] = 0;
            result[2] = new ArrayList<>();
        }
        return result;
    }

    /**
     * 查询LOB数据
     *
     * @param tableName   表名
     * @param whereValues key:字段名称,value:字段值
     * @param lobColName  lob字段名称
     * @return 输入流
     */
    public InputStream doQueryLOB(String tableName, Map<String, Object> whereValues, String lobColName) {
        try {
            InputStream result = null;
            Object[] where = BaseDBInstance.buildWhere(dbType, whereValues);
            String sql = "select " + lobColName.toUpperCase() + " from " + tableName + where[0];
            Object[] param = (Object[]) where[1];
            ResultSet rs;
            if (param.length > 0) {
                rs = doQueryBase(sql, param);
            } else {
                rs = doQueryBase(sql, null);
            }
            if (rs.next()) {
                Blob lob = rs.getBlob(1);
                if (lob != null) {
                    result = lob.getBinaryStream();
                } else {
                    result = null;
                }
            }
            rs.close();
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return null;
        }
    }

    /**
     * 查询CLOB数据
     *
     * @param tableName   表名
     * @param whereValues key:字段名称,value:字段值
     * @param lobColName  lob字段名称
     * @return 结果字符串
     */
    public String doQueryCLOB(String tableName, Map<String, Object> whereValues, String lobColName) {
        try {
            Object[] where = BaseDBInstance.buildWhere(dbType, whereValues);
            String sql = "select " + lobColName.toUpperCase() + " from " + tableName + where[0];
            Object[] param = (Object[]) where[1];
            ResultSet rs;
            if (param.length > 0) {
                rs = doQueryBase(sql, param);
            } else {
                rs = doQueryBase(sql, null);
            }
            Reader result = null;
            if (rs.next()) {
                Clob lob = rs.getClob(1);
                if (lob != null) {
                    result = lob.getCharacterStream();
                } else {
                    result = null;
                }
            }
            rs.close();
            String ret = null;
            if (result != null) {
                BufferedReader br = new BufferedReader(result);
                StringBuilder buffer = new StringBuilder();
                String str = br.readLine();
                while (!CommonUtils.INSTANCE.isNullStr(str)) {
                    buffer.append(str);
                    str = br.readLine();
                }
                ret = buffer.toString();
            }
            if (connection.getAutoCommit()) {
                this.close();
            }
            return ret;
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return null;
        }
    }

    /**
     * 执行SQL
     *
     * @param sql sql语句
     * @return 成功或失败
     */
    public boolean doUpdate(String sql) {
        try {
            doExecute(sql, null);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return true;
        } catch (Exception e) {
            log.error("excute Exception, sql:" + sql);
            log.error("excute Exception:" + e.getMessage(), e);
            this.close();
            return false;
        }
    }

    /**
     * 执行SQL
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 成功或失败
     */
    public boolean doUpdate(String sql, Object[] param) {
        try {
            doExecute(sql, param);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return true;
        } catch (Exception e) {
            log.error("excute Exception, sql:" + sql);
            log.error("excute Exception:" + e.getMessage(), e);
            this.close();
            return false;
        }
    }

    /**
     * 插入操作，返回自动生成的字段列表
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 自动生成字段列表：KEY=字段名，VALUE=字段值 | 异常：null
     */
    public List<Map<String, Object>> doUpdateWithFill(String sql, Object[] param) {
        List<Map<String, Object>> result;
        try {
            ResultSet rs = doExecute(sql, param);
            result = resultSetToList(rs);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("excute Exception, sql:" + sql);
            log.error("excute Exception:" + e.getMessage(), e);
            this.close();
            return null;
        }
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
        try {
            getConnection();
            stmt.clearBatch();
            boolean commitable = false;
            if (connection.getAutoCommit()) {
                beginTranslist();
                commitable = true;
            }
            BaseDBInstance dbInstance = produceDBInstance();
            dbInstance.doInsertLOB(dbType, connection, tableName, whereValues, lobColName, input);
            if (commitable) {
                commitTranslist();
            }
            return true;
        } catch (Exception e) {
            log.error("excute Exception:" + e.getMessage(), e);
            this.close();
            return false;
        }
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
        try {
            getConnection();
            stmt.clearBatch();
            boolean commitable = false;
            if (connection.getAutoCommit()) {
                beginTranslist();
                commitable = true;
            }
            BaseDBInstance dbInstance = produceDBInstance();
            dbInstance.doUpdateLOB(dbType, connection, tableName, whereValues, lobColName, input);
            if (commitable) {
                commitTranslist();
            }
            return true;
        } catch (Exception e) {
            log.error("excute Exception:" + e.getMessage(), e);
            this.close();
            return false;
        }
    }

    /**
     * 开始事务
     */
    public void beginTranslist() {
        try {
            getConnection();
            connection.setAutoCommit(false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.close();
        }
    }

    /**
     * 提交事务
     */
    public void commitTranslist() {
        try {
            if (connection != null && !connection.getAutoCommit()) {
                connection.commit();
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            this.close();
        }
    }

    /**
     * 回滚事务
     */
    public void rollBackTranslist() {
        try {
            if (connection != null && !connection.getAutoCommit()) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            this.close();
        }
    }

    /**
     * 添加批量SQL语句
     *
     * @param sql sql语句
     */
    public void addBatch(String sql) {
        try {
            getConnection();
            stmt.addBatch(sql);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.close();
        }
    }

    /**
     * 批量执行SQL
     *
     * @return 成功或失败
     */
    public boolean excuteBatch() {
        try {
            getConnection();
            this.beginTranslist();
            stmt.executeBatch();
            stmt.clearBatch();
            this.commitTranslist();
            return true;
        } catch (Exception e) {
            this.rollBackTranslist();
            log.error("excute Exception:" + e.getMessage(), e);
            return false;
        } finally {
            this.close();
        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        try {
            if (pstmt != null && !pstmt.isClosed()) {
                pstmt.close();
            }
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
            if (connection != null && !connection.isClosed()) {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                }
                connection.close();
            }
            pstmt = null;
            stmt = null;
            connection = null;
        } catch (Exception e) {
            log.error("close database connection Exception:", e);
        }
    }

    /**
     * 获取数据库链接类型
     *
     * @return 链接类型
     */
    public DbType getDbType() {
        return dbType;
    }

    /**
     * 获取数据源编号
     *
     * @return 数据源编号
     */
    public int getDbNo() {
        return connectionNo;
    }

    /**
     * 获取数据库驱动类型
     *
     * @return 驱动类型
     */
    public String getDbDriverType() {
        try {
            getConnection();
            String dbDriver = connection.getMetaData().getDriverName().toLowerCase();
            this.close();
            return dbDriver;
        } catch (Exception e) {
            log.error("getDbDriverType Exception:" + e.getMessage(), e);
            this.close();
            return "";
        }
    }

    /**
     * 是否自动提交事务
     *
     * @return 是否自动提交
     */
    public boolean isAutoCommit() {
        try {
            return connection == null || connection.getAutoCommit();
        } catch (SQLException e) {
            log.error("isAutoCommit Exception:" + e.getMessage(), e);
            return true;
        }
    }

    public static int getDefaultConnectionNo() {
        return DEFAULT_CONNECTION_NO;
    }

    public static void setDefaultConnectionNo(int defaultConnectionNo) {
        DEFAULT_CONNECTION_NO = defaultConnectionNo;
    }

}
