package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

public interface AbstractStoryDAO {

    GetStoryResponse getStory(GetStoryRequest request); // Might not need class
    PostStatusResponse postStatus(PostStatusRequest request);
}
