package pl.net.oth.smartbillions.api;


import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import pl.net.oth.smartbillions.model.EthGasStation;

@Component
public class GasStationApi {
	
	private EthGasStation ethGasStation;
	@Scheduled(fixedRate = 5000)
	private void getApiInfo() {
		System.out.println("Pobieranie danych z ethGasStation");
		RestTemplate restTemplate = new RestTemplate();		
		HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

		ethGasStation = restTemplate.exchange("https://ethgasstation.info/json/ethgasAPI.json",HttpMethod.GET,entity, EthGasStation.class).getBody();     	
	}
	public EthGasStation getEthInfo() {
		return ethGasStation;
	}
	public Integer getGasPrice() {
		return (int)Double.parseDouble(ethGasStation.getSafeLow())/10;
	}
}
