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
    private final String tiktokUrl = "https://www.tiktok.com/@aespa_official";


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
            } catch (TimeoutException ignore) {
            }

            // 피드 컨테이너가 렌더될 때까지 대기 (Instagram main grid)
            By feedContainer = By.cssSelector("main article");
            By feedItems = By.cssSelector("main article a[role='link'][href*='/p/'], main article a[role='link'][href*='/reel/']");
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
            } catch (TimeoutException ignore) {
            }

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

                if (mFollowersEn.find())
                    followersStr = mFollowersEn.group(1) + " " + nullToEmpty(mFollowersEn.group(2));
                else if (mFollowersKo.find())
                    followersStr = mFollowersKo.group(1) + " " + nullToEmpty(mFollowersKo.group(2));

                if (mFollowingEn.find())
                    followingStr = mFollowingEn.group(1) + " " + nullToEmpty(mFollowingEn.group(2));
                else if (mFollowingKo.find())
                    followingStr = mFollowingKo.group(1) + " " + nullToEmpty(mFollowingKo.group(2));

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
                } catch (NoSuchElementException ignore) {
                }

                // 2) 팔로잉: following 링크에서 정확값 읽기
                try {
                    WebElement aFollowing = header.findElement(By.cssSelector("a[href$='/following/'], a[href*='following']"));
                    long v = extractPreciseCount(aFollowing);
                    if (v > 0) following = v;
                } catch (NoSuchElementException ignore) {
                }

                // 3) 게시물: posts li에서 읽기 (링크가 아닐 수 있음)
                try {
                    // posts는 보통 첫 번째 li 또는 aria-label/텍스트로 표시
                    List<WebElement> lis = header.findElements(By.cssSelector("ul li"));
                    for (WebElement li : lis) {
                        String t = li.getText().toLowerCase();
                        if (t.contains("post") || t.contains("게시물")) {
                            long v = extractPreciseCount(li);
                            if (v > 0) {
                                posts = v;
                                break;
                            }
                        }
                    }
                } catch (Exception ignore) {
                }
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

    public void xInfoCrawling() throws Exception {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(xUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // (선택) 팝업 닫기 시도
            try {
                WebElement dialogClose = new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.presenceOfElementLocated(
                                By.cssSelector("div[role='dialog'] [aria-label*='Close'], button[aria-label*='Close'], button[id*='accept'], button[aria-label*='Accept']")));
                dialogClose.click();
            } catch (TimeoutException ignore) {
            }

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("main")));
            // 렌더 지연 보정: 약간 스크롤하며 렌더 유도
            try {
                for (int i = 0; i < 3; i++) {
                    ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, arguments[0]);", 200 * (i + 1));
                    Thread.sleep(300);
                }
            } catch (InterruptedException ignored) {
            }

            long followers = 0L, following = 0L;


            // ---- 팔로워 ----
            WebElement followersLink = findFirstPresent(driver,
                    // CSS
                    "a[href$='/followers']",
                    "a[href*='/followers?']",
                    "a[role='link'][href*='/followers']",
                    "a[aria-label*='Followers']",
                    "[role='link'][aria-label*='Followers']",
                    "a[aria-label*='팔로워']",
                    "[role='link'][aria-label*='팔로워']",
                    // XPath
                    "//a[contains(@href,'/followers')]",
                    "//*[@role='link' and contains(@href,'/followers')]",
                    "//*[@role='link' and contains(translate(@aria-label,'FOLWERS','folwers'),'followers')]",
                    "//*[@role='link' and contains(@aria-label,'팔로워')]",
                    "//a[.//span[contains(translate(normalize-space(string(.)),'F','f'),'followers')]]",
                    "//*[contains(normalize-space(.), '팔로워')]/ancestor::a[1]"
            );
            if (followersLink == null) {
                // JS로 innerText/aria-label 매칭 폴백
                followersLink = queryByInnerTextJs(driver, "followers", "팔로워");
            }
            if (followersLink != null) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", followersLink);
                long v = extractPreciseCount(followersLink);
                if (v == 0) {
                    String inner = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerText;", followersLink);
                    if (inner != null && !inner.isEmpty()) v = extractCountFromText(inner);
                }
                if (v == 0) {
                    String aria = followersLink.getAttribute("aria-label");
                    if (aria != null && !aria.isEmpty()) v = extractCountFromText(aria);
                }
                followers = v;
            } else {
                log.warn("Followers link not found on X page");
            }

            // ---- 팔로잉 ----
            WebElement followingLink = findFirstPresent(driver,
                    "a[href$='/following']",
                    "a[href*='/following?']",
                    "a[role='link'][href*='/following']",
                    "//a[contains(@href,'/following')]"
            );
            if (followingLink != null) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", followingLink);
                long v = extractPreciseCount(followingLink);
                if (v == 0) {
                    String inner = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerText;", followingLink);
                    if (inner != null && !inner.isEmpty()) v = extractCountFromText(inner);
                }
                if (v == 0) {
                    String aria = followingLink.getAttribute("aria-label");
                    if (aria != null && !aria.isEmpty()) v = extractCountFromText(aria);
                }
                following = v;
            } else {
                log.warn("Following link not found on X page");
            }

            // ---- 메타 백업(있을 때만) ----
            if (followers == 0 || following == 0) {
                try {
                    WebElement metaDesc = driver.findElement(By.cssSelector("head meta[name='description']"));
                    String desc = metaDesc.getAttribute("content"); // “… 4.5M Followers · 2 Following …” 등
                    if (desc != null) {
                        desc = desc.replace("·", " ");
                        if (followers == 0) {
                            java.util.regex.Matcher mfEn = java.util.regex.Pattern
                                    .compile("(\\d[\\d,\\.]*)\\s*(K|M|B|thousand|million|billion)?\\s*Followers", java.util.regex.Pattern.CASE_INSENSITIVE)
                                    .matcher(desc);
                            java.util.regex.Matcher mfKo = java.util.regex.Pattern
                                    .compile("팔로워\\s*(\\d[\\d,\\.]*)\\s*(억|백만|만|천)?")
                                    .matcher(desc);
                            if (mfEn.find())
                                followers = parseCount(mfEn.group(1) + " " + (mfEn.group(2) == null ? "" : mfEn.group(2)));
                            else if (mfKo.find())
                                followers = parseCount(mfKo.group(1) + " " + (mfKo.group(2) == null ? "" : mfKo.group(2)));
                        }
                        if (following == 0) {
                            java.util.regex.Matcher mgEn = java.util.regex.Pattern
                                    .compile("(\\d[\\d,\\.]*)\\s*(K|M|B|thousand|million|billion)?\\s*Following", java.util.regex.Pattern.CASE_INSENSITIVE)
                                    .matcher(desc);
                            java.util.regex.Matcher mgKo = java.util.regex.Pattern
                                    .compile("(?:팔로우|팔로잉)\\s*(\\d[\\d,\\.]*)\\s*(억|백만|만|천)?")
                                    .matcher(desc);
                            if (mgEn.find())
                                following = parseCount(mgEn.group(1) + " " + (mgEn.group(2) == null ? "" : mgEn.group(2)));
                            else if (mgKo.find())
                                following = parseCount(mgKo.group(1) + " " + (mgKo.group(2) == null ? "" : mgKo.group(2)));
                        }
                    }
                } catch (Exception ignore) {
                }
            }

            log.info("X profile info => followers={}, following={}", followers, following);
        } finally {
            driver.quit();
        }
    }

    public void tiktokInfo() throws Exception {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(tiktokUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // (선택) 쿠키/지역 동의 닫기
            try {
                WebElement consent = new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.presenceOfElementLocated(
                                By.cssSelector(
                                        "button[data-e2e='cookie-banner-accept-button'], " +
                                                "button[id*='accept'], button[aria-label*='Accept'], " +
                                                "div[role='dialog'] button[aria-label*='Close']"
                                )));
                consent.click();
            } catch (TimeoutException ignore) {
            }

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("main, div[data-e2e='user-page']")));

            // 렌더 지연 보정: 살짝 스크롤
            try {
                for (int i = 0; i < 3; i++) {
                    ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, arguments[0]);", 200 * (i + 1));
                    Thread.sleep(250);
                }
            } catch (InterruptedException ignored) {
            }

            long followers = 0L;
            long following = 0L;
            long likes = 0L;

            // 1) data-e2e 안정 셀렉터
            try {
                WebElement followersEl = findFirstPresent(driver,
                        "[data-e2e='followers-count']",
                        "strong[data-e2e='followers-count']",
                        "div[data-e2e='user-stats'] [data-e2e='followers-count']"
                );
                if (followersEl != null) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", followersEl);
                    long v = extractPreciseCount(followersEl);
                    if (v == 0) {
                        String inner = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerText;", followersEl);
                        if (inner != null && !inner.isEmpty()) v = extractCountFromText(inner);
                    }
                    followers = v;
                }

                WebElement followingEl = findFirstPresent(driver,
                        "[data-e2e='following-count']",
                        "strong[data-e2e='following-count']",
                        "div[data-e2e='user-stats'] [data-e2e='following-count']"
                );
                if (followingEl != null) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", followingEl);
                    long v = extractPreciseCount(followingEl);
                    if (v == 0) {
                        String inner = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerText;", followingEl);
                        if (inner != null && !inner.isEmpty()) v = extractCountFromText(inner);
                    }
                    // 3000 이상이면 잘못된 값으로 보고 버림
                    if (v > 0 && v < 3000) {
                        following = v;
                    }
                }

                // Fallback - JS 검색 시도
                if (following == 0) {
                    WebElement el = queryByInnerTextJs(driver, "following", "팔로잉", "팔로우");
                    if (el != null) {
                        long v = extractPreciseCount(el);
                        if (v == 0) {
                            String inner = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerText;", el);
                            if (inner != null && !inner.isEmpty()) v = extractCountFromText(inner);
                        }
                        // 여기도 숫자 검증
                        if (v > 0 && v < 3000) {
                            following = v;
                        }
                    }
                }

                WebElement likesEl = findFirstPresent(driver,
                        "[data-e2e='likes-count']",
                        "strong[data-e2e='likes-count']",
                        "div[data-e2e='user-stats'] [data-e2e='likes-count']"
                );
                if (likesEl != null) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", likesEl);
                    long v = extractPreciseCount(likesEl);
                    if (v == 0) {
                        String inner = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerText;", likesEl);
                        if (inner != null && !inner.isEmpty()) v = extractCountFromText(inner);
                    }
                    likes = v;
                }
            } catch (Exception e) {
                log.warn("TikTok primary selectors failed", e);
            }

            // 2) 메타 description 파싱 (예: "X Followers, Y Following, Z Likes")
            if (followers == 0 || following == 0 || likes == 0) {
                try {
                    WebElement meta = driver.findElement(By.cssSelector("head meta[name='description']"));
                    String desc = meta.getAttribute("content");
                    if (desc != null) {
                        // Followers
                        if (followers == 0) {
                            java.util.regex.Matcher mfEn = java.util.regex.Pattern
                                    .compile("(\\d[\\d,\\.]*)\\s*(K|M|B|thousand|million|billion)?\\s*Followers", java.util.regex.Pattern.CASE_INSENSITIVE)
                                    .matcher(desc);
                            java.util.regex.Matcher mfKo = java.util.regex.Pattern
                                    .compile("팔로워\\s*(\\d[\\d,\\.]*)\\s*(억|백만|만|천)?")
                                    .matcher(desc);
                            if (mfEn.find())
                                followers = parseCount(mfEn.group(1) + " " + (mfEn.group(2) == null ? "" : mfEn.group(2)));
                            else if (mfKo.find())
                                followers = parseCount(mfKo.group(1) + " " + (mfKo.group(2) == null ? "" : mfKo.group(2)));
                        }
                        // Following
                        if (following == 0) {
                            java.util.regex.Matcher mgEn = java.util.regex.Pattern
                                    .compile("(\\d[\\d,\\.]*)\\s*(K|M|B|thousand|million|billion)?\\s*Following", java.util.regex.Pattern.CASE_INSENSITIVE)
                                    .matcher(desc);
                            java.util.regex.Matcher mgKo = java.util.regex.Pattern
                                    .compile("(?:팔로잉|팔로우)\\s*(\\d[\\d,\\.]*)\\s*(억|백만|만|천)?")
                                    .matcher(desc);
                            if (mgEn.find())
                                following = parseCount(mgEn.group(1) + " " + (mgEn.group(2) == null ? "" : mgEn.group(2)));
                            else if (mgKo.find())
                                following = parseCount(mgKo.group(1) + " " + (mgKo.group(2) == null ? "" : mgKo.group(2)));
                        }
                        // Likes
                        if (likes == 0) {
                            java.util.regex.Matcher mlEn = java.util.regex.Pattern
                                    .compile("(\\d[\\d,\\.]*)\\s*(K|M|B|thousand|million|billion)?\\s*Likes", java.util.regex.Pattern.CASE_INSENSITIVE)
                                    .matcher(desc);
                            java.util.regex.Matcher mlKo = java.util.regex.Pattern
                                    .compile("좋아요\\s*(\\d[\\d,\\.]*)\\s*(억|백만|만|천)?")
                                    .matcher(desc);
                            if (mlEn.find())
                                likes = parseCount(mlEn.group(1) + " " + (mlEn.group(2) == null ? "" : mlEn.group(2)));
                            else if (mlKo.find())
                                likes = parseCount(mlKo.group(1) + " " + (mlKo.group(2) == null ? "" : mlKo.group(2)));
                        }
                    }
                } catch (Exception ignore) {
                }
            }

            // 3) JS 텍스트 탐색 폴백
            if (followers == 0) {
                WebElement el = queryByInnerTextJs(driver, "followers", "팔로워");
                if (el != null) {
                    long v = extractPreciseCount(el);
                    if (v == 0) {
                        String inner = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerText;", el);
                        if (inner != null && !inner.isEmpty()) v = extractCountFromText(inner);
                    }
                    if (v > 0) followers = v;
                }
            }
            if (following == 0) {
                WebElement el = queryByInnerTextJs(driver, "following", "팔로잉", "팔로우");
                if (el != null) {
                    long v = extractPreciseCount(el);
                    if (v == 0) {
                        String inner = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerText;", el);
                        if (inner != null && !inner.isEmpty()) v = extractCountFromText(inner);
                    }
                    if (v > 0) following = v;
                }
            }
            if (likes == 0) {
                WebElement el = queryByInnerTextJs(driver, "likes", "좋아요");
                if (el != null) {
                    long v = extractPreciseCount(el);
                    if (v == 0) {
                        String inner = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerText;", el);
                        if (inner != null && !inner.isEmpty()) v = extractCountFromText(inner);
                    }
                    if (v > 0) likes = v;
                }
            }

            log.info("TikTok profile info => followers={}, following={}, likes={}", followers, following, likes);

        } finally {
            driver.quit();
        }
    }

    // 유틸
    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

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
        } catch (NoSuchElementException ignored) {
        }

        // 2) aria-label 속성 시도 (예: "1,608만명 팔로워")
        try {
            String aria = el.getAttribute("aria-label");
            if (aria != null && !aria.isEmpty()) {
                long v = extractCountFromText(aria);
                if (v > 0) return v;
            }
        } catch (Exception ignored) {
        }

        // 3) 최후: 요소의 가시 텍스트에서 숫자 파싱
        try {
            String text = el.getText();
            if (text != null && !text.isEmpty()) {
                long v = extractCountFromText(text);
                if (v > 0) return v;
            }
        } catch (Exception ignored) {
        }

        return 0L;
    }

    /**
     * 여러 CSS/XPath 셀렉터를 순서대로 시도하여 가장 먼저 발견되는 요소를 반환
     * 메서드는 클래스 레벨에 있어야 하며, 메서드 안에 중첩 정의하면 컴파일 에러가 납니다.
     */
    private WebElement findFirstPresent(WebDriver driver, String... selectors) {
        for (String sel : selectors) {
            try {
                List<WebElement> found;
                if (sel.startsWith("//")) { // XPath
                    found = driver.findElements(By.xpath(sel));
                } else { // CSS
                    found = driver.findElements(By.cssSelector(sel));
                }
                if (found != null && !found.isEmpty()) {
                    return found.get(0);
                }
            } catch (Exception ignore) {
            }
        }
        return null;
    }

    /**
     * JS로 여러 후보 노드를 훑으면서 innerText/aria-label에 키워드가 포함된 첫 요소 반환 (대소문자 무시)
     */
    private WebElement queryByInnerTextJs(WebDriver driver, String... needles) {
        try {
            String script =
                    "const needles = Array.from(arguments).map(s => String(s).toLowerCase());" +
                            "const els = Array.from(document.querySelectorAll('a,[role=\"link\"],div,span'));" +
                            "for (const el of els) {" +
                            "  const t = (el.innerText||'').toLowerCase();" +
                            "  const a = (el.getAttribute('aria-label')||'').toLowerCase();" +
                            "  if (needles.some(n => t.includes(n) || a.includes(n))) return el;" +
                            "}" +
                            "return null;";
            Object ret = ((JavascriptExecutor) driver).executeScript(script, (Object[]) needles);
            if (ret instanceof WebElement) {
                return (WebElement) ret;
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
