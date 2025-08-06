package sk.atos.fri.dao;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import sk.atos.fri.dao.libias.model.BamUser;
import sk.atos.fri.dao.libias.model.UserRole;
import sk.atos.fri.dao.libias.model.Workplace;
import sk.atos.fri.dao.libias.service.UserRoleService;
import sk.atos.fri.dao.libias.service.UserService;
import sk.atos.fri.dao.libias.service.WorkplaceService;
import sk.atos.fri.log.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static sk.atos.fri.common.Constants.*;

/**
 * @author : A761498, Kamil Macek
 * @since : 20 Aug 2019
 *
 * Service class using for parsing injected headers sent from authorization webgate
 **/
@Service
public class HeaderParserService {

    @Autowired
    private Logger LOG;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private WorkplaceService workplaceService;

    @Autowired
    private UserService userService;

    /**
     *
     * @param httpServletRequest - request sent from web client
     * @return fullname of logged user according to infos in header
     *
     * When header is invalid (in develop mode to login as Admin / when service for some reasons stopped send headers)
     * this method is trying to find user in database, otherwise it will parse header infos - firt name + last name
     */
    public String getUserFullName(HttpServletRequest httpServletRequest) {
        if(!isHeaderValid(httpServletRequest)) {
            BamUser bamUser = userService.find(httpServletRequest.getUserPrincipal().getName());
            if(bamUser == null) {
                return "";
            } else {
                return bamUser.getFirstName() + " " + bamUser.getLastName();
            }
        }

        return httpServletRequest.getHeader(HTTP_HEADER_FIRST_NAME) + " " + httpServletRequest.getHeader(HTTP_HEADER_LAST_NAME);
    }

    /**
     *
     * @param httpServletRequest - request sent from web client
     * @param addFullName - prepend user full name
     * @return department of logged user according to infos in header
     */
    public String getUserDepartment(HttpServletRequest httpServletRequest, boolean addFullName) {
        String department = httpServletRequest != null ? httpServletRequest.getHeader(HTTP_HEADER_DEPARTMENT) : null;
        if (StringUtils.isNotBlank(department)) {
            return addFullName ? (getUserFullName(httpServletRequest) + ", " + department) : department;
        } else {
            return addFullName ? getUserFullName(httpServletRequest) : "";
        }
    }

    /**
     *
     * @param httpServletRequest - request sent from web client
     * @return full BamUser
     *
     * When sending an request from client to server, this method is used for getting BamUser.
     * When header isn't valid, trying to find user in dB, setting him workplaces properly and also roles.
     * In case of valid header, creating BamUser based on header parameters, such as username, first name etc.
     * Also set up roles, when user has some workplaces, immediately is added also AussenstelleRole
     */
    public BamUser getUser(HttpServletRequest httpServletRequest) {
        if(!isHeaderValid(httpServletRequest)) {
            BamUser bamUser = userService.find(httpServletRequest.getUserPrincipal().getName());

            if(bamUser == null) {
                return null;
            }

            if(bamUser.getWorkplaceId().contains(":")) {
                bamUser.setWorkplace(workplaceService.getWorkplaces(bamUser.getWorkplaceId().split(":"), false));
            } else if(bamUser.getWorkplaceId().isEmpty()) {
                bamUser.setWorkplace(null);
            } else {
                List<Workplace> workplaces = new ArrayList<>();
                workplaces.add(workplaceService.get(bamUser.getWorkplaceId()));
                bamUser.setWorkplace(workplaces);
            }

            bamUser.setUserRoleCollection(userRoleService.getAllUserRoles(bamUser.getUserId()));

            return bamUser;
        }

        BamUser bamUser = new BamUser();

        String[] idmRoles = getIdmRoles(httpServletRequest);

        bamUser.setUsername(httpServletRequest.getHeader(HTTP_HEADER_USERNAME));
        bamUser.setFirstName(httpServletRequest.getHeader(HTTP_HEADER_FIRST_NAME));
        bamUser.setLastName(httpServletRequest.getHeader(HTTP_HEADER_LAST_NAME));
        bamUser.setWorkplace(workplaceService.getWorkplaces(idmRoles, true));

        if(bamUser.getWorkplace().size() > 0) {
            StringBuilder dienststelleIds = new StringBuilder();
            for(Workplace workplace : bamUser.getWorkplace()) {
                dienststelleIds.append(workplace.getId()).append(":");
            }

            // remove last : from string
            bamUser.setWorkplaceId(dienststelleIds.substring(0, dienststelleIds.length() - 1));

            bamUser.setUserRoleCollection(userRoleService.getRoles(idmRoles, true));
        } else {
            bamUser.setUserRoleCollection(userRoleService.getRoles(idmRoles, false));
        }

        bamUser.setActive(false);

        return bamUser;
    }

    /**
     *
     * @param httpServletRequest - request sent from web client
     * @return parsed idm roles from header parameter, delimited by ; or :
     */
    public String[] getIdmRoles(HttpServletRequest httpServletRequest) {
        String idmRoles = httpServletRequest.getHeader(HTTP_HEADER_IDM_ROLES);

        return Strings.split(idmRoles, idmRoles.contains(";") ? ';' : ':');
    }

    /**
     *
     * @param user - BamUser
     * @return list of granted authorities for specific user
     *
     * Granted authorities are basic roles with an prefix added - "ROLE_"
     */
    public List<GrantedAuthority> getGrantedAuthority(BamUser user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (!user.getUserRoleCollection().isEmpty()) {
            for (UserRole r : user.getUserRoleCollection()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + r.getRole()));
            }
        }
        return authorities;
    }

    /**
     *
     * @param httpServletRequest - request sent from web client
     * @return boolean value corresponding to header validity
     *
     * Header is valid when contains this parameters: username, first an last name and roles
     */
    public boolean isHeaderValid(HttpServletRequest httpServletRequest) {
        if(httpServletRequest == null) {
            return false;
        }

        List<String> headerNames = Collections.list(httpServletRequest.getHeaderNames());

        List<String> listOfNecessaryHeader = Arrays.asList(HTTP_HEADER_USERNAME,HTTP_HEADER_LAST_NAME,HTTP_HEADER_FIRST_NAME,HTTP_HEADER_IDM_ROLES);
        listOfNecessaryHeader.replaceAll(String::toLowerCase);

        String idmRoles = httpServletRequest.getHeader(HTTP_HEADER_IDM_ROLES);
        return headerNames.containsAll(listOfNecessaryHeader) && idmRoles.toLowerCase().contains("idms_libias_");
    }
}
