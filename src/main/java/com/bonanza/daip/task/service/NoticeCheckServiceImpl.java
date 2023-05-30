package com.bonanza.daip.task.service;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

//import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NoticeCheckServiceImpl implements NoticeCheckService {
	private final String BITHUMB_URL = "https://cafe.bithumb.com/view/boards/43";
	private final String UPBIT_URL = "https://upbit.com/service_center/notice";
	private final String COINONE_URL = "https://coinone.co.kr/info/notice";
	
	private WebDriver webDriver;
	
	ChromeDriverService chromeDriverService;
	
	File driverFile;
	String driverFilePath = "";
	WebDriverWait webDriverWait;
	
	/**
	 * 서버기동시 최초 한번 호출
	 */
	/*
	@PostConstruct
	private void init() {
		driverFile = new File("src/main/resources/chromedriver.exe");
		driverFilePath = driverFile.getAbsolutePath();
		
        if(!driverFile.exists() && driverFile.isFile()) {
            throw new RuntimeException("Not found file. or this is not file. <" + driverFilePath + ">");
        }
        
		ChromeOptions options = new ChromeOptions();
//		options.addArguments("--disable-popup-blocking");   // 팝업 안띄움
		options.addArguments("--disable-gpu");  			// gpu 비활성화
//		options.addArguments("--blink-settings=imagesEnabled=false");   // 이미지 다운 안받음
		options.addArguments("--remote-allow-origins=*");
		options.addArguments("--disable-default-apps");     // 기본앱 사용안함
		options.addArguments("--no-sandbox"); 
		options.addArguments("--lang=ko");
		options.addArguments("--disable-dev-shm-usage");
		options.setCapability("ignoreProtectedModeSettings", true);
		options.setHeadless(true);							// 브라우저 안띄움
		options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36");
		options.addArguments("--disable-blink-features=AutomationControlled");
		options.addArguments("--disable-plugins-discovery");
		//options.addArguments("--incognito");	//Privacy 모드
		options.addArguments("--profile-directory=Default");
		options.addArguments("--disable-extensions"); 
		options.addArguments("--start-maximized"); //전체스크린


 
		//url이 실행이 아래 코드보다 느리게 실행될 수도 있으니까 Thread 써주기
		chromeDriverService = new ChromeDriverService.Builder()
                .usingDriverExecutable(driverFile)
                .usingAnyFreePort()
                .build();
        try {
        	chromeDriverService.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        webDriver = new ChromeDriver(chromeDriverService, options);
		webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
	}
	*/
	
	/**
	 * 공지사항 스크롤링시작
	 */
	@Override
	public void upbitNotiProcess() {
	        
        try {
    		//실행된 드라이버로 주어진 url 접속시키기
    		webDriver.get(UPBIT_URL);
    		
    		webDriverWait.until(
                    ExpectedConditions.presenceOfElementLocated(By.className("tableB"))
           );
    		WebElement notiDiv = webDriver.findElement(By.className("tableB"));
    		List<WebElement> notiList = notiDiv.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
    		
    		for(WebElement td : notiList) {
    			log.debug("=================================================================");
    			String link = td.findElement(By.tagName("a")).getAttribute("href");
    			//상품 li에서 price라는 클래스를 가지고 있는 요소 안의 strong 태그 요소 찾아서 그 안에 적힌 텍스트 긁어오기
    			String title = td.findElement(By.tagName("a")).getText();
    			String regDt = td.findElement(By.xpath("td[2]")).getText();
    			String count = td.findElement(By.className("rAlign")).getText();
    			log.debug("link : "+link);
    			log.debug("regDt : "+regDt);
    			log.debug("count : "+count);
    			log.debug("title : "+title);
    			
    			//상세가기 버튼 클릭
    			/*
    			if(title.contains("아이오텍스")) {
    				td.findElement(By.tagName("a")).click();
    				Thread.sleep(500);
    				
    				WebElement productDiv = webDriver.findElement(By.id("markdown_notice_body"));
    				String content = productDiv.getAttribute("innerHTML");
    				log.debug("content : "+content);
    				break;
    				driver.findElement(By.className("ty02")).findElement(By.className("btnRight")).findElement(By.tagName("a")).click();
    				Thread.sleep(2000);
    			}
    			*/
    			log.debug("=================================================================");
    		}
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * 빗썸 공지사항 스크롤링시작
	 */
	@Override
	public void bithumbNotiProcess() {
	        
        try {
    		//실행된 드라이버로 주어진 url 접속시키기
    		webDriver.get(BITHUMB_URL);
    		
    		webDriverWait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("dataTables"))
           );
    		WebElement notiDiv = webDriver.findElement(By.id("dataTables"));
    		List<WebElement> notiList = notiDiv.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
    		
    		for(WebElement td : notiList) {
    			log.debug("=================================================================");
    			String link = td.findElement(By.tagName("a")).getAttribute("onclick");
    			//상품 li에서 price라는 클래스를 가지고 있는 요소 안의 strong 태그 요소 찾아서 그 안에 적힌 텍스트 긁어오기
    			String title = td.findElement(By.xpath("td[2]")).getText();
    			String regDt = td.findElement(By.xpath("td[3]")).getText();
    			log.debug("link : "+link);
    			log.debug("regDt : "+regDt);
    			log.debug("title : "+title);
    			
    			//상세가기 버튼 클릭
    			/*
    			if(title.contains("[입출금]")) {
    				td.findElement(By.tagName("a")).click();
    				Thread.sleep(500);
    				
    				WebElement productDiv = webDriver.findElement(By.className("board-content"));
    				String content = productDiv.getAttribute("innerHTML");
    				log.debug("content : "+content);
    				break;
    				driver.findElement(By.className("ty02")).findElement(By.className("btnRight")).findElement(By.tagName("a")).click();
    				Thread.sleep(2000);
    			}
    			*/ 
    			log.debug("=================================================================");
    		}
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	/**
	 * 코인원 공지사항 스크롤링시작
	 */
	@Override
	public void coninoneNotiProcess() {
		//process();
		
		try {
			//실행된 드라이버로 주어진 url 접속시키기
			webDriver.get(COINONE_URL);
			
			webDriverWait.until(
					ExpectedConditions.presenceOfElementLocated(By.className("ng-tns-c7-1"))
					);
			WebElement notiDiv = webDriver.findElement(By.className("ng-tns-c7-1"));
			List<WebElement> notiList = notiDiv.findElement(By.tagName("div")).findElements(By.className("ng-star-inserted"));
			
			for(WebElement div : notiList) {
				log.debug("=================================================================");
				String link = div.findElement(By.tagName("a")).getAttribute("href");
				//상품 li에서 price라는 클래스를 가지고 있는 요소 안의 strong 태그 요소 찾아서 그 안에 적힌 텍스트 긁어오기
				String title = div.findElement(By.className("title")).getText();
				String regDt = div.findElement(By.className("date")).getText();
				String notiTp = div.findElement(By.className("category")).getText();
				log.debug("link : "+link);
				log.debug("regDt : "+regDt);
				log.debug("title : "+title);
				
				//상세가기 버튼 클릭
				if(notiTp.contains("입출금")) {
					div.findElement(By.tagName("a")).click();
					Thread.sleep(500);
					
					WebElement productDiv = webDriver.findElement(By.className("board-content"));
					String content = productDiv.getAttribute("innerHTML");
					log.debug("content : "+content);
					break;
				}
				log.debug("=================================================================");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * JSOUP를 통한 스크래핑
	 */
	public void process() {
		Connection conn = Jsoup.connect(COINONE_URL);
        //Jsoup 커넥션 생성

        Document document = null;
        try {
            document = conn
            		.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36")
                    .get();
        } catch (IOException e) { 
            e.printStackTrace();
        }

        Elements selects = document.select(".ng-star-inserted");
        log.debug("selects : " + selects.html());
	}
}
