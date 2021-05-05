package tech.maslick.s3test;

import software.amazon.awssdk.auth.signer.AwsS3V4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4PresignerParams;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.time.Instant;


public class Test {
    // Create an AWS s3 client
    static S3Client s3 = S3Client.builder()
            .region(Region.EU_WEST_1)
            .credentialsProvider(CredsProviders.envVarsCredsProvider())
            .build();

    // Signed URL will expire in 5 min
    static long EXPIRES_IN_MIN = 5;

    public static void main(String[] args) throws IOException {
        String bucket = "my-lovely-bucket";
        String key = "helloworld.txt";

        System.out.println("########################################");
        System.out.println("# This program tests integration with S3");
        System.out.println("########################################");
        System.out.println("# signed URL for GET");
        String url = createSignedUrl2Get(bucket, key);
        System.out.println(url);

        System.out.println("########################################");
        System.out.println("# signed URL for PUT");
        url = createSignedUrl2Put(bucket, key);
        System.out.println(url);
        System.out.println("########################################");
    }

    public static String createSignedUrl2Get(String bucket, String key) {
        Aws4PresignerParams params = Aws4PresignerParams.builder()
                .signingName("s3")
                .signingRegion(Region.EU_WEST_1)
                .awsCredentials(CredsProviders.envVarsCredsProvider().resolveCredentials())
                .expirationTime(Instant.ofEpochMilli(System.currentTimeMillis() + 1000 * 60 * EXPIRES_IN_MIN))
                .build();

        SdkHttpFullRequest request = SdkHttpFullRequest.builder()
                .host(bucket + ".s3.amazonaws.com")
                .encodedPath(key)
                .method(SdkHttpMethod.GET)
                .protocol("https")
                .build();

        SdkHttpFullRequest result = AwsS3V4Signer.create().presign(request, params);
        return result.getUri().toString();
    }

    public static String createSignedUrl2Put(String bucket, String key) {
        Aws4PresignerParams params = Aws4PresignerParams.builder()
                .signingName("s3")
                .signingRegion(Region.EU_WEST_1)
                .awsCredentials(CredsProviders.envVarsCredsProvider().resolveCredentials())
                .expirationTime(Instant.ofEpochMilli(System.currentTimeMillis() + 1000 * 60 * EXPIRES_IN_MIN))
                .build();

        SdkHttpFullRequest request = SdkHttpFullRequest.builder()
                .host(bucket + ".s3.amazonaws.com")
                .encodedPath(key)
                .method(SdkHttpMethod.PUT)
                .protocol("https")
                .build();

        SdkHttpFullRequest result = AwsS3V4Signer.create().presign(request, params);
        return result.getUri().toString();
    }
}

