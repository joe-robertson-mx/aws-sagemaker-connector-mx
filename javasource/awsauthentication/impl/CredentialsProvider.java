package awsauthentication.impl;

import java.time.Instant;

import com.mendix.core.CoreException;
import com.mendix.systemwideinterfaces.core.IContext;

import awsauthentication.proxies.Credentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

public abstract class CredentialsProvider {
	protected Credentials credentials;
	protected IContext context;
	
	public CredentialsProvider(IContext context, Credentials credentials) {
		this.credentials = credentials;
		this.context = context;
	}
	
	public abstract AwsCredentialsProvider getAwsCredentialsProvider() throws CoreException;
	public abstract Instant getExpiration() throws CoreException;
}
