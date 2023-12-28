package edu.byu.cs.tweeter.server.service;

import java.util.Random;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.AbstractFollowDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DAOFactoryImpl;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.AuthTokenDAODynamoDB;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        boolean tokenValid = new AuthTokenDAODynamoDB().validateToken(request.getAuthToken().getAuthToken());
        if (!tokenValid) {
            return new FollowingResponse("Cant find Validation");
        }

        return getFollowDAO().getFollowing(request);
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        boolean tokenValid = new AuthTokenDAODynamoDB().validateToken(request.getAuthToken().getAuthToken());
        if (!tokenValid) {
            return new FollowersResponse("Cant find Validation");
        }

        return getFollowDAO().getFollowers(request);
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        boolean tokenValid = new AuthTokenDAODynamoDB().validateToken(request.getAuthToken().getAuthToken());
        if (!tokenValid) {
            return new UnfollowResponse(false);
        }
        return getFollowDAO().unFollow(request);
    }

    public FollowResponse follow(FollowRequest request) {
        boolean tokenValid = new AuthTokenDAODynamoDB().validateToken(request.getAuthToken().getAuthToken());
        if (!tokenValid) {
            return new FollowResponse(false);
        }
        return getFollowDAO().follow(request);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        boolean tokenValid = new AuthTokenDAODynamoDB().validateToken(request.getAuthToken().getAuthToken());
        if (!tokenValid) {
            return new IsFollowerResponse("Cant find Validation");
        }
        return getFollowDAO().isFollower(request);
    } // Changes with M4

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        boolean tokenValid = new AuthTokenDAODynamoDB().validateToken(request.getAuthToken().getAuthToken());
        if (!tokenValid) {
            return new GetFollowersCountResponse("Cant find Validation");
        }
        return getFollowDAO().getFollowersCount(request);
    }

    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        boolean tokenValid = new AuthTokenDAODynamoDB().validateToken(request.getAuthToken().getAuthToken());
        if (!tokenValid) {
            return new GetFollowingCountResponse("Cant find Validation");
        }
        return getFollowDAO().getFollowingCount(request);
    }


    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    AbstractFollowDAO getFollowDAO() {
        DAOFactory.setInstance(new DAOFactoryImpl());
        return DAOFactory.getInstance().getFollowDAO();
    }
}
