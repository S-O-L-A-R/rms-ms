package com.solar.ms.rms.model.line;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineMessage {
    private String type;
    private String text;
    private boolean notificationDisabled;
}
