package pl.net.oth.smartbillions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

import pl.net.oth.smartbillions.api.EthPriceApi;
import pl.net.oth.smartbillions.api.GasStationApi;
import pl.net.oth.smartbillions.model.hibernate.OutTrx;
import pl.net.oth.smartbillions.api.EthTransactionApi;

@Component
public class Utils {
	@Autowired
	private GasStationApi gasStationApi;
	
	@Autowired
	private EthPriceApi ethPriceApi;
	
	@Autowired
	private EthTransactionApi ethTransactionApi;
	
	public Double getTrxPrice() {
		return Convert.fromWei(
				Convert.toWei(
						String.valueOf(gasStationApi.getGasPrice()*EthTransactionApi.GAS_LIMIT), Unit.GWEI
				), Unit.ETHER).doubleValue();
		
	}
	public Double getTrxPriceInUSD() {
		return getTrxPrice()*ethPriceApi.getEthereumPrice();
	}
	public static int checkResults(OutTrx outTrx) {
		int match=0;
		for(int i=0; i<outTrx.getLotteryResults().length(); i++) {
			if(outTrx.getLotteryResults().charAt(i)==outTrx.getMyNumbers().charAt(i))
				match++;
		}
		return match;
	}
}
