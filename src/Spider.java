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
	
	  private final int MAX_PAGES_TO_SEARCH = 10;
	  private static List<String> links = new LinkedList<String>();
	  Parser parser = new Parser();
	  public static Object[] message;
	  /*private static String[] name=new  String[10];
	  private static String[] profile=new String[10];
	  private static String[] tel=new String[10];
	  private static String[] email=new String[10];
	  private static String[] direction=new String[10];
	  private static int n=0;*/

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Spider spider = new Spider();
		spider.getLinks();
		spider.runSingleThread();
		spider.runMultiThread();
		DBconnection connect = new DBconnection();
		connect.connectdb("insert into teacherlist(tel,e_mail,profile,name,direction) values(?,?,?,?,?)",message);
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
				parser.parse(doc1);
				message=parser.getArray();
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
				parser.parse(doc);
				message=parser.getArray();
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
}


