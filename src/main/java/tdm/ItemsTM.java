package tdm;

import javafx.scene.image.ImageView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemsTM {
    private String itemCode;
    private String description;
    private int QTY;
    private double selling_price;
    private double Purchase_price;
    private ImageView img;
    private String suppler_Id;
    private String ex_date;
}
