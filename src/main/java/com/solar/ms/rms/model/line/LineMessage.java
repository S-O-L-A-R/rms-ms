package com.solar.ms.rms.model.line;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LineMessage {
    private String type;
    private String text;
    private boolean notificationDisabled;
    private String altText;
    private Object contents;

    public LineMessage(String type, String text, boolean notificationDisabled) {
        this.type = type;
        this.text = text;
        this.notificationDisabled = notificationDisabled;
    }
}
