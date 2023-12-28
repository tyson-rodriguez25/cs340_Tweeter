package edu.byu.cs.tweeter.client.observer;

public interface LogoutServiceObserver extends ServiceObserver {
    void handleLogoutSuccess();
}
