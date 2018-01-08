package pl.net.oth.smartbillions;

import java.text.SimpleDateFormat;
import java.util.Date;

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
		
		ethTransactionApi.checkTransaction("0x6074a9cc81ab761c67775b70f495b2d75acc3efb970e143ce56b67b10695d4b9");
		if(1==1)
			return;
		log.info("GasPrice: " + gasStationApi.getGasPrice() + ". Wait: " + ethGasStation.getSafeLowWait() + ". ETH: "
				+ String.format("%10.2f", ethPriceApi.getEthereumPrice()) + "$ ." + "TRX Price: "
				+ String.format("%10.2f", utils.getTrxPriceInUSD()) + "$ ");
		log.info(String.valueOf(gasStationApi.getGasPrice()));
		log.info(String.valueOf(ethPriceApi.getEthereumPrice()));
		log.info(utils.getTrxPrice() + " " + utils.getTrxPriceInUSD());
		
		if(gasStationApi.getGasPrice()<=20) {
			EthTransactionResult result=ethTransactionApi.send(gasStationApi.getGasPrice()/2);
			if(result.getErrorCode()!=0) {
				handleError(result);
				return;
			}
			log.info("Złożono transakcję "+result.getTrxHash());
			databaseAPI.saveEthTrx(result.getTrxHash());
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
