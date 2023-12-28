package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

public class UserDAO implements AbstractUserDAO{
    @Override
    public AuthenticationResponse login(LoginRequest request) {
        return null;
    }

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        return null;
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        return null;
    }

    @Override
    public GetUserResponse getUser(GetUserRequest request) {
        return null;
    }

    @Override
    public void addUserBatch(List<User> users) {

    }
}
