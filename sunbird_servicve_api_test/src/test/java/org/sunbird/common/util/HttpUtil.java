/**
 * 
 */
package org.sunbird.common.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @author Manzarul
 *
 */
public class HttpUtil {
	
	/**
	 * This method is written for deleting test data from elastic search.
	 * @param url String complete url including the id of the element need to be deleted.
	 * @return boolean true if deleted else false;
	 */
	public static boolean doDeleteOperation (String url) {
		 boolean deleteResponse = true;
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpDelete httpDelete = new HttpDelete(url);
            System.out.println("Executing request " + httpDelete.getRequestLine());
            // Create a custom response handler
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            String responseBody = httpclient.execute(httpDelete, responseHandler);
            System.out.println(responseBody);
        } catch (Exception e) {
        	deleteResponse = false;
        	e.printStackTrace();
		}
		return deleteResponse;
	}

}
