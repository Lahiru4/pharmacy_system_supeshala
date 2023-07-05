package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDTO {
    private String id;
    private String importer;
    private String areaDistributor;
    private String salesRepresent;
    private String agency;
    private int totalQty;
    private double totalPurchasePrice;
    private String stockDate;
}
