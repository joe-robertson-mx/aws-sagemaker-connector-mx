package awsauthentication.impl;

import java.time.Instant;

import com.mendix.core.CoreException;
import com.mendix.systemwideinterfaces.core.IContext;

import awsauthentication.proxies.Credentials;
import awsauthentication.proxies.SessionToken;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;

public class SessionCredentialsProvider extends CredentialsProvider {

	public SessionCredentialsProvider(IContext context, Credentials credentials) {
		super(context, credentials);
	}

	@Override
	public AwsCredentialsProvider getAwsCredentialsProvider() throws CoreException {
		SessionToken sessionToken = credentials.getCredentials_SessionToken();
		
		AwsSessionCredentials awsSessionCreds = AwsSessionCredentials.create(
				credentials.getAccessKeyId(), 
				credentials.getSecretAccessKey(),
				sessionToken.getToken());
		
		return software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create(awsSessionCreds);
	}

	@Override
	public Instant getExpiration() throws CoreException {
		return null;
	}

	
	
}
