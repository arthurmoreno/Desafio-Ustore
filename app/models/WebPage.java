package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
