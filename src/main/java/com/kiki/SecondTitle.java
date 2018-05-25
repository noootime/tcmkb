package com.kiki;

import java.util.Objects;

public class SecondTitle {
    private String parentTitle;
    private String title;
    private String url;

    public String getParentTitle() {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "SecondTitle{" +
                "parentTitle='" + parentTitle + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SecondTitle) {
            SecondTitle st = (SecondTitle) obj;
            return (Objects.equals((parentTitle + title + url), (st.getParentTitle() + st.getTitle() + st.getUrl())));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parentTitle + title + url);
    }
}
