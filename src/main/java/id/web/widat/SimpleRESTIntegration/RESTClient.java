package id.web.widat.SimpleRESTIntegration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import id.web.widat.SimpleRESTIntegration.constants.Method;
import id.web.widat.SimpleRESTIntegration.constants.Protocol;
import id.web.widat.SimpleRESTIntegration.model.Response;

public class RESTClient {

	private static class DefaultTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

	}

	private static SSLContext disabledSSL() {

		try {

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());

			return sslContext;

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;

	}

	private static String getQuery(Map<String, String> params) {

		StringBuilder result = new StringBuilder();
		boolean first = false;

		try {

			Set<String> keys = params.keySet();

			for (String key : keys) {

				if (!first) {
					first = true;
				} else {
					result.append("&");
				}

				result.append(URLEncoder.encode(key, "UTF-8"));
				result.append("=");
				result.append(URLEncoder.encode(params.get(key), "UTF-8"));

			}

		} catch (Exception e) {

			System.out.println(e.getMessage());

		}

		return result.toString();

	}

	@SuppressWarnings("unchecked")
	public static Response pull(String protocol, String address, Object data, String method,
			Map<String, String> property) {

		HttpURLConnection httpURLConnection = null;
		HttpsURLConnection httpsURLConnection = null;
		BufferedReader bufferedReader = null;

		OutputStream outputStream = null;

		Response response = new Response();

		try {

			if (protocol.equalsIgnoreCase(Protocol.HTTPS)) {

				SSLContext.setDefault(disabledSSL());

			}

			URL url = new URL(address);

			if (protocol.equalsIgnoreCase(Protocol.HTTP)) {

				httpURLConnection = (HttpURLConnection) url.openConnection();

				if (method.equalsIgnoreCase(Method.GET)) {

					httpURLConnection.setRequestMethod(Method.GET);

				} else if (method.equalsIgnoreCase(Method.POST)) {

					httpURLConnection.setRequestMethod(Method.POST);
					httpURLConnection.setDoInput(true);
					httpURLConnection.setDoOutput(true);

				}

				if (property != null) {

					Set<String> keySet = property.keySet();

					for (String key : keySet) {
						httpURLConnection.setRequestProperty(key, property.get(key));
					}

				}

			} else if (protocol.equalsIgnoreCase(Protocol.HTTPS)) {

				httpsURLConnection = (HttpsURLConnection) url.openConnection();

				httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {

					public boolean verify(String hostname, SSLSession session) {

						return true;

					}

				});

				if (method.equalsIgnoreCase(Method.GET)) {

					httpsURLConnection.setRequestMethod(Method.GET);

				} else if (method.equalsIgnoreCase(Method.POST)) {

					httpsURLConnection.setRequestMethod(Method.POST);
					httpsURLConnection.setDoInput(true);
					httpsURLConnection.setDoOutput(true);

				}
				if (property != null) {

					Set<String> keySet = property.keySet();

					for (String key : keySet) {

						httpsURLConnection.setRequestProperty(key, property.get(key));

					}

				}

			}

			if (method.equalsIgnoreCase(Method.POST) || method.equalsIgnoreCase(Method.PUT)) {

				Map<String, String> mapData = new HashMap<String, String>();

				if (data != null) {

					if (protocol.equalsIgnoreCase(Protocol.HTTP)) {

						outputStream = httpURLConnection.getOutputStream();

					} else if (protocol.equalsIgnoreCase(Protocol.HTTPS)) {

						outputStream = httpsURLConnection.getOutputStream();

					}

					if (data instanceof Map) {

						mapData = (Map<String, String>) data;

						BufferedWriter bufferedWriter = new BufferedWriter(
								new OutputStreamWriter(outputStream, "UTF-8"));

						if (property.get("Content-Type").equalsIgnoreCase("application/x-www-form-urlencoded")) {

							bufferedWriter.write(getQuery(mapData));

						}

						bufferedWriter.flush();
						bufferedWriter.close();

					} else if (data instanceof String) {

						BufferedWriter bufferedWriter = new BufferedWriter(
								new OutputStreamWriter(outputStream, "UTF-8"));

						bufferedWriter.write((String) data);
						bufferedWriter.flush();
						bufferedWriter.close();

					} else if (data instanceof ByteArrayOutputStream) {

						outputStream.write(((ByteArrayOutputStream) data).toByteArray());
						BufferedWriter bufferedWriter = new BufferedWriter(
								new OutputStreamWriter(outputStream, "UTF-8"));

						bufferedWriter.flush();
						bufferedWriter.close();

					}

					outputStream.close();

				}
			}

			if (protocol.equalsIgnoreCase(Protocol.HTTP)) {

				response.setCode(httpURLConnection.getResponseCode());
				response.setMessage(httpURLConnection.getResponseMessage());
				bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

			} else if (protocol.equalsIgnoreCase(Protocol.HTTPS)) {

				response.setCode(httpsURLConnection.getResponseCode());
				response.setMessage(httpsURLConnection.getResponseMessage());
				bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));

			}

			StringBuilder stringBuilder = new StringBuilder();

			if (property.get("x-stream") != null) {

				if (property.get("x-stream").equalsIgnoreCase("available")) {

					byte[] bytes = new byte[1024];
					ByteArrayOutputStream baos = new ByteArrayOutputStream();

					int length;

					InputStream is = httpsURLConnection.getInputStream();

					while ((length = is.read(bytes)) > -1) {
						baos.write(bytes, 0, length);
					}

					baos.flush();

					String base64result = Base64.getEncoder().encodeToString(baos.toByteArray());

					stringBuilder.append(base64result);

				}

			} else {

				String line = bufferedReader.readLine();

				while (line != null) {

					if (property.get("Content-Type") != null) {

						if (property.get("Content-Type").equalsIgnoreCase("text/xml")) {
							if (line.startsWith("<")) {
								stringBuilder.append(line);
							}

						} else {

							stringBuilder.append(line);

						}

					} else {

						stringBuilder.append(line);

					}

					line = bufferedReader.readLine();
				}

			}
			if (protocol.equalsIgnoreCase(Protocol.HTTP)) {

				httpURLConnection.disconnect();

			} else if (protocol.equalsIgnoreCase(Protocol.HTTPS)) {

				httpsURLConnection.disconnect();

			}

			response.setResult(stringBuilder.toString());

			return response;

		} catch (Exception e) {

			System.out.println(e.getMessage());

		}

		return null;

	}

	private static Document parseToXML(String in) {

		DocumentBuilderFactory dbf = null;
		DocumentBuilder db = null;
		InputSource is = null;
		Document document = null;

		try {

			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			is = new InputSource(new StringReader(in));
			document = db.parse(is);

		} catch (ParserConfigurationException e) {

			System.out.println(e.getMessage());

		} catch (SAXException e) {
			
			System.out.println(e.getMessage());
		
		} catch (IOException e) {
		
			System.out.println(e.getMessage());
		
		}

		return document;
	}

}
