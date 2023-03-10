// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package awssagemakerconnector.actions;

import java.util.Map;
import com.mendix.core.Core;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.MendixRuntimeException;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import awsauthentication.impl.AuthCredentialsProvider;
import awsauthentication.impl.CredentialsProvider;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.systemwideinterfaces.core.IMendixObjectMember;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClient;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointRequest;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointResponse;
import java.nio.charset.Charset;

public class SageMakerEndPoint_DBU_Initial extends CustomJavaAction<java.lang.Void>
{
	private IMendixObject __Credentials;
	private awsauthentication.proxies.Credentials Credentials;

	public SageMakerEndPoint_DBU_Initial(IContext context, IMendixObject Credentials)
	{
		super(context);
		this.__Credentials = Credentials;
	}

	@java.lang.Override
	public java.lang.Void executeAction() throws Exception
	{
		this.Credentials = this.__Credentials == null ? null : awsauthentication.proxies.Credentials.initialize(getContext(), __Credentials);

		// BEGIN USER CODE
        String endpointName = "jumpstart-dft-hf-eqa-distilbert-base-uncased";
        String payload = "[\"Paris is the capital of France\", \"What is the capital of France?\"]";
        String contentType = "application/list-text";
        Region region = Region.EU_WEST_1;
        
        ILogNode LOGGER = Core.getLogger("SageMaker");
        
		try
		{
	        
	        LOGGER.info("Creating client for Sagemaker");
	        
			CredentialsProvider credentialsProvider=AuthCredentialsProvider.getCredentialsProvider(getContext(), Credentials);
			AwsCredentialsProvider awsCredentialsProvider=credentialsProvider.getAwsCredentialsProvider();
	        
	        SageMakerRuntimeClient runtimeClient = SageMakerRuntimeClient.builder()
	            .region(region)
	            .credentialsProvider(awsCredentialsProvider)
	            .build();

	        InvokeEndpointRequest endpointRequest = InvokeEndpointRequest.builder()
	                .endpointName(endpointName)
	                .contentType(contentType)
	                .body(SdkBytes.fromString(payload, Charset.defaultCharset()))
	                .build();

	         InvokeEndpointResponse response = runtimeClient.invokeEndpoint(endpointRequest);
	         System.out.println(response.body().asString(Charset.defaultCharset()));
	         LOGGER.info(response.body().asString(Charset.defaultCharset()));
		}
		catch (SdkClientException e) 
		{
			LOGGER.error("Exception in Java Client Code, Failed"+e.getMessage());
			throw new MendixRuntimeException(e);
			
		} catch(SdkServiceException e)
		{
			LOGGER.error("Error Response from AWS Service, Failed"+e.getMessage());
			throw new MendixRuntimeException(e);
		}
		catch (Exception e) 
		{
			LOGGER.error("Exception while creating STS S3 Client "+e.getMessage());
			throw new MendixRuntimeException(e);
		}

		return null;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 * @return a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "SageMakerEndPoint_DBU_Initial";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}
