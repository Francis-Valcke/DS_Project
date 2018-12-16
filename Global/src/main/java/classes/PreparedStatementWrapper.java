package classes;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


//Wrapper class voor Prepared statements
public class PreparedStatementWrapper implements Serializable {
    private String sql;
    private Map<Integer, Object> parameters;

    public PreparedStatementWrapper(String sql) {
        this.sql = sql;
        this.parameters = new HashMap<>();
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Map<Integer, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<Integer, Object> parameters) {
        this.parameters = parameters;
    }

    public void setString(int index, String x) {
        parameters.put(index, x);
    }

    public void setInt(int index, int x) {
        parameters.put(index, x);
    }

    public void setLong(int index, long x) {
        parameters.put(index, x);
    }

    public void setBytes(int index, byte[] x) {
        parameters.put(index, x);
    }

    public void setBoolean(int index, boolean x) {
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
            if (o.getValue() instanceof Boolean) {
                pstmt.setBoolean(o.getKey(), (boolean) o.getValue());
            }
        }
        return pstmt;
    }

}
