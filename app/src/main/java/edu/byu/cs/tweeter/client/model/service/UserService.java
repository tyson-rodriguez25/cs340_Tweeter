package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.AuthenticationTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.observer.AuthenticationServiceObserver;
import edu.byu.cs.tweeter.client.observer.GetUserObserver;
import edu.byu.cs.tweeter.client.observer.IsFollowerServiceObserver;
import edu.byu.cs.tweeter.client.observer.LogoutServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;

public class UserService {

    private ServerFacade serverFacade;

    public void register(String firstName, String lastName, String alias, String password, String imageBytes, AuthenticationServiceObserver observer) {
        RegisterTask registerTask = new RegisterTask(firstName, lastName,alias,password, imageBytes,new AuthenticationHandler(observer));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(registerTask);

    }
    // RegisterHandler

    public void login(String alias, String password, AuthenticationServiceObserver observer) {
        // Run a Login Task to login the user
        LoginTask loginTask = new LoginTask(alias, password, new AuthenticationHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(loginTask); //Move for Model B

    }

    /**
     * Message handler (i.e., observer) for LoginTask
     */
    private class AuthenticationHandler extends BackgroundTaskHandler {
        public AuthenticationHandler(AuthenticationServiceObserver observer) {
            super(observer);
        }

        @Override
        public void handleSuccessMessage(Message msg) { //Move Handlers to Task package for future.
            User loggedInUser = (User) msg.getData().getSerializable(AuthenticationTask.USER_KEY);
            AuthToken authToken = (AuthToken) msg.getData().getSerializable(AuthenticationTask.AUTH_TOKEN_KEY);
            // Cache user session information
            Cache.getInstance().setCurrUser(loggedInUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);
            ((AuthenticationServiceObserver)observer). handleLoginSuccess(authToken, loggedInUser);
            }
        }




    public void logoutUser(LogoutServiceObserver observer) {
        LogoutTask logoutTask = new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new LogoutHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(logoutTask);
    }

    // LogoutHandler

    public class LogoutHandler extends BackgroundTaskHandler {

        public LogoutHandler(LogoutServiceObserver observer) {
            super(observer);
        }
        @Override
        public void handleSuccessMessage(Message msg) {
            ((LogoutServiceObserver)observer).handleLogoutSuccess();
        }
    }


    public void getUser(AuthToken authToken, String alias, GetUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(authToken, alias, new GetUserHandler(observer) {
        });
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getUserTask);

    }
    /*
     * Message handler (i.e., observer) for GetUserTask.
     */
    private class GetUserHandler extends BackgroundTaskHandler {
        public GetUserHandler(GetUserObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage( Message msg) {
            User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
            ((GetUserObserver)observer).handleSuccess(user);
        }
    }

    ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }



}
