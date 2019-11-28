package cloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.CreateImageRequest;
import com.amazonaws.services.ec2.model.CreateImageResult;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;

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
		while(true)
		{
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
			System.out.println(" 9. create image 10. terminate instance");

			System.out.println(" 99. quit ");
			System.out.println("------------------------------------------------------------");
			System.out.print("Enter an integer: ");
		
			number = menu.nextInt();
			switch(number) {
			case 1:
				listInstances();
				break;
			case 2:
				availableZones();
				break;
			case 3:
				startInstance();
				break;
			case 4:
				availableRegions();
				break;
			case 5:
				stopInstance();
				break;
			case 6:
				createInstance();
				break;
			case 7:
				rebootInstance();
				break;
			case 8:
				listImages();
				break;
			case 9:
				createImage();
				break;
			case 10:
				terminateInstance();
				break;
			case 99:
				break;
			}
			
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
	
	public static void availableZones() {
		DescribeAvailabilityZonesResult zones_response = ec2.describeAvailabilityZones();

		for(AvailabilityZone zone : zones_response.getAvailabilityZones()) {
		    System.out.printf(
		        "Found availability zone %s " +
		        "with status %s " +
		        "in region %s",
		        zone.getZoneName(),
		        zone.getState(),
		        zone.getRegionName());
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
	
	public static void availableRegions() {
		String result = null;
		DescribeRegionsResult regions_response = ec2.describeRegions();

		for(Region region : regions_response.getRegions()) {
		    System.out.printf(
		        "Found region %s " +
		        "with endpoint %s",
		        region.getRegionName(),
		        region.getEndpoint());
		}
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
	
	public static void createInstance() {
		String img = null;
		System.out.println("Enter ami id : ");

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			img = br.readLine();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		RunInstancesRequest run_request = new RunInstancesRequest()
			    .withImageId(img)
			    .withInstanceType(InstanceType.T2Micro)
			    .withMaxCount(1)
			    .withMinCount(1);

		RunInstancesResult run_response = ec2.runInstances(run_request);

		String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();
	    
		System.out.printf(
	        "Successfully started EC2 instance %s based on AMI %s",
	            reservation_id, img);
	}
	
	public static void listImages() {
		//Preconditions.checkNotNull(credentialsAsStream, "File 'AwsCredentials.properties' NOT found in the classpath");
		DescribeImagesRequest request = new DescribeImagesRequest();
		
		request.withOwners("self");
		System.out.println("list of AMIs");
		Collection<Image> images = ec2.describeImages(request).getImages();
		for (Image tmp:images) {
	        String id = tmp.getImageId();
	        String name = tmp.getName();
	        String hyp = tmp.getHypervisor();
			System.out.println("Ami-id: "+id+" || Hypervisor: "+hyp+" || Name: "+name);
		}
	}
	
	public static void createImage() {
		Boolean noReboot = true; // set this to false if you want to shutdown the instance and tehn create an AMI.
		String instanceId = null;
		String aminame = null;
		System.out.println("enter the Instance-id for which you would like to create an AMI");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			instanceId = br.readLine();
			System.out.println(" instance entered is: "+instanceId);
			//instanceid.add(instanceId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Enter the name of the AMI");
		BufferedReader name = new BufferedReader(new InputStreamReader(System.in));
		try {
			aminame = name.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CreateImageRequest AMI = new CreateImageRequest()
					.withName(aminame)
					.withDescription("test creation using java api")
					//.withRootDeviceName("/dev/sda1")
					.withNoReboot(noReboot)
					.withInstanceId(instanceId);
		try{
		ec2.createImage(AMI);
		CreateImageResult Ami = new CreateImageResult();
		String Imgid = Ami.getImageId();
		int Hscode = Ami.hashCode();
		Imgid = Ami.getImageId();
		System.out.println("Ami is being created ");
		//System.out.println("Last line "+Ami.getImageId()+"hashcode "+Hscode);
		}catch(Exception a){
			System.out.println("Wrong input");
		}
		System.out.println("Successfully AMI is created.");
	}
	
	public static void terminateInstance() {
		TerminateInstancesRequest tir = new TerminateInstancesRequest();
		String instanceId = null;
		ArrayList<String> instanceIds = new ArrayList<>();

		System.out.println("Enter the instance id to be terminated (Ex:i-3c8b3908)");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			instanceId = br.readLine();
			System.out.println(" instance entered is: "+instanceId);
			try{
				instanceIds.add(instanceId);
			}catch (Exception e){
				System.out.println("you have entered a wrong instance id");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
		tir.setInstanceIds(instanceIds);
		ec2.terminateInstances(tir); System.out.println("termianting reqstd.");
		System.out.println("The requested instance " + instanceId +" has been terminated");
		}catch (Exception a){
			System.out.println("You have entered a wrong instance-id");
		}
	}
}
