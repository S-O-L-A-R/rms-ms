package com.solar.ms.rms.model.line;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushLineMessageRequest {
    private String to;
    private List<LineMessage> messages;
}
