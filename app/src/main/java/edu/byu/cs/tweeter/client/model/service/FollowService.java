package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.observer.FollowServiceObserver;
import edu.byu.cs.tweeter.client.observer.ListServiceObserver;
import edu.byu.cs.tweeter.client.observer.IsFollowerServiceObserver;
import edu.byu.cs.tweeter.client.observer.PostStatusServiceObserver;
import edu.byu.cs.tweeter.client.observer.ServiceObserver;
import edu.byu.cs.tweeter.client.observer.UnfollowServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower, ListServiceObserver<User> observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(authToken, targetUser, limit,
                lastFollower, new GetFollowersHandler(observer) );
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowersTask);

    }

    /**
     * Message handler (i.e., observer) for GetFollowersTask.
     */
    private class GetFollowersHandler extends StatusListHandler<ListServiceObserver> {
        public GetFollowersHandler(ListServiceObserver<User> observer) { super(observer); }

        //private GetFollowersObserver observer;
        //public GetFollowersHandler(GetFollowersObserver observer) {this.observer = observer;}
        /*@Override // Might have to move
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowersTask.SUCCESS_KEY);
            if (success) {
                List<User> followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.FOLLOWERS_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowersTask.MORE_PAGES_KEY);
                observer.getFollowersSucceeded(followers, hasMorePages);
            } else if (msg.getData().containsKey(GetFollowersTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersTask.MESSAGE_KEY);
                observer.getFollowersFailed(message);
            } else if (msg.getData().containsKey(GetFollowersTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersTask.EXCEPTION_KEY);
                observer.getFollowersThrewException(ex);
            }
        }*/
    }
    public void getFollowing(AuthToken authToken, User targetUser, int limit, User lastFollowee, ListServiceObserver<User> observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(authToken, targetUser, limit,
                lastFollowee, new GetFollowingHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowingTask);

    }
    /**
     * Message handler (i.e., observer) for GetFollowingTask.
     */
    private class GetFollowingHandler extends StatusListHandler<ListServiceObserver<User>> {

        public GetFollowingHandler(ListServiceObserver<User> observer) {
            super(observer);
        }


    }

    public void FollowAction(AuthToken authToken, User selectedUser, FollowServiceObserver observer) {
        System.out.println(authToken);
        FollowTask followTask = new FollowTask(authToken, selectedUser, new FollowHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followTask);
    }

    // FollowHandler

    public class FollowHandler extends BackgroundTaskHandler {
        public FollowHandler(FollowServiceObserver observer) {
            super(observer);
        }
        public void handleSuccessMessage(Message msg) {
            ((FollowServiceObserver)observer).handleFollowSuccess();
        }
    }

    public void UnfollowAction(AuthToken authToken,User selectedUser, UnfollowServiceObserver observer) {
        UnfollowTask unfollowTask = new UnfollowTask(authToken,selectedUser, new UnfollowHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(unfollowTask);
    }

    // UnfollowHandler
    private class UnfollowHandler extends BackgroundTaskHandler {
        public UnfollowHandler(UnfollowServiceObserver observer) { super(observer); }
        public void handleSuccessMessage(Message msg) {
            ((UnfollowServiceObserver)observer).handleUnfollowSuccess();
        }
    }

    public interface GetFollowerCountObserver extends ServiceObserver {
        void getFollowerCountSucceeded(int count);
    }

    public interface GetFolloweeCountObserver extends ServiceObserver {
        void getFolloweeCountSucceeded(int count);
    }

    public void getFollowerCount(AuthToken authToken, User selectedUser, GetFollowerCountObserver observer) {
        //ExecutorService executor = Executors.newFixedThreadPool(2);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Get count of most recently selected user's followers.
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(authToken, selectedUser, new GetFollowersCountHandler(observer));
        executor.execute(followersCountTask);

    }
    public void getFolloweeCount(AuthToken authToken, User selectedUser, GetFolloweeCountObserver observer) {
        //ExecutorService executor = Executors.newFixedThreadPool(2);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // Get count of most recently selected user's followees (who they are following)
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(authToken, selectedUser, new GetFollowingCountHandler(observer));
        executor.execute(followingCountTask);


    }

    // GetFollowingCountHandler
    private class GetFollowingCountHandler extends BackgroundTaskHandler {
        public GetFollowingCountHandler(GetFolloweeCountObserver observer) { super(observer); }
        @Override
        public void handleSuccessMessage(Message msg) {
            int count = msg.getData().getInt(GetFollowingCountTask.COUNT_KEY);
            ((GetFolloweeCountObserver)observer).getFolloweeCountSucceeded(count);
        }

    }

    // GetFollowersCountHandler
    private class GetFollowersCountHandler extends BackgroundTaskHandler {
        public GetFollowersCountHandler(GetFollowerCountObserver observer) { super(observer); }

        @Override
        protected void handleSuccessMessage(Message msg) {
            int count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
            ((GetFollowerCountObserver)observer).getFollowerCountSucceeded(count);
        }
    }

    public void IsFollowerAction(AuthToken authToken, User follower, User followee, IsFollowerServiceObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(authToken, follower, followee, new IsFollowerHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(isFollowerTask);
    }

    // IsFollowerHandler
    private class IsFollowerHandler extends BackgroundTaskHandler {
        public IsFollowerHandler(IsFollowerServiceObserver observer) { super(observer); }
        @Override
        public void handleSuccessMessage( Message msg) {
            boolean isFollower = msg.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
            System.out.println(isFollower);
            ((IsFollowerServiceObserver)observer).handleIsFollowerSuccess(isFollower);
            /*if (success) {
                boolean isFollower = msg.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);

                // If logged in user if a follower of the selected user, display the follow button as "following"
                if (isFollower) {
                    observer.IsFollowerSucceeded();
                } else {
                    observer.IsNotFollowerSucceeded(); //Go To Presenter
                }
            }*/
        }
    }
}
