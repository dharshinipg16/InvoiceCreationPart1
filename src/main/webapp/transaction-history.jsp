<%@ page import="java.io.IOException" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Transaction History</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script>
        function checkTransactions() {
            // Prevent the form from submitting the traditional way
            event.preventDefault();

            var customerName = $('#customerName').val(); //gets value from id
            var city = $('#city').val();
            var state = $('#state').val();

            $.ajax({
                url: '/demooo/transactions',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    customerName: customerName,
                    city: city,
                    state: state
                }),
                dataType: 'json',

                success: function(response) {

                    console.log(response);

                    // Build the table based on response JSON data
                    var tableHtml = '<table border="1"><thead><tr><th>Invoice ID</th><th>Customer Name</th><th>Order ID</th><th>Total</th><th>Invoice Date</th></tr></thead><tbody>';

                    if (response && response.length > 0) {
                        $.each(response, function(index, transaction) {
                            tableHtml += '<tr>';
                            tableHtml += '<td>' + transaction.iid + '</td>';
                            tableHtml += '<td>' + transaction.icname + '</td>';
                            tableHtml += '<td><a href="OrderItems.jsp?orderId=' + transaction.iorderid + '">' + transaction.iorderid + '</a></td>';
                            tableHtml += '<td>' + transaction.total + '</td>';
                            tableHtml += '<td>' + transaction.invoicedate + '</td>';
                            tableHtml += '</tr>';
                        });
                    } else {
                        tableHtml += '<tr><td colspan="5">No transactions found for the given details.</td></tr>';
                    }

                    tableHtml += '</tbody></table>';
                    $('#transactionTable').html(tableHtml);
                },
                error: function(xhr, status, error) {
                    console.error('Error fetching transaction history:', error, status);
                }
            });
        }
    </script>
</head>
<body>
    <h1>View Your Past Transactions</h1>
    <form id="transactionForm" onsubmit="checkTransactions()">
        <label for="customerName">Customer Name:</label>
        <input type="text" id="customerName" name="customerName" required>

        <label for="city">City:</label>
        <input type="text" id="city" name="city" required>

        <label for="state">State:</label>
        <input type="text" id="state" name="state" required>

        <input type="submit" value="Check Transactions">
    </form>

    <div id="transactionTable"></div>
</body>
</html>


