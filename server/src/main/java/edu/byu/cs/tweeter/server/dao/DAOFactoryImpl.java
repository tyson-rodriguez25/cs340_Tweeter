package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.dynamodb.AuthTokenDAODynamoDB;
import edu.byu.cs.tweeter.server.dao.dynamodb.FeedDAODynamoDB;
import edu.byu.cs.tweeter.server.dao.dynamodb.FollowDAODynamoDB;
import edu.byu.cs.tweeter.server.dao.dynamodb.StoryDAODynamoDB;
import edu.byu.cs.tweeter.server.dao.dynamodb.UserDAODynamoDB;

public class DAOFactoryImpl extends DAOFactory {

    @Override
    public AbstractFollowDAO getFollowDAO() { return new FollowDAODynamoDB(); }

    @Override
    public AbstractUserDAO getUserDAO() { return new UserDAODynamoDB();}

    @Override
    public AbstractFeedDAO getFeedDAO() {
        return new FeedDAODynamoDB();
    }

    @Override
    public AbstractStoryDAO getStoryDAO() {
        return new StoryDAODynamoDB();
    }

    @Override
    public AbstractAuthTokenDAO getAuthTokenDAO() {
        return new AuthTokenDAODynamoDB();
    }
}
