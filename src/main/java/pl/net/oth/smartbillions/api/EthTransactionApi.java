package pl.net.oth.smartbillions.api;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

import pl.net.oth.smartbillions.Timer;
import pl.net.oth.smartbillions.model.EthTransactionResult;

@Component
public class EthTransactionApi {
	private static final Logger LOG = LoggerFactory.getLogger(EthTransactionApi.class);

	private static final String GETH_ADDRESS = "http://192.168.1.223:8545";

	private static final String MY_ETH_ADDRESS = "0x6886ab0b9f907d0104703935c864d6c77cedab8b";

	private static final String SMART_BILLION_ADDRESS = "0x6f6deb5db0c4994a8283a01d6cfeeb27fc3bbe9c";

	private static final String ETH_TRX_DATA = "0x2669957600000000000000000000000000000000000000000000000000000000009f9171000000000000000000000000dfffe934c59da76f4f4245937704c015bc364866";

	public static final Integer GAS_LIMIT = 100000;

	private static final Integer CONNECT_ERROR = 1;

	private static Web3j web3 = Web3j.build(new HttpService(GETH_ADDRESS));
	private static Admin web3jadmin = Admin.build(new HttpService(GETH_ADDRESS));
	
	public EthTransactionResult send(Integer gasPrice) {
		EthTransactionResult ethTransactionResult = new EthTransactionResult();
		ethTransactionResult.setErrorCode(0);
		try {			
			PersonalUnlockAccount personalUnlockAccount = web3jadmin.personalUnlockAccount(MY_ETH_ADDRESS, "so11fest")
					.send();
			if (personalUnlockAccount.accountUnlocked()) {
				LOG.info("Konto " + MY_ETH_ADDRESS + " odblokowane poprawnie.");
			} else {
				LOG.error("Konto " + MY_ETH_ADDRESS + " odblokowane poprawnie.");
				ethTransactionResult.setErrorCode(2);
				return ethTransactionResult;
			}
			EthGetTransactionCount ethGetTransactionCount = web3
					.ethGetTransactionCount(MY_ETH_ADDRESS, DefaultBlockParameterName.LATEST).send();

			BigInteger nonce = ethGetTransactionCount.getTransactionCount().add(new BigInteger("1"));
			LOG.info("Nonce: " + nonce);
			Transaction transaction = Transaction.createFunctionCallTransaction(MY_ETH_ADDRESS, nonce,
					BigInteger.valueOf(gasPrice * 1000000000L), BigInteger.valueOf(GAS_LIMIT), SMART_BILLION_ADDRESS,
					Convert.toWei("0.00002", Unit.ETHER).toBigInteger(), ETH_TRX_DATA);
			EthSendTransaction transactionResponse = web3.ethSendTransaction(transaction).send();
			if(transactionResponse.hasError()) {
				LOG.error("callTransactionResult: "+transactionResponse.getError().getCode()+" "+transactionResponse.getError().getMessage());
				ethTransactionResult.setErrorCode(99);
				return ethTransactionResult;
			}
			ethTransactionResult.setTrxHash(transactionResponse.getTransactionHash());
			return ethTransactionResult;
		} catch (ConnectException e) {
			ethTransactionResult.setErrorCode(CONNECT_ERROR);
			ethTransactionResult.setErrorMessage("Brak możliwości połączenia z GETH");
			return ethTransactionResult;		
		} catch (IOException e) {
			ethTransactionResult.setErrorCode(99);
			ethTransactionResult.setErrorMessage(e.getMessage());
			return ethTransactionResult;			
		}						
	}
	public boolean checkTransaction(String trxHash) {
		try {
			EthTransaction ethTransaction=web3.ethGetTransactionByHash(trxHash).send();
			System.out.println(ethTransaction.getResult()!=null);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
		
	}
}
