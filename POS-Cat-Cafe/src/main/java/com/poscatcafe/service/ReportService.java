package com.poscatcafe.service;

import com.poscatcafe.model.Order;
import com.poscatcafe.model.OrderItem;
import com.poscatcafe.model.Product;
import com.poscatcafe.repository.OrderRepository;
import com.poscatcafe.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public Map<String, Object> getReportsData() {
        Map<String, Object> reportsData = new LinkedHashMap<>();

        List<Order> allOrders = orderRepository.findAll();
        List<Product> allProducts = productRepository.findAll();

        // Calculate total sales, total orders, total products
        double totalSales = allOrders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
        long totalOrders = allOrders.size();
        long totalProducts = allProducts.size();

        reportsData.put("totalSales", totalSales);
        reportsData.put("totalOrders", totalOrders);
        reportsData.put("totalProducts", totalProducts);

        // Prepare sales data for chart (daily sales)
        Map<LocalDate, Double> dailySales = allOrders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getOrderDate().atZone(ZoneId.systemDefault()).toLocalDate(),
                        Collectors.summingDouble(Order::getTotalAmount)
                ));

        List<String> salesLabels = dailySales.keySet().stream()
                .sorted()
                .map(LocalDate::toString)
                .collect(Collectors.toList());
        List<Double> salesData = dailySales.keySet().stream()
                .sorted()
                .map(dailySales::get)
                .collect(Collectors.toList());

        Map<String, Object> salesChartData = new LinkedHashMap<>();
        salesChartData.put("labels", salesLabels);
        salesChartData.put("data", salesData);
        reportsData.put("salesData", salesChartData);

        // Prepare product sales data for chart (quantity sold per product)
        Map<String, Long> productSales = allOrders.stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(
                        orderItem -> orderItem.getProduct().getName(),
                        Collectors.summingLong(OrderItem::getQuantity)
                ));

        List<String> productSalesLabels = productSales.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
        List<Long> productSalesData = productSales.keySet().stream()
                .sorted()
                .map(productSales::get)
                .collect(Collectors.toList());

        Map<String, Object> productSalesChartData = new LinkedHashMap<>();
        productSalesChartData.put("labels", productSalesLabels);
        productSalesChartData.put("data", productSalesData);
        reportsData.put("productSalesData", productSalesChartData);

        return reportsData;
    }
}
