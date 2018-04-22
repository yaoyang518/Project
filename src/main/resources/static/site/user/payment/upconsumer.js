$(function () {
     var token = $.AMUI.utils.cookie.get("token");
     var price = 10000;
     loadUP();
     function loadUP() {
         showLoading();
         $.ajax({
             type: "GET",
             url: "/userApi/consumerBusinessInfo",
             dataType: "JSON",
             beforeSend: function (request) {
                 request.setRequestHeader("token", token);
             },
             success: function (result) {
                 var data = result.data;
                 if (data.code == "0000") {
                	 price=data.price;
                	 if(data.bitcoin<data.price){
                		 $("#amount").attr("disabled",true);
                		 $("#upErr").html("报单积分不足"+price+",无法升级");
                		 $('#up').removeClass("lv-bg-green");
                		 $('#up').addClass("lv-bg-grey");
                		 $('#up').attr("disabled",true);
                     }else{
                    	 $('#up').attr("disabled",true);
                    	 $("#amount").attr("disabled",false);
                    	 $("#upErr").html("")
                     }
                    $("#username").html(data.username);
                    $("#id").html(data.id);
                 } else if (data.code == "0010" || data.code == "0027") {
                     location.replace("/login.html");
                     return;
                 } else {
                     alert(data.msg);
                 }
                 hideLoading();
             }
         })
     }
     
    $("#amount").on('input', function () {
    	 var amount = $("#amount").val();
    	 if(amount>=price){
    		 $('#up').removeClass("lv-bg-grey");
    		 $('#up').addClass("lv-bg-green");
    		 $('#up').attr("disabled",false);
    	 }else{
    		 $('#up').removeClass("lv-bg-green");
    		 $('#up').addClass("lv-bg-grey");
    		 $('#up').attr("disabled",true);
    	 }
     })

	$('#up').click(
		function () {
			var amount = $("#amount").val();
			showLoading();
			$.ajax({
				type:"POST",
				url:"/userApi/consumerBusiness?amount="+amount,
				contentType: "application/json; charset=utf-8",
				beforeSend: function (request) {
					request.setRequestHeader("token", token);
				},
				success: function (result) {
					var data = result.data;
					if (data.code == "0000") {
						location.replace("/user/payment/upConsuccess.html");
					}else if (data.code == "0035") {
						$("#upErr").html("报单积分不足")
					}else if(data.code == "0010" || data.code == "0027"){
                        location.replace("/login.html");
					}
					hideLoading();
				}
			})
		})
})