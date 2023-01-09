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

public class SageMakerEndPoint_DBU_Paramaterise_Simple extends CustomJavaAction<java.lang.String>
{
	private IMendixObject __Credentials;
	private awsauthentication.proxies.Credentials Credentials;
	private java.lang.String Endpoint;
	private java.lang.String RegionStr;
	private java.lang.String Statement;
	private java.lang.String Question;

	public SageMakerEndPoint_DBU_Paramaterise_Simple(IContext context, IMendixObject Credentials, java.lang.String Endpoint, java.lang.String RegionStr, java.lang.String Statement, java.lang.String Question)
	{
		super(context);
		this.__Credentials = Credentials;
		this.Endpoint = Endpoint;
		this.RegionStr = RegionStr;
		this.Statement = Statement;
		this.Question = Question;
	}

	@java.lang.Override
	public java.lang.String executeAction() throws Exception
	{
		this.Credentials = this.__Credentials == null ? null : awsauthentication.proxies.Credentials.initialize(getContext(), __Credentials);

		// BEGIN USER CODE
		Region region = Region.of(RegionStr);
		String payload = buildPayload(Statement, Question);
        String contentType = "application/list-text";
        
        ILogNode LOGGER = Core.getLogger("SageMaker");
        LOGGER.info(payload);
        
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
	                .endpointName(Endpoint)
	                .contentType(contentType)
	                .body(SdkBytes.fromString(payload, Charset.defaultCharset()))
	                .build();

	         InvokeEndpointResponse response = runtimeClient.invokeEndpoint(endpointRequest);
	         String jsonStrResponse = response.body().asString(Charset.defaultCharset());
	         LOGGER.info(jsonStrResponse);
	         return jsonStrResponse;
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
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 * @return a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "SageMakerEndPoint_DBU_Paramaterise_Simple";
	}

	// BEGIN EXTRA CODE
	private static String buildPayload (String statement, String question) {
		String[] qaArray = {question, statement};
		StringBuilder sb = new StringBuilder();
		sb.append("[\"");
		for (int i = 0; i < qaArray.length; i++) {
		    sb.append(qaArray[i]);
		    if (i < qaArray.length - 1) {
		        sb.append("\", \"");
		    }
		}
		sb.append("\"]");
		String result = sb.toString();
		return result;
	}
	
	// END EXTRA CODE
}
