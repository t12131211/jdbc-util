import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DqlUtil {

    public static <T> List<T> executeQuery(String sql, T t, Object... objs) {
        List<T> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);

            if (ps != null) {
                for (int i = 0; i < objs.length; i++) {
                    ps.setObject((i + 1), objs[i]);
                }
            }

            rs = ps.executeQuery();
            ResultSetMetaData rm = rs.getMetaData();
            int columnCount = rm.getColumnCount();

            while (rs.next()) {
                Class<? extends Object> cla = t.getClass();
                T newInstance = (T) cla.newInstance();

                // 一個for循環封裝一列資料所有值
                for (int i = 0; i < columnCount; i++) {
                    String columnName = rm.getColumnName((i + 1));
                    // 獲取變數對應的setter方法
                    String methodName = "set" + columnName.substring(0, 1).toUpperCase()
                            + columnName.substring(1).toLowerCase();
                    String columnClassName = rm.getColumnClassName((i + 1));
                    // 創建方法對象
                    Method method = cla.getDeclaredMethod(methodName, Class.forName(columnClassName));
                    method.invoke(newInstance, rs.getObject(columnName));
                }
                list.add(newInstance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JdbcUtil.closeConnection(rs, ps, conn);
        }
        return list;
    }

    public static <T> List<T> queryGeneric(T t, String sql, Object... obj) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<T> list = new ArrayList<T>();
        try {
            con = JdbcUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            for (int i = 0; i < obj.length; i++) {
                pstmt.setObject(i + 1, obj[i]);
            }

            rs = pstmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            while (rs.next()) {
                Class<? extends Object> clazz = t.getClass();
                T bean = (T) clazz.newInstance();

                for (int j = 0; j < columnCount; j++) {
                    String column = rsmd.getColumnLabel(j + 1);
                    Field field = clazz.getDeclaredField(column);
                    field.setAccessible(true);
                    Object value = rs.getObject(column);
                    field.set(bean, value);
                }
                list.add(bean);
            }

        } catch(NoSuchFieldException nf) {
            nf.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            JdbcUtil.closeConnection(rs, pstmt, con);
        }

        return list;
    }
}
