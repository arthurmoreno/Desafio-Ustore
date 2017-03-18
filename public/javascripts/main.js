// ***********************************************
// Funções gerais relativas a todas as páginas  **
// ***********************************************

$.fn.formAsJson = function(){
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
//    console.log(o);
    return JSON.stringify(o)
};