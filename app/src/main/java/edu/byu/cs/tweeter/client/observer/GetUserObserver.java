package edu.byu.cs.tweeter.client.observer;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public interface GetUserObserver extends ServiceObserver {
    void handleSuccess(User user);
}
