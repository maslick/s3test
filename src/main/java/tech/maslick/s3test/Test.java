package tech.maslick.s3test;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Test {
    // Create an AWS s3 client
    static S3Client s3 = S3Client.builder()
            .region(Region.EU_WEST_1)
            .credentialsProvider(CredsProviders.profileCredsProvider())
            .build();

    public static void main(String[] args) throws IOException {
        System.out.println("########################################");
        System.out.println("# This program tests integration with S3");
        System.out.println("########################################");
        fetchFile("my-lovely-bucket", "hello-world.txt");
    }

    public static void fetchFile(String bucket, String key) throws IOException {
        // Get file as stream
        InputStream initialStream = s3.getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()
        );

        FileOutputStream outStream = new FileOutputStream(key);
        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        while ((bytesRead = initialStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        initialStream.close();
        outStream.close();
    }
}

