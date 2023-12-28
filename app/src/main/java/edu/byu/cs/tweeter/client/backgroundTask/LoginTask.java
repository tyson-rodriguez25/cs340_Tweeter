package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticationTask {

    private ServerFacade serverFacade;

    private static final String URL_PATH = "/login";

    private static final String LOG_TAG = "LoginTask";


    public LoginTask(String username, String password, Handler messageHandler) {
        super(messageHandler, username, password);
    }

    ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }

    @Override
    protected AuthenticationResponse runAuthenticationTask() throws IOException, TweeterRemoteException {
        LoginRequest request = new LoginRequest(username, password);
        AuthenticationResponse response = getServerFacade().login(request, URL_PATH);
        return response;

    }
}
