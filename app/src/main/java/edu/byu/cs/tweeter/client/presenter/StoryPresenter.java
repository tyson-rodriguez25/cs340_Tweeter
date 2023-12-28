package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.observer.ListServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> implements ListServiceObserver<Status> {


    public interface StoryView extends PagedView<Status> {
        void displayErrorMessage(String message);
        void displayInfoMessage(String message);
    }

    public StoryPresenter(StoryView view, AuthToken authToken, User targetUser) {
        super(view, authToken, targetUser);
        this.authToken = authToken;
        this.targetUser = targetUser;
    }

    @Override
    public void getItems(AuthToken authToken, User targetUser, int limit, Status lastStatus) { // Changed from getFeed
        new StatusService().getStory(authToken, targetUser, limit, lastStatus,this);
    }

    String getFailureMessage() {
        return "Story";
    }
}