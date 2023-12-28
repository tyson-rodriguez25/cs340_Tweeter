package edu.byu.cs.tweeter.client.observer;

public interface IsFollowerServiceObserver extends ServiceObserver {
    void handleIsFollowerSuccess(boolean success);
}
