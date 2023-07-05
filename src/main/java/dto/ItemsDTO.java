package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemsDTO {
    private String itemCode;
    private String itemDescription;
    private int qty;
    private double sellingPrice;
    private double purchasePrice;
    private String stockID;
    private String expirationDate;
}
