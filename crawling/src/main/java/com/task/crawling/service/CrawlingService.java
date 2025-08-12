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
    private final String xUrl = "https://x.com/aespa_official";

    public void instagramFeedCrawling() throws Exception {
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


    public void instagramInfoCrawling() throws Exception {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(xUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // 선택: 쿠키/동의 팝업 닫기 시도 (없으면 무시)
            try {
                WebElement consent = new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.presenceOfElementLocated(
                                By.cssSelector("button[id*='accept'], button[aria-label*='Accept']")));
                consent.click();
            } catch (TimeoutException ignore) {}

            // 프로필 페이지의 OG description 대기
            WebElement og = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("head meta[property='og:description']"))
            );
            String desc = og.getAttribute("content"); // 예: "13.5M Followers, 16 Following, 1,234 Posts"
            //     "팔로워 1,234만명, 팔로잉 16명, 게시물 1,234개"
            String postsStr = "";
            String followersStr = "";
            String followingStr = "";

            if (desc != null) {
                // EN 패턴 (축약 K/M/B + 단어 thousand/million/billion 지원)
                java.util.regex.Matcher mFollowersEn = java.util.regex.Pattern
                        .compile("(\\d[\\d,\\.]*)\\s*(K|M|B|thousand|million|billion)?\\s*followers", java.util.regex.Pattern.CASE_INSENSITIVE)
                        .matcher(desc);
                java.util.regex.Matcher mFollowingEn = java.util.regex.Pattern
                        .compile("(\\d[\\d,\\.]*)\\s*(K|M|B|thousand|million|billion)?\\s*following", java.util.regex.Pattern.CASE_INSENSITIVE)
                        .matcher(desc);
                java.util.regex.Matcher mPostsEn = java.util.regex.Pattern
                        .compile("(\\d[\\d,\\.]*)\\s*(K|M|B|thousand|million|billion)?\\s*posts?", java.util.regex.Pattern.CASE_INSENSITIVE)
                        .matcher(desc);

                // KO 패턴 ("팔로워/팔로우/팔로잉" 모두 허용, 단위 억/백만/만/천 지원)
                java.util.regex.Matcher mFollowersKo = java.util.regex.Pattern
                        .compile("(?:팔로워|팔로우)\\s*(\\d[\\d,\\.]*)\\s*(억|백만|만|천)?(?:명)?")
                        .matcher(desc);
                java.util.regex.Matcher mFollowingKo = java.util.regex.Pattern
                        .compile("(?:팔로잉|팔로우)\\s*(\\d[\\d,\\.]*)\\s*(억|백만|만|천)?(?:명)?")
                        .matcher(desc);
                java.util.regex.Matcher mPostsKo = java.util.regex.Pattern
                        .compile("게시물\\s*(\\d[\\d,\\.]*)\\s*(억|백만|만|천)?(?:개)?")
                        .matcher(desc);

                if (mFollowersEn.find()) followersStr = mFollowersEn.group(1) + " " + nullToEmpty(mFollowersEn.group(2));
                else if (mFollowersKo.find()) followersStr = mFollowersKo.group(1) + " " + nullToEmpty(mFollowersKo.group(2));

                if (mFollowingEn.find()) followingStr = mFollowingEn.group(1) + " " + nullToEmpty(mFollowingEn.group(2));
                else if (mFollowingKo.find()) followingStr = mFollowingKo.group(1) + " " + nullToEmpty(mFollowingKo.group(2));

                if (mPostsEn.find()) postsStr = mPostsEn.group(1) + " " + nullToEmpty(mPostsEn.group(2));
                else if (mPostsKo.find()) postsStr = mPostsKo.group(1) + " " + nullToEmpty(mPostsKo.group(2));
            }

            long followers = parseCount(followersStr);
            long following = parseCount(followingStr);
            long posts = parseCount(postsStr);

            // ---- Fallback: DOM에서 직접 재추출 (og가 축약/지연된 경우 보정) ----
            try {
                WebElement header = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("header")));
                // 헤더가 다 렌더링될 때까지 (통계 li가 최소 3개)
                new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(d -> header.findElements(By.cssSelector("ul li")).size() >= 3);

                // 1) 팔로워: followers 링크에서 정확값 읽기
                try {
                    WebElement aFollowers = header.findElement(By.cssSelector("a[href$='/followers/'], a[href*='followers']"));
                    long v = extractPreciseCount(aFollowers);
                    if (v > 0) followers = v;
                } catch (NoSuchElementException ignore) {}

                // 2) 팔로잉: following 링크에서 정확값 읽기
                try {
                    WebElement aFollowing = header.findElement(By.cssSelector("a[href$='/following/'], a[href*='following']"));
                    long v = extractPreciseCount(aFollowing);
                    if (v > 0) following = v;
                } catch (NoSuchElementException ignore) {}

                // 3) 게시물: posts li에서 읽기 (링크가 아닐 수 있음)
                try {
                    // posts는 보통 첫 번째 li 또는 aria-label/텍스트로 표시
                    List<WebElement> lis = header.findElements(By.cssSelector("ul li"));
                    for (WebElement li : lis) {
                        String t = li.getText().toLowerCase();
                        if (t.contains("post") || t.contains("게시물")) {
                            long v = extractPreciseCount(li);
                            if (v > 0) { posts = v; break; }
                        }
                    }
                } catch (Exception ignore) {}
            } catch (Exception e) {
                log.warn("Fallback DOM parse failed", e);
            }

            log.info("Instagram profile info => posts={}, followers={}, following={}", posts, followers, following);

            // TODO DTO 리턴 or DB 저장
            // return new InstagramProfileInfo(posts, followers, following);

        } finally {
            driver.quit();
        }
    }

    // 유틸
    private String nullToEmpty(String s) { return s == null ? "" : s; }

    /**
     * "16.08 million", "1.2M", "1,608만", "8천" 등 다양한 표기를 정수로 변환
     */
    private long parseCount(String raw) {
        if (raw == null) return 0L;
        String s = raw.trim()
                .replace("명", "")
                .replace("개", "")
                .replace(",", "")
                .toLowerCase(); // million/thousand/billion, k/m/b 대비

        try {
            // 영문 단어 단위
            if (s.endsWith(" billion")) {
                double v = Double.parseDouble(s.replace(" billion", "").trim());
                return Math.round(v * 1_000_000_000L);
            }
            if (s.endsWith(" million")) {
                double v = Double.parseDouble(s.replace(" million", "").trim());
                return Math.round(v * 1_000_000L);
            }
            if (s.endsWith(" thousand")) {
                double v = Double.parseDouble(s.replace(" thousand", "").trim());
                return Math.round(v * 1_000L);
            }

            // 영문 축약
            if (s.endsWith("b")) {
                double v = Double.parseDouble(s.substring(0, s.length() - 1).trim());
                return Math.round(v * 1_000_000_000L);
            }
            if (s.endsWith("m")) {
                double v = Double.parseDouble(s.substring(0, s.length() - 1).trim());
                return Math.round(v * 1_000_000L);
            }
            if (s.endsWith("k")) {
                double v = Double.parseDouble(s.substring(0, s.length() - 1).trim());
                return Math.round(v * 1_000L);
            }

            // 한글 단위
            if (s.endsWith("억")) {
                double v = Double.parseDouble(s.substring(0, s.length() - 1).trim());
                return Math.round(v * 100_000_000L);
            }
            if (s.endsWith("백만")) {
                double v = Double.parseDouble(s.replace("백만", "").trim());
                return Math.round(v * 1_000_000L);
            }
            if (s.endsWith("만")) {
                double v = Double.parseDouble(s.substring(0, s.length() - 1).trim());
                return Math.round(v * 10_000L);
            }
            if (s.endsWith("천")) {
                double v = Double.parseDouble(s.substring(0, s.length() - 1).trim());
                return Math.round(v * 1_000L);
            }

            // 기본 숫자 또는 소수
            if (s.contains(".")) {
                double v = Double.parseDouble(s);
                return Math.round(v);
            }
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse count from '{}'", raw);
            return 0L;
        }
    }

    /**
     * 텍스트 안에서 첫 번째 수치(영/한 단위 포함)를 찾아 parseCount로 환산
     */
    private long extractCountFromText(String text) {
        if (text == null) return 0L;
        String t = text.trim();
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("(\\d[\\d,\\.]*)\\s*(K|M|B|thousand|million|billion|억|백만|만|천)?", java.util.regex.Pattern.CASE_INSENSITIVE)
                .matcher(t);
        if (m.find()) {
            String num = m.group(1);
            String unit = m.group(2);
            return parseCount(num + (unit != null && !unit.isEmpty() ? " " + unit : ""));
        }
        return 0L;
    }

    /**
     * 요소 내부에서 가능한 가장 정확한 수치를 읽어온다.
     * 우선순위: <span title="정확숫자"> > aria-label > 텍스트 파싱
     */
    private long extractPreciseCount(WebElement el) {
        // 1) <span title="..."> 가 있으면 title로 전체 숫자 제공됨
        try {
            WebElement spanTitle = el.findElement(By.cssSelector("span[title]"));
            String title = spanTitle.getAttribute("title");
            if (title != null && !title.isEmpty()) {
                return parseCount(title);
            }
        } catch (NoSuchElementException ignored) {}

        // 2) aria-label 속성 시도 (예: "1,608만명 팔로워")
        try {
            String aria = el.getAttribute("aria-label");
            if (aria != null && !aria.isEmpty()) {
                long v = extractCountFromText(aria);
                if (v > 0) return v;
            }
        } catch (Exception ignored) {}

        // 3) 최후: 요소의 가시 텍스트에서 숫자 파싱
        try {
            String text = el.getText();
            if (text != null && !text.isEmpty()) {
                long v = extractCountFromText(text);
                if (v > 0) return v;
            }
        } catch (Exception ignored) {}

        return 0L;
    }
}
