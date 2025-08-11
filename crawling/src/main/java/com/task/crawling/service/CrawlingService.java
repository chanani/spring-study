package com.task.crawling.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrawlingService {

    private final String instagramUrl = "https://www.instagram.com/aespa_official/";

    public void instagramCrawling() throws Exception {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(instagramUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // 선택: 쿠키/동의 팝업 닫기 시도 (없으면 무시)
            try {
                WebElement consent = new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.presenceOfElementLocated(
                                By.cssSelector("button[id*='accept'], button[aria-label*='Accept']")
                        ));
                consent.click();
            } catch (TimeoutException ignore) {}

            // 피드 컨테이너가 렌더될 때까지 대기 (Instagram main grid)
            By feedContainer = By.cssSelector("main article");
            By feedItems     = By.cssSelector("main article a[role='link'][href*='/p/'], main article a[role='link'][href*='/reel/']");
            wait.until(ExpectedConditions.presenceOfElementLocated(feedContainer));

            // 충분히 스크롤해서 lazy-load된 아이템 로드
            int prev = -1;
            for (int i = 0; i < 10; i++) {
                ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
                Thread.sleep(1200);
                int now = driver.findElements(feedItems).size();
                if (now == prev) break;
                prev = now;
            }

            List<WebElement> productElements = driver.findElements(feedItems);
            log.info("Found {} posts", productElements.size());
            for (WebElement el : productElements) {
                String href = el.getAttribute("href");
                String alt = "";
                String src = "";
                try {
                    WebElement img = el.findElement(By.cssSelector("img"));
                    alt = img.getAttribute("alt");
                    src = img.getAttribute("src");
                } catch (NoSuchElementException ignore) { /* some tiles may be videos without immediate <img> */ }
                log.info("post: href={}, alt={}, thumb={}", href, alt, src);
            }
        } finally {
            driver.quit();
        }
    }
}
