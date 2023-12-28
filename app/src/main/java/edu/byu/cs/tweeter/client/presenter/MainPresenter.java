package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.observer.AuthenticationServiceObserver;
import edu.byu.cs.tweeter.client.observer.FollowServiceObserver;
import edu.byu.cs.tweeter.client.observer.IsFollowerServiceObserver;
import edu.byu.cs.tweeter.client.observer.LogoutServiceObserver;
import edu.byu.cs.tweeter.client.observer.PostStatusServiceObserver;
import edu.byu.cs.tweeter.client.observer.ServiceObserver;
import edu.byu.cs.tweeter.client.observer.UnfollowServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter implements LogoutServiceObserver, FollowServiceObserver,
        IsFollowerServiceObserver , PostStatusServiceObserver, ServiceObserver, UnfollowServiceObserver,
        FollowService.GetFolloweeCountObserver,FollowService.GetFollowerCountObserver{



    public interface View extends PresenterView {
        void logoutUser();
        void clearErrorMessage();
        void clearInfoMessage();
        void displayFollowerCount(int count);
        void displayFolloweeCount(int count);
        void updateFollowButton(boolean flipped);
        void enableFollow(boolean enabled);
        void displayIsFollowerButton();
        void displayIsNotFollowerButton();
        void displayStatus();


    }

    public StatusService getStatusService() {
        if (statusService == null) {
            statusService = new StatusService();
        }
        return statusService;
    }

    private View view;
    private User targetUser;
    //private AuthToken authToken
    private AuthToken authToken;
    private StatusService statusService;


    public MainPresenter(View view) {
        super(view);
        this.view = view;
    }

    public void logoutUser(){
        view.clearErrorMessage();
        view.clearInfoMessage();
        new UserService().logoutUser(this);
    }

    @Override
    public void handleLogoutSuccess() {
        view.logoutUser();
        view.displayInfoMessage("Goodbye"); //Logout User
    }



    public void getFollowerCount(AuthToken authToken, User targetUser) {
        view.clearErrorMessage();
        view.clearInfoMessage();
        new FollowService().getFollowerCount(authToken,targetUser,this);
    }
    public void getFolloweeCount(AuthToken authToken, User targetUser) {
        view.clearErrorMessage();
        view.clearInfoMessage();
        new FollowService().getFolloweeCount(authToken,targetUser,this);
    }

    public void FollowAction(AuthToken authToken, User targetUser) {
        view.clearErrorMessage();
        view.clearInfoMessage();
        new FollowService().FollowAction(authToken, targetUser, this);
    }

    public void UnfollowAction(AuthToken authToken, User targetUser) {
        view.clearInfoMessage();
        view.clearErrorMessage();
        new FollowService().UnfollowAction(authToken,targetUser,this);
    }

    @Override
    public void handleFollowSuccess() {
        getFollowerCount(authToken, targetUser);
        //view.displayFollowerCount(+1);
        view.updateFollowButton(false);
        view.enableFollow(true);
        view.displayInfoMessage("Followed");
    }

    @Override
    public void handleUnfollowSuccess() {
        getFollowerCount(authToken, targetUser);
        view.updateFollowButton(true);
        view.enableFollow(true);
        view.displayInfoMessage("Successfully Unfollowed"); //Unfollow Succeeded
    }

    @Override
    public void getFollowerCountSucceeded(int count) {
        view.displayFollowerCount(count);
    }

    @Override
    public void getFolloweeCountSucceeded(int count) {
        view.displayFolloweeCount(count);
    }

    /*@Override
    public void handleIsNotFollowerSuccess(boolean success) {
        view.displayIsNotFollowerButton(); // Is Not Follower
    }*/

    @Override
    public void handleIsFollowerSuccess(boolean success) {
        if (!success) {
            view.displayIsNotFollowerButton();
        }
        else {
            view.displayIsFollowerButton();
        }

    }

    @Override
    public void handlePostStatusSuccess() {
        view.displayStatus(); // Post Status
    }

    public void PostNewStatus(Status newStatus) {
        getStatusService().PostStatus(Cache.getInstance().getCurrUserAuthToken(), newStatus, this);
    }

    @Override
    public String getFailureMessage() {
        return "Post Status";
    }


    public void IsFollowers(AuthToken authToken, User targetUser, User currentUser) {
        new FollowService().IsFollowerAction(authToken, currentUser, targetUser,this );
    }
}
