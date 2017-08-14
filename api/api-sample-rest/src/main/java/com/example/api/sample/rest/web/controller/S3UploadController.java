package com.example.api.sample.rest.web.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.common.file.core.S3FileHandler;
import com.example.common.file.core.S3GetFileDesc;
import com.example.common.file.core.S3PutFileDesc;

//@RestController
public class S3UploadController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private AmazonS3 s3;
	
	@Autowired
	private S3FileHandler s3FileHandler;

	@Autowired
	private AsyncTaskExecutor executor;

	@PostMapping(value="/s3upload")
	public DeferredResult<ResponseEntity<String>> handle(@RequestParam("headimg") Part headimg) {
		long timeout = 600000L;
		DeferredResult<ResponseEntity<String>> deferredResult = new DeferredResult<>( timeout );
		
		deferredResult.onCompletion( new Runnable(){
			@Override
			public void run() {
				logger.info("completion thread id is : {}, result:{}", Thread.currentThread().getId(), deferredResult.getResult() );
			}
		}); 
		
		deferredResult.onTimeout( new Runnable(){
			@Override
			public void run() {
				logger.info("timeout thread id is : {} ", Thread.currentThread().getId() );
				deferredResult.setResult( ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build() );
			}
		});
		
		executor.submit( new Runnable(){
			@Override
			public void run() {
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
				
				try {
					S3PutFileDesc s3PutFileDesc = new S3PutFileDesc(headimg.getInputStream(),headimg.getSize(),headimg.getSubmittedFileName(),headimg.getContentType());
					S3GetFileDesc s3GetFileDesc = s3FileHandler.putFile( s3PutFileDesc );
					deferredResult.setResult( ResponseEntity.ok().body(s3GetFileDesc.getKey()) );
				} catch (Exception e) {
					e.printStackTrace();
					deferredResult.setResult( ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() );
				}
			}
		});
		
		return deferredResult;
	}
	
	
	@GetMapping(value="/s3download")
	public DeferredResult<ResponseEntity<byte[]>> down(String key, HttpServletResponse resp) {
		long timeout = 600000L;
		DeferredResult<ResponseEntity<byte[]>> deferredResult = new DeferredResult<>( timeout );
		
		deferredResult.onCompletion( new Runnable(){
			@Override
			public void run() {
				logger.info("completion thread id is : {}, result:{}", Thread.currentThread().getId(), deferredResult.getResult() );
			}
		}); 
		
		deferredResult.onTimeout( new Runnable(){
			@Override
			public void run() {
				logger.info("timeout thread id is : {} ", Thread.currentThread().getId() );
				deferredResult.setResult( ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build() );
			}
		});
		
		executor.submit( new Runnable(){
			@Override
			public void run() {
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
				
				try {
					InputStream in = s3FileHandler.getInputStream(key);
					deferredResult.setResult( ResponseEntity.ok().header("content-type", "image/png").body(IOUtils.toByteArray(in)) );
				} catch (Exception e) {
					e.printStackTrace();
					deferredResult.setResult( ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() );
				}
			}
		});
		
		return deferredResult;
	}
	
	@PostMapping(value="/s3upload3", produces="application/json")
	public DeferredResult<ResponseEntity<String>> handle3() {
		long timeout = 600000L;
		DeferredResult<ResponseEntity<String>> deferredResult = new DeferredResult<>( timeout );
		
		deferredResult.onCompletion( new Runnable(){
			@Override
			public void run() {
				logger.info("completion thread id is : {}, result:{}", Thread.currentThread().getId(), deferredResult.getResult() );
			}
		}); 
		
		deferredResult.onTimeout( new Runnable(){
			@Override
			public void run() {
				logger.info("timeout thread id is : {} ", Thread.currentThread().getId() );
				deferredResult.setResult( ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("timeout") );
			}
		});
		
		executor.submit( new Runnable(){
			@Override
			public void run() {
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
				
				try {
					sendData();					
					deferredResult.setResult( ResponseEntity.ok().body("ok") );
				} catch (Exception e) {
					e.printStackTrace();
					deferredResult.setResult( ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error") );
				}
			}
		});
		
		return deferredResult;
	}
	
	class MyThread extends Thread {
		
		String bucketName;
		
		String key;
		int i;
		File f;
		
		CountDownLatch latch;
		
		public MyThread(String bucketName, String key, int i,final File f, final CountDownLatch latch) {
			this.bucketName = bucketName;
			this.key = key;
			this.i = i;
			this.f =f;
			this.latch = latch;
		}

		@Override
		public void run() {
			long startTime = System.currentTimeMillis();
			logger.info("Uploading a new object {} to S3 from a file\n", i);
			PutObjectResult r = s3.putObject(new PutObjectRequest(bucketName, key, f));
			logger.info( "put "+i+" : " +r.getContentMd5() + "times: " + (System.currentTimeMillis()-startTime) + " ms");
			latch.countDown();
		}
		
	}

	private void sendData() throws Exception {
		final int num = 100;
		CountDownLatch latch = new CountDownLatch( num);
		
		String bucketName = "dev-headimg";
		String keyPre = "keyName-4-";
		
		File f = createSampleFile();
		for ( int i=0; i<num; i++) {
			StringBuffer sb = new StringBuffer().append(keyPre+i);
			final String key = sb.reverse().toString();
			
			// new MyThread(bucketName,key,i,f,latch).start();
			executor.submit( new MyThread(bucketName,key,i,f,latch) );
		}
		
		try {
			latch.await(600, TimeUnit.SECONDS);
		}catch (InterruptedException e) {
            e.printStackTrace();
        }
		
		/*
		String bucketName = "my-first-s3-bucket-" + UUID.randomUUID();
        String key = "MyObjectKey";
        
        System.out.println("Creating bucket " + bucketName + "\n");
        s3.createBucket(bucketName);
        
        long startTime = System.currentTimeMillis();
        System.out.println("Uploading a new object to S3 from a file\n");
        PutObjectResult r = s3.putObject(new PutObjectRequest(bucketName, key, createSampleFile()));
        System.out.println( "put: " +r.getContentMd5() + "times: " + (System.currentTimeMillis()-startTime) + " ms");

        startTime = System.currentTimeMillis();
        System.out.println("Downloading an object");
        S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
        System.out.println("Content-Type: "  + object.getObjectMetadata().getContentType());
        displayTextInputStream(object.getObjectContent());
        System.out.println( "get: " + "times: " + (System.currentTimeMillis()-startTime) + " ms");

        startTime = System.currentTimeMillis();
        System.out.println("Deleting an object\n");
        s3.deleteObject(bucketName, key);
        System.out.println( "deleteObject: " + "times: " + (System.currentTimeMillis()-startTime) + " ms");

        startTime = System.currentTimeMillis();
        System.out.println("Deleting bucket " + bucketName + "\n");
        s3.deleteBucket(bucketName);
        System.out.println( "deleteBucket: " + "times: " + (System.currentTimeMillis()-startTime) + " ms");
        */
	}
	
	
	@PostMapping(value="/s3upload2", produces="application/json")
	public ResponseEntity<String> handle2() {

        String bucketName = "my-first-s3-bucket-" + UUID.randomUUID();
        String key = "MyObjectKey";

        System.out.println("===========================================");
        System.out.println("Getting Started with Amazon S3");
        System.out.println("===========================================\n");

        try {
            /*
             * Create a new S3 bucket - Amazon S3 bucket names are globally unique,
             * so once a bucket name has been taken by any user, you can't create
             * another bucket with that same name.
             *
             * You can optionally specify a location for your bucket if you want to
             * keep your data closer to your applications or users.
             */
            System.out.println("Creating bucket " + bucketName + "\n");
            s3.createBucket(bucketName);
            
//            // 1. Enable bucket for Amazon S3 Transfer Acceleration.
//            s3.setBucketAccelerateConfiguration(new
//            SetBucketAccelerateConfigurationRequest(bucketName,
//            new BucketAccelerateConfiguration(BucketAccelerateStatus.Enabled)));
//            
//            // 2. Get the acceleration status of the bucket.
//            String accelerateStatus = s3.getBucketAccelerateConfiguration(new
//            GetBucketAccelerateConfigurationRequest(bucketName)).getStatus();
//            System.out.println("Acceleration status = " + accelerateStatus);
            
            /*
             * List the buckets in your account
             */
            System.out.println("Listing buckets");
            for (Bucket bucket : s3.listBuckets()) {
                System.out.println(" - " + bucket.getName());
            }
            System.out.println();

            /*
             * Upload an object to your bucket - You can easily upload a file to
             * S3, or upload directly an InputStream if you know the length of
             * the data in the stream. You can also specify your own metadata
             * when uploading to S3, which allows you set a variety of options
             * like content-type and content-encoding, plus additional metadata
             * specific to your applications.
             */
            long startTime = System.currentTimeMillis();
            System.out.println("Uploading a new object to S3 from a file\n");
            PutObjectResult r = s3.putObject(new PutObjectRequest(bucketName, key, createSampleFile()));
            System.out.println( "put: " +r.getContentMd5() + "times: " + (System.currentTimeMillis()-startTime) + " ms");

            /*
             * Download an object - When you download an object, you get all of
             * the object's metadata and a stream from which to read the contents.
             * It's important to read the contents of the stream as quickly as
             * possibly since the data is streamed directly from Amazon S3 and your
             * network connection will remain open until you read all the data or
             * close the input stream.
             *
             * GetObjectRequest also supports several other options, including
             * conditional downloading of objects based on modification times,
             * ETags, and selectively downloading a range of an object.
             */
            startTime = System.currentTimeMillis();
            System.out.println("Downloading an object");
            S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
            System.out.println("Content-Type: "  + object.getObjectMetadata().getContentType());
            displayTextInputStream(object.getObjectContent());
            System.out.println( "get: " + "times: " + (System.currentTimeMillis()-startTime) + " ms");

            /*
             * List objects in your bucket by prefix - There are many options for
             * listing the objects in your bucket.  Keep in mind that buckets with
             * many objects might truncate their results when listing their objects,
             * so be sure to check if the returned object listing is truncated, and
             * use the AmazonS3.listNextBatchOfObjects(...) operation to retrieve
             * additional results.
             */
            System.out.println("Listing objects");
            ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                    .withBucketName(bucketName)
                    .withPrefix("My"));
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                System.out.println(" - " + objectSummary.getKey() + "  " +
                        "(size = " + objectSummary.getSize() + ")");
            }
            System.out.println();

            /*
             * Delete an object - Unless versioning has been turned on for your bucket,
             * there is no way to undelete an object, so use caution when deleting objects.
             */
            System.out.println("Deleting an object\n");
            s3.deleteObject(bucketName, key);

            /*
             * Delete a bucket - A bucket must be completely empty before it can be
             * deleted, so remember to delete any objects from your buckets before
             * you try to delete them.
             */
            System.out.println("Deleting bucket " + bucketName + "\n");
            s3.deleteBucket(bucketName);
            
    		return ResponseEntity.ok().body("ok");
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
 			return ResponseEntity.ok().body("error");
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
 			return ResponseEntity.ok().body("error");
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
 			return ResponseEntity.ok().body("error");
		}
	}
	
	private void displayTextInputStream(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;

            System.out.println("    " + line);
        }
        System.out.println();
    }

	@PostMapping(value="/s3upload1", produces="application/json")
	public ResponseEntity<String> handle1() {// @RequestPart("file1") Part file1 , @RequestPart("file2") Part file2 ) {
		
		AmazonS3 s3client = new AmazonS3Client();// new ProfileCredentialsProvider());  

        Region usEast1 = Region.getRegion(Regions.US_WEST_2);
        s3client.setRegion( usEast1 );
        
		String bucketName = "bucketName";
		String keyName = "keyName";
		
        try {
        	if ( logger.isDebugEnabled() ) {
        		logger.debug("Uploading a new object to S3 from a file\n");
        	}
        	

			//        	InputStream in = file1.getInputStream();
//        	ObjectMetadata meta = new ObjectMetadata();
//        	meta.setContentLength( file1.getSize() );
            PutObjectResult result = s3client.putObject( new PutObjectRequest(bucketName, keyName, createSampleFile()) );

            if ( logger.isDebugEnabled() ) {
    			logger.debug( "put file to s3, md5:{}, versionId:{}", result.getContentMd5(),result.getVersionId() );
    		}

    		return ResponseEntity.ok().body("ok");
         } catch (IOException e) {
 			e.printStackTrace();
 			return ResponseEntity.ok().body("error");
 		}
 		catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
            		"means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
 			return ResponseEntity.ok().body("error");
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
            		"means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
 			return ResponseEntity.ok().body("error");
        } 
	}
	
	private File createSampleFile() throws IOException {
        File file = File.createTempFile("aws-java-sdk-", ".txt");
        file.deleteOnExit();

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        
        for (int i=0;i<1000;i++) {
	        writer.write("abcdefghijklmnopqrstuvwxyz\n");
	        writer.write("01234567890112345678901234\n");
	        writer.write("!@#$%^&*()-=[]{};':',.<>/?\n");
	        writer.write("01234567890112345678901234\n");
	        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        }
        writer.close();

        return file;
    }
}
