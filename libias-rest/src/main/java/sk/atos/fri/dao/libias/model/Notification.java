package sk.atos.fri.dao.libias.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "NOTIFICATION")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Notification.findAll", query = "SELECT n FROM Notification n")
})
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    @SequenceGenerator(name = "NOTIFICATION_SEQ", sequenceName = "NOTIFICATION_SEQ", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "TEXT")
    @Size(max = 1000)
    private String text;

    @Column(name = "VALID_FROM")
    private Date validFrom;

    @Column(name = "VALID_TO")
    private Date validTo;

    @Column(name = "TIMESTAMP")
    private Timestamp timestamp;

    @Column(name = "USERNAME")
    @Size(max = 20)
    private String username;

    public Notification() {
    }

    public Notification(Long id, String text, Date validFrom, Date validTo, Timestamp timestamp, String username) {
        this.id = id;
        this.text = text;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.timestamp = timestamp;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                ", timestamp=" + timestamp +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Notification)) {
            return false;
        }
        Notification other = (Notification) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
}
