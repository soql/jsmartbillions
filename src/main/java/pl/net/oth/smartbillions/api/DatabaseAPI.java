package pl.net.oth.smartbillions.api;

import java.util.Date;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import pl.net.oth.smartbillions.HibernateUtil;
import pl.net.oth.smartbillions.model.hibernate.OutTrx;

@Component
public class DatabaseAPI {
	private static final Logger LOG = LoggerFactory.getLogger(EthTransactionApi.class);
	public void saveEthTrx(String trxHash) {
		OutTrx outTrx=new OutTrx();
		outTrx.setTrxId(trxHash);
		outTrx.setCreationDate(new Date());
		Session session = HibernateUtil.getSessionAnnotationFactory().getCurrentSession();
		session.beginTransaction();
		session.save(outTrx);
		session.getTransaction().commit();
	}
}
