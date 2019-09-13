package com.solar.ms.rms.service;

import com.solar.ms.rms.model.line.LineMessage;
import com.solar.ms.rms.model.line.PushLineMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.solar.ms.rms.config.CommonConstants.AUTHORIZATION_HEADER_BEARER;

@Slf4j
@Service
public class LineMessageService {
    @Autowired
    private HttpHeaders httpHeaders;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${line.channel-access-token:default}")
    private String channelAccessToken;

    @Value("${service.line.push-message.url}")
    private String pushMessageUrl;

    public ResponseEntity<String> sendPushMessage(String to, List<LineMessage> messages){
        httpHeaders.set(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_BEARER + channelAccessToken);
        PushLineMessageRequest pushLineMessageRequest = new PushLineMessageRequest(to, messages);

        return restTemplate.exchange(
                pushMessageUrl,
                HttpMethod.POST,
                new HttpEntity<>(pushLineMessageRequest, httpHeaders),
                String.class
        );
    }
}
