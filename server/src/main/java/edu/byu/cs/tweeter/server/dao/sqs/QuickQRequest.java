package edu.byu.cs.tweeter.server.dao.sqs;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class QuickQRequest {
    private String datetime;
    private String userAlias;
    private String firstName;
    private String lastName;
    private String imageURL;
    private List<String> mentions;
    private List<String> urls;
    private String post;
    private AuthToken authToken;

    public QuickQRequest(){}


    public QuickQRequest(String datetime, String userAlias, String firstName, String lastName,String imageURL, List<String> mentions, List<String> urls, String post, AuthToken authToken) {

        this.datetime = datetime;
        this.userAlias = userAlias;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageURL = imageURL;
        this.mentions = mentions;
        this.urls = urls;
        this.post = post;
        this.authToken = authToken;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
