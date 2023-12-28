package edu.byu.cs.tweeter.model.net.response;

import java.util.List;
import java.util.Objects;

/**
 * A response that can indicate whether there is more data available from the server.
 */
public class PagedResponse<T> extends Response {

    private List<T> items;
    private boolean hasMorePages;

    public PagedResponse(){
        super();
    }

    PagedResponse(boolean success, List<T> items, boolean hasMorePages) {
        super(success);
        this.items = items;
        this.hasMorePages = hasMorePages;
    }

    PagedResponse(boolean success, String message, boolean hasMorePages) {
        super(success, message);
        this.hasMorePages = hasMorePages;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages=hasMorePages;
    }

    /**
     * An indicator of whether more data is available from the server. A value of true indicates
     * that the result was limited by a maximum value in the request and an additional request
     * would return additional data.
     *
     * @return true if more data is available; otherwise, false.
     */


    public boolean getHasMorePages() {
        return hasMorePages;
    }

    public List<T> getItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PagedResponse<?> that = (PagedResponse<?>) o;
        return hasMorePages == that.hasMorePages && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, hasMorePages);
    }
}
