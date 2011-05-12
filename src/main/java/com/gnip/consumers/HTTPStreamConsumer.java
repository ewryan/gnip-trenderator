package com.gnip.consumers;

import com.gnip.StreamHandler;
import com.gnip.StreamIterator;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPStreamConsumer implements IStreamConsumer {

    public void getStream(StreamHandler handler) throws Exception {
        String dataCollectorURL = "https://machine.gnip.com/data_collectors/1/track.json";
        String username = "username";
        String password = "********";

        CookieHandler.setDefault(new CookieManager());

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            connection = getConnection(dataCollectorURL, username, password);

            inputStream = connection.getInputStream();
            int responseCode = connection.getResponseCode();

            if (responseCode >= 200 && responseCode <= 299) {
                StreamIterator streamIterator = new StreamIterator(inputStream);

                while (streamIterator.hasNext()) {
                    handler.handleLine(streamIterator.next());
                }

            } else {
                handleNonSuccessResponse(connection);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                handleNonSuccessResponse(connection);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private static void handleNonSuccessResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        System.out.println("Non-success response: " + responseCode + " -- " + responseMessage);
    }

    private static HttpURLConnection getConnection(String urlString, String username, String password) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(1000 * 60 * 60);
        connection.setConnectTimeout(1000 * 10);

        connection.setRequestProperty("Authorization", createAuthHeader(username, password));

        return connection;
    }

    private static String createAuthHeader(String username, String password) throws UnsupportedEncodingException {
        String authToken = username + ":" + password;
        byte[] encodedBytes = Base64.encodeBase64(authToken.getBytes("UTF-8"));
        return new String(encodedBytes, "UTF-8");
    }

}
