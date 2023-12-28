package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.observer.ListServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User> implements ListServiceObserver<User> {

    @Override
    protected void getItems(AuthToken authToken, User targetUser, int limit, User lastFollower) {
        new FollowService().getFollowers(authToken, targetUser, limit, lastFollower,this);

    }

    public interface FollowersView extends PagedView<User> {
        void displayErrorMessage(String message);
        void displayInfoMessage(String message);
    }


    FollowersView view;
    private User targetUser;
    private AuthToken authToken;

    private User lastFollower;
    private boolean hasMorePages = true;
    private boolean isLoading = false;

    public FollowersPresenter(FollowersView view, AuthToken authToken, User targetUser) {
        super(view, authToken, targetUser);
        this.view = view;
        this.authToken = authToken;
        this.targetUser = targetUser;
    }

    String getFailureMessage() {
        return "Followers";
    }
}
