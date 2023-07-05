package tdm;

import com.jfoenix.controls.JFXButton;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockTM {
    private String id;
    private String importer;
    private String areaDistributor;
    private String salesRepresent;
    private String agency;
    private int totalQty;
    private double totalPurchasePrice;
    private String stockDate;
    private JFXButton btn;
}
