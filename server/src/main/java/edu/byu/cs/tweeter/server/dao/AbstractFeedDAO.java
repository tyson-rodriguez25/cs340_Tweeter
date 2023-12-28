package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;

public interface AbstractFeedDAO {
    GetFeedResponse getFeed(GetFeedRequest request); // Might not need Classes
    void updateFeeds(PostStatusRequest request);

}
