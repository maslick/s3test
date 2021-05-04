package tech.maslick.s3test;

import software.amazon.awssdk.auth.signer.AwsS3V4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4PresignerParams;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Instant;


public class Test {
    // Create an AWS s3 client
    static S3Client s3 = S3Client.builder()
            .region(Region.EU_WEST_1)
            .credentialsProvider(CredsProviders.envVarsCredsProvider())
            .build();

    public static void main(String[] args) throws IOException {
        System.out.println("########################################");
        System.out.println("# This program tests integration with S3");
        System.out.println("########################################");
        fetchFile("my-lovely-bucket", "helloworld.txt");
        putFile("my-lovely-bucket", "helloworld.txt");

        System.out.println("########################################");
        System.out.println("# signed URL for GET");
        String url = createSignedUrl2Fetch("my-lovely-bucket", "helloworld.txt");
        System.out.println(url);

        System.out.println("########################################");
        System.out.println("# signed URL for PUT");
        url = createSignedUrl2Put("my-lovely-bucket", "helloworld.txt");
        System.out.println(url);
        System.out.println("########################################");
    }

    public static void fetchFile(String bucket, String key) throws IOException {
        System.out.println("1. Downloading file from S3...");
        // Get file as stream
        InputStream initialStream = s3.getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()
        );

        // Save stream to file
        FileOutputStream outStream = new FileOutputStream(key);
        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        while ((bytesRead = initialStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        // Close streams
        initialStream.close();
        outStream.close();
    }

    public static void putFile(String bucket, String key) {
        System.out.println("2. Writing helloworld.txt to S3...");
        PutObjectResponse response = s3.putObject(
                PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build(),
                Paths.get("helloworld.txt")
        );

        System.out.println("Success: " + response.sdkHttpResponse().isSuccessful());
    }

    public static String createSignedUrl2Fetch(String bucket, String key) {
        Aws4PresignerParams params = Aws4PresignerParams.builder()
                .signingName("s3")
                .signingRegion(Region.EU_WEST_1)
                .awsCredentials(CredsProviders.envVarsCredsProvider().resolveCredentials())
                .expirationTime(Instant.ofEpochMilli(System.currentTimeMillis() + 60*1000*5))
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
                .expirationTime(Instant.ofEpochMilli(System.currentTimeMillis() + 60*1000*5))
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

