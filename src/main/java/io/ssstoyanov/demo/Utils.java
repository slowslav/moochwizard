package io.ssstoyanov.demo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Utils {

    private static final String SESSION_ID = "sessionid";
    private static final String YT_DLP = "yt-dlp";
    private static final String SUMY = "sumy";

    @NotNull
    @Retryable(maxAttempts = 2, backoff = @Backoff(delay = 30000), retryFor = IOException.class)
    public static Document getPage(String url) throws IOException {
        return Jsoup.connect(url).followRedirects(true).ignoreHttpErrors(true).get();
    }

    @NotNull
    @Retryable(maxAttempts = 2, backoff = @Backoff(delay = 30000), retryFor = IOException.class)
    public static String getInstagramJson(String url, String sessionId) throws IOException {
        return Jsoup.connect(url).cookie(SESSION_ID, sessionId)
                .ignoreContentType(true)
                .ignoreHttpErrors(true).execute().body();
    }

    public static InputStream getInputStream(String url) {
        try {
            return new URL(url).openStream();
        } catch (IOException e) {
            log.error("An error occurred while getting image input stream", e);
        }
        return null;
    }

    /**
     * @param url tiktok or youtube url
     * @return direct video url
     */
    public static String getVideoUrl(String url) {
        String option = "--get-url";
        return getUrl(url, YT_DLP, option);
    }

    /**
     * @param url tiktok or youtube url
     * @return direct thumbnail url
     */
    public static String getThumbUrl(String url) {
        String option = "--get-thumbnail";
        return getUrl(url, YT_DLP, option);
    }

    /**
     * @param url tiktok or youtube url
     * @return direct video url
     */
    public static String getVideoUrlYT(String url) {
        String option = "-f";
        String format = "best[ext=mp4]/mp4";
        String getUrlOption = "--get-url";

        ProcessBuilder processBuilder = new ProcessBuilder(YT_DLP, option, format, getUrlOption, url);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            int exitCode = process.waitFor();
            String line = reader.lines().findAny().orElse("");
            if (exitCode != 0) {
                log.error("Error: yt-dlp exited with code " + exitCode);
            }
            return line;
        } catch (IOException | InterruptedException e) {
            log.error("Error: yt-dlp exited", e);
        }
        return "";
    }

    /**
     * @param url newsmaker or meduza url
     * @return text summary
     */
    public static String getSummary(String url) {
        String method = "text-rank";
        String option = "--length=6";

        ProcessBuilder processBuilder = new ProcessBuilder(SUMY, method, option, "--url=" + url);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            int exitCode = process.waitFor();
            String line = reader.lines().findAny().orElse("");
            if (exitCode != 0) {
                log.error("Error: sumy exited with code " + exitCode);
            }
            return line;
        } catch (IOException | InterruptedException e) {
            log.error("Error: sumy exited", e);
        }
        return "";
    }

    @NotNull
    private static String getUrl(String url, String command, String option) {
        ProcessBuilder processBuilder = new ProcessBuilder(command, option, url);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Wait for the process to finish and check for errors
            int exitCode = process.waitFor();
            String line = reader.lines().collect(Collectors.joining());
            if (exitCode != 0) {
                log.error("Error: yt-dlp exited with code " + exitCode);
            }
            return line;
        } catch (IOException | InterruptedException e) {
            log.error("Error: yt-dlp exited", e);
        }
        return "";
    }

}
