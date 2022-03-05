function fetchImg(data, type, full, meta) {
        var bookId = full.id;

        $.get("http://localhost:8080/api/books/bookcover/"+bookId, function (dataImg) {
            return '<img src="data:image/png;base64,' + dataImg + '" />';
        });
    }

$(document).ready(function () {
    $.getJSON("http://localhost:8080/api/books", function (data) {
                $('#table').dataTable( {
                "aaData": data,
                "columns": [
                    { "data": "id" },
                    { "data": "title" },
                    { "data": "authors" },
                    { "data": "PublisherName" },
                    { "data": "amountInStock" }
                ]
                 });
/*
                 $('#table tbody').on('click', 'tr', function () {
                    // todo
                 } );
*/
        });
});
