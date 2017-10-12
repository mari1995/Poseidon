package frame.util;


import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * 数据库工具类
 * Created by sumei on 17/9/11.
 *
 */
public class JDBCUtil {
	
	// 根据c3p0获得数据库的配置信息
	private static ComboPooledDataSource datasource = new ComboPooledDataSource();
	
	// 多线程中的副本，每个线程中独立一份资源
	public static ThreadLocal<Connection> tconn = new ThreadLocal<>();

	static {
		try {
			Class.forName(datasource.getDriverClass());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获得数据库链接
	 * @return
	 */
	public static Connection getConnection() {
		if ( tconn.get() == null ) {
			try {
				return datasource.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return tconn.get();
	}
	
	
	/**
	 * 事务回滚
	 */
	public static void rollback() {
		if ( tconn.get() != null ) {
			Connection conn = tconn.get();
			
			try {
				conn.rollback();
				conn.close();
				tconn.set(null);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 事务提交
	 */
	public static void commit() {
		if ( tconn.get() != null ) {
			Connection conn = tconn.get();
			
			try {
				conn.commit();
				conn.close();
				tconn.set(null);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 数据库事务的开启
	 */
	public static void beginTransaction() {
		if ( tconn.get() == null ) {
			Connection newConn = getConnection();
			tconn.set(newConn);
			
			try {
				newConn.setAutoCommit(false);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 关闭资源通道
	 * @param conn
	 * @param stmt
	 * @param rs
	 */
	public static void close( Connection conn , PreparedStatement stmt , ResultSet rs ) {
		if ( conn != null ) {
			try {
				// 判断有没有自动提交，连接池中链接
				if ( conn.getAutoCommit() ) {
					conn.close();
				}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} 
		
		if ( stmt != null ) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if ( rs != null ) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
