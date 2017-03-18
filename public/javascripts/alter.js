// ************************************
// Funções da página de Modificação  **
// ************************************

$( document ).ready(function() {
    var $alterForm = $("#alterForm");
    var urlSucess = "http://localhost:9000/login"

    $alterForm.off('submit');
    $alterForm.on('submit', function () {
        $.ajax({
            url: $alterForm.attr('action'),
            type: $alterForm.attr('method'),
            contentType: "application/json",
            data: $alterForm.formAsJson(),
            success:function(request){
                alert(JSON.stringify(request));
                window.location.href = urlSucess;
                console.log("Sucesso!");
            },
            error: function(request, status, error){
                alert(request.responseText);
            }
        });
        return false;
    });
});

