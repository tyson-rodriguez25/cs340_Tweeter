package edu.byu.cs.tweeter.server.dao.dynamodb;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.server.dao.AbstractAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.AbstractUserDAO;

public class AuthTokenDAODynamoDB extends DynamoDBImpl implements AbstractAuthTokenDAO {

    private static final String TableName = "AuthToken";

    private static final String TokenAttr = "Token";
    private static final String TimestampAttr = "CreationTime";
    private static final String ExpirationAttr = "Expiration";
    private static final String AliasAttr = "Alias";

    private Table table = getTable(TableName);

    public void addToken(String token, String alias, String timestamp) {
        //Table table = dynamoDB.getTable(TableName);

        long expirTime =(60*20) + System.currentTimeMillis() / 1000L;

        Item item = new Item()
                .withPrimaryKey(TokenAttr, token)
                .withString(AliasAttr, alias)
                .withString(TimestampAttr, timestamp)
                .withLong(ExpirationAttr, expirTime);
        table.putItem(item);
    }

    public void deleteToken(String token) { //Use on Logout
        //Table table = dynamoDB.getTable(TableName);
        table.deleteItem(TokenAttr, token);
    }

    public String getUserFromAuthtoken(String authToken) {
        Item targetAlias = table.getItem(TokenAttr, authToken);
        String userAlias = targetAlias.getString(AliasAttr);

        if(targetAlias == null ) {
            return "Sorry Loser";
        }

        return userAlias;
    }


    /**
     * Tries to find a token in the authorizations Table.
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        Item authorizationItem = table.getItem(TokenAttr, token);
        // Item not found, return -1, which can be used later to signal no valid token
        if (authorizationItem == null) {
            return false;
        }

        if((System.currentTimeMillis()/1000L) >= authorizationItem.getLong(ExpirationAttr)) {
            table.deleteItem(TokenAttr,token);
            return false;
        }
        // Returns the Creation Time
        return true;
    }
}
