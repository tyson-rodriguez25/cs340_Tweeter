package edu.byu.cs.tweeter.client.model.net;

import static org.junit.jupiter.api.Assertions.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;

class ServerFacadeTest {
    ServerFacade serverFacade;

    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";
    private static final String FEMALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png";

    private static final User user1 = new User("Allen", "Anderson", "@allen", MALE_IMAGE_URL);
    private static final User user2 = new User("Amy", "Ames", "@amy", FEMALE_IMAGE_URL);
    private static final User user3 = new User("Bob", "Bobson", "@bob", MALE_IMAGE_URL);
    private static final User user4 = new User("Bonnie", "Beatty", "@bonnie", FEMALE_IMAGE_URL);
    private static final User user5 = new User("Chris", "Colston", "@chris", MALE_IMAGE_URL);
    private static final User user6 = new User("Cindy", "Coats", "@cindy", FEMALE_IMAGE_URL);
    private static final User user7 = new User("Dan", "Donaldson", "@dan", MALE_IMAGE_URL);
    private static final User user8 = new User("Dee", "Dempsey", "@dee", FEMALE_IMAGE_URL);
    private static final User user9 = new User("Elliott", "Enderson", "@elliott", MALE_IMAGE_URL);
    private static final User user10 = new User("Elizabeth", "Engle", "@elizabeth", FEMALE_IMAGE_URL);
    private static final User user11 = new User("Frank", "Frandson", "@frank", MALE_IMAGE_URL);
    private static final User user12 = new User("Fran", "Franklin", "@fran", FEMALE_IMAGE_URL);
    private static final User user13 = new User("Gary", "Gilbert", "@gary", MALE_IMAGE_URL);
    private static final User user14 = new User("Giovanna", "Giles", "@giovanna", FEMALE_IMAGE_URL);
    private static final User user15 = new User("Henry", "Henderson", "@henry", MALE_IMAGE_URL);
    private static final User user16 = new User("Helen", "Hopwell", "@helen", FEMALE_IMAGE_URL);
    private static final User user17 = new User("Igor", "Isaacson", "@igor", MALE_IMAGE_URL);
    private static final User user18 = new User("Isabel", "Isaacson", "@isabel", FEMALE_IMAGE_URL);
    private static final User user19 = new User("Justin", "Jones", "@justin", MALE_IMAGE_URL);
    private static final User user20 = new User("Jill", "Johnson", "@jill", FEMALE_IMAGE_URL);
    private static final User user21 = new User("John", "Brown", "@john", MALE_IMAGE_URL);
    private static final List<User> allUsers = Arrays.asList(
            user2, user3, user4, user5, user6, user7, user8, user9, user10, user11,
            user12, user13, user14, user15, user16, user17, user18, user19, user20, user21
    );



    @BeforeEach
    void setUp() {
        AuthToken authToken = new AuthToken();
        User currentUser = new User("Tyson", "Rodriguez",null);
        serverFacade = new ServerFacade();



    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void register() throws IOException, TweeterRemoteException {
        User currentUser = new User("Allen", "Anderson", "@allen","https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        AuthToken authToken = new AuthToken();
        AuthenticationResponse expectedResponse = new AuthenticationResponse(currentUser,authToken);
        final String URL_PATH = "/register";
        RegisterRequest request = new RegisterRequest("Allen", "Anderson", "@allen",
                "password", "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");

        AuthenticationResponse response = serverFacade.register(request, URL_PATH);

        Assertions.assertEquals(expectedResponse,response);
    }



    @Test
    void getFollowers() throws IOException, TweeterRemoteException {
        final String URL_PATH = "/followers";
        User currentUser = new User("Allen", "Anderson", "@allen","https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        AuthToken authToken = new AuthToken();
        List<User> expectedList = allUsers;
        FollowersResponse expectedResponse = new FollowersResponse(expectedList,false);
        FollowersRequest request = new FollowersRequest(authToken,"@allen", 20,currentUser);
        FollowersResponse response = serverFacade.getFollowers(request,URL_PATH);

        Assertions.assertEquals(expectedResponse,response);
    }

    @Test
    void getFollowersCount() throws IOException, TweeterRemoteException {
        User currentUser = new User("Allen", "Anderson", "@allen","https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        AuthToken authToken = new AuthToken();
        GetFollowersCountResponse expectedResponse = new GetFollowersCountResponse(true, 20);
        final String URL_PATH = "/get_follower_count";
        GetFollowersCountRequest request = new GetFollowersCountRequest(authToken, currentUser);

        GetFollowersCountResponse response = serverFacade.getFollowersCount(request,URL_PATH);
        Assertions.assertEquals(expectedResponse,response);
    }

    @Test
    void getFollowingCount() throws IOException, TweeterRemoteException {

        User currentUser = new User("Allen", "Anderson", "@allen","https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        AuthToken authToken = new AuthToken();
        GetFollowingCountResponse expectedResponse = new GetFollowingCountResponse(true, 20);
        final String URL_PATH = "/get_following_count";
        GetFollowingCountRequest request = new GetFollowingCountRequest(authToken, currentUser);

        GetFollowingCountResponse response = serverFacade.getFollowingCount(request,URL_PATH);
        Assertions.assertEquals(expectedResponse,response);
    }
}