package edu.byu.cs.tweeter.server.dao.dynamodb;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.model.TrimmedDataAccessException;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Time;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.AbstractUserDAO;
import edu.byu.cs.tweeter.server.util.SaltedSHAHashing;

public class UserDAODynamoDB extends DynamoDBImpl implements AbstractUserDAO {

    private static final String TableName = "user";
    private static final String UserAlias = "user_alias"; // Partition Key
    private static final String PasswordAttr = "password";
    private static final String FirstNameAttr = "first_name";
    private static final String LastNameAttr = "last_name";
    private static final String ImgURLAttr = "img_url";
    private static final String FollowerCountAttr = "follower_count";
    private static final String FollowingCountAttr = "following_count";
    private static final String SaltAttr = "salt";
    private static final String BUCKET_NAME = "cs340tweeterclone";

    private Table table = getTable(TableName);

    @Override
    public AuthenticationResponse login(LoginRequest request) {
        Item item = table.getItem(UserAlias, request.getUsername());


        if(item == null) {
            return new AuthenticationResponse("User was not found. Please check spelling");
        }

        String alias = item.getString(UserAlias);
        String firstName = item.getString(FirstNameAttr);
        String lastName = item.getString(LastNameAttr);
        String profileImg = item.getString(ImgURLAttr);
        String salt = item.getString(SaltAttr);
        String password = item.getString(PasswordAttr);

        if( !password.equals(new SaltedSHAHashing().getSecurePassword(request.getPassword(),salt))) {
            return new AuthenticationResponse("Password is Invalid.");
        }
        System.out.println(salt);
        Date date =java.util.Calendar.getInstance().getTime();
        String uuid = UUID.randomUUID().toString();
        new AuthTokenDAODynamoDB().addToken(uuid,alias,date.toString());
        AuthToken authToken = new AuthToken(uuid);

        return new AuthenticationResponse(new User(firstName,lastName,alias,profileImg), authToken );
    }

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        Item foundItem = table.getItem(UserAlias, request.getUsername());
        if(foundItem != null) {
            return new AuthenticationResponse("User already exists, try logging in.");
        }


        String salt = new SaltedSHAHashing().getSalt();
        String securePassword = new SaltedSHAHashing().getSecurePassword(request.getPassword(), salt);

        ImageDAO s3Bucket = new ImageDAO();
        String imageURL = s3Bucket.upload(request.getUsername(), request.getImageBytes());


        Item newUser = new Item()
                .withPrimaryKey(UserAlias, request.getUsername())
                .withString(FirstNameAttr, request.getFirstname())
                .withString(LastNameAttr, request.getLastname())
                .withString(ImgURLAttr, imageURL)
                .withString(PasswordAttr, securePassword)
                .withInt(FollowerCountAttr,0)
                .withInt(FollowingCountAttr,0)
                .withString(SaltAttr, salt);
        table.putItem(newUser);

        User user = new User(request.getFirstname(), request.getLastname(), request.getUsername(), imageURL);
        Date date =java.util.Calendar.getInstance().getTime();
        String uuid = UUID.randomUUID().toString();
        new AuthTokenDAODynamoDB().addToken(uuid,request.getUsername(),date.toString());
        AuthToken authToken = new AuthToken(uuid);

        return new AuthenticationResponse(user,authToken);
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        new AuthTokenDAODynamoDB().deleteToken(request.getAuthToken().getAuthToken());
        return new LogoutResponse(true);
    } // Take care of Auth Token First

    @Override
    public GetUserResponse getUser(GetUserRequest request) {
        Item targetUser = table.getItem(UserAlias, request.getAlias());
        if(targetUser == null) {
            return new GetUserResponse("Could not find user.") ;
        }


        String userAlias = targetUser.getString(UserAlias);
        String firstName = targetUser.getString(FirstNameAttr);
        String lastName = targetUser.getString(LastNameAttr);
        String profileImg = targetUser.getString(ImgURLAttr);

        User gottenUser = new User(firstName,lastName,userAlias,profileImg);

        return new GetUserResponse(request.getAuthToken(), gottenUser );
    }

    public void updateFollowingCount(String alias,int updatedNumber) {
        Map<String,String> expressionAttributeNames = new HashMap<String,String>();
        expressionAttributeNames.put("#following_count", FollowingCountAttr);

        Map<String,Object> expressionAttributeValues = new HashMap<String,Object>();
        expressionAttributeValues.put(":val", updatedNumber);

        UpdateItemOutcome outcome = table.updateItem(
                UserAlias, alias,
                "set #following_count = #following_count + :val",
                expressionAttributeNames,
                expressionAttributeValues);
    }

    public void updateFollowersCount(String alias,int updatedNumber) {
        Map<String,String> expressionAttributeNames = new HashMap<String,String>();
        expressionAttributeNames.put("#followers_count", FollowerCountAttr);

        Map<String,Object> expressionAttributeValues = new HashMap<String,Object>();
        expressionAttributeValues.put(":val", updatedNumber);

        UpdateItemOutcome outcome = table.updateItem(
                UserAlias, alias,
                "set #followers_count = #followers_count + :val",
                expressionAttributeNames,
                expressionAttributeValues);
    }

    public int getFollowingCount(String alias) {
        Item item = table.getItem(UserAlias,alias);
        int count = item.getInt(FollowingCountAttr);
        return count;

    }

    public int getFollowersCount(String alias) {
        Item item = table.getItem(UserAlias,alias);
        int count = item.getInt(FollowerCountAttr);
        return count;

    }

    public String upload(String profileAlias, String base64Image) throws TrimmedDataAccessException {
        URL url = null;

        try {
            AmazonS3 s3 = AmazonS3ClientBuilder
                    .standard()
                    .withRegion(Regions.US_WEST_2)
                    .build();

            String fileName = String.format("%s_profile_image", profileAlias);
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imageBytes.length);
            metadata.setContentType("image/jpeg");

            PutObjectRequest fileRequest = new PutObjectRequest(BUCKET_NAME, fileName,new ByteArrayInputStream(imageBytes),metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            s3.putObject(fileRequest);

            url = s3.getUrl(BUCKET_NAME,fileName);

        } catch(AmazonServiceException e) {
           throw new TrimmedDataAccessException("[Server Error] - Unable to upload image to s3");
        }
        return url.toString();
    }

    public void addUserBatch(List<User> users) {

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
