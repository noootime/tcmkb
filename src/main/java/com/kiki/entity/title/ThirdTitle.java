package com.kiki.entity.title;

import java.util.Objects;

public class ThirdTitle extends AbstractTitle {

    @Override
    public String toString() {
        return "ThirdTitle{" +
                "parentTitle='" + parentTitle + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ThirdTitle) {
            ThirdTitle tt = (ThirdTitle) obj;
            return Objects.equals((parentTitle + title + url), (tt.getParentTitle() + tt.getTitle() + tt.getUrl()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parentTitle + title + url);
    }
}
