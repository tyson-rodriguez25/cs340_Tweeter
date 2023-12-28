package edu.byu.cs.tweeter.client.observer;

public interface PostStatusServiceObserver extends ServiceObserver {
    void handlePostStatusSuccess();
}
