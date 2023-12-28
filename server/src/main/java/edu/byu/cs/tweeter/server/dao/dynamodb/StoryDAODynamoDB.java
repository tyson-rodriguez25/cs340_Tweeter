package edu.byu.cs.tweeter.server.dao.dynamodb;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.AbstractStoryDAO;
import edu.byu.cs.tweeter.server.dao.sqs.QuickQRequest;
import edu.byu.cs.tweeter.server.dao.sqs.SlowQRequest;

public class StoryDAODynamoDB extends DynamoDBImpl implements AbstractStoryDAO {
    private static final String TableName = "Story";

    private static final String StatusAttribute = "status";
    private static final String HandleAttribute = "Alias";
    private static final String FirstNameAttribute = "FirstName";
    private static final String LastNameAttribute = "LastName";
    private static final String ProfileImageURLAttribute = "ProfileImageURL";
    private static final String DatetimeAttribute = "DateTime";
    private static final String URLAttribute = "URLs";
    private static final String MentionsAttribute = "Mentions";



    private Table table = getTable(TableName);

    public GetStoryResponse getStory(GetStoryRequest request) {
        Map<String, String> attrNames = new HashMap<>();
        attrNames.put("#handle", HandleAttribute);

        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":alias", new AttributeValue().withS(request.getUserAlias()));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(TableName)
                .withKeyConditionExpression("#handle = :alias")
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
            for (Map<String, AttributeValue> item : items) {
                String status = item.get(StatusAttribute).getS();
                String alias = item.get(HandleAttribute).getS();
                String first_name = item.get(FirstNameAttribute).getS();
                String last_name = item.get(LastNameAttribute).getS();
                String image_url = item.get(ProfileImageURLAttribute).getS();
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
                //System.out.println(urlStrings);
                for (int j = 0 ;j < mentions.size();++j) {
                    String m = mentions.get(j).toString();
                    m = m.substring(m.indexOf(":") + 2, m.indexOf(","));
                    mentionStrings.add(m);
                }




                User user = new User(first_name, last_name, alias, image_url);
                tweets.add(new Status(status,user,dateTime,urlStrings,mentionStrings)); // What goes here?
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
            return new GetStoryResponse("No Tweets");
        }
        return new GetStoryResponse(tweets, hasMorePages);
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        assert request.getStatus() != null;
        assert request.getAuthToken() != null;

        Date time = Calendar.getInstance().getTime();
        System.out.println("This " + request.getStatus());
        String userAlias = new AuthTokenDAODynamoDB().getUserFromAuthtoken(request.getAuthToken().getAuthToken());


        try {
            PutItemOutcome outcome = table.putItem((new Item()
                    .withPrimaryKey(HandleAttribute, userAlias, "timestamp",System.currentTimeMillis())
                    .withString(DatetimeAttribute, time.toString())
                    .withString(StatusAttribute,request.getStatus().getPost())
                    .withString(ProfileImageURLAttribute, request.getStatus().getUser().getImageUrl())
                    .withString(FirstNameAttribute,request.getStatus().getUser().getFirstName())
                    .withString(LastNameAttribute,request.getStatus().getUser().getLastName())
                    .withList(URLAttribute,request.getStatus().getUrls())
                    .withList(MentionsAttribute,request.getStatus().getMentions())));

            String queueURL = "https://sqs.us-west-2.amazonaws.com/309703252164/PostStatusSQS2"; // Follow Fetcher

            Gson gson = new Gson();
            QuickQRequest newRequest = new QuickQRequest(
                    request.getStatus().getDate(),
                    request.getStatus().getUser().getAlias(),
                    request.getStatus().getUser().getFirstName(),
                    request.getStatus().getUser().getLastName(),
                    request.getStatus().getUser().getImageUrl(),
                    request.getStatus().getMentions(),
                    request.getStatus().getUrls(),
                    request.getStatus().getPost(),
                    request.getAuthToken());
            String messageBody = gson.toJson(newRequest);
            SendMessageRequest sendMessageRequest = new SendMessageRequest()
                    .withQueueUrl(queueURL)
                    .withMessageBody(messageBody);
            AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
            SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageRequest);
            System.out.println(sendMessageResult);


        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new PostStatusResponse(false, "Error Posting Status: " + e);
        }
        return new PostStatusResponse(true, "Posted Successfully :)"); // added message
    }

    //public void batchPostStatus()



}
