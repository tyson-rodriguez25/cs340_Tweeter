package edu.byu.cs.tweeter.model.net.response;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public class GetFeedResponse extends PagedResponse<Status> {

    public GetFeedResponse() {}

    public GetFeedResponse(String message) { super(false,message,false);}

    public GetFeedResponse(List<Status> statuses, boolean hasMorePages) {
        super(true,statuses,hasMorePages);
    }

    public void setFeed(List<Status> feed) {
        setItems(feed);
    }
    public List<Status> getFeed () {
        return  getItems();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
