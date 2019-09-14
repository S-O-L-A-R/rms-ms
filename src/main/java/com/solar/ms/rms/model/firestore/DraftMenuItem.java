package com.solar.ms.rms.model.firestore;

import com.solar.ms.rms.model.line.DraftMenuItemOwner;
import lombok.Data;

@Data
public class DraftMenuItem {
    private DraftMenuItemOwner user;
    private String menuId;
    private int quantity;
    private String memo;
    private String tableId;
}
