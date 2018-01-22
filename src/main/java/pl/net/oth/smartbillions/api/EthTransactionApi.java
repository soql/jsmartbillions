package pl.net.oth.smartbillions.api;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	private static final String MY_ETH_ADDRESS = "0x3347b0a51c0a6e8094af1ddf97e20001c59a21ed";

	private static final String SMART_BILLION_ADDRESS = "0x6f6deb5db0c4994a8283a01d6cfeeb27fc3bbe9c";

	public static final String MY_LUCKY_NUMBERS = "9f9171";

	private static final String ETH_TRX_DATA = "0x266995760000000000000000000000000000000000000000000000000000000000"
			+ MY_LUCKY_NUMBERS + "0000000000000000000000000000000000000000000000000000000000000000";

	public static Integer GAS_LIMIT = 200000;

	private static final Integer CONNECT_ERROR = 1;
	
	private static final String BET_VALUE="0.0002";

	@Autowired
	private DatabaseAPI databaseAPI;

	private static Web3j web3 = Web3j.build(new HttpService(GETH_ADDRESS));
	private static Admin web3jadmin = Admin.build(new HttpService(GETH_ADDRESS));

	public EthTransactionApi() {
		LOG.debug("Uruchomiono konstruktor");

	}

	private boolean unlockAccount() throws IOException {
		PersonalUnlockAccount personalUnlockAccount = web3jadmin.personalUnlockAccount(MY_ETH_ADDRESS, "so11fest")
				.send();
		if (personalUnlockAccount.accountUnlocked()) {
			LOG.info("Konto " + MY_ETH_ADDRESS + " odblokowane poprawnie.");
			return true;
		} else {
			LOG.error("Konto " + MY_ETH_ADDRESS + " nie odblokowane.");
			return false;
		}
	}
	private BigInteger getNextNounce() throws IOException {
		Integer ADD_NONCE = Integer.parseInt(databaseAPI.getConfigurationValue("ADD_NONCE"));
		EthGetTransactionCount ethGetTransactionCount = web3
				.ethGetTransactionCount(MY_ETH_ADDRESS, DefaultBlockParameterName.LATEST).send();

		BigInteger nonce = ethGetTransactionCount.getTransactionCount().add(new BigInteger(ADD_NONCE.toString()));
		return nonce;
	}

	public EthTransactionResult send(Double gasPrice) {
		try {
			GAS_LIMIT = Integer.parseInt(databaseAPI.getConfigurationValue("GAS_LIMIT"));
			BET_VALUE = (databaseAPI.getConfigurationValue("BET_VALUE");
		} catch (Exception e) {

		}
		EthTransactionResult ethTransactionResult = new EthTransactionResult();		
		ethTransactionResult.setErrorCode(0);
		try {
			if (!unlockAccount()) {
				ethTransactionResult.setErrorCode(2);
				return ethTransactionResult;
			}
			BigInteger nonce=getNextNounce();
			LOG.info("Nadano numer nonce: " + nonce);
			
			Transaction transaction = Transaction.createFunctionCallTransaction(MY_ETH_ADDRESS, nonce,
					BigInteger.valueOf((long)(gasPrice * 1000000000L)), BigInteger.valueOf(GAS_LIMIT), SMART_BILLION_ADDRESS,
					Convert.toWei(BET_VALUE, Unit.ETHER).toBigInteger(), ETH_TRX_DATA);
			EthSendTransaction transactionResponse = web3.ethSendTransaction(transaction).send();
			
			if (transactionResponse.hasError()) {
				LOG.error("callTransactionResult: " + transactionResponse.getError().getCode() + " "
						+ transactionResponse.getError().getMessage());
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
			EthGetTransactionReceipt ethTransaction = web3.ethGetTransactionReceipt(trxHash).send();
			TransactionReceipt result = ethTransaction.getResult();
			if (result == null) {
				return null;
			}
			LOG.error("Status transakcji " + result.getStatus());

			if (ethTransaction.getResult().getStatus().equals("0x1")) {
				BigInteger blockNumber = ethTransaction.getTransactionReceipt().get().getBlockNumber();
				return blockNumber;
			}
			LOG.error("Nieprawidłowy status transakcji " + trxHash + " = " + ethTransaction.getResult().getStatus());
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	public String getLotteryResultsBlockHash(BigInteger minedBlock) {

		Block block;
		try {
			block = web3.ethGetBlockByNumber(DefaultBlockParameter.valueOf(minedBlock.add(new BigInteger("3"))), false)
					.send().getBlock();
		} catch (IOException e) {
			LOG.error(e.getMessage());
			return null;
		}
		if (block == null)
			return null;
		return block.getHash();

	}
}
