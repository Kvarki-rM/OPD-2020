package spider;

import logger.LoggerUtils;
import scraper.Scraper;
import utils.Link;

import java.util.Set;
import java.util.concurrent.*;

public class DomainTask {
    private final Context context;
    private final Link domain;
    private final Scraper scraper;
    private final BlockingQueue<Link> linkQueue = new LinkedBlockingDeque<>();
    private final Set<String> resultWords;

    DomainTask(Link domain, Context context, Scraper scraper, Set<String> resultWords) {
        this.domain = domain;
        this.context = context;
        this.scraper = scraper;
        this.resultWords = resultWords;
    }

    void scrapeDomain() {
        LoggerUtils.debugLog.info("Domain Task - Start executing site " + domain);
        try {
            linkQueue.add(domain);
            while (areAllLinksScraped()) {
                checkIfInterrupted();
                scrapeNextLink();
            }
        } catch (InterruptedException e) {
            handleInterruption();
            LoggerUtils.debugLog.error("Domain Task - Interrupted " + domain);
        } finally {
            LoggerUtils.debugLog.info("Domain Task - Stop executing site " + domain);
        }
    }

    // order is important
    private boolean areAllLinksScraped() {
        return scraper.scrapingSitesCount() != 0 || !linkQueue.isEmpty();
    }

    private void checkIfInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }

    private void scrapeNextLink() throws InterruptedException {
        var link = linkQueue.poll(500, TimeUnit.MILLISECONDS);
        if (link != null) {
            scraper.scrape(link, new SiteTask(context, linkQueue, resultWords)::consumeHtml);
        }
    }

    private void handleInterruption() {
        scraper.cancelAll();
    }
}
