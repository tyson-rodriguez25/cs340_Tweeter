package edu.byu.cs.tweeter.model.domain;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents an auth token in the system.
 */
public class AuthToken implements Serializable {

    public String authToken;
    public String creationTime;

    public AuthToken(){}

    public AuthToken(String authToken){
        this.authToken=authToken;

    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getCreationTime() {
        return creationTime;
    }

    @Override
    public String toString() {
        return "authToken{" + authToken + creationTime +
                '}';
    }



    // what does this do? Ask TA.


    @Override
    public boolean equals(Object obj) {
        return true;
    }
}
