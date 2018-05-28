package com.kiki.entity.title;

import java.util.Objects;

public class SecondAbstractTitle extends AbstractTitle {

    @Override
    public String toString() {
        return "SecondAbstractTitle{" +
                "parentTitle='" + parentTitle + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SecondAbstractTitle) {
            SecondAbstractTitle st = (SecondAbstractTitle) obj;
            return (Objects.equals((parentTitle + title + url), (st.getParentTitle() + st.getTitle() + st.getUrl())));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parentTitle + title + url);
    }
}
