package com.solar.ms.rms.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    public ResponseEntity<String> sendPushReceipt(String to, String storeName, double total) throws IOException {
        httpHeaders.set(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_BEARER + " " + channelAccessToken);

        String receipt = "{\n" + "  \"type\": \"bubble\",\n" + "  \"styles\": {\n" + "    \"footer\": {\n"
                + "      \"separator\": true\n" + "    }\n" + "  },\n" + "  \"body\": {\n" + "    \"type\": \"box\",\n"
                + "    \"layout\": \"vertical\",\n" + "    \"contents\": [\n" + "      {\n" + "        \"type\": \"text\",\n"
                + "        \"text\": \"RECEIPT\",\n" + "        \"weight\": \"bold\",\n" + "        \"color\": \"#1DB446\",\n"
                + "        \"size\": \"sm\"\n" + "      },\n" + "      {\n" + "        \"type\": \"text\",\n"
                + "        \"text\": \"${store_name}\",\n" + "        \"weight\": \"bold\",\n" + "        \"size\": \"xxl\",\n"
                + "        \"margin\": \"md\"\n" + "      },\n" + "      {\n" + "        \"type\": \"separator\",\n"
                + "        \"margin\": \"xxl\"\n" + "      },\n" + "      {\n" + "        \"type\": \"box\",\n"
                + "        \"layout\": \"vertical\",\n" + "        \"margin\": \"xxl\",\n" + "        \"spacing\": \"sm\",\n"
                + "        \"contents\": [\n" + "          {\n" + "            \"type\": \"box\",\n"
                + "            \"layout\": \"horizontal\",\n" + "            \"contents\": [\n" + "              {\n"
                + "                \"type\": \"text\",\n" + "                \"text\": \"ค่าอาหาร\",\n"
                + "                \"size\": \"sm\",\n" + "                \"color\": \"#555555\",\n"
                + "                \"flex\": 0\n" + "              },\n" + "              {\n"
                + "                \"type\": \"text\",\n" + "                \"text\": \"${total} บาท\",\n"
                + "                \"size\": \"sm\",\n" + "                \"color\": \"#111111\",\n"
                + "                \"align\": \"end\"\n" + "              }\n" + "            ]\n" + "          },\n"
                + "          {\n" + "            \"type\": \"separator\",\n" + "            \"margin\": \"xxl\"\n"
                + "          },\n" + "          {\n" + "            \"type\": \"box\",\n"
                + "            \"layout\": \"horizontal\",\n" + "            \"margin\": \"xxl\",\n"
                + "            \"contents\": [\n" + "              {\n" + "                \"type\": \"text\",\n"
                + "                \"text\": \"ITEMS\",\n" + "                \"size\": \"sm\",\n"
                + "                \"color\": \"#555555\"\n" + "              },\n" + "              {\n"
                + "                \"type\": \"text\",\n" + "                \"text\": \"1\",\n"
                + "                \"size\": \"sm\",\n" + "                \"color\": \"#111111\",\n"
                + "                \"align\": \"end\"\n" + "              }\n" + "            ]\n" + "          },\n"
                + "          {\n" + "            \"type\": \"box\",\n" + "            \"layout\": \"horizontal\",\n"
                + "            \"contents\": [\n" + "              {\n" + "                \"type\": \"text\",\n"
                + "                \"text\": \"TOTAL\",\n" + "                \"size\": \"sm\",\n"
                + "                \"color\": \"#555555\"\n" + "              },\n" + "              {\n"
                + "                \"type\": \"text\",\n" + "                \"text\": \"${total} บาท\",\n"
                + "                \"size\": \"sm\",\n" + "                \"color\": \"#111111\",\n"
                + "                \"align\": \"end\"\n" + "              }\n" + "            ]\n" + "          }\n"
                + "        ]\n" + "      },\n" + "      {\n" + "        \"type\": \"separator\",\n"
                + "        \"margin\": \"xxl\"\n" + "      },\n" + "      {\n" + "        \"type\": \"box\",\n"
                + "        \"layout\": \"horizontal\",\n" + "        \"margin\": \"md\",\n" + "        \"contents\": [\n"
                + "          {\n" + "            \"type\": \"text\",\n" + "            \"text\": \"PAYMENT ID\",\n"
                + "            \"size\": \"xs\",\n" + "            \"color\": \"#aaaaaa\",\n" + "            \"flex\": 0\n"
                + "          },\n" + "          {\n" + "            \"type\": \"text\",\n"
                + "            \"text\": \"#${receipt_id}\",\n" + "            \"color\": \"#aaaaaa\",\n"
                + "            \"size\": \"xs\",\n" + "            \"align\": \"end\"\n" + "          }\n" + "        ]\n"
                + "      }\n" + "    ]\n" + "  }\n" + "}";
        receipt = receipt.replace("${store_name}", storeName);
        receipt = receipt.replace("${total}", total + "");
        receipt = receipt.replace("${receipt_id}", UUID.randomUUID().toString().substring(10));

        LineMessage lineMessage = new LineMessage();
        lineMessage.setType("flex");
        lineMessage.setAltText("Receipt");
        lineMessage.setContents(new ObjectMapper().readValue(receipt, Object.class));

        LineMessageRequest lineMessageRequest = new LineMessageRequest();
        lineMessageRequest.setTo(to);
        lineMessageRequest.setMessages(Collections.singletonList(lineMessage));

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
