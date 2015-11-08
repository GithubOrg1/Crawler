import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser implements Runnable {
	private Buffer buffer;

	public Parser(Buffer b) {
		this.buffer = b;
	}

	/**
	 * this method is to read HTML into the environment
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Document readHTML(File file) throws IOException {
		Document doc = Jsoup.parse(file, "GBK");
		return doc;
	}

	public static Document readHTML(String fileName) throws IOException {
		Document doc = Jsoup.parse(new File(fileName), "GBK");
		return doc;
	}

	public static String getName(Document doc) {
		String name = "";
		Elements headers = doc.getElementsByTag("h2");
		for (Element header : headers) {
			if (!header.ownText().equals("")) {
				name = header.ownText();
			}
		}
		return name;
	}

	/**
	 * this method is to find all the hyperlinks inside a web page
	 * 
	 * @param doc
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static ArrayList<String> findHref() throws IOException,
			InterruptedException {
		// Crawler.getSingleHTMLAsynchronizedly(
		// "http://www.wpi.edu/academics/cs/research-interests.html",
		// "linkpage.html");
		Document doc = readHTML("linkpage.html");
		Elements links = doc.getElementsByTag("a");
		ArrayList<String> linkList = new ArrayList<String>();
		for (Element link : links) {
			linkList.add(link.attr("href"));
		}
		return linkList;
	}

	/**
	 * this method is to get all the contact info from a single web page
	 * 
	 * @param doc
	 *            : web page(Document)
	 * @return key: email for the email info;
	 * @return key: phone for the phone info;
	 */
	private static HashMap<String, String> getContactInfo(Document doc) {
		Element contactInfo = doc.getElementById("contactinfo");
		String contactInfoStr = contactInfo.toString();
		Pattern pEmail = Pattern
				.compile("[\\w]+@[\\w]+\\.[a-zA-Z]+");
		Pattern pPhone = Pattern
				.compile("\\+((\\-)?[\\d]+)+");
		Matcher mEmail = pEmail.matcher(contactInfoStr);
		Matcher mPhone = pPhone.matcher(contactInfoStr);
		HashMap<String, String> contactInfoMap = new HashMap<String, String>();
		if (mEmail.find()) {
			contactInfoMap.put("email", mEmail.group(0));
		}
		if (mPhone.find()) {
			contactInfoMap.put("phone", mPhone.group(0));
		}
		return contactInfoMap;
	}

	/**
	 * this method is to find out the education background of a prof.
	 * 
	 * @param doc
	 * @return
	 */
	private static String getEducationBackground(Document doc) {
		Elements title = doc.getElementsMatchingOwnText("Education");
		Element educationTitle = title.get(0);
		Element educationBackground = educationTitle.nextElementSibling();
		Elements allEducationInfo = educationBackground.getAllElements();
		String educationInfoStr = "";
		for (Element educationInfo : allEducationInfo) {
			if (educationInfo.hasText()) {
				educationInfoStr += educationInfo.ownText() + " ";
			}
		}
		return educationInfoStr;
	}

	/**
	 * this method is to find out the research interests of a prof.
	 * 
	 * @param doc
	 * @return
	 */
	private static String getResearchInterests(Document doc) {
		Elements title = doc.getElementsMatchingOwnText("Research Interests");
		String researchInterestsStr = "";
		if (title.hasText()) {
			Element researchInterestsTitle = title.get(0);
			Element researchInterestsEle = researchInterestsTitle
					.nextElementSibling();
			Elements allResearchInterests = researchInterestsEle
					.getAllElements();
			for (Element researchInterest : allResearchInterests) {
				if (researchInterest.hasText()) {
					researchInterestsStr += researchInterest.ownText() + "\n";
				}
			}
		} else {

		}

		return researchInterestsStr;
	}

	/**
	 * insert all the info of a prof. into the database
	 * 
	 * @param page
	 * @return an Obj of class ProfessorInfo that contains all the info of a
	 *         prof.
	 * @throws IOException
	 */
	public static ProfessorInfo getAllParams(File page) throws IOException {
		Document doc = readHTML(page);
		Map<String, String> contactInfo = getContactInfo(doc);
		String educationBackground = getEducationBackground(doc);
		String researchInterests = getResearchInterests(doc);
		String name = getName(doc);
		String email = contactInfo.get("email");
		String phone = contactInfo.get("phone");
		Object[] params = { name, educationBackground, researchInterests,
				email, phone };
		ProfessorInfo pi = new ProfessorInfo(contactInfo, educationBackground,
				researchInterests);
		System.out.println("new page processed!");
		DbHelper dao = new DbHelper();
		dao.runUpdate(
				"insert into professor_info(name,educationBackground,researchInterests,email,phone) values(?,?,?,?,?)",
				params);
		return pi;
	}

	/**
	 * parse all the pages downloaded before
	 * 
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public ArrayList<ProfessorInfo> parseAll() throws IOException,
			InterruptedException {
		// File dir = new File("pages");
		// File[] pages = new File[100];

		ArrayList<ProfessorInfo> profInfo = new ArrayList<ProfessorInfo>();
		// if (dir.isDirectory()) {
		// pages = dir.listFiles();
		// } else {
		// System.err.println("directory error!");
		// }
		profInfo.add(getAllParams(buffer.getAUrl()));
		return profInfo;
	}

	public void run() {
		while (true) {
			try {
				parseAll();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (Crawler.endSig) {
				break;
			}
		}
	}
}
