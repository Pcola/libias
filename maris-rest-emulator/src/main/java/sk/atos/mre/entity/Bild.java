package sk.atos.mre.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="BILD")
public class Bild {
	
	@Id
	@Column(name="OID")
	private Long oid;
		
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="antragsteller_oid")
	private Antragsteller antragsteller;
	
	public Bild() {
	}
	
	public void setOid(Long oid) {
		this.oid = oid;
	}
	
	public Long getOid() {
		return this.oid;
	}
	
	public void setAntragsteller(Antragsteller antragsteller) {
		this.antragsteller = antragsteller;
	}
	
	public Antragsteller getAntragsteller() {
		return this.antragsteller;
	}
}
