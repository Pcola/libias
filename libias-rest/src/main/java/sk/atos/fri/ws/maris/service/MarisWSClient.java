package sk.atos.fri.ws.maris.service;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;
import sk.atos.fri.ws.maris.model.*;

/**
 *
 * @author Jaroslav Kollar
 */
public class MarisWSClient {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private String personUrl = null;
	private String akteUrl = null;
	private String deletedPersonsUrl = null;
	private String deletedFilesUrl = null;
	private String lockedFilesUrl = null;
	private String updateUrl = null;
	private String tokenUrl = null;
	private String username = null;
	private String password = null;
	private String marisImageInfoServiceUrl = null;
	private String marisDeletedPersonsServiceUrl = null;
	private String marisDeletedFilesServiceUrl = null;
	private String marisLockedFilesServiceUrl = null;
	private String marisOauthServiceUrl = null;

	private final RestTemplate restTemplate = getRestTemplate();

	private final Logger LOG = new Logger();

	private final String HTTP_UNAUTHORIZED = "" + HttpStatus.UNAUTHORIZED.value();

	public void setPersonUrl(String personUrl) {
		this.personUrl = personUrl;
	}

	public void setAkteUrl(String akteUrl) {
		this.akteUrl = akteUrl;
	}

	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
	}

	public void setTokenUrl(String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setMarisImageInfoServiceUrl(String marisImageInfoServiceUrl) {
		this.marisImageInfoServiceUrl = marisImageInfoServiceUrl;
	}

	public void setMarisDeletedPersonsServiceUrl(String marisDeletedPersonsServiceUrl) {
		this.marisDeletedPersonsServiceUrl = marisDeletedPersonsServiceUrl;
	}

	public void setMarisDeletedFilesServiceUrl(String marisDeletedFilesServiceUrl) {
		this.marisDeletedFilesServiceUrl = marisDeletedFilesServiceUrl;
	}

	public void setMarisLockedFilesServiceUrl(String marisLockedFilesServiceUrl) {
		this.marisLockedFilesServiceUrl = marisLockedFilesServiceUrl;
	}

	public void setMarisOauthServiceUrl(String marisOauthServiceUrl) {
		this.marisOauthServiceUrl = marisOauthServiceUrl;
	}

	public void setDeletedPersonsUrl(String deletedPersonsUrl) {
		this.deletedPersonsUrl = deletedPersonsUrl;
	}

	public void setDeletedFilesUrl(String deletedFilesUrl) {
		this.deletedFilesUrl = deletedFilesUrl;
	}

	public void setLockedFilesUrl(String lockedFilesUrl) {
		this.lockedFilesUrl = lockedFilesUrl;
	}

	private RestTemplate getRestTemplate() {
		HttpComponentsClientHttpRequestFactory httpClientFactory = new HttpComponentsClientHttpRequestFactory();
		try {
			SSLContext sslContext = SSLContexts.custom().
					loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslContext, new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			httpClientFactory.setHttpClient(HttpClients.custom().setSSLSocketFactory(sslsf).build());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		RestTemplate template = new RestTemplate(httpClientFactory);
		template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		template.getMessageConverters().add(new StringHttpMessageConverter());
		return template;
	}

	public Token getToken() {
		try {
			HttpEntity<String> entity = createHttpHeaderForGetToken();
			String url = marisOauthServiceUrl + tokenUrl;
			ResponseEntity<Token> response = restTemplate.postForEntity(url, entity, Token.class);
			Token newToken = response.getBody();
			if (newToken != null) {
				LOG.debug(null, "Got new token");
			} else {
				LOG.debug(null, "Got NULL token");
			}
			return newToken;
		} catch (Exception e) {
			LOG.error(Error.GET_MARIS_TOKEN, e);
		}
		return null;
	}

	public PersonResponse getPerson(Long imageId) {
		try {
			String url = marisImageInfoServiceUrl + personUrl + imageId + "/antragsteller";
			PersonResponse res = callMarisAuthorized(url, HttpMethod.GET, PersonResponse.class);
			if (res != null) {
				res.setImageOid(imageId);
			}
			if (res != null) {
				LOG.debug(null, "Got person " + res.getApplicantOid() + " for image " + imageId);
			} else {
				LOG.debug(null, "Got NULL person for image " + imageId);
			}
			return res;
		} catch (Exception e) {
			LOG.error(Error.GET_MARIS_BILD/*, e*/);
		}
		return null;
	}

	public AkteResponse getAkte(String aktenzeichenA, String aktenzeichenB) {
		try {
			String url = marisImageInfoServiceUrl + akteUrl + aktenzeichenA + "/" + aktenzeichenB;
			AkteResponse res = callMarisAuthorized(url, HttpMethod.GET, AkteResponse.class);
			if (res != null) {
				LOG.debug(null, "Got references between acts " + aktenzeichenA + " and " + aktenzeichenB + ", size " + (res.getRecords() != null ? res.getRecords().size() : "unknown"));
			} else {
				LOG.debug(null, "Got NULL references between acts " + aktenzeichenA + " and " + aktenzeichenB);
			}
			return res;
		} catch (Exception e) {
			LOG.error(Error.GET_MARIS_AKTE, e);
		}
		return null;
	}

	public DeletedPersons getDeletedPersons(Date datum) {
		try {
			String url = marisDeletedPersonsServiceUrl + deletedPersonsUrl + DATE_FORMAT.format(datum);
			DeletedPersons res = callMarisAuthorized(url, HttpMethod.GET, DeletedPersons.class);
			if (res != null) {
				LOG.debug(null, "Got deleted persons, size " + (res.getDeletedPersons() != null ? res.getDeletedPersons().size() : "unknown"));
			} else {
				LOG.debug(null, "Got NULL deleted persons");
			}
			return res;
		} catch (Exception e) {
			LOG.error(Error.GET_MARIS_DELETED_PERSONS, e);
		}
		return null;
	}

	public DeletedFiles getDeletedFiles(Date datum) {
		try {
			String url = marisDeletedFilesServiceUrl + deletedFilesUrl + DATE_FORMAT.format(datum);
			DeletedFiles res = callMarisAuthorized(url, HttpMethod.GET, DeletedFiles.class);
			if (res != null) {
				LOG.debug(null, "Got deleted files, size " + (res.getDeletedFiles() != null ? res.getDeletedFiles().size() : "unknown"));
			} else {
				LOG.debug(null, "Got NULL deleted files");
			}
			return res;
		} catch (Exception e) {
			LOG.error(Error.GET_MARIS_DELETED_FILES, e);
		}
		return null;
	}

	public LockedFiles getLockedFiles() {
		try {
			String url = marisLockedFilesServiceUrl + lockedFilesUrl;
			LockedFiles res = callMarisAuthorized(url, HttpMethod.GET, LockedFiles.class);
			if (res != null) {
				LOG.debug(null, "Got locked files, size " + (res.getLockedFiles() != null ? res.getLockedFiles().size() : "unknown"));
			} else {
				LOG.debug(null, "Got NULL locked files");
			}
			return res;
		} catch (Exception e) {
			LOG.error(Error.GET_MARIS_LOCKED_FILES, e);
		}
		return null;
	}

	public UpdatedApplicants getUpdatedApplicants() {
		try {
			String url = marisImageInfoServiceUrl + updateUrl;
			UpdatedApplicants res = callMarisAuthorized(url, HttpMethod.GET, UpdatedApplicants.class);
			if (res != null) {
				LOG.debug(null, "Got updated applicants, size " + (res.getUpdatedApplicants() != null ? res.getUpdatedApplicants().size() : "unknown"));
			} else {
				LOG.debug(null, "Got NULL updated applicants");
			}
			return res;
		} catch (Exception e) {
			LOG.error(Error.GET_MARIS_UPDATE, e);
		}
		return null;
	}

	private <T> T callMarisAuthorized(String url, HttpMethod method, Class<T> responseType) throws Exception {
		Token token = TokenStore.getToken();
		ResponseEntity<T> response = null;
		// if 401 returned on stored token, try again with new token

		if (token != null) {
			try {
				response = restTemplate.exchange(url, method, createHttpHeaderWithToken(token), responseType);
				return response.getBody();

			} catch (RestClientException e) {
				if (!HTTP_UNAUTHORIZED.equals(e.getMessage().trim())
						&& (response == null || !response.getStatusCode().equals(HttpStatus.UNAUTHORIZED))) {
					throw e;
				}
			}
		}

		try {
			token = getToken();

			if (token != null) {
				TokenStore.setToken(token);
				response = restTemplate.exchange(url, method, createHttpHeaderWithToken(token), responseType);
				return response.getBody();
			}

		} catch (Exception e) {
			throw e;
		}

		return null;
	}

	private HttpEntity createHttpHeaderWithToken(Token token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token.getAccessToken());
		HttpEntity<String> entity = new HttpEntity<>(headers);
		return entity;
	}

	private HttpEntity createHttpHeaderForGetToken() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType(MediaType.APPLICATION_FORM_URLENCODED, StandardCharsets.UTF_8));
		headers.add("authorization", "Basic " + new String(Base64.encodeBase64((username + ":" + password).getBytes())));

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", "client_credentials");
		map.add("client_id", username);
		map.add("client_secret", password);

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
		return entity;
	}

}
