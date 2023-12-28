package edu.byu.cs.tweeter.model.net.response;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public class GetStoryResponse extends PagedResponse<Status> {

    public GetStoryResponse() {}

    public GetStoryResponse(String message) { super(false,message,false);}

    public GetStoryResponse(List<Status> statuses, boolean hasMorePages) {
        super(true, statuses,hasMorePages);
    }

    public void setStory(List<Status> story) {
        setItems(story);
    }

    public List<Status> getStory () {
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
