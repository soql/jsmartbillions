package pl.net.oth.smartbillions;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pl.net.oth.smartbillions.api.EthPriceApi;
import pl.net.oth.smartbillions.api.GasStationApi;
import pl.net.oth.smartbillions.api.SMSApi;
import pl.net.oth.smartbillions.model.EthGasStation;
import pl.net.oth.smartbillions.model.hibernate.OutTrx;

@Component
public class Timer {	
	@Autowired
	private GasStationApi gasStationApi;
	
	@Autowired
	private EthPriceApi ethPriceApi;
	
	@Autowired
	private Utils utils;
	
	@Autowired
	private SMSApi smsApi;
	
	 private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	 @Scheduled(fixedRate = 5000)
	    public void runProcess() {		 
		 	System.out.println("The time is now "+dateFormat.format(new Date()));
	        EthGasStation ethGasStation=gasStationApi.getEthInfo();
	        if(ethGasStation!=null && ethPriceApi!=null) {
	        System.out.println("GasPrice "+ethGasStation.getSafeLow()+" wait "+ethGasStation.getSafeLowWait());
	        System.out.println(gasStationApi.getGasPrice());
	        System.out.println(ethPriceApi.getEthereumPrice());
	        System.out.println(utils.getTrxPrice()+" "+utils.getTrxPriceInUSD());
	        }
	      /*  OutTrx outTrx=new OutTrx();
	        outTrx.setTrxId("dfijfdoiojfd"+dateFormat.format(new Date()));
	        SessionFactory sessionFactory = HibernateUtil.getSessionAnnotationFactory();
	        Session session = sessionFactory.getCurrentSession();
	        session.beginTransaction(); 
	        session.save(outTrx);
	        session.getTransaction().commit();	*/        
	        
	    }
}
