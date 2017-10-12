package frame.base;


import frame.annotation.AField;
import frame.util.JDBCUtil;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作工具类
 * Created by sumei on 17/9/7.
 */
public class DBSQL<T> {

    private Connection conn;
    private PreparedStatement stmt;
    private ResultSet rs;

    // 查询语句
    private StringBuffer sql;

    // 查询的参数
    private Object[] params;

    // 操作实体类
    Class<T> clazz;

    /**
     * 创建DBSQL
     * @param sql
     */
    public DBSQL(StringBuffer sql) {
        this.sql = sql;

        try {
            conn = JDBCUtil.getConnection();
            stmt = conn.prepareStatement(sql.toString());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加sql语句的查询参数
     *
     * @param params
     * @return
     */
    public DBSQL addParams(Object... params) {
        this.params = params;
        try {
            if (null != params && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 添加操作实体类进行操作
     *
     * @param clazz
     * @return
     */
    public DBSQL addEntity(Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }

    /**
     * 添加数据，并且获得添加数据的主键
     * @return
     * @throws SQLException
     */
    public int add() throws SQLException {
        try {
            int rows = stmt.executeUpdate();

            if ( rows > 0 ) {
                // 获得插入数据的主键
                rs = stmt.getGeneratedKeys();

                if ( rs.next() ) {
                    return rs.getInt(1);
                }
            }
        } finally {
            JDBCUtil.close(conn,stmt,rs);
        }
        return -1;
    }

    /**
     * 修改数据和删除数据
     *
     * @return
     */
    public int update() throws SQLException {
        try {
            return stmt.executeUpdate();
        } finally {
            JDBCUtil.close(conn, stmt, rs);
        }
    }

    /**
     * 查询所有
     *
     * @return
     */
    public List<T> queryAll() throws SQLException {
        List<T> list = new ArrayList<>();

        // 获得实体类中的所有成员变量属性
        Field[] fields = clazz.getDeclaredFields();

        try {
            rs = stmt.executeQuery();

            while (rs.next()) {
                T t = clazz.newInstance();

                for (Field f : fields) {
                    f.setAccessible(true);

                    String fieldName = null;
                    AField anno = f.getAnnotation(AField.class);
                    if ( anno != null ) {
                        fieldName = anno.name();
                    } else {
                        fieldName = f.getName();
                    }

                    Object value = null;
                    try {
                        value = rs.getObject(fieldName);
                    } catch (SQLException e) {
                    }


                    f.set(t, value);
                }
                list.add(t);

            }


            return list;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(conn, stmt, rs);
        }
        return null;
    }

    public T queryOne() throws SQLException {
        List<T> ts = queryAll();

        if ( null != ts && ts.size() > 0 ) {
            return  ts.get(0);
        }
        return null;
    }

    /**
     * 获得离线结果集
     * @return
     * @throws SQLException
     */
    public ResultSet queryResultSet() throws SQLException {
        try {
            rs = stmt.executeQuery();

            // 创建离线结果集
            RowSetFactory factory = RowSetProvider.newFactory();
            CachedRowSet cachedRowSet = factory.createCachedRowSet();

            // 赋值
            cachedRowSet.populate(rs);

            return cachedRowSet;
        } finally {
            JDBCUtil.close(conn,stmt,rs);
        }
    }
}

