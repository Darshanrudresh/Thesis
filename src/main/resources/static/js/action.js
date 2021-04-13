function letMeCallYou()
{
    alert("Bazinga!!!  you called letMeCallYou")
}

$(function (){
    var $orders = $('#orders');
   $.ajax({
       type: 'GET',
       url: 'http://localhost:8085/json',
       success: function (orders){
           $orders.append('<li>name:'+order.name+'</li>' );
           $.each(orders,function (i,order){
               $orders.append('<li>name:'+order.name+'</li>' );
           });
       }
   });
});

