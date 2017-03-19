package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Web Page Class to be persisted on the database.
 * This Class are used by the Crawlers after they reach
 * some page.
 */
@Entity
public class WebPage {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    public String text;

    public String html;

    public WebPage(String text, String html) {
        this.text = text;
        this.html = html;
    }
}
