import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler implements Runnable {

	/**
	 * 
	 * @param url
	 * @param fileName
	 */
	private Buffer buffer;
	public static boolean endSig = false;

	public Crawler(Buffer b) {
		this.buffer = b;
	}


	public static void getSingleHTMLAsynchronizedly(String url, String fileName)
			throws InterruptedException {
		// set headers
		HttpRequest response = HttpRequest.get(url);
		if (response.ok()) {
			System.out.println("OK");
			response.receive(new File(fileName));
		}
	}

	public void getSingleHTMLSynchronizedly(String url, String fileName)
			throws InterruptedException {
		HttpRequest response = HttpRequest.get(url);
		if (response.ok()) {
			System.out.println("OK");
			File f = new File(fileName);
			response.receive(new File(fileName));
			buffer.addToWaitList(f);
		}
	}
	public static void crawlAllAsynchronizedly() throws InterruptedException, IOException{
		ArrayList<String> linkList = Parser.findHref();
		ArrayList<String> validLinkList = new ArrayList<String>();
		for (String link : linkList) {
			Pattern p = Pattern
					.compile("(www\\.wpi\\.edu/academics/(facultydir)|(datascience)/)(.+)");
			Matcher m = p.matcher(link);
			if (m.find() && (!validLinkList.contains(link))) {//check the reduplicated url
				validLinkList.add(link);
			} else {
				System.out.println("not a professor's page");
			}
		}
		Iterator<String> validLinkListIter = validLinkList.iterator();
		while(validLinkListIter.hasNext()){
			String link = validLinkListIter.next();
			Pattern p = Pattern
					.compile("(www\\.wpi\\.edu/academics/(facultydir)|(datascience)/)(.+)");
			Matcher m = p.matcher(link);
			if (m.find()) {
				getSingleHTMLAsynchronizedly(link, "pages/" + m.group(4));
				validLinkListIter.remove();
			}
		}
	}

	/**
	 * download all the pages required
	 * 
	 * @param doc
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void crawlAll() throws InterruptedException, IOException {
		ArrayList<String> linkList = Parser.findHref();
		ArrayList<String> validLinkList = new ArrayList<String>();
		for (String link : linkList) {
			Pattern p = Pattern
					.compile("(www\\.wpi\\.edu/academics/(facultydir)|(datascience)/)(.+)");
			Matcher m = p.matcher(link);
			if (m.find() && (!validLinkList.contains(link))) {//check the reduplicated url
				validLinkList.add(link);
			} else {
				System.out.println("not a professor's page");
			}
		}
//		ArrayList<String> sigList = validLinkList;
		Iterator<String> validLinkListIter = validLinkList.iterator();
		while(validLinkListIter.hasNext()){
			String link = validLinkListIter.next();
			Pattern p = Pattern
					.compile("(www\\.wpi\\.edu/academics/(facultydir)|(datascience)/)(.+)");
			Matcher m = p.matcher(link);
			if (m.find()) {
				getSingleHTMLSynchronizedly(link, "pages/" + m.group(4));
				validLinkListIter.remove();
			}
		}
		endSig = true;
//		for (String link : validLinkList) {
//			
//
//		}

	}

	public void run() {
		try {
			crawlAll();
		} catch (InterruptedException | IOException e) {
			System.err.println("Crawling Error");
		}
	}

}
