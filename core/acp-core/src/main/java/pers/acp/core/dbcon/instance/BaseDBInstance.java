package pers.acp.core.dbcon.instance;

import pers.acp.core.dbcon.DbType;
import pers.acp.core.log.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2016/7/21.
 * 数据库操作工厂基类
 */
public abstract class BaseDBInstance {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private PreparedStatement pstmt = null;

    public PreparedStatement getPstmt() {
        return pstmt;
    }

    void setPstmt(PreparedStatement pstmt) {
        this.pstmt = pstmt;
    }

    /**
     * 构建LOB where条件语句
     *
     * @param dbType      数据库类型
     * @param whereValues where条件
     * @return Object[]:0-sql，1-参数Object[]
     */
    public static Object[] buildWhere(DbType dbType, Map<String, Object> whereValues) {
        List<Object> param = new ArrayList<>();
        StringBuilder sql = new StringBuilder(" where 1=1");
        for (Map.Entry<String, Object> entry : whereValues.entrySet()) {
            if (entry.getValue() == null) {
                sql.append(" and ").append(dbType.formatName(entry.getKey().toUpperCase())).append(" is null ");
            } else {
                param.add(entry.getValue());
                sql.append(" and ").append(dbType.formatName(entry.getKey().toUpperCase())).append("=? ");
            }
        }
        return new Object[]{sql.toString(), param.toArray()};
    }

    /**
     * 分页查询
     *
     * @param dbType     数据库类型
     * @param connection 数据库连接对象
     * @param param      参数
     * @param currPage   查询的当前页码
     * @param maxCount   每页最大记录数
     * @param pKey       主键名称
     * @param sqlArray   [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return 结果集
     */
    public abstract ResultSet doQueryPage(DbType dbType, Connection connection, Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray) throws SQLException;

    /**
     * 插入LOB数据
     *
     * @param dbType      数据库类型
     * @param connection  数据库连接对象
     * @param tableName   表名
     * @param whereValues key:字段名称,value:字段值
     * @param lobColName  lob字段名称
     * @param input       lob数据流
     */
    public void doInsertLOB(DbType dbType, Connection connection, String tableName, Map<String, Object> whereValues, String lobColName, InputStream input) throws SQLException, IOException {
        StringBuilder builder = new StringBuilder();
        StringBuilder param = new StringBuilder();
        builder.append("insert into ").append(dbType.formatName(tableName)).append("(").append(dbType.formatName(lobColName));
        whereValues.entrySet().stream().filter(entry -> entry.getValue() != null).forEach(entry -> {
            builder.append(",").append(dbType.formatName(entry.getKey().toUpperCase()));
            param.append(",?");
        });
        builder.append(") values(?").append(param).append(")");
        log.debug("sql=" + builder.toString());
        PreparedStatement pstmt = connection.prepareStatement(builder.toString());
        pstmt.setBlob(1, input);
        int count = 2;
        for (Map.Entry<String, Object> entry : whereValues.entrySet()) {
            if (entry.getValue() != null) {
                pstmt.setObject(count, entry.getValue());
                count++;
            }
        }
        pstmt.executeUpdate();
        if (input != null) {
            input.close();
        }
        pstmt.close();
    }

    /**
     * 更新LOB数据
     *
     * @param dbType      数据库类型
     * @param connection  数据库连接对象
     * @param tableName   表名
     * @param whereValues key:字段名称,value:字段值
     * @param lobColName  lob字段名称
     * @param input       lob数据流
     */
    public void doUpdateLOB(DbType dbType, Connection connection, String tableName, Map<String, Object> whereValues, String lobColName, InputStream input) throws SQLException, IOException {
        Object[] where = buildWhere(dbType, whereValues);
        String sqlStr = "update " + dbType.formatName(tableName) + " set " + dbType.formatName(lobColName) + "=? " + where[0];
        log.debug("sql=" + sqlStr);
        PreparedStatement pstmt = connection.prepareStatement(sqlStr);
        pstmt.setBlob(1, input);
        int count = 2;
        for (Map.Entry<String, Object> entry : whereValues.entrySet()) {
            if (entry.getValue() != null) {
                pstmt.setObject(count, entry.getValue());
                count++;
            }
        }
        pstmt.executeUpdate();
        if (input != null) {
            input.close();
        }
        pstmt.close();
    }

}
