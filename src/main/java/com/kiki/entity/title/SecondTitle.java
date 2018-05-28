package com.kiki.entity.title;

import java.util.Objects;

public class SecondTitle extends AbstractTitle {

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
