package edu.byu.cs.tweeter.model.net.response;

import java.util.Objects;

public class GetFollowingCountResponse extends Response {

    private int followingCount;

    public GetFollowingCountResponse(String message) { super(false,message);}

    public GetFollowingCountResponse(boolean success, int followingCount) {
        super(success);
        this.followingCount = followingCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetFollowingCountResponse that = (GetFollowingCountResponse) o;
        return followingCount == that.followingCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(followingCount);
    }
}
