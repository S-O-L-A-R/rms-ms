package com.solar.ms.rms.service;

import com.solar.ms.rms.model.payment.ConfirmPaymentRequest;
import com.solar.ms.rms.model.payment.ReservePaymentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
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
        ResponseEntity<String> response = loadBalancedRestTemplate.exchange(
                linepayReserveUrl,
                HttpMethod.POST,
                new HttpEntity<>(reservePaymentRequest, httpHeaders),
                String.class
        );

        log.info("RESERVE RESPONSE: {}", response.getBody());

        return response;
    }

    public ResponseEntity<?> confirmPayment(ConfirmPaymentRequest confirmPaymentRequest){
        ResponseEntity<String> response = loadBalancedRestTemplate.exchange(
                linepayConfirmUrl,
                HttpMethod.POST,
                new HttpEntity<>(confirmPaymentRequest, httpHeaders),
                String.class
        );

        log.info("CONFIRM RESPONSE: {}", response.getBody());

        return response;
    }
}
