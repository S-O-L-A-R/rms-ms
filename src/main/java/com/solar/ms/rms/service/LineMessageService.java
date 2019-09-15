package com.solar.ms.rms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.solar.ms.rms.model.line.LineMessage;
import com.solar.ms.rms.model.line.LineMessageRequest;

import static com.solar.ms.rms.config.CommonConstants.AUTHORIZATION_HEADER_BEARER;

import lombok.extern.slf4j.Slf4j;

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

    @Value("${service.line.broadcast-message.url}")
    private String broadcastMessageUrl;

    public ResponseEntity<String> sendPushMessage(String to, List<LineMessage> messages){
        httpHeaders.set(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_BEARER + " " + channelAccessToken);
        LineMessageRequest lineMessageRequest = new LineMessageRequest(to, messages);

        return restTemplate.exchange(
                pushMessageUrl,
                HttpMethod.POST,
                new HttpEntity<>(lineMessageRequest, httpHeaders),
                String.class
        );
    }

    public ResponseEntity<String> sendBroadCastMessage(List<LineMessage> messages){
        httpHeaders.set(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_BEARER + " " + channelAccessToken);
        log.info("{}", httpHeaders);
        LineMessageRequest lineMessageRequest = new LineMessageRequest().setMessages(messages);

        return restTemplate.exchange(
                broadcastMessageUrl,
                HttpMethod.POST,
                new HttpEntity<>(lineMessageRequest, httpHeaders),
                String.class
        );
    }
}
