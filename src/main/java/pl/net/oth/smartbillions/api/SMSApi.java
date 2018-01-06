package pl.net.oth.smartbillions.api;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import pl.net.oth.smartbillions.App;
import pl.net.oth.smartbillions.model.EthGasStation;
import pl.net.oth.smartbillions.model.SMSMessage;

@Component
public class SMSApi {
	public void sendSMS(String message) {
		System.out.println("Wysyłka SMS");
		SMSMessage smsMessage=new SMSMessage();
		smsMessage.setText(message);
		smsMessage.setPhoneNumber(App.PHONE_NUMBER);
		HttpEntity<SMSMessage> request = new HttpEntity<>(smsMessage);
		RestTemplate restTemplate = new RestTemplate();		
		Boolean result = restTemplate.postForObject("http://zjc.oth.net.pl/sendSMS",request, Boolean.class);     	
	}
}
