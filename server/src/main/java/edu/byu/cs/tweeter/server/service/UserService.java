package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.AbstractAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.AbstractUserDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DAOFactoryImpl;
import edu.byu.cs.tweeter.server.dao.dynamodb.AuthTokenDAODynamoDB;
import edu.byu.cs.tweeter.server.util.FakeData;

public class UserService {

    public AuthenticationResponse login(LoginRequest request) {
        return getUserDAO().login(request) ;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        return getUserDAO().register(request);
    }

    public LogoutResponse logout(LogoutRequest request) {
        return getUserDAO().logout(request);
    }

    public GetUserResponse getuser(GetUserRequest request) {
        boolean tokenValid = new AuthTokenDAODynamoDB().validateToken(request.getAuthToken().getAuthToken());
        if (!tokenValid) {
            return new GetUserResponse("Cant find Validation");
        }

        return getUserDAO().getUser(request);
    }

    AbstractUserDAO getUserDAO() {
        DAOFactory.setInstance(new DAOFactoryImpl());
        return DAOFactory.getInstance().getUserDAO();
    }

    AbstractAuthTokenDAO AuthTokenDAO() {
        DAOFactory.setInstance(new DAOFactoryImpl());
        return DAOFactory.getInstance().getAuthTokenDAO();
    }

    /**
     * Returns the dummy user to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    User getDummyUser() {
        return getFakeData().getFirstUser();
    }

    User getDummyUserbyAlias(String alias) {
        return getFakeData().findUserByAlias(alias);
    }

    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return new FakeData();
    }
}
