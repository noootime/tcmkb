package com.kiki.entity.title;

import java.util.Objects;

public class ThirdAbstractTitle extends AbstractTitle {

    @Override
    public String toString() {
        return "ThirdAbstractTitle{" +
                "parentTitle='" + parentTitle + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ThirdAbstractTitle) {
            ThirdAbstractTitle tt = (ThirdAbstractTitle) obj;
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
