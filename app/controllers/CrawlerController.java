package controllers;

import controllers.action.Secured;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import models.Crawler4j;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;

/**
 * Controller of the Crawlers. Here the parameters are set,
 * and the Crawlers started.
 *
 * @author Yasser Ganjisaffar
 */
public class CrawlerController extends Controller {

    @Security.Authenticated(Secured.class)
    public Result startCrawler4j() throws Exception {
        String crawlStorageFolder = "public/data/crawler";
        int numberOfCrawlers = 3;
        int maxPagesToFetch = 75;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxPagesToFetch(maxPagesToFetch);

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed("https://pt.wikipedia.org/wiki/Arte");
        controller.addSeed("https://pt.wikipedia.org/wiki/Computador");
        controller.addSeed("https://pt.wikipedia.org/wiki/Informatica");

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(Crawler4j.class, numberOfCrawlers);
        return ok(index.render(session("user")));
    }
}
