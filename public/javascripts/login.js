// ******************************
// Funções da página de login  **
// ******************************

$( document ).ready(function() {
    var $loginForm = $("#loginForm");
    var urlSucess = "http://localhost:9000/"

    $loginForm.off('submit');
    $loginForm.on('submit', function () {
        $.ajax({
            url: $loginForm.attr('action'),
            type: $loginForm.attr('method'),
            contentType: "application/json",
            data: $loginForm.formAsJson(),
            success:function(){
                // Tentar mudar essa abordagem
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

