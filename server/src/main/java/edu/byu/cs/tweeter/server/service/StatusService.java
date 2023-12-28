package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.AbstractFeedDAO;
import edu.byu.cs.tweeter.server.dao.AbstractStoryDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DAOFactoryImpl;
import edu.byu.cs.tweeter.server.dao.dynamodb.AuthTokenDAODynamoDB;

public class StatusService {

    public PostStatusResponse poststatus(PostStatusRequest request) {
        boolean tokenValid = new AuthTokenDAODynamoDB().validateToken(request.getAuthToken().getAuthToken());
        if (!tokenValid) {
            return new PostStatusResponse(false);
        }
        PostStatusResponse response = getStoryDAO().postStatus(request);
        //getFeedDAO().updateFeeds(request);
        return response;
    }

    public GetStoryResponse getStory(GetStoryRequest request) {
        boolean tokenValid = new AuthTokenDAODynamoDB().validateToken(request.getAuthToken().getAuthToken());
        if (!tokenValid) {
            return new GetStoryResponse("Cant find Validation");
        }
        return getStoryDAO().getStory(request);
    }

    public GetFeedResponse getFeed(GetFeedRequest request) {
        boolean tokenValid = new AuthTokenDAODynamoDB().validateToken(request.getAuthToken().getAuthToken());
        if (!tokenValid) {
            return new GetFeedResponse("Cant find Validation");
        }
        return getFeedDAO().getFeed(request);
    }


    AbstractStoryDAO getStoryDAO() {
        DAOFactory.setInstance(new DAOFactoryImpl());
        return DAOFactory.getInstance().getStoryDAO();
    }
    AbstractFeedDAO getFeedDAO() {
        DAOFactory.setInstance(new DAOFactoryImpl());
        return DAOFactory.getInstance().getFeedDAO();
    }

}
