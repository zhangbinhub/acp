package pers.acp.core.dbcon.instance;

import pers.acp.core.dbcon.DbType;
import pers.acp.core.log.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.Map;

/**
 * Created by zhang on 2016/7/21.
 * Oracle操作工厂
 */
public class OracleInstance extends BaseDBInstance {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    /**
     * 以流形式往数据库中写入Blob
     *
     * @param blob  blob对象
     * @param input 输入流
     */
    private void writeBLOB(Blob blob, InputStream input) throws SQLException, IOException {
        OutputStream out = blob.setBinaryStream(1L);
        byte buffer[] = new byte[1024];
        int len;
        while ((len = input.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        out.close();
    }

    @Override
    public ResultSet doQueryPage(DbType dbType, Connection connection, Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray) throws SQLException {
        String sql = "" + "select " + sqlArray[0] + " from " + sqlArray[1] + " " + sqlArray[2] + " " + sqlArray[3];
        String strSQL = "select q_table_t.* from (select q_table.*,ROWNUM as rn from (" + sql + ") q_table where ROWNUM<=" + (currPage * maxCount) + ") q_table_t where q_table_t.rn>" + (currPage - 1) * maxCount;
        log.debug("sql=" + strSQL);
        PreparedStatement pstmt = connection.prepareStatement(strSQL);
        if (param != null) {
            for (int i = 0; i < param.length; i++) {
                pstmt.setObject(i + 1, param[i]);
            }
        }
        this.setPstmt(pstmt);
        return pstmt.executeQuery();
    }

    @Override
    public void doInsertLOB(DbType dbType, Connection connection, String tableName, Map<String, Object> whereValues, String lobColName, InputStream input) throws SQLException, IOException {
        StringBuilder builder = new StringBuilder();
        StringBuilder param = new StringBuilder();
        builder.append("insert into ").append(dbType.formatName(tableName)).append("(").append(dbType.formatName(lobColName));
        whereValues.entrySet().stream().filter(entry -> entry.getValue() != null).forEach(entry -> {
            builder.append(",").append(dbType.formatName(entry.getKey().toUpperCase()));
            param.append(",?");
        });
        builder.append(") values(EMPTY_BLOB()").append(param).append(")");
        log.debug("sql=" + builder.toString());
        PreparedStatement pstmt = connection.prepareStatement(builder.toString());
        int count = 1;
        for (Map.Entry<String, Object> entry : whereValues.entrySet()) {
            if (entry.getValue() != null) {
                pstmt.setObject(count, entry.getValue());
                count++;
            }
        }
        pstmt.executeUpdate();
        pstmt.close();
        if (input != null) {
            Object[] where = buildWhere(dbType, whereValues);
            String sqlStr = "select " + dbType.formatName(lobColName.toUpperCase()) + " from " + dbType.formatName(tableName) + where[0] + " for update";
            log.debug("sql=" + sqlStr);
            pstmt = connection.prepareStatement(sqlStr);
            count = 1;
            for (Map.Entry<String, Object> entry : whereValues.entrySet()) {
                if (entry.getValue() != null) {
                    pstmt.setObject(count, entry.getValue());
                    count++;
                }
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Blob blob = rs.getBlob(lobColName.toUpperCase());
                writeBLOB(blob, input);
                input.close();
            }
            rs.close();
            pstmt.close();
        }
    }

    @Override
    public void doUpdateLOB(DbType dbType, Connection connection, String tableName, Map<String, Object> whereValues, String lobColName, InputStream input) throws SQLException, IOException {
        Object[] where = buildWhere(dbType, whereValues);
        if (input != null) {
            String sqlStr = "select " + dbType.formatName(lobColName.toUpperCase()) + " from " + dbType.formatName(tableName) + where[0] + " for update";
            log.debug("sql=" + sqlStr);
            PreparedStatement pstmt = connection.prepareStatement(sqlStr);
            int count = 1;
            for (Map.Entry<String, Object> entry : whereValues.entrySet()) {
                if (entry.getValue() != null) {
                    pstmt.setObject(count, entry.getValue());
                    count++;
                }
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Blob blob = rs.getBlob(lobColName.toUpperCase());
                writeBLOB(blob, input);
                input.close();
            }
            rs.close();
            pstmt.close();
        } else {
            String sqlStr = "update " + dbType.formatName(tableName) + " set " + dbType.formatName(lobColName.toUpperCase()) + "=empty_blob() " + where[0];
            log.debug("sql=" + sqlStr);
            PreparedStatement pstmt = connection.prepareStatement(sqlStr);
            int count = 1;
            for (Map.Entry<String, Object> entry : whereValues.entrySet()) {
                if (entry.getValue() != null) {
                    pstmt.setObject(count, entry.getValue());
                    count++;
                }
            }
            pstmt.executeUpdate();
            pstmt.close();
        }
    }

}
