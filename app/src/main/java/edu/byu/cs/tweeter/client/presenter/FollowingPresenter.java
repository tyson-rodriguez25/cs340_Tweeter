package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.observer.ListServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User> implements ListServiceObserver<User> {

    public interface FollowingView extends PagedView<User> {
        void displayErrorMessage(String message);
        void displayInfoMessage(String message);
    }

    FollowingView view;
    private User targetUser;
    private AuthToken authToken;
    private User lastFollowee;
    private boolean hasMorePages = true;
    private boolean isLoading = false;

    public FollowingPresenter(FollowingView view, AuthToken authToken, User targetUser) {
        super(view, authToken, targetUser);
        this.view = view;
        this.authToken = authToken;
        this.targetUser = targetUser;
    }

    @Override
    protected void getItems(AuthToken authToken, User targetUser, int limit, User lastFollowee) {
        new FollowService().getFollowing(authToken, targetUser, limit, lastFollowee,this);

    }

    String getFailureMessage() {
        return "Following";
    }

}
