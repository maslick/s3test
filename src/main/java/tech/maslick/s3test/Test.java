package tech.maslick.s3test;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;


public class Test {
    // Presigner instance
    static S3Presigner presigner = S3Presigner.builder()
            .region(Region.EU_WEST_1)
            .credentialsProvider(CredsProviders.envVarsCredsProvider())
            .build();

    // Signed URL will expire in 5 min
    static long EXPIRES_IN_MIN = 5;

    public static void main(String[] args) {
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
        GetObjectRequest req = GetObjectRequest.builder().bucket(bucket).key(key).build();

        GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(EXPIRES_IN_MIN))
                .getObjectRequest(req)
                .build();

        PresignedGetObjectRequest presignedReq = presigner.presignGetObject(presignReq);
        return presignedReq.url().toString();
    }

    public static String createSignedUrl2Put(String bucket, String key) {
        PutObjectRequest req = PutObjectRequest.builder().bucket(bucket).key(key).build();

        PutObjectPresignRequest presignReq = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(EXPIRES_IN_MIN))
                .putObjectRequest(req)
                .build();

        PresignedPutObjectRequest presignedReq = presigner.presignPutObject(presignReq);
        return presignedReq.url().toString();
    }
}
