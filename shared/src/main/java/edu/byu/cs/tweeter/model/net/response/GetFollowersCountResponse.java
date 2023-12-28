package edu.byu.cs.tweeter.model.net.response;

import java.util.Objects;

public class GetFollowersCountResponse extends Response {

    private int followerCount;

    public GetFollowersCountResponse(String message) { super(false, message);}

    public GetFollowersCountResponse(boolean success, int followerCount) {
        super(success);
        this.followerCount = followerCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetFollowersCountResponse response = (GetFollowersCountResponse) o;
        return followerCount == response.followerCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerCount);
    }
}
