package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.observer.ListServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status> implements ListServiceObserver<Status> {


    public interface FeedView extends PagedView<Status> {

    }


    public FeedPresenter(FeedView view, AuthToken authToken, User targetUser) {
        super(view, authToken, targetUser);
        this.authToken = authToken;
        this.targetUser = targetUser;
    }
    @Override
    public void getItems(AuthToken authToken, User targetUser, int limit, Status lastStatus) { // Changed from getFeed
        new StatusService().getFeed(authToken, targetUser, limit, lastStatus,this);
    }

    @Override
    public void handleFailure(String message) {
        view.displayErrorMessage("Feed failed to Load: " + message);
    }

    @Override
    public void handleException(String message) {
        view.displayErrorMessage("Feed Failed: " + message);
    }

    String getFailureMessage() {
        return "Feed";
    }

}
