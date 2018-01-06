package pl.net.oth.smartbillions;

import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;


@SpringBootApplication
@EnableScheduling
public class App 
{		
	public static final String PHONE_NUMBER = "+48501296664";
	public static Long GAS_LIMIT=150200L;
	 public static void main(String[] args) throws Exception {
	        SpringApplication.run(App.class);
	    }
	 
   /* public static void main( String[] args )
    {
    	Web3j web3 = Web3j.build(new HttpService("http://localhost:8900"));
    	Admin web3jadmin = Admin.build(new HttpService("http://localhost:8900"));
    	
		try {
			Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().send();
			String clientVersion = web3ClientVersion.getWeb3ClientVersion();
			System.out.println(clientVersion);
			PersonalUnlockAccount personalUnlockAccount = web3jadmin.personalUnlockAccount("0x6886ab0b9f907d0104703935c864d6c77cedab8b", "so11fe").sendAsync().get();
			if (personalUnlockAccount.accountUnlocked()) {
			    System.out.println("UNLOCKED");
			}
			EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
					"0x6886ab0b9f907d0104703935c864d6c77cedab8b", DefaultBlockParameterName.LATEST).send();

		     BigInteger nonce = ethGetTransactionCount.getTransactionCount();
		     System.out.println("NONCE "+nonce);
			Transaction transaction = Transaction.createFunctionCallTransaction(
		              "0x6886ab0b9f907d0104703935c864d6c77cedab8b",
		              nonce,
		              BigInteger.valueOf(51000000000L),
		              BigInteger.valueOf(30000),
		              "0x6f6deb5db0c4994a8283a01d6cfeeb27fc3bbe9c",
		              Convert.toWei("0.00002", Unit.ETHER).toBigInteger(),
		              "0x2669957600000000000000000000000000000000000000000000000000000000009f9171000000000000000000000000dfffe934c59da76f4f4245937704c015bc364866"
		      );			
			EthSendTransaction transactionResponse =
		             web3.ethSendTransaction(transaction).send();

		String transactionHash = transactionResponse.getTransactionHash();
			System.out.println(transactionHash);
			System.out.println(" "+transactionResponse.getResult()+" "+transactionResponse.getError().getCode());
			System.out.println(" "+transactionResponse.getResult()+" "+transactionResponse.getError().getMessage());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }*/
   
}
