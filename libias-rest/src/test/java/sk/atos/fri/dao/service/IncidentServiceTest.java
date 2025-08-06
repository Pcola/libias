package sk.atos.fri.dao.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sk.atos.fri.dao.libias.model.IncidentFilter;
import sk.atos.fri.dao.libias.service.IncidentService;
import sk.atos.fri.rest.model.IncidentSearchRequest;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * @author : A761498, Kamil Macek
 * @since : 30 Sep 2019
 **/
@SpringBootTest
public class IncidentServiceTest extends AbstractDaoTest {

    public static final Long VALID_CASE_ID = 5L;

    @Autowired
    private IncidentService incidentService;

    @Test
    @Transactional
    public void testFindByCaseId() {
        incidentService.findByCaseId(1L);
    }

    @Test
    @Transactional
    public void testSearchIncident() {
        incidentService.searchIncident(false, getFilter(), getRequest());
    }

    @Test
    @Transactional
    public void testIncidentsCount() {
        incidentService.incidentsCount(false, getFilter(), getRequest());
    }

    @Test
    @Transactional
    public void testFindAllReferenceType() {
        incidentService.findAllReferenceType();
    }

    @Test
    @Transactional
    public void testFindAussenstellerCases() {
        incidentService.findAussenstellerCases(getFilter(), "skuska");
    }

    @Test
    @Transactional
    public void testAussenstellerCasesCount() {
        incidentService.aussenstellerCasesCount(getFilter(), "skuska");
    }

    @Test
    @Transactional
    public void testCountCasesByStatus() {
        incidentService.countCasesByStatus();
    }

    @Test
    @Transactional
    public void testGetRelatedCases() {
        incidentService.getRelatedCases(VALID_CASE_ID);
    }

    @Test
    @Transactional
    public void testGetSiteRelatedCases() {
        incidentService.getSiteRelatedCases(VALID_CASE_ID, "skuska");
    }

    @Test
    @Transactional
    public void testFindAllNationalities() {
        incidentService.findAllNationalities();
    }

    @Test
    @Transactional
    public void testGetImageOidsWhereMissingPersonData() {
        incidentService.getImageOidsWhereMissingPersonData();
    }

    @Test
    @Transactional
    public void testGetIncident() {
        IncidentFilter incidentFilter = new IncidentFilter();
        incidentFilter.setImageOid(1L);

        incidentService.getIncident(incidentFilter);
    }

    private IncidentSearchRequest getFilter() {
        IncidentSearchRequest incidentSearchRequest = new IncidentSearchRequest();
        incidentSearchRequest.setAzrNumber("");
        incidentSearchRequest.setFirst(1);
        incidentSearchRequest.setRows(20);
        return incidentSearchRequest;
    }

    private HttpServletRequest getRequest() {
        return new HttpServletRequest() {
            @Override
            public String getAuthType() {
                return null;
            }

            @Override
            public Cookie[] getCookies() {
                return new Cookie[0];
            }

            @Override
            public long getDateHeader(String name) {
                return 0;
            }

            @Override
            public String getHeader(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getHeaders(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                return null;
            }

            @Override
            public int getIntHeader(String name) {
                return 0;
            }

            @Override
            public String getMethod() {
                return null;
            }

            @Override
            public String getPathInfo() {
                return null;
            }

            @Override
            public String getPathTranslated() {
                return null;
            }

            @Override
            public String getContextPath() {
                return null;
            }

            @Override
            public String getQueryString() {
                return null;
            }

            @Override
            public String getRemoteUser() {
                return null;
            }

            @Override
            public boolean isUserInRole(String role) {
                return true;
            }

            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public String getRequestedSessionId() {
                return null;
            }

            @Override
            public String getRequestURI() {
                return null;
            }

            @Override
            public StringBuffer getRequestURL() {
                return null;
            }

            @Override
            public String getServletPath() {
                return null;
            }

            @Override
            public HttpSession getSession(boolean create) {
                return null;
            }

            @Override
            public HttpSession getSession() {
                return null;
            }

            @Override
            public String changeSessionId() {
                return null;
            }

            @Override
            public boolean isRequestedSessionIdValid() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromCookie() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromURL() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromUrl() {
                return false;
            }

            @Override
            public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
                return false;
            }

            @Override
            public void login(String username, String password) throws ServletException {

            }

            @Override
            public void logout() throws ServletException {

            }

            @Override
            public Collection<Part> getParts() throws IOException, ServletException {
                return null;
            }

            @Override
            public Part getPart(String name) throws IOException, ServletException {
                return null;
            }

            @Override
            public <T extends HttpUpgradeHandler> T upgrade(Class<T> httpUpgradeHandlerClass) throws IOException, ServletException {
                return null;
            }

            @Override
            public Object getAttribute(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return null;
            }

            @Override
            public String getCharacterEncoding() {
                return null;
            }

            @Override
            public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

            }

            @Override
            public int getContentLength() {
                return 0;
            }

            @Override
            public long getContentLengthLong() {
                return 0;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public ServletInputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public String getParameter(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getParameterNames() {
                return null;
            }

            @Override
            public String[] getParameterValues(String name) {
                return new String[0];
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                return null;
            }

            @Override
            public String getProtocol() {
                return null;
            }

            @Override
            public String getScheme() {
                return null;
            }

            @Override
            public String getServerName() {
                return null;
            }

            @Override
            public int getServerPort() {
                return 0;
            }

            @Override
            public BufferedReader getReader() throws IOException {
                return null;
            }

            @Override
            public String getRemoteAddr() {
                return null;
            }

            @Override
            public String getRemoteHost() {
                return null;
            }

            @Override
            public void setAttribute(String name, Object o) {

            }

            @Override
            public void removeAttribute(String name) {

            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public Enumeration<Locale> getLocales() {
                return null;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(String path) {
                return null;
            }

            @Override
            public String getRealPath(String path) {
                return null;
            }

            @Override
            public int getRemotePort() {
                return 0;
            }

            @Override
            public String getLocalName() {
                return null;
            }

            @Override
            public String getLocalAddr() {
                return null;
            }

            @Override
            public int getLocalPort() {
                return 0;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public AsyncContext startAsync() throws IllegalStateException {
                return null;
            }

            @Override
            public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
                return null;
            }

            @Override
            public boolean isAsyncStarted() {
                return false;
            }

            @Override
            public boolean isAsyncSupported() {
                return false;
            }

            @Override
            public AsyncContext getAsyncContext() {
                return null;
            }

            @Override
            public DispatcherType getDispatcherType() {
                return null;
            }
        };
    }

}
