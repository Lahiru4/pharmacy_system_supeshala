package tdm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Items {
    private String itemCode;
    private String description;
    private int QTY;
    private double selling_price;
    private double Purchase_price;
    private String suppler_Id;

    private Date expiration_date;

}
