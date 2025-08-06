package sk.atos.fri.dao.libias.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author : A761498, Kamil Macek
 * @since : 19 Sep 2019
 **/
@Entity
@Table(name = "USER_ROLE2BAM_USER")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "UserRole2BamUser.findAll", query = "SELECT ur FROM UserRole2BamUser ur")})
public class UserRole2BamUser implements Serializable {

    @EmbeddedId
    private UserRole2BamUser.UserRole2BamUserPK pk;

    public UserRole2BamUser() {
        pk = new UserRole2BamUserPK();
    }

    public UserRole2BamUserPK getPk() {
        return pk;
    }

    public void setPk(UserRole2BamUserPK pk) {
        this.pk = pk;
    }

    @Embeddable
    public static class UserRole2BamUserPK implements Serializable {
        @Column(name = "ROLE_ID")
        private Long roleId;

        @Column(name = "USER_ID")
        private Long userId;

        public UserRole2BamUserPK() {
        }

        public UserRole2BamUserPK(Long roleId, Long userId) {
            this.roleId = roleId;
            this.userId = userId;
        }

        public Long getRoleId() {
            return roleId;
        }

        public void setRoleId(Long roleId) {
            this.roleId = roleId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }
    }
}
