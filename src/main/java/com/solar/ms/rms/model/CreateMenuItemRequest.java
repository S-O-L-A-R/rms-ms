package com.solar.ms.rms.model;

import javax.validation.Valid;

import com.solar.ms.rms.model.line.DraftMenuItemOwner;
import lombok.Data;

@Data
public class CreateMenuItemRequest {
    @Valid
    private DraftMenuItemOwner user;
    private String menuId;
    private int quantity;
    private String memo;
    private String tableId;
}
