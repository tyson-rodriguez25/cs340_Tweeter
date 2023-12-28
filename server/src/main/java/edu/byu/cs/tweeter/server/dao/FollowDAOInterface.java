package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;

public interface FollowDAOInterface {
    Integer getFolloweeCount(User follower);

    FollowingResponse getFollowees(FollowingRequest request);

    FollowersResponse getFollowers(FollowersRequest request);

    FollowResponse followUser(FollowRequest request); // Might not need
}
