package models;

import com.google.inject.Inject;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Crawler Class. This class decides which URLs
 * should be crawled and handles the downloaded page.
 *
 * @author Yasser Ganjisaffar
 * @version 4.1
 */
public class Crawler4j extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp3|zip|gz))$");

    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "https://pt.wikipedia.org/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches()
                && href.startsWith("https://pt.wikipedia.org/");
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Transactional
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            WebPage webPage = new WebPage(text, html);
            System.out.println("Text length: " + webPage.text.length());
            System.out.println("Html length: " + webPage.html.length());

            //TODO: Save pages here with the Object WebPage
        }
    }
}