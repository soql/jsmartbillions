package pl.net.oth.smartbillions.model.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class OutTrx {
	@Id
	@Column
private String trxId;
	@Column
	private Date creationDate;
	@Column
	private Date miningDate;
	public String getTrxId() {
		return trxId;
	}
	public void setTrxId(String trxId) {
		this.trxId = trxId;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Date getMiningDate() {
		return miningDate;
	}
	public void setMiningDate(Date miningDate) {
		this.miningDate = miningDate;
	}
	
}
