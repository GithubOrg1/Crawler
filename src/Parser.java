import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;


public class Parser {
	
	private String[] name=new  String[10];
	private String[] profile=new String[10];
	private String[] tel=new String[10];
	private String[] email=new String[10];
	private String[] direction=new String[10];
	private int n=0;
	private Object[] message = {tel,email,profile,name,direction};
	
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
		String profileStr = doc.getElementById("content").getElementsByClass("titles").text();
		
		if(n<10){
	    	name[n]=nameStr;
	    	profile[n]=profileStr;
	    	tel[n]=telStr;
	    	email[n]=emailStr;
	    	direction[n]=directionStr;
	    	n++;
		}
	}
	
	public Object[] getArray()
	{
		return message;
	}
}
