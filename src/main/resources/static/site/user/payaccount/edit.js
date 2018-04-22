$(function() {
	var token = $.AMUI.utils.cookie.get("token");
	var id = location.search.split("=", 2)[1];

	if (typeof (id) == "undefined") {
		url = "/bankAccountApi/default?bankAccountId="
	} else {
		url = "/bankAccountApi/default?bankAccountId=" + id;
		$("#bank-delete").removeClass("am-hide");
		loadBank();
	}

	// 创建列表
	function loadBank() {
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
					$("#userName").val(data.username);
					$("#cardNum").val(data.cardNo);
					$("#bank").val(data.bank);
					$("#branch").val(data.branch)
				} else if (data.code == "0010" || data.code == "0027") {
                    location.replace("/login.html");
                    return;
                }  else {
					alert(data.msg);
				}
				hideLoading();
			}
		})
	}

	$('#bank-delete').click(function(){
		$.ajax({
			type:"DELETE",
			url:"/bankAccountApi/delete?bankAccountId=" + id,
			dataType:"JSON",
			beforeSend:function(request) {
				request.setRequestHeader("token", token);
			},
			success : function(result) {
				var data = result.data;
				if (data.code == "0000") {
					location.replace("/user/allBank.html");
				} else if (data.code == "0046") {
					$("#cardNumErr").html("银行卡号有误")
				} else if (data.code == "0047") {
					$("#cardNumErr").html("银行卡已添加")
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
			var bank = $("#bank").val();
			var branch = $("#branch").val();
			
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
				$("#cardNumErr").html("银行卡号不能为空")
			}
			else{
				$("#cardNum").css("border", "1px solid grey");
				$("#cardNumErr").html("")
			}

			if (bank == "") {
				$("#bank").css("border", "1px solid red");
				$("#bankErr").html("银行不能为空")
			}else{
				$("#bank").css("border", "1px solid grey");
				$("#bankErr").html("")
			}

			if (branch == "") {
				$("#branch").css("border", "1px solid red");
				$("#branchErr").html("网点不能为空")
			}else{
				$("#branch").css("border", "1px solid grey");
				$("#branchErr").html("")
			}
			var reg = /^([1-9]{1})(\d{14}|\d{15}|\d{18})$/;
			if(!reg.test(cardNum)){
				$("#cardNum").css("border", "1px solid red");
				$("#cardNumErr").html("银行卡号错误")
				return;
			}else{
				$("#cardNum").css("border", "1px solid grey");
				$("#cardNumErr").html("")
			}
			if (userName != "" || cardNum != "" || bank != "" || branch != "") {
				showLoading();
				$.ajax({
					type : "POST",
					url : "/bankAccountApi/add",
					dataType : "JSON",
					contentType : "application/json; charset=utf-8",
					beforeSend : function(request) {
						request.setRequestHeader("token", token);
					},
					data : JSON.stringify({
						"userName" : userName,
						"cardNo" : cardNum,
						"bank" : bank,
						"branch" : branch,
						"asDefault":box
					}),
					success : function(result) {
						var data = result.data;
						if (data.code == "0000") {
							location.replace("/user/allBank.html");
						} else if (data.code == "0046") {
							$("#cardNumErr").html("银行卡号有误")
						} else if (data.code == "0047") {
							$("#cardNumErr").html("银行卡已添加")
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
			var bank = $("#bank").val();
			var branch = $("#branch").val();
			
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

			if (bank == "") {
				$("#bank").css("border", "1px solid red");
				$("#bankErr").html("银行不能为空")
			}

			if (branch == "") {
				$("#branch").css("border", "1px solid red");
				$("#branchErr").html("网点不能为空")
			}

			if (userName != "" || cardNum != "" || bank != "" || branch != "") {
				showLoading();
				$.ajax({
					type : "PUT",
					url : "/bankAccountApi/update",
					dataType : "JSON",
					contentType : "application/json; charset=utf-8",
					beforeSend : function(request) {
						request.setRequestHeader("token", token);
					},
					data : JSON.stringify({
						"userName" : userName,
						"cardNo" : cardNum,
						"bank" : bank,
						"branch" : branch,
						"id" : id,
						"asDefault":box
					}),
					success : function(result) {
						var data = result.data;
						if (data.code == "0000") {
							location.replace("/user/allBank.html");
						} else if (data.code == "0046") {
							$("#cardNumErr").html("银行卡号有误")
						} else if (data.code == "0048") {
							$("#cardNumErr").html("银行卡不存在")
						} else if (data.code == "0047") {
							$("#cardNumErr").html("银行卡已添加")
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