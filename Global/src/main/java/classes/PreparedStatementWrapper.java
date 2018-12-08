package classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


//Wrapper class voor Prepared statements
public class PreparedStatementWrapper {
    private String sql;
    private Map<Integer, Object> parameters;

    public PreparedStatementWrapper(String sql) throws SQLException {
        this.sql = sql;
        this.parameters = new HashMap<>();
    }


    public void setString(int index, String x) throws SQLException {
        parameters.put(index, x);
    }

    public void setInt(int index, int x) throws SQLException {
        parameters.put(index, x);
    }

    public void setLong(int index, long x) throws SQLException {
        parameters.put(index, x);
    }

    public void setBytes(int index, byte[] x) throws SQLException {
        parameters.put(index, x);
    }

    public ResultSet executeQuery(Connection conn) throws SQLException {
        return createStatement(conn).executeQuery();
    }

    public void executeUpdate(Connection conn) throws SQLException {
        createStatement(conn).executeUpdate();
    }

    private PreparedStatement createStatement(Connection conn) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for (Map.Entry<Integer, Object> o : parameters.entrySet()) {
            if (o.getValue() instanceof Integer) {
                pstmt.setInt(o.getKey(), (int) o.getValue());
            }
            if (o.getValue() instanceof Long) {
                pstmt.setLong(o.getKey(), (long) o.getValue());
            }
            if (o.getValue() instanceof byte[]) {
                pstmt.setBytes(o.getKey(), (byte[]) o.getValue());
            }
            if (o.getValue() instanceof String) {
                pstmt.setString(o.getKey(), (String) o.getValue());
            }
        }
        return pstmt;
    }

}
