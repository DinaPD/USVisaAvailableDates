package base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

public class HttpClient {

    public static List<AvailableDay> getAvailableDates(String token, String ua) {

        System.out.println("Using token: " + token);
        System.out.println("Using UA: " + ua);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {

            HttpGet request = new HttpGet("https://ais.usvisa-info.com/en-ca/niv/schedule/44624449/appointment/days/94.json?appointments[expedite]=false");

            // add request headers
            request.addHeader("Cookie", token);
            request.addHeader("User-Agent", ua);
            request.addHeader("Host", "ais.usvisa-info.com");
            request.addHeader("X-Requested-With", "XMLHttpRequest");
            CloseableHttpResponse response = httpClient.execute(request);
            System.out.println("Response: " + response);
            try {

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    String result = EntityUtils.toString(response.getEntity());
                    System.out.println("The entity: '%s'".formatted(result));
                    final ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readValue(result, new TypeReference<List<AvailableDay>>() {
                    });
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
