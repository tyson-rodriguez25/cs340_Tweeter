package edu.byu.cs.tweeter.server.dao.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.AbstractFollowDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAOInterface;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.DynamoDBImpl;


public class FollowDAODynamoDB extends DynamoDBImpl implements AbstractFollowDAO {

    private static final String TableName = "Follows";
    private static final String IndexName = "followsindex";

    // Attribute
    // Follower
    private static final String FollowerAliasAttribute = "followerAlias";
    private static final String FollowerProfileImageURLAttribute = "followerProfileImageURL";
    private static final String FollowerFirstNameAttribute = "followerFirstName";
    private static final String FollowerLastNameAttribute = "followerLastName";
    // Followee
    private static final String FolloweeAliasAttribute = "followeeAlias";
    private static final String FolloweeProfileImageURLAttr = "followeeProfileImageURL";
    private static final String FolloweeFNameAttr = "followeeFirstName";
    private static final String FolloweeLNameAttr = "followeeLastName";


    private Table table = getTable(TableName);

    @Override
    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        int count = new UserDAODynamoDB().getFollowingCount(request.getTargetUser().getAlias());
        return new GetFollowingCountResponse(true, count);
    }

    @Override
    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        System.out.println(request.getTargetUser().toString());
        int count = new UserDAODynamoDB().getFollowersCount(request.getTargetUser().getAlias());

        return new GetFollowersCountResponse(true, count);
    }

    @Override
    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        boolean doesFollow = false;
        Item item = table.getItem(FollowerAliasAttribute, request.getFollower().getAlias(), FolloweeAliasAttribute, request.getFollowee().getAlias());
        if (item != null) {
            doesFollow = true;
        }
        return new IsFollowerResponse(true, doesFollow);
    }

    @Override
    public FollowResponse follow(FollowRequest request) {
        System.out.println(request.getAuthToken());
        String followerAlias = new AuthTokenDAODynamoDB().getUserFromAuthtoken(request.getAuthToken().getAuthToken());

        Item item = new Item()
                .withPrimaryKey(FollowerAliasAttribute, followerAlias, FolloweeAliasAttribute, request.getFollowee().getAlias())
                .withString(FolloweeFNameAttr, request.getFollowee().getFirstName())
                .withString(FolloweeLNameAttr, request.getFollowee().getLastName())
                .withString(FolloweeProfileImageURLAttr, request.getFollowee().getImageUrl());
        table.putItem(item);

        new UserDAODynamoDB().updateFollowingCount(followerAlias, 1);
        new UserDAODynamoDB().updateFollowersCount(request.getFollowee().getAlias(), 1);

        System.out.println(request.getAuthToken().getAuthToken());
        return new FollowResponse(true);
    }

    @Override
    public UnfollowResponse unFollow(UnfollowRequest request) {
        String followerAlias = new AuthTokenDAODynamoDB().getUserFromAuthtoken(request.getAuthToken().getAuthToken());
        new UserDAODynamoDB().updateFollowingCount(followerAlias, -1);
        new UserDAODynamoDB().updateFollowersCount(request.getFollowee().getAlias(), -1);
        table.deleteItem(FollowerAliasAttribute, followerAlias, FolloweeAliasAttribute, request.getFollowee().getAlias());
        return new UnfollowResponse(true);
    }

    @Override
    public FollowingResponse getFollowing(FollowingRequest request) {
        Map<String, String> attrNames = new HashMap<>();
        attrNames.put("#follower", FollowerAliasAttribute);

        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":followerAlias", new AttributeValue().withS(request.getFollowerAlias()));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(TableName)
                .withKeyConditionExpression("#follower = :followerAlias")
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues)
                .withLimit(request.getLimit());

        if (request.getLastFollowee() != null) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FollowerAliasAttribute, new AttributeValue().withS(request.getFollowerAlias()));
            startKey.put(FolloweeAliasAttribute, new AttributeValue().withS(request.getLastFollowee().getAlias()));

            queryRequest = queryRequest.withExclusiveStartKey(startKey);
        }

        List<User> followees = new ArrayList<>();
        QueryResult queryResult = amazonDynamoDB.query(queryRequest);
        List<Map<String, AttributeValue>> items = queryResult.getItems();
        if (items != null) {
            for (Map<String, AttributeValue> item: items) {
                String alias = item.get(FolloweeAliasAttribute).getS();
                String first_name = item.get(FolloweeFNameAttr).getS();
                String last_name = item.get(FolloweeLNameAttr).getS();
                String image_url = item.get(FolloweeProfileImageURLAttr).getS();
                followees.add(new User(first_name, last_name, alias, image_url));
            }
        }

        boolean hasMorePages = false;
        Map<String, AttributeValue> lastKey = queryResult.getLastEvaluatedKey();
        if (lastKey != null) {
            hasMorePages = true;
        }

        return new FollowingResponse(followees, hasMorePages);
    }

    @Override
    public FollowersResponse getFollowers(FollowersRequest request) {
        Map<String, String> attrNames = new HashMap<>();
        attrNames.put("#followee", FolloweeAliasAttribute);

        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":followeeAlias", new AttributeValue().withS(request.getFolloweeAlias()));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(TableName)
                .withIndexName(IndexName)
                .withKeyConditionExpression("#followee = :followeeAlias")
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues)
                .withLimit(request.getLimit());

        if (request.getLastFollower() != null) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FolloweeAliasAttribute, new AttributeValue().withS(request.getFolloweeAlias()));
            startKey.put(FollowerAliasAttribute, new AttributeValue().withS(request.getLastFollower().getAlias()));

            queryRequest = queryRequest.withExclusiveStartKey(startKey);
        }

        List<User> followers = new ArrayList<>();
        System.out.println(request.getFolloweeAlias());
        QueryResult queryResult = amazonDynamoDB.query(queryRequest);
        List<Map<String, AttributeValue>> items = queryResult.getItems();
        if (items != null) {
            for (Map<String, AttributeValue> item: items) {
                String alias = item.get(FollowerAliasAttribute).getS();
                GetUserRequest tempRequest = new GetUserRequest(request.getAuthToken(),alias);
                followers.add(new UserDAODynamoDB().getUser(tempRequest).getTargetUser());

            }
        }

        boolean hasMorePages = false;
        Map<String, AttributeValue> lastKey = queryResult.getLastEvaluatedKey();
        if (lastKey != null) {
            hasMorePages = true;
        }

        return new FollowersResponse(followers, hasMorePages);
    }

    public void addFollowersBatch(List<User> users) {

        // Constructor for TableWriteItems takes the name of the table, which I have stored in TABLE_USER
        TableWriteItems items = new TableWriteItems(TableName);

        // Add each user into the TableWriteItems object
        for (User user : users) {
            Item item = new Item()
                    .withPrimaryKey("alias", user.getAlias())
                    .withString("name", user.getName());
            items.addItemToPut(item);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems(TableName);
            }
        }

        // Write any leftover items
        if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
            loopBatchWrite(items);
        }
    }
    private void loopBatchWrite(TableWriteItems items) {

        // The 'dynamoDB' object is of type DynamoDB and is declared statically in this example
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(items);
        //logger.log("Wrote User Batch");

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
            //logger.log("Wrote more Users");
        }
    }





}
