package com.example.servlet;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.annotation.WebServlet;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

class Product {
    private int pid;
    private String pname;
    private int prate;

    public Product(int pid, String pname, int prate) {
        this.pid = pid;
        this.pname = pname;
        this.prate = prate;
    }

    public int getPid() { return pid; }
    public String getPname() { return pname; }
    public int getPrate() { return prate; }
}
//@WebServlet("/create-invoice")
public class InvoiceServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String customerName = request.getParameter("customerName");
        int customerid = 0;
        String invoiceDate = request.getParameter("invoiceDate");
        String customerAddressLine1 = request.getParameter("customerAddressLine1");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String orderId = request.getParameter("orderId");
        LocalDate currentDate = LocalDate.now();
        LocalDateTime currentDateTime = LocalDateTime.now();
        String[] selectedProducts = request.getParameterValues("selectedProducts");

        //***** DECLARATION ***********************************************************************************************************************************************
        Connection con = null;
        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        //*************************************************************************************************************************************************

        // INSERTING CUSTOMER DETAILS IF NOT PRESENT PREVIOUSLY
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "PUT PASSWORD IN HERE");

            // Check if the customer already exists
            String checkSql = "SELECT * FROM customers WHERE cname = ? AND ccity = ? AND cstate = ?";
            pstmt = con.prepareStatement(checkSql);
            pstmt.setString(1, customerName);
            pstmt.setString(2, city);
            pstmt.setString(3, state);

            rs = pstmt.executeQuery();

            if (!rs.next()) {
                // Customer does not exist, so insert the new customer
                String insertSql = "INSERT INTO customers (cname, caddress, ccity, cstate) VALUES (?, ?, ?, ?)";
                pstmt = con.prepareStatement(insertSql);
                pstmt.setString(1, customerName);
                pstmt.setString(2, customerAddressLine1);
                pstmt.setString(3, city);
                pstmt.setString(4, state);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    // Insertion successful
                    System.out.println("Customer details inserted successfully.");
                } else {
                    // Insertion failed
                    System.out.println("Failed to insert customer details.");
                }
            } else {
                // Customer already exists
                System.out.println("Customer already exists.");
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (con != null) try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        //*************************************************************************************************************************************************

        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

            // Draw a border around the entire page
            float margin = 20; // Margin for the border
            contentStream.setStrokingColor(Color.GRAY); // Set border color
            contentStream.setLineWidth(1); // Set border width
            contentStream.addRect(margin, margin, page.getMediaBox().getWidth() - 2 * margin, page.getMediaBox().getHeight() - 2 * margin);
            contentStream.stroke();

            // Store info TOP
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 15);
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.newLineAtOffset(50, 700);
            contentStream.showText("TAX INVOICE");
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(0, -15); // Move to the next line with some spacing
            contentStream.showText("TamilNadu");
            contentStream.newLineAtOffset(0, -15); // Move to the next line with some spacing
            contentStream.showText("India");
            contentStream.newLineAtOffset(0, -15); // Move to the next line with some spacing
            contentStream.showText("admin@gmail.com");
            contentStream.endText();

            // Title
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 25);
            contentStream.setNonStrokingColor(Color.BLUE); // Set text color to blue
            contentStream.newLineAtOffset(450, 700);
            contentStream.showText("COUTRE");
            contentStream.endText();

            // Draw a line separator
            contentStream.setStrokingColor(Color.GRAY); // Set line color
            contentStream.moveTo(20, 620);
            contentStream.lineTo(page.getMediaBox().getWidth() - margin, 620);
            contentStream.stroke();

            // Draw horizontal line from X=250, Y=620 to X=250, Y=445
            contentStream.setLineWidth(1); // Set line width
            contentStream.setStrokingColor(Color.GRAY); // Set line color
            contentStream.moveTo(350, 620); // Start point (X=250, Y=620)
            contentStream.lineTo(350, 445); // End point (X=250, Y=445)
            contentStream.stroke();

            // 2nd part
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.newLineAtOffset(50, 600);
            contentStream.showText("#");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Invoice Date");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Terms");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Order id");

            contentStream.newLineAtOffset(100, 45);
            contentStream.showText(": INV" + currentDateTime);
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText(": " + invoiceDate);
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText(": Due on Receipt");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText(": " + orderId);
            contentStream.endText();

            // Draw a line separator
            contentStream.setStrokingColor(Color.GRAY); // Set line color
            contentStream.moveTo(20, 545);
            contentStream.lineTo(page.getMediaBox().getWidth() - margin, 545);
            contentStream.stroke();

            // Customer Info
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.setNonStrokingColor(Color.BLACK); // Set text color to black
            contentStream.newLineAtOffset(100, 530);
            contentStream.showText("BILL TO");
            contentStream.newLineAtOffset(325, 0); // Move to the next line with some spacing
            contentStream.showText("SHIP TO");
            contentStream.endText();

            // Draw horizontal line for "BILL TO" and "SHIP TO"
            contentStream.setStrokingColor(Color.GRAY); // Set line color
            contentStream.moveTo(20, 520);
            contentStream.lineTo(page.getMediaBox().getWidth() - margin, 520);
            contentStream.stroke();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.setNonStrokingColor(Color.BLACK); // Set text color to black
            contentStream.newLineAtOffset(50, 500);
            contentStream.showText(customerName);
            contentStream.newLineAtOffset(0, -15); // Move to the next line with some spacing
            contentStream.showText(customerAddressLine1);
            contentStream.newLineAtOffset(0, -15); // Move to the next line with some spacing
            contentStream.showText(city);
            contentStream.newLineAtOffset(0, -15); // Move to the next line with some spacing
            contentStream.showText(state);
            contentStream.endText();

            // Draw horizontal line after customer info
            contentStream.setStrokingColor(Color.GRAY); // Set line color
            contentStream.moveTo(20, 445);
            contentStream.lineTo(page.getMediaBox().getWidth() - margin, 445);
            contentStream.stroke();

            // Add additional customer address info
            contentStream.beginText();
            contentStream.newLineAtOffset(375, 500);
            contentStream.showText(customerAddressLine1);
            contentStream.newLineAtOffset(0, -15); // Move to the next line with some spacing
            contentStream.showText(city);
            contentStream.newLineAtOffset(0, -15); // Move to the next line with some spacing
            contentStream.showText(state);
            contentStream.endText();

            //****************ITEM DISPLAY****************
            // Define column widths and initial Y position
            float xStart = 50;
            float yStart = 425;
            float itemNameWidth = 200;
            float quantityWidth = 80;
            float rateWidth = 100;
            float amountWidth = 100;
            float columnSpacing = 10; // Space between columns

            // Define table header
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.beginText();
            contentStream.newLineAtOffset(xStart, yStart);
            contentStream.showText("ITEMNAME");
            contentStream.newLineAtOffset(itemNameWidth + columnSpacing, 0);
            contentStream.showText("QUANTITY");
            contentStream.newLineAtOffset(quantityWidth + columnSpacing, 0);
            contentStream.showText("RATE");
            contentStream.newLineAtOffset(rateWidth + columnSpacing, 0);
            contentStream.showText("AMOUNT");
            contentStream.endText();

            // Draw a line under the header
            contentStream.setStrokingColor(Color.GRAY);
            contentStream.setLineWidth(1);
            contentStream.moveTo(xStart, yStart - 10);
            contentStream.lineTo(xStart + itemNameWidth + quantityWidth + rateWidth + amountWidth + 3 * columnSpacing, yStart - 10);
            contentStream.stroke();

            // Set font for table content
            contentStream.setFont(PDType1Font.HELVETICA, 10);

            // Draw each item row
            float rowHeight = 15; // Height of each row
            float yPosition = yStart - 30; // Start below header line

            double total = 0;

            //******* INPUT ITEMS FROM ORDER TO PDF AND DATABASE ******************
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "MYSQL12345@zohocorp");

                StringBuilder checker = new StringBuilder();
                if (selectedProducts != null) {
                    for (String productId : selectedProducts) {

                        String quantityParam = request.getParameter("quantity_" + productId);
                        int quantity = quantityParam != null ? Integer.parseInt(quantityParam) : 0;

                        String priceParam = request.getParameter("productrate_" + productId);
                        double price = priceParam != null ? Double.parseDouble(priceParam) : 0;

                        String nameParam = request.getParameter("productname_" + productId);
                        String name = nameParam != null ? nameParam: "";

                        double amount = quantity  * price;
                        total += amount;

                        contentStream.beginText();
                        contentStream.newLineAtOffset(xStart, yPosition);
                        contentStream.showText(name);
                        contentStream.newLineAtOffset(itemNameWidth + columnSpacing, 0);
                        contentStream.showText(String.valueOf(quantity));
                        contentStream.newLineAtOffset(quantityWidth + columnSpacing, 0);
                        contentStream.showText(String.format("%.2f", price));
                        contentStream.newLineAtOffset(rateWidth + columnSpacing, 0);
                        contentStream.showText(String.format("%.2f", amount));
                        contentStream.endText();

                        yPosition -= rowHeight;

                        // Item insertion in orders
                        String sql = "INSERT INTO orders (orderid,OItemId,oItemName,oQuantity) VALUES (?,?,?,?)";
                        pstmt = con.prepareStatement(sql);
                        pstmt.setString(1,orderId);
                        pstmt.setInt(2,Integer.parseInt(productId));
                        pstmt.setString(3,name);
                        pstmt.setInt(4,quantity);

                        int rowsaffected = pstmt.executeUpdate();
//                        if (rowsaffected >0) {
//                            System.out.println("Order Item details updated sucessfully");
//                        }else {
//                            System.out.println("Failed Order Item Insertion");
//                        }

                        System.out.println("Product ID: " + productId + ", Product Name: " + name + ", Quantity: " + quantity + ", Price: " + price);
                        checker.append("\n" + "Product ID: ").append(productId).append(", Product Name: ").append(name).append(", Quantity: ").append(quantity).append(", Price: ").append(price);
                    }
                    response.setContentType("application/json");
                    response.getWriter().write("{checker: }" + checker);
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } finally {
                if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
                if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
                if (con != null) try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }


            //*******************************************************************************************************************************************************************************************
            //NOW HAVING CUSTOMER DETAILS (ID), ORDER DETAILS AND AMOUNT CALCULATED, INSERT ALL IN THE INVOICE TABLE
            try {
                // Load the JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "MYSQL12345@zohocorp");

                // Check if the customer exists
                String checkSql = "SELECT cid FROM customers WHERE cname = ? AND ccity = ? AND cstate = ?";
                pstmt = con.prepareStatement(checkSql);
                pstmt.setString(1, customerName);
                pstmt.setString(2, city);
                pstmt.setString(3, state);

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    // Retrieve customer ID
                    customerid = rs.getInt("cid");
                    System.out.println("Customer ID found: " + customerid);
                } else {
                    System.out.println("Customer does not exist.");
                    // Handle case where customer does not exist (e.g., insert new customer)
                }

                // Close previous PreparedStatement and ResultSet
                if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
                if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }

                // Insert invoice details
                String insertsql = "INSERT INTO invoices (icname, icid, iorderid, total, invoicedate) VALUES (?, ?, ?, ?, ?)";
                pstmt = con.prepareStatement(insertsql);
                pstmt.setString(1, customerName);
                pstmt.setInt(2, customerid);
                pstmt.setString(3, orderId);
                pstmt.setDouble(4, total); // Use setDouble() if total is a double
                pstmt.setString(5, invoiceDate);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Invoice details inserted successfully.");
                } else {
                    System.out.println("Failed to insert invoice details.");
                }

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } finally {
                // Close resources
                if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
                if (con != null) try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            //********************************************************************************************************************************************************************************************


            //STROKE JUST FOR TAX/AMOUNT
            contentStream.setStrokingColor(Color.GRAY); // Set line color
            contentStream.moveTo(xStart + itemNameWidth+quantityWidth+ columnSpacing*2, yPosition - 10);
            contentStream.lineTo(xStart + itemNameWidth +quantityWidth +rateWidth + amountWidth +columnSpacing*3  , yPosition - 10);
            contentStream.stroke();

            yPosition-=25; // UPDATE POS AFTER STROLE TO AVOID OVERLAPPING
            double tax = (0.12) * total;

            //SUB TOTAL
            contentStream.beginText();
            contentStream.newLineAtOffset(xStart + itemNameWidth+quantityWidth+ columnSpacing*2 , yPosition);
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.showText("Sub Total");
            contentStream.newLineAtOffset(rateWidth + columnSpacing,0);
            contentStream.showText(String.format("%.2f", total));
            contentStream.endText();

            //TAX AMOUNT DISPLAY
            yPosition-=15;
            contentStream.beginText();
            contentStream.newLineAtOffset(xStart + itemNameWidth+quantityWidth+ columnSpacing*2 , yPosition);
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.showText("Tax charges");
            contentStream.newLineAtOffset(rateWidth + columnSpacing,0);
            contentStream.showText(String.format("%.2f", tax));
            contentStream.endText();

            //SHIPPING CHARGES
            yPosition-=15;
            contentStream.beginText();
            contentStream.newLineAtOffset(xStart + itemNameWidth+quantityWidth+ columnSpacing*2 , yPosition);
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.showText("Shipping Charges");
            contentStream.newLineAtOffset(rateWidth + columnSpacing,0);
            contentStream.showText("200.00");
            contentStream.endText();

            //STROKE JUST FOR TAX/AMOUNT
            contentStream.setStrokingColor(Color.GRAY); // Set line color
            contentStream.moveTo(xStart + itemNameWidth+quantityWidth+ columnSpacing*2, yPosition - 10);
            contentStream.lineTo(xStart + itemNameWidth +quantityWidth +rateWidth + amountWidth +columnSpacing*3  , yPosition - 10);
            contentStream.stroke();


            //TOTAL AMOUNT PRINTING
            yPosition-=25;
            contentStream.beginText();
            contentStream.newLineAtOffset(xStart + itemNameWidth+quantityWidth+ columnSpacing*2 , yPosition);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.showText("TOTAL");
            contentStream.newLineAtOffset(rateWidth + columnSpacing,0);
            contentStream.showText(String.format("Rs. %.2f", total+tax+200));
            contentStream.endText();

            //PAYED AMOUNT PRINTING
            yPosition-=25;
            contentStream.beginText();
            contentStream.newLineAtOffset(xStart + itemNameWidth+quantityWidth+ columnSpacing*2 , yPosition);
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.showText("Payment Made");
            contentStream.newLineAtOffset(rateWidth + columnSpacing,0);
            contentStream.setNonStrokingColor(Color.RED);
            contentStream.showText(String.format("(-) %.2f", total+tax+200));
            contentStream.endText();

            //BALANCE PRINTING
            yPosition-=25;
            contentStream.beginText();
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.newLineAtOffset(xStart + itemNameWidth+quantityWidth+ columnSpacing*2 , yPosition);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.showText("Balance Due");
            contentStream.newLineAtOffset(rateWidth + columnSpacing,0);
            contentStream.showText("Rs.0.00");
            contentStream.endText();




            // Draw a line separator at the end of the items list
            contentStream.setStrokingColor(Color.GRAY); // Set line color
            contentStream.moveTo(20, yPosition - 10);
            contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition - 10);
            contentStream.stroke();

//            // Draw a closed grey stroke box around the content
//            float boxX = 45; // X position for the box
//            float boxY = 495; // Y position for the top of the box
//            float boxWidth = 510; // Width of the box
//            float boxHeight = 700 - yPosition; // Height of the box
//
//            contentStream.setStrokingColor(Color.GRAY); // Set box stroke color
//            contentStream.addRect(boxX, boxY - boxHeight, boxWidth, boxHeight);
//            contentStream.stroke();

        }


        //STORING THE PDF IN FILE
        String fileName = "invoice_" + customerName + "_"+ System.currentTimeMillis() + ".pdf";
        String directoryPath = getServletContext().getRealPath("/") + "invoices/";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdir();
        }
        String filePath = directoryPath + fileName;
        document.save(new File(filePath));
        document.close();

        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"Invoice created successfully\", \"filePath\": \"" + filePath + "\"}");
    }
}

