package edu.byu.cs.tweeter.server.dao;

public abstract class DAOFactory {

    public static DAOFactory instance;

    public static void setInstance(DAOFactory factory) { instance = factory; }

    public static DAOFactory getInstance() {
        return instance;
    }

    public abstract AbstractFollowDAO getFollowDAO();
    public abstract AbstractUserDAO getUserDAO();
    public abstract AbstractFeedDAO getFeedDAO();
    public abstract AbstractStoryDAO getStoryDAO();
    public abstract AbstractAuthTokenDAO getAuthTokenDAO();
}
