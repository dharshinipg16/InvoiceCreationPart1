<%@ page import="java.io.IOException" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Delete Invoice</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>

    <script>
        function deleteInvoice() {
            // Prevent the form from submitting the traditional way
            event.preventDefault();

            var icname = $('#icname').val();
            var iorderid = $('#iorderid').val();

            $.ajax({
                url: '/demooo/delete-invoice',
                type: 'DELETE',
                contentType: 'application/json',
                data: JSON.stringify({
                    icname: icname,
                    iorderid : iorderid,
                }),
                dataType: 'json',

                success: function(response) {

                    console.log(response);
                    var message= "";
                    if (response && response === "success") {
                        message = "success";
                    } else {
                        message = "invoice not deleted, maybe invalid details ? check again !";
                    }

                    $('#result').html(message);
                },
                error: function(xhr, status, error) {
                    console.error('Error deleting invoice details:', error, status);
                    $('#result').html("An error occurred while deleting the invoice.");
                }
            });
        }
    </script>
</head>
<body>
    <h1>Delete past invoices using ID</h1>
    <form id="deleteInvoiceForm" onsubmit="deleteInvoice()">


        <label for="icname">Name:</label>
        <input type="text" id="icname" name="icname" required>

        <label for="iorderid">Order Id:</label>
        <input type="text" id="iorderid" name="iorderid" required>

        <input type="submit" value="deleteInvoice">
    </form>

    <div id="result"></div>
</body>
</html>


