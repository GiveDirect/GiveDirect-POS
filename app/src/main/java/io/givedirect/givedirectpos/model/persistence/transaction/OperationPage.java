package io.givedirect.givedirectpos.model.persistence.transaction;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.givedirect.givedirectpos.model.persistence.EmbeddedHals;
import io.givedirect.givedirectpos.model.persistence.Link;
import io.reactivex.functions.Function;
import timber.log.Timber;

public class OperationPage {
    @Nullable
    private OperationPageLinks _links;

    @Nullable
    private EmbeddedHals<Operation> _embedded;

    @Nullable
    public OperationPageLinks get_links() {
        return _links;
    }

    public void set_links(@Nullable OperationPageLinks _links) {
        this._links = _links;
    }

    @Nullable
    public EmbeddedHals<Operation> get_embedded() {
        return _embedded;
    }

    public void set_embedded(@Nullable EmbeddedHals<Operation> _embedded) {
        this._embedded = _embedded;
    }

    @Nullable
    private String getNextPage() {
        return getLink(OperationPageLinks::getNext);
    }

    @Nullable
    private String getPreviousPage() {
        return getLink(OperationPageLinks::getPrev);
    }

    @Nullable
    private String getLink(@NonNull Function<OperationPageLinks, Link> linkFunction) {
        if (_links == null) {
            return null;
        }

        try {
            Link link = linkFunction.apply(_links);
            return link == null ? null : link.getHref();
        } catch (Exception e) {
            Timber.e(e, "Failed to fetch link");
            return null;
        }
    }

    @NonNull
    public List<Operation> getOperations() {
        return _embedded == null ? new ArrayList<>() : _embedded.getRecordsList();
    }

    @Nullable
    public String getPreviousPageLink() {
        if (_links == null) {
            return null;
        }

        Link prevLink = _links.getPrev();

        return prevLink == null ? null : prevLink.getHref();
    }

    @Nullable
    public String getNextPageLink() {
        if (_links == null) {
            return null;
        }

        Link nextLink = _links.getNext();

        return nextLink == null ? null : nextLink.getHref();
    }

    public static class OperationPageLinks {
        @Nullable
        Link next,prev,self;

        @Nullable
        public Link getNext() {
            return next;
        }

        public void setNext(@Nullable Link next) {
            this.next = next;
        }

        @Nullable
        public Link getPrev() {
            return prev;
        }

        public void setPrev(@Nullable Link prev) {
            this.prev = prev;
        }

        @Nullable
        public Link getSelf() {
            return self;
        }

        public void setSelf(@Nullable Link self) {
            this.self = self;
        }
    }
}
