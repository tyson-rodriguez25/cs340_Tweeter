package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.observer.AuthenticationServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter extends Presenter implements AuthenticationServiceObserver {

    public interface View extends PresenterView {

        void navigateToUser(User user);

        void clearErrorMessage();

        void clearInfoMessage();
    }

    private View view;

    public LoginPresenter(LoginPresenter.View view) {
        super(view);
        this.view = view;
    }

    public void login(String alias, String password) {

        view.clearErrorMessage();
        view.clearInfoMessage();
        String message = validateLogin(alias, password);
        if(message == null) {
            view.displayInfoMessage("Logging in...");
            new UserService().login(alias, password, this);
        }
        else {
            view.displayErrorMessage("Login failed: " + message);
        }

    }

    private String validateLogin(String alias, String password) {
        if (alias.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (alias.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        return null;
    }

    @Override
    public void handleLoginSuccess(AuthToken authToken, User user) {
        view.navigateToUser(user);
        view.clearErrorMessage();
        view.displayInfoMessage("Hello " + user.getName());

    }

    @Override
    public String getFailureMessage() {
        return "Login";
    }
}
