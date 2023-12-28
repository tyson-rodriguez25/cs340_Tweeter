package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.observer.ServiceObserver;

public abstract class Presenter implements ServiceObserver {

     PresenterView view;



    public Presenter(PresenterView view) { this.view = view;}

    @Override
    public void handleFailure(String message) {
        String whateverFailed = getFailureMessage();
        view.displayErrorMessage(whateverFailed +  "failed: " + message);
    }

    @Override
    public void handleException(String message) {
        String whateverFailed = getFailureMessage();
        view.displayErrorMessage(whateverFailed + "threw exception: " + message);
    }

    PresenterView getView() {
        return view;
    }

    abstract String getFailureMessage();


}
