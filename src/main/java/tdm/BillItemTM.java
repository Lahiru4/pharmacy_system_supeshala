package tdm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillItemTM {
    private String description;
    private int qty;
    private double selling_price;
    private String itemCode;
    private double Purchase_price;
}
