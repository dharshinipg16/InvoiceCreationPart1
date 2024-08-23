<%@ page import="java.sql.Connection, java.sql.DriverManager, java.sql.ResultSet, java.sql.SQLException, java.sql.Statement" %>
<%@ page import="java.util.ArrayList, java.util.List" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.sql.Statement" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create Invoice</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f7f7f7;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }
        form {
            background-color: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            max-width: 400px;
            width: 100%;
        }
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
        }
        input[type="text"],
        input[type="date"] {
            width: calc(100% - 22px);
            padding: 8px;
            margin-bottom: 15px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 14px;
        }
        button {
            width: 100%;
            margin: 10px;
            padding: 8px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
        }
        button:hover {
            background-color: #45a049;
        }

        .submit {
            padding:8px;
            margin : 5px;
        }

        .table-wrapper {
            max-height: 200px; /* Set the desired height */
            overflow-y: auto; /* Enable vertical scrolling */
            border: 1px solid #ddd; /* Optional border for better visibility */
            margin: 5px 0; /* Optional margin */
        }

        .table-wrapper2 {
            max-height: 100%; /* Set the desired height */
             /* Enable vertical scrolling */
            border: 1px solid #ddd; /* Optional border for better visibility */
            margin: 5px 0; /* Optional margin */
        }


        table {
            width: 100%; /* Ensure table fits the container width */
            border-collapse: collapse; /* Optional for better appearance */
        }

        th, td {
            padding: 4px; /* Adjust padding as needed */
            text-align: left; /* Align text to the left */
        }

        thead {
            background-color: #f2f2f2; /* Optional for better header visibility */
        }

        th {
            position: sticky;
            top: 0; /* Make the header sticky */
            background-color: #f2f2f2; /* Ensure header has a background */
        }
    </style>
</head>
<body>
    <div class="table-wrapper2">
    <form action="create-invoice" method="post">
        <label for="customerName">Customer Name:</label>
        <input type="text" id="customerName" name="customerName" required>

        <label for="customerAddressLine1">Address line1 :</label>
        <input type="text" id="customerAddressLine1" name="customerAddressLine1" required>

        <label for="city">City</label>
        <input type="text" id="city" name="city" required>

        <label for="state">State:</label>
        <input type="text" id="state" name="state" required>

        <label for="invoiceDate">Invoice Date:</label>
        <input type="date" id="invoiceDate" name="invoiceDate" required>

        <label for="orderId">Order Id</label>
        <input type="text" id="orderId" name="orderId" required>

        <div class = "table-wrapper">
        <table border="1">
            <thead>
                <tr>
                    <th>Product ID</th>
                    <th>Product Name</th>
                    <th>Price</th>
                    <th>Select</th>
                </tr>
            </thead>

            <tbody>
                <%
                    class Product {
                        private int pid;
                        private String pname;
                        private int prate;

                        public Product(int pid, String pname, int prate) {
                            this.pid = pid;
                            this.pname = pname;
                            this.prate = prate;
                        }

                        public int getPid() {
                            return pid;
                        }
                        public String getPname() {
                            return pname;
                        }
                        public int getPrate() {
                            return prate;
                        }
                    }

                    List<Product> products = new ArrayList<>();
                    Connection con = null;
                    Statement stmt = null;
                    ResultSet rs = null;

                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "PUT PASSWORD IN HERE");
                        stmt = con.createStatement();
                        rs = stmt.executeQuery("SELECT * FROM products");

                        while (rs.next()) {
                            int pid = rs.getInt("pid");
                            String pname = rs.getString("pname");
                            int prate = rs.getInt("prate");
                            products.add(new Product(pid, pname, prate));
                        }
                    } catch (ClassNotFoundException | SQLException e) {
                        e.printStackTrace();
                    } finally {
                        if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
                        if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
                        if (con != null) try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
                    }

                    for (Product product : products) {
                %>
                <tr>
                    <td><%= product.getPid() %></td>
                    <td><%= product.getPname() %></td>
                    <td><%= product.getPrate() %></td>
                    <td>
                        <input type="checkbox" name="selectedProducts" value="<%= product.getPid() %>">
                        <select name="quantity_<%= product.getPid() %>">
                            <%
                                for (int i = 1; i <= 10; i++) { // Adjust the range as needed
                            %>
                            <option value="<%= i %>"><%= i %></option>
                            <% } %>
                        </select>
                        <input type="hidden" name="productrate_<%= product.getPid() %>" value="<%= product.getPrate() %>">
                        <input type="hidden" name="productname_<%= product.getPid() %>" value="<%= product.getPname() %>">
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>
        </div>

        <button type="submit">Create Invoice</button>

        <a href="transaction-history.jsp">View Past Transactions</a>

        <a href="deleteInvoice.jsp">Delete past Invoices</a>

        <a href="updateInvoice.jsp">Update past Invoices</a>

    </form>
    </div>
</body>
</html>
