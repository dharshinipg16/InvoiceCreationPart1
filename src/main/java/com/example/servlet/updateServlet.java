package com.example.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class UpdateRequest {
    private String icname;
    private String iorderid;
    private String neworderid;

    public String geticname() { return icname; }
    public void seticname(String icname) { this.icname = icname; }
    public String getiorderid() { return iorderid; }
    public void setiorderid(String iorderid) { this.iorderid = iorderid; }
    public String getneworderid() { return neworderid; }
    public void setneworderid(String neworderid) { this.neworderid = neworderid; }

}

//updates the order id

//@WebServlet("/demooo/update-invoice")
public class updateServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/demo";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "MYSQL12345@zohocorp";

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        Gson gson = new Gson();

        UpdateRequest updateRequest = null;
        try {
            updateRequest = gson.fromJson(request.getReader(), UpdateRequest.class);

            if (updateRequest == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Invalid request payload.\"}");
                out.flush();
                return;
            }
        } catch (JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Malformed JSON.\"}");
            out.flush();
            return;
        }

        String message = "failure";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Start transaction
            conn.setAutoCommit(false);

            // First update
            String sql1 = "UPDATE invoices SET iorderid = ? WHERE LOWER(iorderid) = LOWER(?)";
            try (PreparedStatement pstmt1 = conn.prepareStatement(sql1)) {
                pstmt1.setString(1, updateRequest.getneworderid());
                pstmt1.setString(2, updateRequest.getiorderid());
                int rowsAffected1 = pstmt1.executeUpdate();

                if (rowsAffected1 <= 0) {
                    conn.rollback();
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"error\": \"Failed to update invoices.\"}");
                    out.flush();
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                conn.rollback();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Error executing first update.\"}");
                out.flush();
                return;
            }

            // Second update
            String sql2 = "UPDATE orders SET orderid = ? WHERE LOWER(orderid) = LOWER(?)";
            try (PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
                pstmt2.setString(1, updateRequest.getneworderid());
                pstmt2.setString(2, updateRequest.getiorderid());
                int rowsAffected2 = pstmt2.executeUpdate();

                if (rowsAffected2 <= 0) {
                    conn.rollback();
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"error\": \"Failed to update orders.\"}");
                    out.flush();
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                conn.rollback();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Error executing second update.\"}");
                out.flush();
                return;
            }

            // Commit transaction
            conn.commit();
            message = "success";
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"An error occurred while processing the request.\"}");
            out.flush();
            return;
        }

        // Write JSON response
        String jsonResponse = gson.toJson(message);
        out.print(jsonResponse);
        out.flush();

    }
}
