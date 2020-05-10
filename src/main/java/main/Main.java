package main;

import config.ConfigurationUtils;
import database.Database;
import logger.LoggerUtils;
import spider.DefaultContextFactory;
import spider.Spider;

public class Main {
    private static final String INPUT_PATH = "src/main/resources/websites_data_medium.csv";
    private static final String OUTPUT_PATH = "export.csv";

    // prevents class instantiation
    private Main() {}

    public static void main(String[] args) {
        ConfigurationUtils.configure();
        LoggerUtils.debugLog.info("Main - START");
        var spider = new Spider(new DefaultContextFactory(), Database.newInstance());
        spider.scrapeFromCSVFile(INPUT_PATH, OUTPUT_PATH);
        LoggerUtils.debugLog.info("Main - " + LoggerUtils.getPagesScraped() + " pages were scraped in total");
        LoggerUtils.consoleLog.info("Main - " + LoggerUtils.getPagesScraped() + " pages were scraped in total");
    }
}