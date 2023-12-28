package edu.byu.cs.tweeter.client.observer;
import android.os.Handler;

import java.util.List;

public interface ListServiceObserver<T> extends ServiceObserver {
    void handleSuccess(List<T> items, boolean addMorePages);
}
