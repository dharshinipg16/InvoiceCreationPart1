
package com.example.servlet;

import com.google.gson.Gson;
//import com.google.code.gson;
import javax.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class TransactionRequest {
    private String customerName;
    private String city;
    private String state;

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}

// Class to represent a transaction
class Transaction {
    private int iid;
    private String icname;
    private String iorderid;
    private double total;
    private String invoicedate;

    public int getIid() { return iid; }
    public void setIid(int iid) { this.iid = iid; }
    public String getIcname() { return icname; }
    public void setIcname(String icname) { this.icname = icname; }
    public String getIorderid() { return iorderid; }
    public void setIorderid(String iorderid) { this.iorderid = iorderid; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getInvoicedate() { return invoicedate; }
    public void setInvoicedate(String invoicedate) { this.invoicedate = invoicedate; }
}


//@WebServlet("/transactions")
public class TransactionsServlet extends HttpServlet {

    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/demo";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "MYSQL12345@zohocorp";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Read the JSON request body
        Gson gson = new Gson();
        TransactionRequest transactionRequest = gson.fromJson(request.getReader(), TransactionRequest.class);

        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM invoices WHERE LOWER(icname) = LOWER(?) AND icid IN (SELECT cid FROM customers WHERE lower(ccity) = LOWER(?) AND LOWER(cstate) = LOWER(?) )";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, transactionRequest.getCustomerName());
                pstmt.setString(2, transactionRequest.getCity());
                pstmt.setString(3, transactionRequest.getState());

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Transaction transaction = new Transaction();
                        transaction.setIid(rs.getInt("iid"));
                        transaction.setIcname(rs.getString("icname"));
                        transaction.setIorderid(rs.getString("iorderid"));
                        transaction.setTotal(rs.getDouble("total"));
                        transaction.setInvoicedate(rs.getString("invoicedate"));
                        transactions.add(transaction);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"An error occurred while processing the request.\"}");
            return;
        }

        // Write JSON response
        String jsonResponse = gson.toJson(transactions);
        out.print(jsonResponse);
        out.flush();
    }

}


