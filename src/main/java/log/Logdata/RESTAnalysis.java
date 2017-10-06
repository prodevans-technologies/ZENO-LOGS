package log.Logdata;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;

public class RESTAnalysis {
     
     private RestTemplate restTemplate;
    private HttpEntity<String> entity;

    final static private Logger logger = LoggerFactory.getLogger(RESTAnalysis.class);

    public RESTAnalysis(){
          try {
            /*
			 * Create a trust manager that does not validate certificate chains. It required
			 * for the generating the SSL certificate. If not provided then while connection
			 * it throws SSL certification error.
             */
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            logger.info("Cretificate installation is succesful.");

        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            logger.error(e.getMessage());
        }
     }
    
    public String getAPI()
    {
        
        /*
		 * Creation of the response object with the string data type. Because it return
		 * the JSON.
         */
        ResponseEntity<String> person;

        /*
		 * Headers for the response type if we want to return JSON response then we
		 * require to add.
         */
        HttpHeaders headers = new HttpHeaders();

        /*
		 * Adding of the response header with application/json type
         */
        headers.add("Accept", "application/json");

        /*
		 * Creation of the Entity object for the adding the headers into request.
         */
        entity = new HttpEntity<>(headers);

        /*
		 * Creation of REST TEMPLET object for the executing of the REST calls.
         */
        restTemplate = new RestTemplate();

        /*
		 * Adding the basic type of authentication on the REST TEMPLETE.
         */
        restTemplate.getInterceptors()
                .add(new BasicAuthorizationInterceptor("Administrator", "Oneeight@admin18"));

        /*
		 * Execution of the REST call with basic authentication and JSON response type
         */
       person = restTemplate.exchange("http://52.172.222.197:8080/versa/login?username=NED_Ops&password=@WSX3edc4rfv", HttpMethod.POST, entity, String.class);
       // System.out.println(""+person.toString());
        //headers.add("Cookie", "JSESSIONID=0FC37952D64B545C46969EFEC0E4FD12");
        headers.add("Cookie", person.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
        entity = new HttpEntity<>(headers);
        person = restTemplate.exchange("http://52.172.222.197:8080/versa/analytics/v1.0.0/data/provider/tenants/OneEight/features/CGNAT/?&start-date=15daysAgo&end-date=today&q=log&count=10&qt=table", HttpMethod.GET, entity, String.class);

        /*
		 * Returning the response body with string format that easily readable.
         */
        return person.getBody();
    }
    
    public static void main(String[] args) {
        System.out.println("DATA" + new RESTAnalysis().getAPI());
    }
 
}

