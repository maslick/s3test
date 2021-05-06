package tech.maslick.s3test;

import tech.maslick.s3test.auth.AWS4SignerBase;
import tech.maslick.s3test.auth.AWS4SignerForAuthorizationHeader;
import tech.maslick.s3test.util.BinaryUtils;
import tech.maslick.s3test.util.HttpUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;



/**
 * Sample code showing how to PUT objects to Amazon S3 with Signature V4
 * authorization
 */
public class PutS3ObjectSample {

    private static final String objectContent = "helloworld\n";
    
    /**
     * Uploads content to an Amazon S3 object in a single call using Signature V4 authorization.
     */
    public static void putS3Object(String bucketName, String key, String regionName, String awsAccessKey, String awsSecretKey) {
        System.out.println("************************************************");
        System.out.println("*        Executing sample 'PutS3Object'        *");
        System.out.println("************************************************");
        
        URL endpointUrl;
        try {
            endpointUrl = new URL("https://s3-" + regionName + ".amazonaws.com/" + bucketName + "/" + key);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to parse service endpoint: " + e.getMessage());
        }
        
        // precompute hash of the body content
        byte[] contentHash = AWS4SignerBase.hash(objectContent);
        String contentHashString = BinaryUtils.toHex(contentHash);
        
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-amz-content-sha256", contentHashString);
        headers.put("content-length", "" + objectContent.length());
        headers.put("x-amz-storage-class", "STANDARD");
        
        AWS4SignerForAuthorizationHeader signer = new AWS4SignerForAuthorizationHeader(endpointUrl, "PUT", "s3", regionName);
        String authorization = signer.computeSignature(headers, 
                                                       null, // no query parameters
                                                       contentHashString, 
                                                       awsAccessKey, 
                                                       awsSecretKey);
                
        // express authorization for this as a header
        headers.put("Authorization", authorization);
        
        // make the call to Amazon S3
        String response = HttpUtils.invokeHttpRequest(endpointUrl, "PUT", headers, objectContent);
        System.out.println("--------- Response content ---------");
        System.out.println(response);
        System.out.println("------------------------------------");
    }
}
