package pl.net.oth.smartbillions.api;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import pl.net.oth.smartbillions.HibernateUtil;
import pl.net.oth.smartbillions.model.hibernate.Configuration;
import pl.net.oth.smartbillions.model.hibernate.OutTrx;

@Component
public class DatabaseAPI {
	private static final Logger LOG = LoggerFactory.getLogger(EthTransactionApi.class);
	public void saveEthTrx(String trxHash) {
		OutTrx outTrx=new OutTrx();
		outTrx.setTrxId(trxHash);
		outTrx.setCreationDate(new Date());
		outTrx.setMyNumbers(EthTransactionApi.MY_LUCKY_NUMBERS);
		Session session = HibernateUtil.getSessionAnnotationFactory().getCurrentSession();
		session.beginTransaction();
		session.save(outTrx);		
		session.getTransaction().commit();
	}
	public List<OutTrx> getNoMinedTrx() {
		Session session = HibernateUtil.getSessionAnnotationFactory().getCurrentSession();
		session.beginTransaction();
		Query query=session.createQuery("from OutTrx where miningDate is null");		
		List<OutTrx> result=query.list();
		session.getTransaction().commit();
		return result;
		
	}
	public void updateTrx(OutTrx outTrx) {
		Session session = HibernateUtil.getSessionAnnotationFactory().getCurrentSession();
		session.beginTransaction();
		session.saveOrUpdate(outTrx);		
		session.getTransaction().commit();
		
	}
	public List<OutTrx> getTrxWithoutResults() {
		Session session = HibernateUtil.getSessionAnnotationFactory().getCurrentSession();
		session.beginTransaction();
		Query query=session.createQuery("from OutTrx where lotteryResults is null");		
		List<OutTrx> result=query.list();
		session.getTransaction().commit();
		return result;
	}
	public String getConfigurationValue(String key) {
		Session session = HibernateUtil.getSessionAnnotationFactory().getCurrentSession();
		session.beginTransaction();
		Query query=session.createQuery("from Configuration where key=:k");
		query.setParameter("k",key );
		List<Configuration> result=query.list();
		if(result==null || result.size()==0) {
			LOG.error("Brak wpisu z konfiguracją dla klucza "+key);
			return null;
		}
		return result.get(0).getValue();
	}
}
