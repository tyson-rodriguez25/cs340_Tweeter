package edu.byu.cs.tweeter.server.dao.sqs;

import java.util.List;

import edu.byu.cs.tweeter.model.net.response.FollowersResponse;

public class SlowQRequest {

    private FollowersResponse getFollowersResponse;
    private String datetime;
    private String userAlias;
    private String firstName;
    private String lastName;
    private String imageURL;
    private List<String> mentions;
    private List<String> urls;
    private String post;

    public SlowQRequest(){}

    public SlowQRequest(FollowersResponse getFollowersResponse, String datetime, String userAlias, String firstName, String lastName,String imageURL, List<String> mentions, List<String> urls, String post) {
        this.getFollowersResponse = getFollowersResponse;
        this.datetime = datetime;
        this.userAlias = userAlias;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageURL = imageURL;
        this.mentions = mentions;
        this.urls = urls;
        this.post = post;
    }

    public FollowersResponse getGetFollowersResponse() {
        return getFollowersResponse;
    }

    public void setGetFollowersResponse(FollowersResponse getFollowersResponse) {
        this.getFollowersResponse = getFollowersResponse;
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
}
