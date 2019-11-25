package cloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;

public class cloud {
	/*
	* Cloud Computing, Data Computing Laboratory
	* Department of Computer Science
	* Chungbuk National University
	*/
	static AmazonEC2 ec2;
	private static void init() throws Exception {
	/*
	* The ProfileCredentialsProvider will return your [default]
	* credential profile by reading from the credentials file located at
	* (~/.aws/credentials).
	*/
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
		throw new AmazonClientException(
				
			"Cannot load the credentials from the credential profiles file. " +
			"Please make sure that your credentials file is at the correct " +
			"location (~/.aws/credentials), and is in valid format.",e);
		}
		ec2 = AmazonEC2ClientBuilder.standard()
		.withCredentials(credentialsProvider)
		.withRegion("us-east-1") 
		.build();
		
	}

	public static void main(String[] args) throws Exception {
		init();
		Scanner menu = new Scanner(System.in);
		Scanner id_string = new Scanner(System.in);
		int number = 0;
		//while(true)
		//{
			System.out.println(" ");
			System.out.println(" ");
			System.out.println("------------------------------------------------------------");
			System.out.println(" Amazon AWS Control Panel using SDK ");
			System.out.println(" ");
			System.out.println(" Cloud Computing, Computer Science Department ");
			System.out.println(" at Chungbuk National University ");
			System.out.println("------------------------------------------------------------");
			System.out.println(" 1. list instance 2. available zones ");
			System.out.println(" 3. start instance 4. available regions ");
			System.out.println(" 5. stop instance 6. create instance ");
			System.out.println(" 7. reboot instance 8. list images ");
			System.out.println(" 99. quit ");
			System.out.println("------------------------------------------------------------");
			System.out.print("Enter an integer: ");
		//}
			number = menu.nextInt();
			switch(number) {
			case 1:
				listInstances();
				break;
			case 3:
				startInstance();
			case 5:
				stopInstance();
			case 7:
				rebootInstance();
			}
	}
	
	public static void listInstances()
	{
		System.out.println("Listing instances....");
		boolean done = false;
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		while(!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);
			for(Reservation reservation : response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					System.out.printf(
					"[id] %s, " +
					"[AMI] %s, " +
					"[type] %s, " +
					"[state] %10s, " +
					"[monitoring state] %s",
					instance.getInstanceId(),
					instance.getImageId(),
					instance.getInstanceType(),
					instance.getState().getName(),
					instance.getMonitoring().getState());
				}
				System.out.println();
				}
				request.setNextToken(response.getNextToken());
				if(response.getNextToken() == null) {
					done = true;
			}
		}
	}
	
	public static void startInstance() {
		
		String result = null;
		StartInstancesRequest startRequest = new StartInstancesRequest();
        ArrayList<String> instanceId = new ArrayList<>();
        System.out.println("Enter insatnce id : ");

        try {
	        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        String instance;
	        instance = br.readLine();
			instanceId.add(instance);
        }catch(IOException e) {
        	System.out.println("wrong input");
        }
        
		startRequest.setInstanceIds(instanceId);
		StartInstancesResult sirs = ec2.startInstances(startRequest);
    	System.out.println("Starting... "+result);

    	result = sirs.getStartingInstances().get(0).getCurrentState().getName();
    	System.out.println("Successfully started instancen "+result);

	}
	
	public static void stopInstance() {
        String result = null;
		ArrayList<String> instanceId = new ArrayList<>();
		StopInstancesRequest stopRequest = new StopInstancesRequest();
		System.out.println("Enter insatnce id : ");

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        String instance;
	        instance = br.readLine();
			instanceId.add(instance);
		} catch(IOException e){
			e.printStackTrace();
		}
		
		stopRequest.setInstanceIds(instanceId);
        StopInstancesResult sirs = ec2.stopInstances(stopRequest);

        result=sirs.getStoppingInstances().get(0).getCurrentState().getName() ;

    	System.out.println("Successfully stop instance "+result);
	}
	
	
	public static void rebootInstance() {
        String result = null;
        String instance= null;
		ArrayList<String> instanceId = new ArrayList<>();
		RebootInstancesRequest rebootRequest = new RebootInstancesRequest();
		System.out.println("Enter insatnce id : ");

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        instance = br.readLine();
			instanceId.add(instance);
		} catch(IOException e){
			e.printStackTrace();
		}
		
		rebootRequest.setInstanceIds(instanceId);
		rebootRequest.withInstanceIds(instanceId);
		rebootRequest.withInstanceIds(instance);
    	System.out.println("Rebooting... " + instance);
		ec2.rebootInstances(rebootRequest);
    	System.out.println("Successfully rebooted instance " + instance);
	}
}
