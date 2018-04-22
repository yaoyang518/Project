$(function () {
     var token = $.AMUI.utils.cookie.get("token");
     var idflag = false;
     var amountState = false
     checkId();
     
     function checkId() {
    	 $("#userid").on('input', function() {
    			var id = $('#userid').val();
    			if(id){
    				$.get("/check/user/" + id, function(result, status) {
    					if (status == "success") {
    						switch (result.data.code) {
    						case "0012":// id不存在
    							$("#giveUser").html("该用户不存在");
    							$("#giveUser").addClass("am-text-danger");
    							idflag = false;
    							check(amountState,idflag);
    							break;
    						case "0001":// id存在
    							$("#giveUser").html("用户手机: " + result.data.username);
    							$("#giveUser").removeClass("am-text-danger");
    							idflag = true;
    							check(amountState,idflag);
    							break;
    						 case "0010":
    			                    location.replace("/login.html");
    			                    break;
    						 case "0027":
 			                    location.replace("/login.html");
 			                    break;
    						}
    					} else {
    						alert("请求失败")
    					}

    				});
    			}else{
    				$("#giveUser").html("id不能为空");
    				$("#giveUser").addClass("am-text-danger");
    				idflag = false;
    				check(amountState,idflag);
    			}
    			
    		})
    	 
     }
     
     $("#amount").on('input', function () {
         var reg = /(^[1-9]{1}[0-9]*$)|(^[0-9]*\.[0-9]{2}$)/
         var amount = $("#amount").val();
         if (!reg.test(amount)) {
             $("#amountErr").html("只能输入正数，最多保留2位小数")
             amountState = false;
             check(amountState,idflag);
         } else {
             $("#amountErr").html("")
             amountState = true;
             check(amountState,idflag);
         }
     })
     
     function check(amountState,idflag){
    	 if(amountState && idflag){
    		 $("#give").attr("disabled",false);
    	 }else{
    		 $("#give").attr("disabled",true);
    	 }
     }
     
     $('#give').click(
 			function() {
 				var giveId = $('#userid').val();
 				var amount = $('#amount').val();
 				if (amountState && idflag) {
 					$.ajax({
 						type: "POST",
    	                url: "/userApi/bitcoinRecord/give?giveId="+giveId+"&amount="+amount,
    	                contentType: "application/json; charset=utf-8",
    	                beforeSend: function (request) {
    	                    request.setRequestHeader("token", token);
    	                },
 						success : function(result) {
 							switch (result.data.code) {
 							case "0000":
 								 location.replace("/user/payment/giveSuccess.html");
 								 break;
 							case "0049":
 								  $("#amountErr").html("用户余额不足");
 								break;
 							case "0059":
								  $("#amountErr").html("不能转赠给自己");
								break;
							case "0010":
								location.replace("/login.html");
								break;
							case "0027":
								location.replace("/login.html");
								break;
 							}
 						}
 					})
 				} else {
 					$("#give").attr("disabled",true);
 				}
 			})
})