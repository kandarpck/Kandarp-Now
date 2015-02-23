package com.kandarp.launcher.now.search;

/**
 * Created by Kandarp on 2/23/2015.
 */
public class SearchDataRow {
    private String title;
    private String content;
    private String url;

    public SearchDataRow(String title, String content, String url) {

        this.title = title;
        this.content = content;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "SearchDataRow{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

