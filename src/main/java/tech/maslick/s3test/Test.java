package tech.maslick.s3test;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;


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
                Path.of("helloworld.txt")
        );

        System.out.println("Success: " + response.sdkHttpResponse().isSuccessful());
    }
}

