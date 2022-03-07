package com.cisco.josouthe.thales.api;

import com.cisco.josouthe.thales.api.data.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class APICalls {
	private static final Logger logger = LogManager.getFormatterLogger();

	private String baseURL, urlString, userName, userPassword;
	private AuthToken globalAuthToken;
	private int maxLimit = 256;
	private Gson gson;

	public APICalls( String url, String user, String pass) {
		this.urlString=url;
		this.userName=user;
		this.userPassword=pass;
		if( url.endsWith("/") ) {
			this.baseURL = url + "api/v1";
		} else {
			this.baseURL = url + "/api/v1";
		}
		this.gson = new GsonBuilder().setPrettyPrinting().create();
	}

	private OkHttpClient getTrustAllCertsClient() throws NoSuchAlgorithmException, KeyManagementException {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			// @Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
			}

			// @Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
			}

			// @Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}
		} };

		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

		OkHttpClient.Builder newBuilder = new OkHttpClient.Builder();
		newBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
		newBuilder.hostnameVerifier((hostname, session) -> true);
		return newBuilder.build();
	}

	private String getToken() throws IOException {
		if( globalAuthToken == null || globalAuthToken.isExpired() )
			generateToken();
		return globalAuthToken.jwt;
	}

	private String generateToken() throws IOException {
		OkHttpClient newClient = null;
		try {
			newClient = getTrustAllCertsClient();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		// OkHttpClient client = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType,
				"{\r\n \"grant_type\" : \"password\",\r\n \"duration\" : 300,\r\n \"username\" : \""+ userName+ "\",\r\n \"password\" : \""+ userPassword+"\"\r\n}");
		Request request = new Request.Builder().url(baseURL+"/auth/tokens").method("POST", body)
				.addHeader("Content-Type", "application/json").build();
		Response response = newClient.newCall(request).execute();
		String jsonData = response.body().string();
		logger.info("AuthToken JSON: %s",jsonData);
		AuthToken authToken = gson.fromJson(jsonData, AuthToken.class);
		if( authToken == null ) throw new IOException("Error getting auth token from "+ urlString);
		this.globalAuthToken = authToken;
		return authToken.jwt;
	}

	private String getRequest( String url ) throws IOException {
		return executeRequest( url, "GET", null);
	}
	private String executeRequest( String url, String method, RequestBody requestBody) throws IOException {
		String token = getToken();
		// System.out.println(token);
		String bearer = "Bearer " + token;
		// System.out.println(bearer);
		OkHttpClient client = null;
		try {
			client = getTrustAllCertsClient();
		} catch (KeyManagementException e) {
			throw new IOException("Key Management Exception: "+ e.getLocalizedMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("No Such Algorithm Exception: "+ e.getLocalizedMessage());
		}
		Request request = new Request.Builder().url( url )
				.method(method, requestBody)
				.addHeader("Authorization", bearer).build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	public ListTokens listTokens() throws IOException {
		ListTokens listTokens = listTokens(0,maxLimit);
		while( listTokens.hasMore() ) {
			listTokens.add( listTokens( listTokens.limit, maxLimit) );
		}
		return listTokens;
	}

	private ListTokens listTokens(int skip, int limit) throws IOException {

		String json = getRequest(String.format("%s/auth/tokens/?skip=%d&limit=%d",baseURL, skip, limit));
		return gson.fromJson(json, ListTokens.class);

	}

	public ListAlarms listAlarms() throws IOException {
		ListAlarms listAlarms = listAlarms(0, maxLimit);
		while( listAlarms.hasMore() ) {
			listAlarms.add( listAlarms( listAlarms.limit, maxLimit) );
		}
		return listAlarms;
	}

	private ListAlarms listAlarms(int skip, int limit) throws IOException {

		String json = getRequest(String.format("%s/system/alarms?skip=%d&limit=%d", baseURL, skip, limit) );
		return gson.fromJson(json, ListAlarms.class);
	}

	public ListClientCerts listClientsCerts() throws IOException {
		ListClientCerts listClientCerts = listClientsCerts(0, maxLimit);
		while( listClientCerts.hasMore() ) {
			listClientCerts.add( listClientsCerts( listClientCerts.limit, maxLimit));
		}
		return listClientCerts;
	}

	private ListClientCerts listClientsCerts(int skip, int limit) throws IOException {

		String json = getRequest(String.format("%s/client-management/clients/?skip=%d&limit=%d", baseURL, skip, limit ) );
		logger.info("List Client Certs JSON Response: '%s'", json );
		return gson.fromJson(json, ListClientCerts.class);
	}

	public ListClients listClients() throws IOException {
		ListClients listClients = listClients(0, maxLimit);
		while( listClients.hasMore() ) {
			listClients.add( listClients( listClients.limit, maxLimit));
		}
		return listClients;
	}

	private ListClients listClients(int skip, int limit) throws IOException {

		String json = getRequest(String.format("%s/transparent-encryption/clients/?skip=%d&limit=%d&sort=updatedAt",baseURL, skip, limit) );
		return gson.fromJson(json, ListClients.class);
	}

	public ListClientHealthReport listClientHealthReport() throws IOException {
		ListClientHealthReport listClientHealthReport = listClientHealthReport(0, maxLimit);
		while( listClientHealthReport.hasMore() ) {
			listClientHealthReport.add( listClientHealthReport( listClientHealthReport.limit, maxLimit));
		}
		return listClientHealthReport;
	}

	private ListClientHealthReport listClientHealthReport(int skip, int limit) throws IOException {

		String json = getRequest(String.format("%s/transparent-encryption/reports/clients/?sort=client_name&skip=%d&limit=%d",baseURL,skip,limit ));
		return gson.fromJson(json, ListClientHealthReport.class);
	}

	public ListClusterNodeHealth clusterNodes() throws IOException {

		String json = getRequest(baseURL + "/nodes");
		return gson.fromJson(json, ListClusterNodeHealth.class);
	}

	public String listClusters() throws IOException {

		String json = getRequest(baseURL + "/cluster");
		return json;
	}

	public ListConnections listConnections() throws IOException {
		ListConnections listConnections = listConnections(0, maxLimit);
		while (listConnections.hasMore() ) {
			listConnections.add( listConnections(listConnections.limit, maxLimit));
		}
		return listConnections;
	}

	private ListConnections listConnections(int skip, int limit) throws IOException {

		String json = getRequest(String.format("%s/connectionmgmt/connections?skip=%d&limit=%d&sort=updatedAt", baseURL, skip, limit) );
		return gson.fromJson(json, ListConnections.class);
	}
	
	public void run() {
		
		try {
			
			
			System.out.println("*************************************List Alarms****************************************");
			System.out.println(listAlarms());
			System.out.println("*************************************List Tokens****************************************");
			System.out.println(listTokens());
			System.out.println(
					"*************************************List Client Info****************************************");
			System.out.println(listClientsCerts());
			System.out.println("*************************************List Clients****************************************");
			System.out.println(listClients());
			System.out.println(
					"*************************************List Client Health Report****************************************");
			System.out.println(listClientHealthReport());
			System.out.println(
					"*************************************Cluster Node Health****************************************");
			System.out.println(clusterNodes());
			System.out.println(
					"*************************************List Connections****************************************");
			System.out.println(listConnections());
			System.out
					.println("*************************************List Clusters****************************************");
			System.out.println(listClusters());
			System.out.println("*************************************END****************************************");
			}catch(Exception e) {
				e.printStackTrace();
			}
		
	}
	

	public static void main(String[] args) throws IOException {
		System.out.println("Usage: <java command> http://hostname/ <user> <password>");
		APICalls apiCalls = new APICalls(args[0], args[1], args[2]);
		ListClientCerts listClientCerts = apiCalls.listClientsCerts();
		logger.info("Total Client Certs: %d", listClientCerts.total);
		for( ClientCertificateInfo clientCertificateInfo :  listClientCerts.resources ) {
			logger.info("Client Certificates|%s: %d",  clientCertificateInfo.name, clientCertificateInfo.daysUntilExpired() );
		}
		ListAlarms listAlarms = apiCalls.listAlarms();
		Map<String,Integer> alarmsMap = listAlarms.getActiveAlarmCountsBySeverity();
		for( String severity : alarmsMap.keySet() )
			logger.info("Alarms Active Severity %s: %d", severity, alarmsMap.get(severity) );
		ListTokens listTokens = apiCalls.listTokens();
		Map<String,Integer> tokensMap = listTokens.getTokensCountsByStatus();
		for( String state : tokensMap.keySet() )
			logger.info("Tokens %s: %d", state, tokensMap.get(state) );
	}

}
