package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;
import android.view.View;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.observer.GetUserObserver;
import edu.byu.cs.tweeter.client.observer.ListServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter implements GetUserObserver, ListServiceObserver<T> {

    private static final String LOG_TAG = "PagedPresenter";
    private static final int PAGE_SIZE = 10;

    PagedView<T> view;

    public interface PagedView<U> extends PresenterView {
        void setLoading(boolean value);

        void addItems(List<U> newItems);
        void navigateToUser(User user);
    }

    private UserService userService;
    protected User targetUser;
    protected AuthToken authToken;

    protected T lastItem;
    protected boolean hasMorePages = true;
    protected boolean isLoading = false;
    protected boolean isGettingUser = false;

    protected PagedPresenter(PagedView<T> view, AuthToken authToken, User targetUser) {
        super(view);
        this.view = view;
        this.userService = new UserService();
        this.authToken = authToken;
        this.targetUser = targetUser;
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            isLoading = true;
            view.setLoading(true);
            getItems(authToken, targetUser, PAGE_SIZE, lastItem);
        }
    }

    public void getUser(String alias) {
        getView().displayInfoMessage("Getting user's profile...");
        new UserService().getUser(authToken,alias,this);
    }

    public void handleSuccess(User user) {
        view.navigateToUser(user);
    }
    @Override
    public void handleSuccess(List<T> items, boolean hasMorePages) {
        view.setLoading(false);
        this.hasMorePages = hasMorePages;
        this.isLoading = false;
        view.addItems(items);
    }

    //public void setLoading(boolean setLoading) {
    //    view.
    //}

    protected abstract void getItems(AuthToken authToken, User targetUser, int limit, T lastItem);

}
