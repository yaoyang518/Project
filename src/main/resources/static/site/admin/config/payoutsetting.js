$(function () {
	 initHeader('config');
    var token = $.AMUI.utils.cookie.get("adminToken");

    loadData();
    function loadData() {
        $.ajax({
            type: "GET",
            url: "/config/payoutConfig",
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            success: function (result) {
                var data = result.data;
                if (data.amount) {
                   $("#payout-rule").html('当前提现最小金额是'+data.amount+'元')
                } else {
                   $("#payout-rule").html('提现规则未配置')
                }


                hideLoading();
            }
        })
    }

    $('#level-set').click(function () {
    		var payoutAmount = $("#payoutAmount").val();
    		var r = /^[1-9]\d*00$/;
    		if(payoutAmount==""){
    			$("#payoutAmount").css("border", "1px solid red");
            	$("#payoutError").html("金额配置不能为空")
    		}else if(r.test(payoutAmount)==false){
    			$("#payoutAmount").css("border", "1px solid red");
            	$("#payoutError").html("金额配置必须为100的倍数")
    		}else{
    			set(payoutAmount);
    		}
    		})
    
    function set(payoutAmount) {
        showLoading();
        $.ajax({
            type: "POST",
            url: "/config/payoutConfig/save?amount=" + payoutAmount,
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                   $("#payout-rule").html('当前最低提现金额是'+payoutAmount+'元')
                   $("#payoutAmount").css("border", "1px solid #ccc");
                   $("#payoutError").html("")
                }else if(data.code == "0017"){
                	$("#payoutError").html("金额配置有误")
                } else if(data.code == "0043"){
                	$("#payoutAmount").css("border", "1px solid red");
                	$("#payoutError").html("金额配置不能为空")
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }
})