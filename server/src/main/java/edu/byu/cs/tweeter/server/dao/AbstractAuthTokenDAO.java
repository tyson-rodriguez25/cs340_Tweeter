package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;

public interface AbstractAuthTokenDAO {

    void addToken(String authToken, String alias, String timeStamp);
}
