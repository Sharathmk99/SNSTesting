/**
 * Copyright 2014-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitosync.model.Platform;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformApplicationRequest;
import com.amazonaws.services.sns.model.CreatePlatformApplicationResult;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;

/**
 * Amazon Simple Notification Service
 */

public class AmazonSNS {

	/**
	 * WARNING: To avoid accidental leakage of your credentials, DO NOT keep the
	 * credentials file in your source directory.
	 */

	/**
	 * Specifying notification topic, reading email addresses to which
	 * notification is subscribed, Specifying message to be published, publish
	 * to a SNS topic.
	 * 
	 * @param args
	 */
	static AmazonSNSClient snsClient;

	public static void main(String[] args) {

		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);// creating scan object
		System.out.println("Enter the topic");// Enter topic for the
												// notification
		String topicName = scan.next();
		System.out.println("Enter the email id to subscribe");// Enter email id
																// to subscribe
																// for
																// notification
		String emailId = "APA91bE-yHEAxoi4E1J15zGmtfw0cUOEYCcnKTjeaOkuCnYGO0lzg11vQvoK4nD-g1F3NlObOP2FICKwYQ_JK5-QTVNsxwxb8K-h78Hsy5Zq-VhPpc7BAVdkudXJPIpYKHdVL3r9xZ9Zdrnjhynz3jFqwhdszquMFFl0eJ6c1qlxO5lP6irzu8o";
		System.out.println("Enter the message to publish");// enter message to
															// be published
		String message = scan.next();

		/*
		 * The ProfileCredentialsProvider will return your [default] credential
		 * profile by reading from the credentials file located at
		 * (/home/mtuser/.aws/credentials).
		 * 
		 * TransferManager manages a pool of threads, so we create a single
		 * instance and share it throughout our application.
		 */

		AWSCredentials credentials = new BasicAWSCredentials(
				"AKIAIW2LLL5G57AADUMA",
				"seLEwbDfxJwt1m9vxLboD2UdrMxSh1DhE4Ab0/Ar");

		// Instantiate an snsClient, which will make the service call with the
		// supplied AWS credentials.
		snsClient = new AmazonSNSClient(credentials);

		// Choose the AWS region of the Amazon SNS endpoint you want to connect
		// to. Note that your production
		// access status, sending limits, and Amazon SNS identity-related
		// settings are specific to a given
		// AWS region, so be sure to select an AWS region in which you set up
		// Amazon SNS. Here, we are using
		// the US East (N. Virginia) region. Examples of other regions that
		// Amazon SNS supports are US_WEST_2
		// and EU_WEST_1. For a complete list, see
		// http://docs.aws.amazon.com/sns/latest/DeveloperGuide/regions.html

		snsClient.setRegion(Region.getRegion(Regions.US_WEST_2));

		// create a new SNS topic
		CreateTopicRequest createTopicRequest = new CreateTopicRequest(
				topicName);
		CreateTopicResult createTopicResult = snsClient
				.createTopic(createTopicRequest);

		// print TopicArn
		System.out.println(createTopicResult);

		// get request id for CreateTopicRequest from SNS metadata
		System.out.println("CreateTopicRequest - "
				+ snsClient.getCachedResponseMetadata(createTopicRequest));

		String topicArn = createTopicResult.getTopicArn();

		System.out.println(createTopicResult.getTopicArn());
		String serverAPIKey = "AIzaSyBVXfX5PAEuqNljoDLpCBk8ToNfiYDKkd0";
		CreatePlatformApplicationResult applicationResult = createPlatformApplication(
				"SNSTesting", Platform.GCM, "", serverAPIKey);
		// subscribe to an SNS topic
		System.out.println(applicationResult.getPlatformApplicationArn());
		SubscribeRequest subRequest = new SubscribeRequest(topicArn,
				"application", applicationResult.getPlatformApplicationArn());
		snsClient.subscribe(subRequest);

		// get request id for SubscribeRequest from SNS metadata
		System.out.println("SubscribeRequest - "
				+ snsClient.getCachedResponseMetadata(subRequest));
		System.out.println("Check your email and confirm subscription.");

		// publish to a SNS topic
		String msg = message;
		PublishRequest publishRequest = new PublishRequest(topicArn, msg);
		PublishResult publishResult = snsClient.publish(publishRequest);

		// print MessageId of message published to SNS topic
		System.out.println("MessageId - " + publishResult.getMessageId());

	}

	private static CreatePlatformApplicationResult createPlatformApplication(
			String applicationName, Platform platform, String principal,
			String credential) {
		CreatePlatformApplicationRequest platformApplicationRequest = new CreatePlatformApplicationRequest();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("PlatformPrincipal", principal);
		attributes.put("PlatformCredential", credential);
		platformApplicationRequest.setAttributes(attributes);
		platformApplicationRequest.setName(applicationName);
		platformApplicationRequest.setPlatform(platform.name());
		return snsClient.createPlatformApplication(platformApplicationRequest);
	}
}