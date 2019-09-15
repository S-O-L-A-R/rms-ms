package com.solar.ms.rms.service;

import com.solar.ms.rms.model.payment.ConfirmPaymentRequest;
import com.solar.ms.rms.model.payment.ReservePaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentService {
    @Autowired
    @Qualifier(value = "loadBalancedRestTemplate")
    private RestTemplate loadBalancedRestTemplate;

    @Autowired
    private HttpHeaders httpHeaders;

    @Value("${service.payment-ms.linepay-reserve.url}")
    private String linepayReserveUrl;

    @Value("${service.payment-ms.linepay-confirm.url}")
    private String linepayConfirmUrl;

    public ResponseEntity<?> reservePayment(ReservePaymentRequest reservePaymentRequest){
        return loadBalancedRestTemplate.exchange(
                linepayReserveUrl,
                HttpMethod.POST,
                new HttpEntity<>(new HttpHeaders()),
                String.class
        );
    }

    public ResponseEntity<?> confirmPayment(ConfirmPaymentRequest confirmPaymentRequest){
        return loadBalancedRestTemplate.exchange(
                linepayConfirmUrl,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                String.class
        );
    }
}
