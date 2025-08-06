package sk.atos.fri.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
//import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import sk.atos.fri.common.Constants;
import sk.atos.fri.dao.libias.model.Notification;
import sk.atos.fri.dao.libias.service.NotificationService;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/notification")
public class NotificationController {

    @Autowired
    private Logger LOG;

    @Autowired
    private NotificationService notificationService;

    /**
     * Endpoint to retrieve all valid notifications.
     *
     * @param httpServletRequest - HttpServletRequest sent from client
     * @return a list of valid notifications
     */
    //@Secured({Constants.ROLE_ADMIN})
    @RequestMapping(path = "/getAllValid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Notification> getAllValidNotifications(HttpServletRequest httpServletRequest) {
        String username = null;
        try {
            username = httpServletRequest.getUserPrincipal().getName();
            return notificationService.getAllValid();
        } catch (Exception e) {
            LOG.error(username, Error.GET_NOTIFICATIONS, e);
            throw e;
        }
    }
}
