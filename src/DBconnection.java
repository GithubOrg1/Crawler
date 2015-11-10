import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class DBconnection {
	public void connectdb(String sql,Object[] message) throws SQLException, ClassNotFoundException
	{
	        Connection conn = null;
	        try {
	        	Class.forName("com.mysql.jdbc.Driver");
	            System.out.println("成功加载MySQL驱动程序");
	            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/my_homepage","root","179324865");
	            PreparedStatement st = conn.prepareStatement(sql);
	            for(int j=0;j<10;j++){
		            for(int i=0;i<message.length;i++){
		            	String[] array = (String[]) message[i];
		            	st.setString(i+1, array[j]);	  
		            }
		            st.executeUpdate();
	            }
	            }finally {
	    			try {
	    				conn.close();
	    			} catch (Exception e) {
	    				e.printStackTrace();
	    			}
	    		}
	 } 
}
