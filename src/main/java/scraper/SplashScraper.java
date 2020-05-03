package scraper;

import logger.LoggerUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import spider.SiteTask;
import splash.HtmlRendererRequestFactory;
import splash.SplashRequestContext;
import utils.Html;
import utils.Link;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SplashScraper implements Scraper {
    private final Set<Call> calls = ConcurrentHashMap.newKeySet();
    private final OkHttpClient httpClient;
    private final HtmlRendererRequestFactory rendererRequestFactory;

    public SplashScraper(OkHttpClient httpClient, HtmlRendererRequestFactory rendererRequestFactory) {
        this.httpClient = httpClient;
        this.rendererRequestFactory = rendererRequestFactory;
    }

    @Override
    public void scrape(Link link, Consumer<Html> consumer) {

    }

    @Override
    public int scrapingSitesCount() {
        return httpClient.dispatcher().runningCallsCount();
    }


    @Override
    public void cancelAll() {
        calls.forEach(Call::cancel);
    }
}