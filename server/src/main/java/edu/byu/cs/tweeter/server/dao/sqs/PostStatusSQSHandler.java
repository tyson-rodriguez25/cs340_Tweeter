package edu.byu.cs.tweeter.server.dao.sqs;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.dynamodb.FollowDAODynamoDB;
import edu.byu.cs.tweeter.server.service.StatusService;

public class PostStatusSQSHandler implements RequestHandler<SQSEvent, Void> { // Follow Fetcher
    // Which one is it?
    private final String queueURL = "https://sqs.us-west-2.amazonaws.com/309703252164/UpdateFeedsSQS";
    private FollowDAODynamoDB followDao = new FollowDAODynamoDB();
    private AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for(SQSEvent.SQSMessage msg :event.getRecords()) {
            Gson gson = new Gson();
            QuickQRequest status = gson.fromJson(msg.getBody(), QuickQRequest.class);
            FollowersRequest tempRequest = new FollowersRequest(status.getAuthToken(), status.getUserAlias(), 25,null);
            FollowersResponse tempResponse = null;
            do {
                tempResponse = followDao.getFollowers(tempRequest);
                SlowQRequest newRequest = new SlowQRequest(tempResponse,
                        status.getDatetime(),
                        status.getUserAlias(),
                        status.getFirstName(),
                        status.getLastName(),
                        status.getImageURL(),
                        status.getMentions(),
                        status.getUrls(),
                        status.getPost());
                String messageBody = gson.toJson(newRequest);

                SendMessageRequest sendMessageRequest = new SendMessageRequest()
                        .withQueueUrl(queueURL)
                        .withMessageBody(messageBody);
                Date time = Calendar.getInstance().getTime();
                if( tempRequest.getLastFollower() != null) {
                    System.out.println( tempRequest.getLastFollower().toString() + time);
                }



                SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageRequest);
                tempRequest.setLastFollower(tempResponse.getFollowers().get(tempResponse.getFollowers().size()-1));

            } while(tempResponse.getHasMorePages());
        }

        return null;
    }
}
