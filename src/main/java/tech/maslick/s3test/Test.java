package tech.maslick.s3test;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import static tech.maslick.s3test.GetS3ObjectSample.getS3Object;
import static tech.maslick.s3test.PresignedUrlSample.getPresignedUrlToS3Object;
import static tech.maslick.s3test.PutS3ObjectSample.putS3Object;


public class Test {
    public static void main(String[] args) {
        String bucket = "my-lovely-bucket";
        String key = "helloworld.txt";

        AwsCredentialsProvider credsProvider = CredsProviders.envVarsCredsProvider();
        String keyId = credsProvider.resolveCredentials().accessKeyId();
        String accessKey = credsProvider.resolveCredentials().secretAccessKey();
        String region = Region.EU_WEST_1.toString();

        putS3Object(bucket, key, region, keyId, accessKey);
        getS3Object(bucket, key, region, keyId, accessKey);
        getPresignedUrlToS3Object(bucket, key, region, keyId, accessKey);
    }
}
