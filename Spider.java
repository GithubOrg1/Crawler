import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Spider {
	public static ArrayList<File> waitList = new ArrayList<File>();

	public static void main(String[] args) {
		long startTimeStamp = System.currentTimeMillis();
		// multithreading
		try {
			multithreadingCrawlingUsingThreads();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// singlethreading
		// try {
		// singleThreadCrawling();
		// } catch (InterruptedException | IOException e) {
		// e.printStackTrace();
		// }
		System.out.println("Processing Time: " + Long.toString(System.currentTimeMillis() - startTimeStamp));
	}

	/**
	 * crawl through 2 threads
	 * 
	 * @throws InterruptedException
	 */
	public static void multithreadingCrawlingUsingThreads() throws InterruptedException {
		Buffer buffer = new Buffer(waitList);
		Thread t1 = new Thread(new Crawler(buffer));
		Thread t2 = new Thread(new Parser(buffer));
		t1.start();
		t2.start();
		t1.join();
		t2.join();
	}

	/**
	 * use thread pool to crawl
	 */
	public static void multithreadingCrawlingUsingPool() {
		ExecutorService pool = Executors.newCachedThreadPool();
		Buffer buffer = new Buffer(waitList);
		pool.execute(new Crawler(buffer));
		pool.execute(new Parser(buffer));
	}

	/**
	 * crawl through single thread
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void singleThreadCrawling() throws InterruptedException, IOException {
		Crawler.crawlAllAsynchronizedly();
		File dir = new File("pages");
		File[] pages = new File[100];
		if (dir.isDirectory()) {
			pages = dir.listFiles();
		} else {
			System.err.println("directory error!");
		}
		for (File page : pages) {
			try {
				Parser.getAllParams(page);
			} catch (IOException e) {
				System.err.println("parsing error!");
			}
		}
	}

}
