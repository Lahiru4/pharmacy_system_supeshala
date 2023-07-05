package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsDTO {
    private String orderId;
    private String itemCode;
    private String name;
    private int quantity;
    private double sellingPrice;
    private double netTotal;
}

