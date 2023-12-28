package edu.byu.cs.tweeter.server.dao.dynamodb;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.model.TrimmedDataAccessException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Base64;

public class ImageDAO {

    private static final String BUCKET_NAME = "cs340tweeterclone";

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
}
