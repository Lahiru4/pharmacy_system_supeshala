package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersDTO {
    private String orderId;
    private int totalQuantity;
    private String orderDate;
    private double itemCost;
    private double profit;
}

