package com.example.servlet;

import com.google.gson.Gson;


import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class OrderItem {
    private String productName;
    private int quantity;
    private double rate;

    public OrderItem(String productName, int quantity, double rate) {
        this.productName = productName;
        this.quantity = quantity;
        this.rate = rate;
    }

    // Getters and setters can be added here if needed
}

//@WebServlet("/api/orders")
public class OrdersServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/demo"; //DB CONECTION
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "PUT PASSWORD IN HERE";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String orderId = request.getParameter("orderId");
        if (orderId == null || orderId.isEmpty()) {         // MOSTLY DOES NOT HAPPEN - FOR IMPROPER ACCESS OF ID
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Order ID is required\"}");
            out.flush();
            return;
        }

        // Retrieve order items from the database
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM orders WHERE orderId = ?";

            try (PreparedStatement pstmt = con.prepareStatement(query)) {
                pstmt.setString(1, orderId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    // Collect the results into a list
                    List<OrderItem> orderItems = new ArrayList<>();
                    while (rs.next()) {
                        String productName = rs.getString("oItemName");
                        int quantity = rs.getInt("oQuantity");
                        double rate = getProductRate(con, productName); // HAS SEPARATE QUERY | FUNCTION - GET FROM PRODUCT TABLE

                        OrderItem item = new OrderItem(productName, quantity, rate);
                        orderItems.add(item);
                    }

                    // Convert the oRDER ITEM list to JSON
                    Gson gson = new Gson();
                    String jsonResponse = gson.toJson(orderItems);
                    out.print(jsonResponse);
                }
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"An error occurred while processing the request.\"}");
            e.printStackTrace();
        } finally {
            out.flush();
        }
    }

    //GET PRODUCT RATE FROM THE PRODUCTS TABLE
    private double getProductRate(Connection con, String productName) throws SQLException {
        String query = "SELECT prate FROM products WHERE LOWER(pname) = LOWER(?)";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, productName.trim().toLowerCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("prate");
                } else {
                    return 0.00; // Default value if product not found
                }
            }
        }
    }
}

