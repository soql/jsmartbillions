package pl.net.oth.smartbillions.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import pl.net.oth.smartbillions.App;
import pl.net.oth.smartbillions.model.EthGasStation;
import pl.net.oth.smartbillions.model.SMSMessage;

@Component
public class SMSApi {
	@Autowired
	private DatabaseAPI databaseAPI;
	
	public void sendSMS(String message) {
		System.out.println("Wysyłka SMS");
		String address=databaseAPI.getConfigurationValue("SMS_API");
		SMSMessage smsMessage=new SMSMessage();
		smsMessage.setText(message);
		smsMessage.setPhoneNumber(App.PHONE_NUMBER);
		HttpEntity<SMSMessage> request = new HttpEntity<>(smsMessage);
		RestTemplate restTemplate = new RestTemplate();		
		Boolean result = restTemplate.postForObject("http://"+address+"/sendSMS",request, Boolean.class);     	
	}
}
