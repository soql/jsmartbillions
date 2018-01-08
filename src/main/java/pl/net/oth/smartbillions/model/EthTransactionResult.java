package pl.net.oth.smartbillions.model;

public class EthTransactionResult {
	private Integer errorCode;
	private String errorMessage;
	private String trxHash;
	public Integer getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getTrxHash() {
		return trxHash;
	}
	public void setTrxHash(String trxHash) {
		this.trxHash = trxHash;
	}
	

}
