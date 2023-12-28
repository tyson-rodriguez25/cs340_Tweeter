package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.os.Handler;
import edu.byu.cs.tweeter.client.observer.ListServiceObserver;

public abstract class StatusListHandler<T extends ListServiceObserver> extends ListBackgroundHandler<T> {

    public StatusListHandler(T observer) { super(observer); }

}
