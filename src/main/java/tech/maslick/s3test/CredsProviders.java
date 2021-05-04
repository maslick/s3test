package tech.maslick.s3test;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

public class CredsProviders {
    public static AwsCredentialsProvider profileCredsProvider() {
        return DefaultCredentialsProvider.builder()
                .profileName("axfood-dev")
                .build();
    }

    public static AwsCredentialsProvider staticCredsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(
                "*****",
                "*****"
        ));
    }

    public static AwsCredentialsProvider envVarsCredsProvider() {
        String aws_access_key_id = System.getenv("AWS_ACCESS_KEY_ID");
        String aws_secret_access_key = System.getenv("AWS_SECRET_ACCESS_KEY");

        return StaticCredentialsProvider.create(AwsBasicCredentials.create(
                aws_access_key_id,
                aws_secret_access_key
        ));
    }
}
