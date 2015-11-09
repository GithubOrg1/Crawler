import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Spider {
	
	  private static final int MAX_PAGES_TO_SEARCH = 10;
	  private static List<String> links = new LinkedList<String>();
	  private static String[] name=new  String[10];
	  private static String[] profile=new String[10];
	  private static String[] tel=new String[10];
	  private static String[] email=new String[10];
	  private static String[] direction=new String[10];
	  private static int n=0;

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Spider spider = new Spider();
		spider.getLinks();
		spider.runSingleThread();
		spider.runMultiThread();
		spider.DBconnection();
	}
	
	public void runSingleThread()
	{
		long startTime1 = System.currentTimeMillis();
		for(int index=0;index<MAX_PAGES_TO_SEARCH;index++)
		{
			try
			{
				Document doc1 = Jsoup.connect(links.get(index)).timeout(30000).get();
				//String string = doc1.body().getAllElements().get(0).html().replaceAll("&nbsp;", "");
				//doc1 = Jsoup.parse(string);
				parse(doc1);
			}
			catch(IOException e)
	        {
	            // We were not successful in our HTTP request
				e.printStackTrace();
	        }
		}
		long endTime1 = System.currentTimeMillis();
		System.out.println("单线程爬取耗时："+(endTime1-startTime1)+"ms");

	}
	
	public void runMultiThread()
	{
		long startTime2 = System.currentTimeMillis();
		for(int index=0;index<MAX_PAGES_TO_SEARCH;index++)
		{
			readUrlThread t = new readUrlThread(links.get(index));
			t.start();
			try
			{
				t.join();
			}
			catch(Exception e)
	        {
	            // We were not successful in our HTTP request
				e.printStackTrace();
	        }
		}
		long endTime2 = System.currentTimeMillis();
		System.out.println("多线程爬取耗时："+(endTime2-startTime2)+"ms");
	}
	
	class readUrlThread extends Thread
	{
		private String url;
		public readUrlThread(String url)
		{
			this.url = url;
		}
		public void run()
		{
			try {
				Document doc = Jsoup.connect(url).timeout(30000).get();
				//String string = doc.body().getAllElements().get(0).html().replaceAll("&nbsp;", "");
				//doc = Jsoup.parse(string);
				parse(doc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public void getLinks()
	{
		String url = "http://www.wpi.edu/academics/cs/research-interests.html";
		try
        {
			Document doc = Jsoup.connect(url).get();
           
            Elements linksOnPage = doc.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");
            for(Element link : linksOnPage)
            {
            	Pattern pattern = Pattern.compile("(www\\.wpi\\.edu/academics/(facultydir)|(datascience)/)(.+)");
                Matcher matcher = pattern.matcher(link.attr("abs:href"));
                while(matcher.find())
                {
                    links.add(link.absUrl("href"));
                    System.out.println(link.attr("abs:href"));
                }
   
            }/*
            System.out.println(links.size());	
            for(int index=0;index<MAX_PAGES_TO_SEARCH;index++) 
            {
           	 File file = new File("homepage"+index+1+".html");
           	 HttpRequest.get(links.get(index)).receive(file);
           	 System.out.println(links.get(index));
            }*/
           
        }
		catch(IOException e)
        {
            // We were not successful in our HTTP request
			e.printStackTrace();
        }
	}
	
	public void parse(Document doc)
	{
		String telStr = null;
		String emailStr = null;
		String nameStr = doc.getElementsByTag("h2").text();
		String contactinfo = doc.getElementById("contactinfo").getElementsByTag("p").get(0).text();
		Pattern p_tel = Pattern.compile("\\+((\\-)?[\\d]+)+");
		Matcher matcher1 = p_tel.matcher(contactinfo);
		Pattern p_email = Pattern.compile("[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?");
		Matcher matcher2 = p_email.matcher(contactinfo);
		if (matcher1.find()) {
		    telStr = matcher1.group(0);
		}
		if (matcher2.find()) {
		    emailStr = matcher2.group(0);
		}
		String directionStr = doc.getElementById("twocol").getElementsByTag("ul").get(0).text();
		String profileStr = doc.getElementById("content").getElementsByTag("p").get(0).text();
		
		if(n<=9){
	    	name[n]=nameStr;
	    	profile[n]=profileStr;
	    	tel[n]=telStr;
	    	email[n]=emailStr;
	    	direction[n]=directionStr;
	    	n++;
		}
	}
	
	public void DBconnection() throws SQLException, ClassNotFoundException
	{
		 //Connection conn = null;

	        String sql;
	       // String url = "jdbc:mysql://127.0.0.1:3306/my_homepage"
	                //+ "user=root&password=179324865";
	        Connection conn = null;
	        try {
	        	Class.forName("com.mysql.jdbc.Driver");
	            System.out.println("成功加载MySQL驱动程序");
	            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/my_homepage","root","179324865");
	            sql = "insert into teacherlist(tel,e_mail,profile,name,direction) values(?,?,?,?,?)";
	            PreparedStatement st = conn.prepareStatement(sql);
	            for(int i=0;i<9;i++){
	       		 st.setString(1, tel[i]);
	       		 st.setString(2, email[i]);
	       		 st.setString(3, profile[i]);
	       		 st.setString(4, name[i]);
	       		 st.setString(5, direction[i]);
	       		 st.executeUpdate();
	            }
	            st.executeUpdate();
	            conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } /*catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            conn.close();
	        }*/
	}
}
