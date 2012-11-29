package bandpage;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.*;
import java.security.*;
import java.math.*;
import java.io.InputStream;

public class FlickrAuth {
public static String apiKey = "7fa06608eb3d80111d00db826ab94b38";
public static String secretKey = "5f2fc03e9064d5f9";
public static String title=" Incredible Violet";
public static String photo = "Violet.jpg";
public static String photoFile = "Violet";
public static String description="This is photo description";
public static String methodGetFrob = "flickr.auth.getFrob";
public static String sig;
public static String frob;
public static String token;

public static void retrieveToken() throws Exception{
	
	sig=secretKey+"api_key"+apiKey+"method"+methodGetFrob;
	//System.out.println("api_sig is: "+ sig);
 
      String signature = MD5(sig);
      //System.out.println("MD5: "+signature);
      String request = "http://api.flickr.com/services/rest/?method=" + methodGetFrob + "&api_key=" + apiKey + "&api_sig=" + signature;

      //System.out.println("GET frob request: " + request);
      
      HttpClient client = new HttpClient();

      GetMethod method = new GetMethod(request);
   // Send GET request
		int statusCode = client.executeMethod(method);
		
		if (statusCode != HttpStatus.SC_OK) {
			System.err.println("Method failed: " + method.getStatusLine());
			throw new Exception("failed the GET request for frob");
		}
      InputStream rstream = method.getResponseBodyAsStream();

      Document response = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(rstream);

      String frob = null; 
    // Check if frob is in the response

      NodeList frobResponse = response.getElementsByTagName("frob");

      Node frobNode = frobResponse.item(0);

      if (frobNode != null) {
    	  frob = frobResponse.item(0).getTextContent();
    	  //System.out.println("Successfully retrieved frob: " + frob);
      } 
      else {
    	  //System.out.println("Failed to retrieved frob: " + frob + ": "+ response);
    	  throw new Exception("failed to retrieve frob");
      } 
      
      sig = secretKey + "api_key" + apiKey + "frob" + frob + "permswrite";
		signature = MD5(sig);
		request = "http://www.flickr.com/services/auth/?api_key=" + apiKey + "&perms=write&frob=" + frob + "&api_sig=" + signature;
		//System.out.println("Login request: " + request);
		 
		if(Bandpage.authenticate(request)){
			
      String methodGetToken = "flickr.auth.getToken";
		 sig = secretKey + "api_key" + apiKey + "frob" + frob + "method" + methodGetToken;
		 signature = MD5(sig);
		request = "http://api.flickr.com/services/rest/?method=" + methodGetToken + "&api_key=" + apiKey + "&frob=" + frob + "&api_sig=" + signature;
		//System.out.println("Token request: " + request);
			
		method = new GetMethod(request);
			
		// Send GET request
		statusCode = client.executeMethod(method);
		
		if (statusCode != HttpStatus.SC_OK) {
			System.err.println("Method failed: " + method.getStatusLine());
			throw new Exception("failed! the GET request for auth token");
		}	
		
		rstream = null;
		
		// Get the response body
		rstream = method.getResponseBodyAsStream();
		response = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(rstream);		
		
		// Check if token is in the response
		NodeList tokenResponse = response.getElementsByTagName("token");
		Node tokenNode = tokenResponse.item(0);
		if (tokenNode != null) {
			token = tokenNode.getTextContent();
			System.out.println("Successfully retrieved auth_stoken: " + token);
			//System.out.println("Auth token successfully received. Now upload a photo to flickr");
			
		} else {
			NodeList error = response.getElementsByTagName("err");
			// Get Flickr error code and msg
			String code = error.item(0).getAttributes().item(0).getTextContent();
			String msg = error.item(0).getAttributes().item(1).getTextContent();
			//System.out.println("Flickr request failed with error code " + code + ", " + msg);
			//System.out.println("Auth token not received. Fix this before uploading photo to flickr");
			throw new Exception("failed to retreive auth token");
			
		}
		}
		else {
			//System.out.println("failed to authenticate!");
			throw new Exception("failed to authorize api access");
		}
		
		
}


public static String upload() throws Exception{
	
	DefaultHttpClient httpclient = new DefaultHttpClient();
	sig = secretKey + "api_key" + apiKey + "auth_token" + token;
	String signature = MD5(sig);
	String photoid = "-1";
        	HttpPost httppost = new HttpPost("http://api.flickr.com/services/upload/");       
        
        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        reqEntity.addPart("api_key",new StringBody(apiKey)); 
        reqEntity.addPart("auth_token",new StringBody(token)); 
        reqEntity.addPart("api_sig",new StringBody(signature)); 

        FileBody bin = new FileBody(new File(photo), "image/jpeg");
        	reqEntity.addPart("photo", bin );
        	httppost.setEntity(reqEntity);
        	//System.out.println("executing request " + httppost.getRequestLine());
        	HttpResponse response = httpclient.execute(httppost);
        	HttpEntity resEntity = response.getEntity();
        	

        	if (resEntity != null) {
        		String page = EntityUtils.toString(resEntity);
        		//System.out.println("PAGE :" + page);
        		ByteArrayInputStream stream = new ByteArrayInputStream(page.getBytes());
        		Document pResp = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
        		NodeList postResponse = pResp.getElementsByTagName("photoid");
        		Node postNode = postResponse.item(0);
        		if (postNode != null) {
        			photoid = postNode.getTextContent();
        			//System.out.println("Successfully retrieved photoid: " + photoid);
        			return photoid;       	
        		}else{
        			throw new Exception("photo uploaded but cannot find photoid");
        		}
        	}
        	throw new Exception("failed to upload photo");
}

/**
 * Get the MD5 hash of a text string
 */
public static String MD5(String text)
{
	String md5Text = "";
	try {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		md5Text = new BigInteger(1, digest.digest((text).getBytes())).toString(16);
	} catch (Exception e) {
		System.out.println("Error in call to MD5");
	}
	
    if (md5Text.length() == 31) {
        md5Text = "0" + md5Text;
    }
	return md5Text;
}

}

