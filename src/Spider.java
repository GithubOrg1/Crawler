import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Spider {
	
	 private static final int MAX_PAGES_TO_SEARCH = 9;
	  private static List<String> links = new LinkedList<String>();
	  //private static List<String> linksMatched = new LinkedList<String>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Spider spider = new Spider();
		spider.runSingleThread();
		spider.runMultiThread();
	}
	
	public void runSingleThread()
	{
		long startTime = System.currentTimeMillis();
		for(int i=0;i<MAX_PAGES_TO_SEARCH;i++)
		{
			try
			{
				String url = "http://cs.whu.edu.cn/plus/view.php?aid=162"+i;
				Document doc = Jsoup.connect(url).get();
				String string = doc.body().getAllElements().get(0).html().replaceAll("&nbsp;", "");
				doc = Jsoup.parse(string);
			}
			catch(IOException e)
	        {
	            // We were not successful in our HTTP request
				e.printStackTrace();
	        }
		}
		long endTime = System.currentTimeMillis();
		System.out.println("单线程爬取耗时："+(endTime-startTime)+"ms");

	}
	
	public void runMultiThread()
	{
		long startTime = System.currentTimeMillis();
		for(int i=0;i<MAX_PAGES_TO_SEARCH;i++)
		{
			String url = "http://cs.whu.edu.cn/plus/view.php?aid=162"+i;
			readUrlThread t = new readUrlThread(url);
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
		long endTime = System.currentTimeMillis();
		System.out.println("多线程爬取耗时："+(endTime-startTime)+"ms");
	}
	
	class readUrlThread extends Thread
	{
		public readUrlThread(String url)
		{
			Document doc;
			try {
				doc = Jsoup.connect(url).get();
				String string = doc.body().getAllElements().get(0).html().replaceAll("&nbsp;", "");
				doc = Jsoup.parse(string);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public void getLinks()
	{
		String url = "http://cs.whu.edu.cn/plus/list.php?tid=36";
		try
        {
			Document doc = Jsoup.connect(url).get();
           
            Elements linksOnPage = doc.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");
            for(Element link : linksOnPage)
            {
            	Pattern pattern = Pattern.compile(".*?aid=162+[0-9]{1}.*?");
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
