package com.example.common.file.config;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.common.file.core.S3FileHandler;
import com.example.common.file.core.S3FileHandlerProvider;

@Configuration
public class AppConfig {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Setter @Getter
	private String accessKeyId = "AKIAJ2BH54LS736TA6SA";
	
	@Setter @Getter
	private String secretKeyId = "QNCQB6dJXtSpvnmhl7ejccxPQz4QJ84DwrIbs+Ss";

	@Bean
	public AmazonS3 amazonS3() {
		
		if ( logger.isInfoEnabled() ) {
			logger.info( "accessKeyId:{}, secretKeyId:{}", accessKeyId, secretKeyId );
		}
		
		// FIXME 此处待优化
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKeyId, secretKeyId);
		AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
            .withRegion( Regions.US_EAST_2 ) // 必须设置，固定下来了就不能设置
//            .withAccelerateModeEnabled( true ) 
            .build();
		
		return s3;
	}
	
	@Bean
	@Qualifier("s3Executor")
	public AsyncTaskExecutor asyncTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setDaemon( true ); // 待定
		executor.setMaxPoolSize( 200 );
		executor.setCorePoolSize( 100 );
		executor.setQueueCapacity( 1000000000 );
		executor.setThreadGroup( new ThreadGroup("s3-executor") );
		executor.setWaitForTasksToCompleteOnShutdown( true );	
		return executor;
	}
	
	// 不能放在一起
//	@Autowired
//	@Qualifier("s3Executor")
//	private AsyncTaskExecutor executor;
//	
//	@Autowired
//	private AmazonS3 amazonS3;
	
	@Bean
	public S3FileHandler s3FileHandler(AmazonS3 amazonS3, AsyncTaskExecutor executor) {
		S3FileHandlerProvider s3FileHandler = new S3FileHandlerProvider( amazonS3, executor );
		s3FileHandler.setDefaultBucketName( "dev-headimg" );
		s3FileHandler.setKeyPrefix( "file" );
		s3FileHandler.setUploadTimeout( 3000 );
		return s3FileHandler;
	}
	
}
