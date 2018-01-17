package pl.net.oth.smartbillions;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.metamodel.relational.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pl.net.oth.smartbillions.api.DatabaseAPI;
import pl.net.oth.smartbillions.api.EthPriceApi;
import pl.net.oth.smartbillions.api.GasStationApi;
import pl.net.oth.smartbillions.api.SMSApi;
import pl.net.oth.smartbillions.api.EthTransactionApi;
import pl.net.oth.smartbillions.model.EthGasStation;
import pl.net.oth.smartbillions.model.EthTransactionResult;
import pl.net.oth.smartbillions.model.hibernate.OutTrx;

@Component
public class Timer {
	private static final Logger log = LoggerFactory.getLogger(Timer.class);
	@Autowired
	private GasStationApi gasStationApi;

	@Autowired
	private EthPriceApi ethPriceApi;

	@Autowired
	private Utils utils;

	@Autowired
	private SMSApi smsApi;

	@Autowired
	private EthTransactionApi ethTransactionApi;	
	
	@Autowired
	private DatabaseAPI databaseAPI;

	@Scheduled(fixedRate = 60000)
	public void runProcess() {
		EthGasStation ethGasStation = gasStationApi.getEthInfo();
		if (ethGasStation == null || ethPriceApi == null) {
			return;
		}
			
		List<OutTrx> noMinedTransactions=databaseAPI.getNoMinedTrx();		
		if(noMinedTransactions!=null && noMinedTransactions.size()>0) {
			OutTrx outTrx=noMinedTransactions.get(0);
			log.info("Istnieją niewykopane transakcje "+outTrx.getTrxId());
			BigInteger result=ethTransactionApi.checkTransaction(outTrx.getTrxId());
			if(result==null) {
				log.info("Transakcja "+outTrx.getTrxId()+" wciąż nie wykopana lub błędna.");
				return;			
			}else {
				log.info("Transakcja "+outTrx.getTrxId()+" wykopana w bloku: "+result+".");				
				outTrx.setMiningDate(new Date());
				outTrx.setMinedBlock(result);
				databaseAPI.updateTrx(outTrx);				
			}			
		}	
		List<OutTrx> noResultTransactions=databaseAPI.getTrxWithoutResults();
		if(noResultTransactions!=null && noResultTransactions.size()>0) {
			OutTrx outTrx=noResultTransactions.get(0);
			log.info("Istnieją transakcje bez wyników: "+outTrx.getTrxId());
			String blockHash=ethTransactionApi.getLotteryResultsBlockHash(outTrx.getMinedBlock());
			if(blockHash==null) {
				log.info("Brak hasha bloku - trzeba czekać.");
			}else {
				log.info("Znaleziono wyniki losowania: "+blockHash);				
				outTrx.setLotteryResults(blockHash.substring(blockHash.length()-6));
				databaseAPI.updateTrx(outTrx);		
			}
			return;
		}
		
		log.info("GasPrice: " + gasStationApi.getGasPrice() + ". Wait: " + ethGasStation.getSafeLowWait() + ". ETH: "
				+ String.format("%10.2f", ethPriceApi.getEthereumPrice()) + "$ ." + "TRX Price: "
				+ String.format("%10.2f", utils.getTrxPriceInUSD()) + "$ ");
		log.info("Cena za transakcję "+utils.getTrxPrice() + "ETH " + utils.getTrxPriceInUSD()+"$");
		Integer gasPriceLimit=null;
		try {
			gasPriceLimit=Integer.parseInt(databaseAPI.getConfigurationValue("MAX_GAS_PRICE"));
		} catch (NumberFormatException e) {
			log.error("Nieprawidłowa maxymalna wartość GasPrice");
			return;
		}
		
		if(gasStationApi.getGasPrice()<=3) {
			EthTransactionResult result=ethTransactionApi.send(gasStationApi.getGasPrice());
			if(result.getErrorCode()!=0) {
				handleError(result);
				return;
			}
			log.info("Złożono transakcję "+result.getTrxHash());
			databaseAPI.saveEthTrx(result.getTrxHash());
			return;
		}else {
			log.info("GasPrice za wysoki - nie gramy.");
		}
	}

	private void handleError(EthTransactionResult result) {
		switch (result.getErrorCode()) {
		case 1:
			log.error("Brak połączenia z GETH");
			break;

		default:
			break;
		}
		
	}
}
