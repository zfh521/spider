package com.mxjn.spider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SpiderTask {
	private BlockingDeque<URL> dd=new LinkedBlockingDeque<URL>();
	private List<URL> allUrls=new ArrayList<URL>();
    private URL original;
	public SpiderTask(String baseUrl) throws MalformedURLException {
		super();
		this.original=new URL(baseUrl);
		allUrls.add(original);
		dd.push(original);
	}

	public void run(){
		
		Work t1 = new Work("t1");
		Work t2 = new Work("t2");
		Work t3 = new Work("t3");
		Work t4 = new Work("t4");
		Work t5 = new Work("t5");
		t1.start();
		t2.start();
		try {
			t1.join();
			t2.join();
			System.out.println("主线程结束");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	class Work extends Thread{
		private String threadName;
		public Work(String threadName){
			this.threadName=threadName;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				Thread.sleep(1000);
				System.out.println(this.threadName+ ":开始");
				while(dd.isEmpty()){

					Thread.sleep(500);
				}
				while(!dd.isEmpty()){
					URL url=dd.pop();
					System.out.println(this.getName() +":"+url);

					Document doc =null;
					try{
						doc=Jsoup.parse(url, 4000);
					}catch(Exception e){
						e.printStackTrace();
					}
					if(doc==null){
						continue;
					}
					Elements hrefElements = doc.select("[href]");
					Elements srcElements = doc.select("[src]");
					hrefElements.addAll(srcElements);
					Iterator<Element> itor=hrefElements.iterator();
					while (itor.hasNext()) {
						Element element = (Element) itor.next();
						try {
                           
							URL href=new URL(element.absUrl("href"));
							if(!(element.hasAttr("href")||element.hasAttr("src"))){
								continue;
							}else if(element.hasAttr("href")){
								href=new URL(element.absUrl("href"));
							}else{
								href=new URL(element.absUrl("src"));
							}
							if(href.getHost().equals(original.getHost())&&!allUrls.contains(href)){
								//System.out.println(href);
								allUrls.add(href);
								dd.push(href);
								//this.
								dd.notifyAll();
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}catch(Exception e){
				
			}
			super.run();
		}
	}
	
}
