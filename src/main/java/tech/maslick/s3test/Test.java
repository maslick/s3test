package tech.maslick.s3test;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class Test {
    public static void main(String[] args) {
        S3Client s3 = S3Client.builder().region(Region.EU_CENTRAL_1).build();

        s3.listBuckets().buckets().forEach(bucket -> {
            System.out.println("Bucket: " + bucket.name());
        });
    }
}
