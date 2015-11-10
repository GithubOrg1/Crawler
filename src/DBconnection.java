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
	            for(int i=0;i<message.length;i++){
	            	String[] array = (String[]) message[i];
	            	for(int j=0;j<10;j++){
	            		st.setString(i+1, array[j]);
	            	}
		       		 //st.setString(2, email[i]);
		       		 //st.setString(3, profile[i]);
		       		 //st.setString(4, name[i]);
		       		 //st.setString(5, direction[i]);
	            	 //st.executeUpdate();
	            	}
	            st.executeUpdate();
	            }finally {
	    			try {
	    				conn.close();
	    			} catch (Exception e) {
	    				e.printStackTrace();
	    			}
	    		}
	 } 
}
