package pl.net.oth.smartbillions.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import pl.net.oth.smartbillions.Timer;
import pl.net.oth.smartbillions.model.EthereumInfo;

@Component
public class EthPriceApi {
	private static final Logger log = LoggerFactory.getLogger(Timer.class);
	private EthereumInfo[] ethereumInfo;
	@Scheduled(fixedRate = 60000)
	private void getApiInfo() {
		log.info("Pobieranie danych z coinmarketcap");
		RestTemplate restTemplate = new RestTemplate();
		ethereumInfo = restTemplate.getForObject("https://api.coinmarketcap.com/v1/ticker/ethereum/", EthereumInfo[].class);     	
	}
	public Double getEthereumPrice() {
		return Double.parseDouble(ethereumInfo[0].getPrice_usd());
	}
}
