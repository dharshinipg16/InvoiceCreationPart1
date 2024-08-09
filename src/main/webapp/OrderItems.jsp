<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Order Items</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script>
        $(document).ready(function() {
            var orderId = getQueryParam('orderId'); //from transaction table elem

            if (orderId) {
                $.ajax({
                    url: '/demooo/orders', // demooo not adding up - added demooo to url temp- but actually just end point url needed
                    type: 'GET',
                    data: { orderId: orderId },
                    dataType: 'json',
                    success: function(response) {

                        console.log(response);
                        var tableHtml = '<table border="1"><thead><tr><th>Product Name</th><th>Quantity</th><th>Rate</th></tr></thead><tbody>';

                        if (response && response.length > 0) {
                            $.each(response, function(index, item) {
                                tableHtml += '<tr>';
                                tableHtml += '<td>' + item.productName + '</td>';
                                tableHtml += '<td>' + item.quantity + '</td>';
                                tableHtml += '<td>' + item.rate + '</td>';
                                tableHtml += '</tr>';
                            });
                        } else {
                            tableHtml += '<tr><td colspan="3">No items found for the given order ID.</td></tr>';
                        }

                        tableHtml += '</tbody></table>';
                        $('#orderItemsTable').html(tableHtml);
                    },
                    error: function(xhr, status, error) {
                        console.error('Error fetching order items:', error);
                        $('#orderItemsTable').html('<p>An error occurred while fetching order items.</p>');
                    }
                });
            }

            function getQueryParam(name) {
                var urlParams = new URLSearchParams(window.location.search);
                return urlParams.get(name);
            }
        });
    </script>
</head>
<body>
    <h1>Order Items</h1>
    <div id="orderItemsTable"></div>
</body>
</html>
