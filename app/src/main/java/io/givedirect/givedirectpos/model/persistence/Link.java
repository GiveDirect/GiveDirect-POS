package io.givedirect.givedirectpos.model.persistence;

import android.support.annotation.Nullable;

public class Link {
    @Nullable
    private String href;

    @Nullable
    private Boolean templated;

    public void setHref(@Nullable String href) {
        this.href = href;
    }

    @Nullable
    public String getHref() {
        return href;
    }

    public void setTemplated(@Nullable Boolean templated) {
        this.templated = templated;
    }

    public boolean getTemplated() {
        return templated != null && templated;
    }
}
