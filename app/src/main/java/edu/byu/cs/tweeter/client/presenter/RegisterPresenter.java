package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.observer.AuthenticationServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter extends Presenter implements AuthenticationServiceObserver {

    public interface View extends PresenterView {
        void navigateToUser(User user);
        void clearErrorMessage();
        void clearInfoMessage();

    }

    private View view;

    public RegisterPresenter(View view) {
        super(view);
        this.view = view;
    }

    public void register(String firstName, String lastName, String alias, String password,  String imgBytes) {
        view.clearErrorMessage();
        view.clearInfoMessage();
        String message = validateRegister(firstName, lastName, alias, password, imgBytes);
        if(message == null) {
            view.displayInfoMessage("Registering...");
            new UserService().register(firstName,lastName,alias,password, imgBytes,this);
        }
    }

    private String validateRegister(String firstName, String lastName, String alias, String password, String imgBytes) {
        if (firstName.length() == 0) {
            return "First name must be filled in";
        }
        if (lastName.length() == 0) {
            return "Last name must be filled in";
        }
        if (alias.length() == 0) {
            return "Username must be filled in";
        }
        if (alias.charAt(0) != '@') {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (alias.length() < 2) {
            return "First name must be filled in";
        }
        if (password.length() == 0) {
            return "Password must be filled in.";
        }

        if (imgBytes.length() == 0) {
            return "Profile image must be uploaded.";
        }
        return null;
    }
    @Override
    public void handleLoginSuccess(AuthToken authToken, User user) {
        view.navigateToUser(user);
        view.clearErrorMessage();
        view.displayInfoMessage("Welcome " + user.getName());
    }

    @Override
    public String getFailureMessage() {
        return "Register";
    }
}
