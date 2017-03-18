// *********************************
// Funções da página de cadastro  **
// *********************************

$( document ).ready(function() {
    var $signupForm = $("#signupForm");
    var urlSucess = "http://localhost:9000/login"

    $signupForm.off('submit');
    $signupForm.on('submit', function () {
        $.ajax({
            url: $signupForm.attr('action'),
            type: $signupForm.attr('method'),
            contentType: "application/json",
            data: $signupForm.formAsJson(),
            success:function(){
                alert("Cadastro realizado com sucesso!");
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

