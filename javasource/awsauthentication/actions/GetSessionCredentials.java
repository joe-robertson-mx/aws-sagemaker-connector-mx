// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package awsauthentication.actions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.SimpleTimeZone;
import com.mendix.core.Core;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.thirdparty.org.json.JSONObject;
import com.mendix.webui.CustomJavaAction;
import awsauthentication.impl.Utils;
import awsauthentication.proxies.AssumeRoleRequest;
import awsauthentication.proxies.Credentials;
import awsauthentication.proxies.SigV4Request;
import awsauthentication.proxies.microflows.Microflows;

/**
 * 'Get Session Credentials' action is used to generate credentials for AWS call by assuming AWS role using IAM Roles Anywhere.
 * 
 * Input parameters:
 * >Region: AWS Region
 * >Role ARN: Arn of the AWS role to assume
 * >Profile ARN: Arn of the Profile created at IAM RolesAnywhere
 * >Trust Anchor ARN: Arn of the Trust Anchor created at IAM RolesAnywhere
 * >Client Certificate Identifier: Identifier mentioned (as Pin) in the Outgoing Certificates in Runtime tab of Mendix Cloud Environment.
 * >Duration: Duration for which the session token should be valid.
 * >Session Name: An identifier for the assumed role session.
 */
public class GetSessionCredentials extends CustomJavaAction<IMendixObject>
{
	private java.lang.String Region;
	private java.lang.String RoleARN;
	private java.lang.String ProfileARN;
	private java.lang.String TrustAnchorARN;
	private java.lang.String ClientCertificateID;
	private java.lang.Long Duration;
	private java.lang.String SessionName;

	public GetSessionCredentials(IContext context, java.lang.String Region, java.lang.String RoleARN, java.lang.String ProfileARN, java.lang.String TrustAnchorARN, java.lang.String ClientCertificateID, java.lang.Long Duration, java.lang.String SessionName)
	{
		super(context);
		this.Region = Region;
		this.RoleARN = RoleARN;
		this.ProfileARN = ProfileARN;
		this.TrustAnchorARN = TrustAnchorARN;
		this.ClientCertificateID = ClientCertificateID;
		this.Duration = Duration;
		this.SessionName = SessionName;
	}

