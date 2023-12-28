package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.service.StatusService;

public class GetStoryHandler implements RequestHandler<GetStoryRequest, GetStoryResponse> {

    @Override
    public GetStoryResponse handleRequest(GetStoryRequest request, Context context) {
        System.out.println(request.getLastStatus());
        System.out.println(request.getLimit());
        System.out.println(request.getUserAlias());
        System.out.println(request.getAuthToken());
        StatusService service = new StatusService();
        GetStoryResponse testResponse = service.getStory(request);
        for(Status status: testResponse.getStory()) {
            System.out.println(status.getUser().getAlias());

        }
        return service.getStory(request);
    }
}
