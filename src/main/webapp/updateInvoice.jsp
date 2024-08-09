<%@ page import="java.io.IOException" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Update Invoice</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>

    <script>
        function updateInvoice() {
            // Prevent the form from submitting the traditional way
            event.preventDefault();

            var icname = $('#icname').val();
            var iorderid = $('#iorderid').val();
            var neworderid = $('#neworderid').val();

            $.ajax({
                url: '/demooo/update-invoice',
                type: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify({
                    icname: icname,
                    iorderid : iorderid,
                    neworderid : neworderid
                }),
                dataType: 'json',

                success: function(response) {

                    console.log(response);
                    var message= "";
                    if (response && response === "success") {
                        message = "success, invoice updated ";
                    } else {
                        message = "invoice not updated , maybe invalid details ? check again !";
                    }

                    $('#result').html(message);
                },
                error: function(xhr, status, error) {
                    console.error('Error updating invoice details:', error, status);
                    $('#result').html("An error occurred while updating the invoice.");
                }
            });
        }
    </script>
</head>
<body>
    <h1>Update past invoices using ID</h1>
    <form id="updateInvoiceForm" onsubmit="updateInvoice()">


        <label for="icname">Name:</label>
        <input type="text" id="icname" name="icname" required>

        <label for="iorderid">Order Id:</label>
        <input type="text" id="iorderid" name="iorderid" required>

        <label for="neworderid">NEW Order Id:</label>
        <input type="text" id="neworderid" name="neworderid" required>

        <input type="submit" value="updateInvoice">
    </form>

    <div id="result"></div>
</body>
</html>


