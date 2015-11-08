import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.nodes.Document;


public interface IParser {
	public Document readHTML(File file) throws IOException;
	public ArrayList<String> findHref(Document doc);
//	public Map<String, String> getContactInfo(Document doc);
//	public String getEducationBackground(Document doc);
//	public String getResearchInterests(Document doc);
	public ProfessorInfo getAllParams(File page) throws IOException;
	public void insertAllIntoDatabase(ArrayList<String> param);

}
