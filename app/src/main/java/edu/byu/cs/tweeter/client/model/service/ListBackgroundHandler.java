package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;
import android.os.Handler;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.PagedTask;
import edu.byu.cs.tweeter.client.observer.ListServiceObserver;
import edu.byu.cs.tweeter.client.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class ListBackgroundHandler<T extends ListServiceObserver> extends BackgroundTaskHandler {

    public ListBackgroundHandler(T observer) { super(observer); }

    protected void handleSuccessMessage(Message msg) {
        List<T> items = (List<T>) msg.getData().getSerializable(PagedTask.ITEMS_KEY);
        boolean hasMorePages = msg.getData().getBoolean(PagedTask.MORE_PAGES_KEY);
        ((ListServiceObserver)observer).handleSuccess(items, hasMorePages); // Need items and hasmorepages from message
    }

}
