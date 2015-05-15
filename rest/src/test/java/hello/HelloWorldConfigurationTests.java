/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hello;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.HttpClientBuilder;
import org.keycloak.constants.ServiceUrlConstants;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.util.JsonSerialization;
import org.keycloak.util.KeycloakUriBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Basic integration tests for service demo application.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = HelloWorldConfiguration.class)
@WebAppConfiguration
@IntegrationTest({ "server.port=0", "management.port=0" })
@DirtiesContext
public class HelloWorldConfigurationTests {

	@Value("${local.server.port}")
	private int port;

	@Value("${local.management.port}")
	private int mgt;



	@Test
	public void whenCallingHelloWorldWithoutToken_Unauthorized() throws Exception {
		ResponseEntity<Greeting> entity = new TestRestTemplate().getForEntity("http://localhost:" + this.port
				+ "/hello-world", Greeting.class);
		assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
	}
	
	
	
	@Test
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void whenCallingHelloWorldWithToken_Authorized_HelloUsername() throws Exception {
		AccessTokenResponse response = getToken();
		org.springframework.http.HttpEntity requestEntity = new org.springframework.http.HttpEntity(getHeaders(response.getToken()));
		ResponseEntity<Greeting> entity = new TestRestTemplate().exchange("http://localhost:" + this.port
				+ "/hello-world", HttpMethod.GET, requestEntity, Greeting.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		assertEquals("Hello, Stranger! Your id is: cdab7dab-817d-4005-bd52-0716eb770e75", entity.getBody().getContent());
	}
	
	

	/**
	 * Obtain a token on behalf of angular-product-app.
	 * Send credentials through direct access api:
	 * http://docs.jboss.org/keycloak/docs/1.2.0.CR1/userguide/html/direct-access-grants.html
	 * Make sure the realm has the Direct Grant API switch ON (it can be found on Settings/Login page!)
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private AccessTokenResponse getToken() throws ClientProtocolException, IOException {
		HttpClient client = new HttpClientBuilder().disableTrustManager().build();
		try {
			HttpPost post = new HttpPost(KeycloakUriBuilder.fromUri("http://localhost:8080/auth")
					.path(ServiceUrlConstants.TOKEN_SERVICE_DIRECT_GRANT_PATH).build("rest-demo"));
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair(OAuth2Constants.GRANT_TYPE, "password"));
			formparams.add(new BasicNameValuePair("username", "user"));
			formparams.add(new BasicNameValuePair("password", "pass"));
		
			//will obtain a token on behalf of angular-product-app
			formparams.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ID, "angular-product-app"));

		
			UrlEncodedFormEntity form = new UrlEncodedFormEntity(formparams, "UTF-8");
			post.setEntity(form);
			HttpResponse response = client.execute(post);
			int status = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			if (status != 200) {
				throw new IOException("Bad status: " + status);
			}
			if (entity == null) {
				throw new IOException("No Entity");
			}
			InputStream is = entity.getContent();
			try {
				AccessTokenResponse tokenResponse = JsonSerialization.readValue(is, AccessTokenResponse.class);
				return tokenResponse;
			} finally {
				try {
					is.close();
				} catch (IOException ignored) {
				}
			}
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	
	/**
	 * Obtain headers for Keycloack authentication.
	 * @param token
	 * @return
	 */
    HttpHeaders getHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + new String(token));
        return headers;
      }

}
