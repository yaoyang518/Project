$(function() {
	var token = $.AMUI.utils.cookie.get("token");
	var id = location.search.split("id=", 2)[1];
	var box = false;
	console.log(id);
	if (typeof (id) == "undefined") {
		url = "/aliPayAccountApi/default?id="
	} else {
		url = "/aliPayAccountApi/default?id=" + id;
		$("#bank-delete").removeClass("am-hide");
		loadAli();
	}

	// 创建列表
	function loadAli() {
		showLoading();
		$.ajax({
			type : "GET",
			url : url,
			dataType : "JSON",
			beforeSend : function(request) {
				request.setRequestHeader("token", token);
			},
			success : function(result) {
				var data = result.data;
				if (data.code == "0000") {
					if(data.asDefault){
						$("#check-detail").hide();
					}else{
						$("#check-detail").show();
					}
					$("#userName").val(data.name);
					$("#cardNum").val(data.accountNumber);
				} else if (data.code == "0010") {
                    location.replace("/login.html");
                    return;
                }else if(result.data.code == "0027") {
                    location.replace("/login.html");
                    return;
                } else {
					alert(data.msg);
				}
				hideLoading();
			}
		})
	}
	
	
	$('#bank-delete').click(function(){
		$.ajax({
			type:"DELETE",
			url:"/aliPayAccountApi/delete?id=" + id,
			dataType:"JSON",
			beforeSend:function(request) {
				request.setRequestHeader("token", token);
			},
			success : function(result) {
				var data = result.data;
				if (data.code == "0000") {
					location.replace("/user/allAlipay.html");
				}else if (data.code == "0047") {
					$("#cardNumErr").html("支付宝已添加")
				} else if (data.code == "0010" || data.code == "0027") {
					location.replace("/login.html");
					return;
				}
			}
		})
	})
	
	$('#bank-set').click(function() {
		if (typeof (id) == "undefined") {
			var userName = $("#userName").val();
			var cardNum = $("#cardNum").val();
			if($("#box").is(':checked')){
				box = true;
			}else {
				box = false;
			}
			if (userName == "") {
				$("#userName").css("border", "1px solid red");
				$("#userNameErr").html("姓名不能为空")
			}else{
				$("#userName").css("border", "1px solid grey");
				$("#userNameErr").html("")
			}
			if (cardNum == "") {
				$("#cardNum").css("border", "1px solid red");
				$("#cardNumErr").html("支付宝不能为空")
			}
			else{
				$("#cardNum").css("border", "1px solid grey");
				$("#cardNumErr").html("")
			}
			if (userName != "" || cardNum != "") {
				showLoading();
				$.ajax({
					type : "POST",
					url : "/aliPayAccountApi/save",
					dataType : "JSON",
					contentType : "application/json; charset=utf-8",
					beforeSend : function(request) {
						request.setRequestHeader("token", token);
					},
					data : JSON.stringify({
						"userName" : userName,
						"accountNumber" : cardNum,
						"asDefault":box
					}),
					success : function(result) {
						console.log(result);
						var data = result.data;
						if (data.code == "0000") {
							location.replace("/user/allAlipay.html");
						} else if (data.code == "0063") {
							$("#cardNumErr").html("支付宝已添加")
						} else if (data.code == "0010" || data.code == "0027") {
							location.replace("/login.html");
							return;
						}
						hideLoading();
					}
				})
			}
		} else {
			var userName = $("#userName").val();
			var cardNum = $("#cardNum").val();
			
			if($("#box").is(':checked')){
				box = true;
			}else {
				box = false;
			}
			if (userName == "") {
				$("#userName").css("border", "1px solid red");
				$("#userNameErr").html("姓名不能为空")
			}
			if (cardNum == "") {
				$("#cardNum").css("border", "1px solid red");
				$("#cardNumErr").html("银行卡号不能为空")
			}
			if (userName != "" || cardNum != "") {
				showLoading();
				$.ajax({
					type : "POST",
					url : "/aliPayAccountApi/save",
					dataType : "JSON",
					contentType : "application/json; charset=utf-8",
					beforeSend : function(request) {
						request.setRequestHeader("token", token);
					},
					data : JSON.stringify({
						"userName" : userName,
						"accountNumber" : cardNum,
						"id" : id,
						"asDefault":box
					}),
					success : function(result) {
						console.log(result);
						var data = result.data;
						if (data.code == "0000") {
							location.replace("/user/allAlipay.html");
						} else if (data.code == "0047") {
							$("#cardNumErr").html("支付宝已添加")
						} else if (data.code == "0010" || data.code == "0027") {
							location.replace("/login.html");
							return;
						}
						hideLoading();
					}
				})
			}
		}

	});
})