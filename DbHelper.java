import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//IDbhelper implement
public class DbHelper implements IDbhelper {
	private String url = "jdbc:mysql://127.0.0.1:3306/my_schema?"+
	"user=root&password=123456";

//	public DataSource getDataSource() {
//		return dataSource;
//	}
//
//	public void setDataSource(DataSource dataSource) {
//		this.dataSource = dataSource;
//	}

	private Connection getConnection(String url) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(url);
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ResultSet runSelect(String sql){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection(url);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return rs;
	}
	// update and delete methods without parameters
	public void runUpdate(String sql) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection(url);
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// update and delete methods with parameters
	public void runUpdate(String sql, Object[] params) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection(url);
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
