package edu.byu.cs.tweeter.client.observer;

public interface FollowServiceObserver extends ServiceObserver {
    void handleFollowSuccess();
}
