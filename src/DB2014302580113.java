import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class DB2014302580113 {
	
	Connection mycon = null;
	
	public void open(String connectionString,String USER,String PASSWORD) throws SQLException, ClassNotFoundException
	{
	        try {
	        	Class.forName("com.mysql.jdbc.Driver");
	            System.out.println("成功加载MySQL驱动程序");
	            mycon = DriverManager.getConnection(connectionString,USER,PASSWORD);
	            } catch (Exception e) {
	    				e.printStackTrace();
	    		}
	 } 
	
	public void excute(String sql,Object[] message) throws SQLException
	{
		try{
			 PreparedStatement st = mycon.prepareStatement(sql);
	         for(int j=0;j<10;j++){
		            for(int i=0;i<message.length;i++){
		            	String[] array = (String[]) message[i];
		            	st.setString(i+1, array[j]);	  
		            }
		            st.executeUpdate();
	         }
		}finally {
			try {
				mycon.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
