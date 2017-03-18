// ************************************
// Funções da página de Modificação  **
// ************************************

$( document ).ready(function() {
    var $deleteLink = $("#deleteLink");
    var urlSucess = "http://localhost:9000/login"

    $deleteLink.off('click');
    $deleteLink.on('click', function () {
        $.ajax({
            url: $deleteLink.attr('link'),
            type: 'GET',
            contentType: "application/json",
            success:function(request){
                alert(request);
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

