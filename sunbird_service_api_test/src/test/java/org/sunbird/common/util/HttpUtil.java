/**
 * 
 */
package org.sunbird.common.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
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
	
	public static String doGetOperation(String url, Map<String, String> headers) {
		StringBuffer result = new StringBuffer();
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			HttpGet request = new HttpGet(url);
			request.addHeader("content-type", ConstantKeys.CONTENT_TYPE_APPLICATION_JSON);
			HttpResponse response = null;
			response = httpclient.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}
}
