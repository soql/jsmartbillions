package pl.net.oth.smartbillions.model.hibernate;

import java.math.BigInteger;
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
	@Column
	private BigInteger minedBlock;
	@Column 
	private String myNumbers;
	@Column 
	private String lotteryResults;
	
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
	public String getLotteryResults() {
		return lotteryResults;
	}
	public void setLotteryResults(String lotteryResults) {
		this.lotteryResults = lotteryResults;
	}
	public BigInteger getMinedBlock() {
		return minedBlock;
	}
	public void setMinedBlock(BigInteger minedBlock) {
		this.minedBlock = minedBlock;
	}
	public String getMyNumbers() {
		return myNumbers;
	}
	public void setMyNumbers(String myNumbers) {
		this.myNumbers = myNumbers;
	}
	
	
	
	
}