	@java.lang.Override
	public IMendixObject executeAction() throws Exception
	{
		// BEGIN USER CODE
		
		// Input validation
		String validationError = "";
		if (Region == null || Region.isEmpty())
			validationError = validationError + "[Region cannot be empty]";
		if (RoleARN == null || RoleARN.isEmpty())
			validationError = validationError + "[RoleARN cannot be empty]";
		if (ProfileARN == null || ProfileARN.isEmpty())
			validationError = validationError + "[ProfileARN cannot be empty]";
		if (TrustAnchorARN == null || TrustAnchorARN.isEmpty()) 
			validationError = validationError + "[TrustAnchorARN cannot be empty]";
		if (ClientCertificateID == null || ClientCertificateID.isEmpty())
			validationError = validationError + "[ClientCertificateID cannot be empty]";
		if(!validationError.isEmpty()) 
			throw new Exception(validationError);
		
		// Create AssumeRoleRequest
        AssumeRoleRequest assumeRoleRequest = new AssumeRoleRequest(getContext());
        assumeRoleRequest.setProfileARN(ProfileARN);
        assumeRoleRequest.setRoleARN(RoleARN);
        assumeRoleRequest.setTrustAnchorARN(TrustAnchorARN);
		
		// Retrieve existing credentials
		
		Credentials existingCred = Microflows.retrieveSessionCredentials(getContext(), assumeRoleRequest);
		if(existingCred != null) {
			Utils.LOGGER.info("Using existing Session Credentials");
			return existingCred.getMendixObject();
		}
		
		
		// Call Assume Role, if exiting token not present
		Utils.LOGGER.info("Getting new Session Credentials by Assuming Role");
		
		String service = "rolesanywhere";
		String url_suffix = "amazonaws.com";
		String host = String.format("%s.%s.%s", service, Region, url_suffix);
		String endpoint = "https://" + host;
		String content_type = "application/json";
		
		String passPhrase = null;
		InputStream stream = null;
		try {
			// Get Client Certificate from Environment variables
			JSONObject cert = Utils.getClientCertificateDetails(ClientCertificateID);
			if(cert != null) {
				Utils.LOGGER.info("Certificate found from Enviroment variables using ClientCertificate Id :: "+ClientCertificateID);
				String pfxCert = cert.getString("pfx");
				passPhrase = cert.getString("password");
				// Load certificate into stream
				stream = new ByteArrayInputStream(Base64.getDecoder().decode(pfxCert.getBytes()));
			}else {
				// Running locally form studio-pro, certificate from configuration.
				Utils.LOGGER.warn("Certificate not found from Enviroment variables, searching in runtime configuration");
				int certIndex = Integer.valueOf(ClientCertificateID) - 1;
				Utils.LOGGER.info("Searching certificate at index :: "+ClientCertificateID);
				stream = Core.getConfiguration().getClientCertificates().get(certIndex);
				passPhrase = Core.getConfiguration().getClientCertificatePasswords().get(certIndex);
			}
		}
		catch(Exception e) {
			Utils.LOGGER.error("Failed while fetching Client Certificate");
			throw e;
		}
		
		// Only pfx format is supported.
        KeyStore store = KeyStore.getInstance("PKCS12");
        store.load(stream, passPhrase.toCharArray());
        stream.close();
        String alias = store.aliases().nextElement();
        PrivateKey private_key =  (PrivateKey)store.getKey(alias, passPhrase.toCharArray());	   
        
        // Get the encryption algorithm
        String keyAlgorithm = private_key.getAlgorithm();
        String algorithmName = Utils.getAlgorithmName(keyAlgorithm);
        String algorithmHeader = Utils.getAlgorithmHeaderString(keyAlgorithm);

        // Load public certificate
        X509Certificate public_certificate = (X509Certificate)store.getCertificate(alias);
        String amz_x509 = new String(Base64.getEncoder().encode(public_certificate.getEncoded()));
        
        // Public certificate serial number in decimal
        BigInteger serial_number_dec = public_certificate.getSerialNumber();

        // Request parameters for CreateSession--passed in a JSON block.
        JSONObject requestJson = new JSONObject();
        requestJson.put("durationSeconds", Duration);
        requestJson.put("profileArn", ProfileARN);
        requestJson.put("roleArn", RoleARN);
        requestJson.put("sessionName", SessionName);
        requestJson.put("trustAnchorArn", TrustAnchorARN);
        String request_parameters = requestJson.toString();
        Utils.LOGGER.debug("RolesAnywhere Call Request Body ["+request_parameters+"]");
        
        // Create a date for headers and the credential string
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        dateTimeFormat.setTimeZone(new SimpleTimeZone(0, "UTC"));
        SimpleDateFormat dateStampFormat = new SimpleDateFormat("yyyyMMdd");
        dateStampFormat.setTimeZone(new SimpleTimeZone(0, "UTC"));
        Date t = new Date();
        String amz_date = dateTimeFormat.format(t);
        String date_stamp = dateStampFormat.format(t);

	        
        // ************* TASK 1: CREATE A CANONICAL REQUEST *************
        // http://docs.aws.amazon.com/general/latest/gr/sigv4-create-canonical-request.html

        // Step 1 is to define the verb (GET, POST, etc.).
	    String method = "POST";

        // Step 2: Create canonical URI--the part of the URI from domain to query string (use '/' if no path)
        String canonical_uri = "/sessions";

        // Step 3: Create the canonical query string. In this example, request parameters are passed in the body of the request and the query string is blank.
        String canonical_querystring = "";

        // Step 4: Create the canonical headers. Header names must be trimmed and lowercase, and sorted in code point order from low to high.
        // Note that there is a trailing \n.
        String canonical_headers = String.format("%s\n%s\n%s\n%s\n",
                "content-type:" + content_type,
                "host:" + host,
                "x-amz-date:" + amz_date,
                "x-amz-x509:" + amz_x509);

        // Step 5: Create the list of signed headers. This lists the headers in the canonical_headers list, delimited with ";" and in alpha order.
        // Note: The request can include any headers; canonical_headers and signed_headers include those that you want to be included in the hash of the request. "Host" and "x-amz-date" are always required.
        // For Roles Anywhere, content-type and x-amz-x509 are also required.
        String signed_headers = "content-type;host;x-amz-date;x-amz-x509";

        // Step 6: Create payload hash. In this example, the payload (body of the request) contains the request parameters.
        String payload_hash = toHex(hash(request_parameters));

        // Step 7: Combine elements to create canonical request
        String canonical_request = String.format("%s\n%s\n%s\n%s\n%s\n%s",
                method,
                canonical_uri,
                canonical_querystring,
                canonical_headers,
                signed_headers,
                payload_hash);


        // ************* TASK 2: CREATE THE STRING TO SIGN*************
        // Match the algorithm to the hashing algorithm you use, SHA-256
        String credential_scope = String.format("%s/%s/%s/aws4_request",
                date_stamp,
                Region,
                service);
        String string_to_sign = String.format("%s\n%s\n%s\n%s",
        		algorithmHeader,
                amz_date,
                credential_scope,
                toHex(hash(canonical_request)));

        byte[] signature = sign(string_to_sign, private_key, algorithmName);
        String signature_hex = toHex(signature);

        // ************* TASK 4: ADD SIGNING INFORMATION TO THE REQUEST *************
        // Put the signature information in a header named Authorization.
        String authorization_header = String.format(algorithmHeader+" Credential=%s/%s, SignedHeaders=%s, Signature=%s",
                serial_number_dec,
                credential_scope,
                signed_headers,
                signature_hex);

        // Create RestCallRequest object
        SigV4Request sigV4Request = new SigV4Request(getContext());
        sigV4Request.setUrl(endpoint + canonical_uri);
        sigV4Request.setHeaderContentType(content_type);
        sigV4Request.setHeaderHost(host);
        sigV4Request.setHeaderXAmzDate(amz_date);
        sigV4Request.setHeaderXAmzX509(amz_x509);
        sigV4Request.setHeaderAuthorization(authorization_header);
        sigV4Request.setRequestBody(request_parameters);
        
        // Assume Role Call
        String error = Microflows.consumeRolesAnywhere(getContext(), sigV4Request, assumeRoleRequest);
        
        if(error == null) {
        	// Retrieve new generated Session Credentials 
        	Credentials newCred = Microflows.retrieveSessionCredentials(getContext(), assumeRoleRequest);
        	Utils.LOGGER.debug("Assume Role call successful");
        	return newCred.getMendixObject();
        }
        else {
        	Utils.LOGGER.error("Error while assume role call: "+error);
        	throw new Exception(error);
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
		return "GetSessionCredentials";
	}

	// BEGIN EXTRA CODE
	private static byte[] hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes());
            return md.digest();
        } catch (Exception e) {
            throw new RuntimeException("Unable to compute hash while signing request: " + e.getMessage(), e);
        }
    }

    private static byte[] sign(String stringData, PrivateKey key, String algorithmName) {
        try {
            Signature sig = Signature.getInstance(algorithmName);
            sig.initSign(key);
            sig.update(stringData.getBytes());
            return sig.sign();
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            throw new RuntimeException("Unable to compute hash while signing request: " + e.getMessage(), e);
        }
    }

    private static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte datum : data) {
            String hex = Integer.toHexString(datum);
            if (hex.length() == 1) {
                sb.append("0");
            } else if (hex.length() == 8) {
                hex = hex.substring(6);
            }
            sb.append(hex);
        }
        return sb.toString().toLowerCase();
    }
	// END EXTRA CODE
}