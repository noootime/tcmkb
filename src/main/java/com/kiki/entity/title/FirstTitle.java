package com.kiki.entity.title;

import java.util.Objects;

public class FirstTitle extends AbstractTitle {

    @Override
    public String toString() {
        return "FirstTitle{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", parentTitle='" + parentTitle + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FirstTitle) {
            FirstTitle ft = (FirstTitle) obj;
            return Objects.equals((title + url + parentTitle), (ft.getTitle() + ft.getUrl() + ft.getParentTitle()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(title + url + parentTitle);
    }
}
