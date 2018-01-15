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
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

import pl.net.oth.smartbillions.Timer;
import pl.net.oth.smartbillions.model.EthTransactionResult;

@Component
public class EthTransactionApi {
	private static final Logger LOG = LoggerFactory.getLogger(EthTransactionApi.class);

	private static final String GETH_ADDRESS = "http://localhost:8545";

	private static final String MY_ETH_ADDRESS = "0x1e0d236d3ec99d39ce0fb42ff7cbce63db415cce";

	private static final String SMART_BILLION_ADDRESS = "0x6f6deb5db0c4994a8283a01d6cfeeb27fc3bbe9c";

	public static final String MY_LUCKY_NUMBERS="9f9171";
	
	private static final String ETH_TRX_DATA = "0x266995760000000000000000000000000000000000000000000000000000000000"+MY_LUCKY_NUMBERS+"000000000000000000000000dfffe934c59da76f4f4245937704c015bc364866";

	public static final Integer GAS_LIMIT = 200000;

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

			BigInteger nonce = ethGetTransactionCount.getTransactionCount();
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
	public BigInteger checkTransaction(String trxHash) {
		try {
			EthGetTransactionReceipt ethTransaction=web3.ethGetTransactionReceipt(trxHash).send();
			TransactionReceipt result = ethTransaction.getResult();
			if(result==null) {			
				return null;
			}			
			LOG.error("Status transakcji "+result.getStatus());
			
			if(ethTransaction.getResult().getStatus().equals("0x1")) {
				BigInteger blockNumber=ethTransaction.getTransactionReceipt().get().getBlockNumber();
				return blockNumber;
			}
			LOG.error("Nieprawidłowy status transakcji "+trxHash+" = "+ethTransaction.getResult().getStatus());
			return null;
		} catch (IOException e) {			
			e.printStackTrace();
		}
		return null;
		
	}
	public String getLotteryResultsBlockHash(BigInteger minedBlock) {
		
		Block block;
		try {
			block = web3.ethGetBlockByNumber(DefaultBlockParameter.valueOf(minedBlock.add( new BigInteger("3"))), false).send().getBlock();
		} catch (IOException e) {
			LOG.error(e.getMessage());
			return null;
		}
		if(block==null)
			return null;
		return block.getHash();
		
	}
}
