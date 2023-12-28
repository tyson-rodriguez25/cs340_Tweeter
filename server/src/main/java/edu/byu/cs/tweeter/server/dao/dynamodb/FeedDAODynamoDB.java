package edu.byu.cs.tweeter.server.dao.dynamodb;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.server.dao.AbstractFeedDAO;

public class FeedDAODynamoDB extends DynamoDBImpl implements AbstractFeedDAO {

    private static final String TableName = "feed";

    private static final String StatusAttribute = "Status";
    private static final String HandleAttribute = "Alias";
    private static final String UserthatPostedAttribute = "PostedUser";
    private static final String FirstNameAttribute = "FirstName";
    private static final String LastNameAttribute = "LastName";
    private static final String ProfileImageURLAttribute = "ProfileImageURL";
    private static final String DatetimeAttribute = "DateTime";
    private static final String URLAttribute = "URLs";
    private static final String MentionsAttribute = "Mentions";

    private Table table = getTable(TableName);

    public GetFeedResponse getFeed(GetFeedRequest request) {
        Map<String, String> attrNames = new HashMap<>();
        attrNames.put("#AliasHandle", HandleAttribute);

        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":alias", new AttributeValue().withS(request.getUserAlias()));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(TableName)
                .withKeyConditionExpression("#AliasHandle = :alias")
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues)
                .withLimit(request.getLimit());

        if (request.getLastStatus() != null) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(HandleAttribute, new AttributeValue().withS(request.getUserAlias()));
            startKey.put(DatetimeAttribute, new AttributeValue().withN(String.valueOf(request.getLastStatus().getDate())));

            queryRequest = queryRequest.withExclusiveStartKey(startKey);
        }

        List<Status> tweets = new ArrayList<>();
        QueryResult queryResult = amazonDynamoDB.query(queryRequest);
        List<Map<String, AttributeValue>> items = queryResult.getItems();
        if (items != null) {
            for (Map<String, AttributeValue> item: items) {
                String status = item.get(StatusAttribute).getS();
                String first_name = item.get(FirstNameAttribute).getS();
                String last_name = item.get(LastNameAttribute).getS();
                String image_url = item.get(ProfileImageURLAttribute).getS();
                String userThatPosted = item.get(UserthatPostedAttribute).getS();
                String dateTime = item.get(DatetimeAttribute).getS();
                List urls = item.get(URLAttribute).getL();
                List mentions = item.get(MentionsAttribute).getL();
                List<String> urlStrings = new ArrayList<String>();
                List<String> mentionStrings = new ArrayList<String>();
                for (int i = 0 ;i < urls.size();++i) {
                    String u = urls.get(i).toString();
                    u = u.substring(u.indexOf(":") + 2, u.indexOf(","));
                    urlStrings.add(u);
                }
                for (int j = 0 ;j < mentions.size();++j) {
                    String m = mentions.get(j).toString();
                    m = m.substring(m.indexOf(":") + 2, m.indexOf(","));
                    mentionStrings.add(m);
                }


                User user = new User(first_name, last_name, userThatPosted, image_url);
                tweets.add(new Status(status,user,dateTime,urlStrings,mentionStrings)); // what goes here?
            }
        }

        boolean hasMorePages = false;
        Map<String, AttributeValue> lastKey = queryResult.getLastEvaluatedKey();
        if (lastKey != null) {
            hasMorePages = true;
        }

        // No items in list
        if(tweets == null && lastKey == null) {
            hasMorePages = false;
            return new GetFeedResponse("No Tweets");
        }

        return new GetFeedResponse(tweets, hasMorePages);
    }

    public void updateFeeds(PostStatusRequest request){
        Date time = Calendar.getInstance().getTime();
        long timeStamp = System.currentTimeMillis();
        String userAlias = new AuthTokenDAODynamoDB().getUserFromAuthtoken(request.getAuthToken().getAuthToken());

        FollowersRequest tempRequest = new FollowersRequest(request.getAuthToken(), userAlias, 10000,null);
        FollowersResponse tempResponse = new FollowDAODynamoDB().getFollowers(tempRequest);

        try {
            for(int i = 0; i < tempResponse.getItems().size();i++) {
                PutItemOutcome outcome = table.putItem((new Item()
                        .withPrimaryKey(HandleAttribute, tempResponse.getFollowers().get(i).getAlias(), "timestamp", timeStamp)
                        .withString(DatetimeAttribute, time.toString())
                        .withString(StatusAttribute,request.getStatus().getPost())
                        .withString(UserthatPostedAttribute, userAlias)
                        .withString(ProfileImageURLAttribute, request.getStatus().getUser().getImageUrl())
                        .withString(FirstNameAttribute,request.getStatus().getUser().getFirstName())
                        .withString(LastNameAttribute,request.getStatus().getUser().getLastName())
                        .withList(URLAttribute,request.getStatus().getUrls())
                        .withList(MentionsAttribute,request.getStatus().getMentions())));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
