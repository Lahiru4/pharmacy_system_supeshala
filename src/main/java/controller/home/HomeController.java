package controller.home;

import com.jfoenix.controls.JFXTextField;
import dao.custom.DayAmountDAO;
import dao.custom.EmployeeDAO;
import dao.custom.ItemsDAO;
import dao.custom.OrderDAO;
import dao.custom.impl.DayAmountDAOImpl;
import dao.custom.impl.EmployeeDAOImpl;
import dao.custom.impl.ItemsDAOImpl;
import dao.custom.impl.OrderDAOImpl;
import db.DbConnection;
import dto.DaySalesCount;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import lombok.SneakyThrows;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HomeController implements Initializable {
    public Label today_bill_count;
    public Label time;
    public PieChart pieChart;
    public Label employ_count;
    public Label stock_count;
    public Label profit_count;
    public JFXTextField cashierAmount;
    private OrderDAO orderDAO = new OrderDAOImpl();
    EmployeeDAO employeeDAO = new EmployeeDAOImpl();
    ItemsDAO itemsDAO = new ItemsDAOImpl();
    public BarChart bar_chart;
    public DayAmountDAO dayAmountDAO = new DayAmountDAOImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
        time.setText(formattedTime);
        List<DaySalesCount> dayBillCount = getDayBillCount();
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Order Counts");
        series1.getData().add(new XYChart.Data<>(dayBillCount.get(6).getDate(), dayBillCount.get(6).getCount()));
        series1.getData().add(new XYChart.Data<>(dayBillCount.get(5).getDate(), dayBillCount.get(5).getCount()));
        series1.getData().add(new XYChart.Data<>(dayBillCount.get(4).getDate(), dayBillCount.get(4).getCount()));
        series1.getData().add(new XYChart.Data<>(dayBillCount.get(3).getDate(), dayBillCount.get(3).getCount()));
        series1.getData().add(new XYChart.Data<>(dayBillCount.get(2).getDate(), dayBillCount.get(2).getCount()));
        series1.getData().add(new XYChart.Data<>(dayBillCount.get(1).getDate(), dayBillCount.get(1).getCount()));
        series1.getData().add(new XYChart.Data<>(dayBillCount.get(0).getDate(), dayBillCount.get(0).getCount()));
        bar_chart.getData().addAll(series1);
        setPieChartData();
        setEmpCount();
        setInvoiceCounr();
        setStockCount();
        settProfit();

        setCashAmount();
    }

    private void setCashAmount() {
        String day = LocalDate.now().toString();
        try {
            boolean exist = dayAmountDAO.exist(day);
            if (exist) {
                String amount = dayAmountDAO.getAmount(day);
                cashierAmount.setText(amount);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void settProfit() {
        double profit = orderDAO.getProfit(LocalDate.now().toString());
        profit_count.setText(profit + "");
    }

    private void setStockCount() {
        int stockCount = itemsDAO.getStockCount();
        stock_count.setText(stockCount + "");
    }

    private void setInvoiceCounr() {
        try {
            int i = orderDAO.yetDateGetCount(LocalDate.now().toString());
            today_bill_count.setText(i + "");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setEmpCount() {
        int employeeCount = employeeDAO.getEmployeeCount();
        employ_count.setText(employeeCount + "");
    }

    private void setPieChartData() {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        double profit = orderDAO.getProfit(LocalDate.now().toString());
        double cost = orderDAO.getCost(LocalDate.now().toString());
        pieData.add(new PieChart.Data("Profit", profit));
        pieData.add(new PieChart.Data("Purchase Cost", Double.parseDouble(getV(cost - profit))));
        pieData.add(new PieChart.Data("Total", Double.parseDouble(getV(cost))));
        pieData.forEach(data ->
                data.nameProperty().bind(
                        Bindings.concat(
                                data.getName(), " :", data.pieValueProperty()
                        )
                ));
        pieChart.setData(pieData);
    }

    private String getV(double vlu) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String format = decimalFormat.format(vlu);
        return format;
    }

    public List<DaySalesCount> getDayBillCount() {
        ArrayList<DaySalesCount> all = new ArrayList<>();

        LocalDate date = LocalDate.now();
        LocalDate y1 = date.minusDays(1);
        LocalDate y2 = date.minusDays(2);
        LocalDate y3 = date.minusDays(3);
        LocalDate y4 = date.minusDays(4);
        LocalDate y5 = date.minusDays(5);
        LocalDate y6 = date.minusDays(6);


        ArrayList<LocalDate> dates = new ArrayList<>();
        dates.add(date);
        dates.add(y1);
        dates.add(y2);
        dates.add(y3);
        dates.add(y4);
        dates.add(y5);
        dates.add(y6);


        for (LocalDate temp : dates) {
            String s = String.valueOf(temp);
            int tot = getTotCount(s);

            all.add(new DaySalesCount(s, tot));
        }
        return all;
    }

    private int getTotCount(String s) {
        int i = 0;
        try {
            i = orderDAO.yetDateGetCount(s);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return i;
    }

    public void dalyReportOnAction(ActionEvent actionEvent) {
        Thread thread = new Thread() {
            @SneakyThrows
            public void run() {
                String sql = "SELECT\n" +
                        "    SUM(tot_qty) AS tot_qty_Grand_total,\n" +
                        "    SUM(item_cost) AS item_cost_Grand_total,\n" +
                        "COUNT(*) AS orderCount," +
                        "    order_date\n" +
                        "FROM\n" +
                        "    orders where order_date='" + LocalDate.now().toString() + "';";
                JasperDesign jasdi = JRXmlLoader.load(this.getClass().getResourceAsStream("/report/daly_report.jrxml"));
                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(sql);
                jasdi.setQuery(newQuery);
                JasperReport js = JasperCompileManager.compileReport(jasdi);
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("cash_amount", Double.parseDouble(cashierAmount.getText()));
                double cost = orderDAO.getCost(LocalDate.now().toString());
                parameters.put("tot", cost+Double.parseDouble(cashierAmount.getText()));

                JasperPrint jp = JasperFillManager.fillReport(js, parameters, DbConnection.getInstance().getConnection());
                JasperViewer viewer = new JasperViewer(jp, false);
                viewer.show();
            }
        };
        thread.start();

    }

    public void cashierAmountOnAction(ActionEvent actionEvent) {
        try {
            boolean exist = dayAmountDAO.exist(LocalDate.now().toString());
            if (exist) {
                boolean b = dayAmountDAO.upDate(cashierAmount.getText());
                ImageView imageView = new ImageView(new Image("/view/assests/images/icons8-ok-48.png"));
                Notifications.create()
                        .graphic(imageView)
                        .text(" Add Amount Successful ")
                        .title("Successful")
                        .hideAfter(Duration.seconds(5))
                        .position(Pos.TOP_RIGHT)
                        .darkStyle()
                        .show();
            }else {
                boolean save = dayAmountDAO.save(Double.parseDouble(cashierAmount.getText()));
                if (save) {
                    ImageView imageView = new ImageView(new Image("/view/assests/images/icons8-ok-48.png"));
                    Notifications.create()
                            .graphic(imageView)
                            .text(" Add Amount Successful ")
                            .title("Successful")
                            .hideAfter(Duration.seconds(5))
                            .position(Pos.TOP_RIGHT)
                            .darkStyle()
                            .show();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
