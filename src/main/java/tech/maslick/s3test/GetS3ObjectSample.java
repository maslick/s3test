package tech.maslick.s3test;

import tech.maslick.s3test.auth.AWS4SignerBase;
import tech.maslick.s3test.auth.AWS4SignerForAuthorizationHeader;
import tech.maslick.s3test.util.HttpUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Samples showing how to GET an object from Amazon S3 using Signature V4
 * authorization.
 */
public class GetS3ObjectSample {
    
    /**
     * Request the content of the object from the given
     * bucket in the given region using virtual hosted-style object addressing.
     */
    public static void getS3Object(String bucketName, String key, String regionName, String awsAccessKey, String awsSecretKey) {
        System.out.println("*******************************************************");
        System.out.println("*  Executing sample 'GetObjectUsingHostedAddressing'  *");
        System.out.println("*******************************************************");
        
        // the region-specific endpoint to the target object expressed in path style
        URL endpointUrl;
        try {
            endpointUrl = new URL("https://" + bucketName + ".s3.amazonaws.com/" + key);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to parse service endpoint: " + e.getMessage());
        }
        
        // for a simple GET, we have no body so supply the precomputed 'empty' hash
        Map<String, String> headers = new HashMap<>();
        headers.put("x-amz-content-sha256", AWS4SignerBase.EMPTY_BODY_SHA256);
        
        AWS4SignerForAuthorizationHeader signer = new AWS4SignerForAuthorizationHeader(endpointUrl, "GET", "s3", regionName);
        String authorization = signer.computeSignature(headers, 
                                                       null, // no query parameters
                                                       AWS4SignerBase.EMPTY_BODY_SHA256, 
                                                       awsAccessKey, 
                                                       awsSecretKey);
                
        // place the computed signature into a formatted 'Authorization' header and call S3
        headers.put("Authorization", authorization);
        String response = HttpUtils.invokeHttpRequest(endpointUrl, "GET", headers, null);
        System.out.println("--------- Response content ---------");
        System.out.println(response);
        System.out.println("------------------------------------");
    }
}
