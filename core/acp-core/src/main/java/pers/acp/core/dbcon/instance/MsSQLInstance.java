package pers.acp.core.dbcon.instance;

import pers.acp.core.dbcon.DbType;
import pers.acp.core.log.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhang on 2016/7/21.
 * MicroSoftSQL操作工厂
 */
public class MsSQLInstance extends BaseDBInstance {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    @Override
    public ResultSet doQueryPage(DbType dbType, Connection connection, Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray) throws SQLException {
        String sql = "select " + sqlArray[0] + " from " + sqlArray[1];
        String strSQL = "select top " + maxCount + " " + sqlArray[0] + " from (select * from (" + sql + ")q_table where q_table." + pKey + " not in (select top " + ((currPage - 1) * maxCount) + " " + pKey + " from " + sqlArray[1] + " " + sqlArray[2] + " " + sqlArray[3] + "))q_table_t " + sqlArray[2] + " " + sqlArray[3];
        log.debug("sql=" + strSQL);
        PreparedStatement pstmt = connection.prepareStatement(strSQL);
        if (param != null) {
            for (int i = 0; i < param.length * 2; i++) {
                pstmt.setObject(i + 1, param[i % param.length]);
            }
        }
        this.setPstmt(pstmt);
        return pstmt.executeQuery();
    }

}
