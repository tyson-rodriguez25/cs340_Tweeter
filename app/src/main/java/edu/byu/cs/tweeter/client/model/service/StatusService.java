package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.observer.ListServiceObserver;
import edu.byu.cs.tweeter.client.observer.IsFollowerServiceObserver;
import edu.byu.cs.tweeter.client.observer.LogoutServiceObserver;
import edu.byu.cs.tweeter.client.observer.PostStatusServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {

    public void getFeed(AuthToken authToken, User user, int limit, Status lastStatus, ListServiceObserver<Status> observer) {
        GetFeedTask getFeedTask = new GetFeedTask(authToken, user, limit, lastStatus, new GetFeedHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFeedTask);
    }

    /**
     * Message handler (i.e., observer) for GetFeedTask.
     */
    private class GetFeedHandler extends StatusListHandler<ListServiceObserver<Status>> {

        public GetFeedHandler(ListServiceObserver<Status> observer) { super(observer); }

    }


    public void getStory(AuthToken authToken, User user, int limit, Status lastStatus, ListServiceObserver<Status> observer) {
        GetStoryTask getStoryTask = new GetStoryTask(authToken, user, limit, lastStatus, new GetStoryHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getStoryTask);
    }

    /**
     * Message handler (i.e., observer) for GetStoryTask.
     */
    private class GetStoryHandler extends StatusListHandler<ListServiceObserver<Status>> {

        public GetStoryHandler(ListServiceObserver<Status> observer) { super(observer); }

    }

    public void PostStatus(AuthToken authToken, Status newStatus, PostStatusServiceObserver observer) {
        PostStatusTask statusTask = new PostStatusTask(authToken, newStatus, new PostStatusHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(statusTask);
    }

    // PostStatusHandler

    private class PostStatusHandler extends BackgroundTaskHandler {

        public PostStatusHandler(PostStatusServiceObserver observer) {
            super(observer);
        }
        public void handleSuccessMessage(Message msg) {
            ((PostStatusServiceObserver)observer).handlePostStatusSuccess();
        }
    }
}
