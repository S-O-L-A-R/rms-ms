package com.solar.ms.rms.model;

import com.solar.ms.rms.model.line.DraftMenuItemOwner;
import lombok.Data;

@Data
public class RemoveMenuItemRequest {
    private DraftMenuItemOwner user;
    private String menuId;
    private String memo;
    private String tableId;
}
