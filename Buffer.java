import java.io.File;
import java.util.ArrayList;


public class Buffer {
	private ArrayList<File> waitList = new ArrayList<File>();
	private boolean signal = false;
	private int numOfLinks = 0;

	public int getNumOfLinks() {
		return numOfLinks;
	}
	public void setNumOfLinks(int numOfLinks) {
		this.numOfLinks = numOfLinks;
	}
	public boolean isEmpty(){
		return waitList.isEmpty();
	}
	public Buffer(ArrayList<File> waitList) {
		super();
		this.waitList = waitList;
	}
	public synchronized void addToWaitList(File page) throws InterruptedException{
		while(signal){
			System.err.println("buffer is full now, crawler is waiting...");
			wait();
		}
		waitList.add(page);
		signal = true;
		notifyAll();
		System.err.println("Crawler has put an url into waitList");
	}

	public synchronized File getAUrl() throws InterruptedException {
		while(!signal){
			System.err.println("Crawling now, cannot parse...");
			wait();
		}
		File url = waitList.get(0);
		System.out.println(url+" got from wait list");
		waitList.remove(0);
		signal = false;
		notifyAll();
		return url;
	}
}
