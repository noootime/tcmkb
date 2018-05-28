package com.kiki.entity.title;

import java.util.Objects;

public abstract class AbstractTitle {
    protected String parentTitle;
    protected String title;
    protected String url;

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
    public boolean equals(Object obj) {
        if (obj instanceof AbstractTitle) {
            AbstractTitle t = (AbstractTitle) obj;
            return Objects.equals((parentTitle + title + url), (t.getParentTitle() + t.getTitle() + t.getUrl()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parentTitle + title + url);
    }
}
