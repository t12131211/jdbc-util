import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DmlUtil {

    public static Integer executeDML(String sql, Boolean flag, Object... objs) {
        Connection conn = null;
        PreparedStatement ps = null;
        Integer i = null;
        try {
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            if (objs != null) {
                for (int j = 0; j < objs.length; j++) {
                    ps.setObject((j + 1), objs[j]);
                }
            }

            conn.setAutoCommit(false);
            i = ps.executeUpdate();
            if (flag) {
                conn.commit();
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
                e.printStackTrace();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            JdbcUtil.closeConnection(ps, conn);
        }
        return i;
    }

}
