package tdm;

import javafx.scene.image.ImageView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@NoArgsConstructor@AllArgsConstructor
public class BillTable2 {
    private String description;
    private int qty;
    private double selling_price;
    private String itemCode;
    private double Purchase_price;
    private double net_tot;

}
