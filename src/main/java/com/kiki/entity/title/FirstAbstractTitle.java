package com.kiki.entity.title;

import java.util.Objects;

public class FirstAbstractTitle extends AbstractTitle {

    @Override
    public String toString() {
        return "FirstAbstractTitle{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", parentTitle='" + parentTitle + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FirstAbstractTitle) {
            FirstAbstractTitle ft = (FirstAbstractTitle) obj;
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
