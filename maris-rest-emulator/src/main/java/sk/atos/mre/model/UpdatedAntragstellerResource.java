package sk.atos.mre.model;

import org.springframework.hateoas.ResourceSupport;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class UpdatedAntragstellerResource extends ResourceSupport {
	
	private Long antragstellerOid;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private Date dateModified;
	
	public Long getAntragstellerOid() {
		return antragstellerOid;
	}

	public void setAntragstellerOid(Long antragstelleOid) {
		this.antragstellerOid = antragstelleOid;
	}
	
	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date date) {
		this.dateModified = date;
	}	
}