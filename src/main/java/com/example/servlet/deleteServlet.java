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

class DeleteRequest {
    private String icname;
    private String iorderid;

    public String geticname() { return icname; }
    public void seticname(String icname) { this.icname = icname; }
    public String getiorderid() { return iorderid; }
    public void setiorderid(String iorderid) { this.iorderid = iorderid; }

}

//@WebServlet("/demooo/delete-invoice")
public class deleteServlet extends HttpServlet {

    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/demo";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "MYSQL12345@zohocorp";

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Read the JSON request body
        Gson gson = new Gson();

        DeleteRequest deleteRequest = null;
        try {
            deleteRequest = gson.fromJson(request.getReader(), DeleteRequest.class);

            if (deleteRequest == null) {
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

        String message = " ";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "DELETE FROM invoices WHERE LOWER(icname) = LOWER(?) AND LOWER(iorderid) = LOWER(?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, deleteRequest.geticname());
                pstmt.setString(2, deleteRequest.getiorderid());

                // Execute the update query and get the number of rows affected
                int rowsAffected = pstmt.executeUpdate();

                // Check if any rows were affected
                if (rowsAffected > 0) {
                    message = "success";
                } else {
                    message = "failure";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                message = "failure"; // Or handle the error appropriately
            }

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
