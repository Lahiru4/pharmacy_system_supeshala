package tdm;

import com.jfoenix.controls.JFXButton;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@AllArgsConstructor@NoArgsConstructor
public class OrderTM {
    private String orderId;
    private int totalQuantity;
    private String orderDate;
    private double itemCost;
    private double profit;
    private JFXButton button;
    private String id;

}
