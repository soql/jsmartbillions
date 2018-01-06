package pl.net.oth.smartbillions;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pl.net.oth.smartbillions.api.EthPriceApi;
import pl.net.oth.smartbillions.api.GasStationApi;
import pl.net.oth.smartbillions.api.SMSApi;
import pl.net.oth.smartbillions.model.EthGasStation;

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

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Scheduled(fixedRate = 60000)
	public void runProcess() {		
		EthGasStation ethGasStation = gasStationApi.getEthInfo();
		if (ethGasStation != null && ethPriceApi != null) {
			log.info("GasPrice: " +gasStationApi.getGasPrice() + 
					". Wait: " + ethGasStation.getSafeLowWait()+
					". ETH: "+String.format("%10.2f",ethPriceApi.getEthereumPrice())+"$ ."+
			"TRX Price: "+String.format("%10.2f",utils.getTrxPriceInUSD())+"$ ");
			log.info(String.valueOf(gasStationApi.getGasPrice()));
			log.info(String.valueOf(ethPriceApi.getEthereumPrice()));
			log.info(utils.getTrxPrice() + " " + utils.getTrxPriceInUSD());
		}
		/*
		 * OutTrx outTrx=new OutTrx();
		 * outTrx.setTrxId("dfijfdoiojfd"+dateFormat.format(new Date())); SessionFactory
		 * sessionFactory = HibernateUtil.getSessionAnnotationFactory(); Session session
		 * = sessionFactory.getCurrentSession(); session.beginTransaction();
		 * session.save(outTrx); session.getTransaction().commit();
		 */

	}
}
