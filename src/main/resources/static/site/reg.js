$(function() { 
	// 刷新验证码
	$(".lv-reg-yzm").click(function() {
		$(".lv-reg-yzm").attr("src", "/reg/kaptcha?flag=" + Math.random());
	});

	// 焦点事件
	var idflag = false;
	var telflag = false;
	var pasflag = false;
	var codeflag = false;
	
	$(".lv-login-ipt0").focus(function() {
		$(".lv-login-lab0").addClass("am-text-xs");
	});
	$(".lv-login-ipt0").blur(function() {
		$(".lv-login-lab0").removeClass("am-text-xs");
	});
	$(".lv-login-ipt0").on('input', function() {
		$(".lv-login-lab0").addClass("am-text-xs");
		var id = $('#lv-sign-id').val();
		if(id){
			$.get("/check/user/" + id, function(result, status) {
				if (status == "success") {
					switch (result.data.code) {
					case "0012":// id不存在
						$(".lv-error-zero").html("上级不存在");
						$("#lv-sign-id").removeClass("lv-border-grey");
						$("#lv-sign-id").addClass("lv-border-red");
						idflag = false;
						disableReg();
						break;
					case "0001":// id存在
						$(".lv-error-zero").html("");
						$("#lv-sign-id").removeClass("lv-border-red");
						$("#lv-sign-id").addClass("lv-border-grey");
						idflag = true;
						check(telflag, pasflag, codeflag);
						break;
					}
				} else {
					alert("请求失败")
				}

			});
		}else{
			$(".lv-error-zero").html("");
			$("#lv-sign-id").removeClass("lv-border-red");
			$("#lv-sign-id").addClass("lv-border-grey");
			idflag = true;
		}
		
	})
	
	$(".lv-login-ipt1").focus(function() {
		$(".lv-login-lab1").addClass("am-text-xs");
	});
	$(".lv-login-ipt1").blur(function() {
		$(".lv-login-lab1").removeClass("am-text-xs");
		var tel = $('#lv-sign-phone').val();
		var reg = /^0?1[3|4|5|8|7][0-9]\d{8}$/;
		if (reg.test(tel)) {
			$.get("/check/mobile/" + tel, function(result, status) {
				/* alert("Data: " + result + "\nStatus: " + status); */
				if (status == "success") {
					switch (result.data.code) {
					case "0001":// 用户已存在
						$(".lv-error-one").html("号码已注册");
						$("#lv-sign-phone").removeClass("lv-border-grey");
						$("#lv-sign-phone").addClass("lv-border-red");
                        disableReg();
						break;
					case "0012":// 用户不存在
						$(".lv-error-one").html("");
						$("#lv-sign-phone").removeClass("lv-border-red");
						$("#lv-sign-phone").addClass("lv-border-grey");
						telflag = true;
						check(telflag, pasflag, codeflag);
						break;
					}
				} else {
					alert("请求失败")
				}

			})
		} else {
			$(".lv-error-one").html("请输入正确的11位手机号码");
			$("#lv-sign-phone").removeClass("lv-border-grey");
			$("#lv-sign-phone").addClass("lv-border-red");
            disableReg();
		}
		
	})
	$(".lv-login-ipt1").on('input', function() {
		$(".lv-login-lab1").addClass("am-text-xs");
		var tel = $('#lv-sign-phone').val();
		var reg = /^0?1[3|4|5|8|7][0-9]\d{8}$/;
		if (tel.length < 11) {
		    disableReg();
			return;
		}
		if (reg.test(tel)) {
			$.get("/check/mobile/" + tel, function(result, status) {
				/* alert("Data: " + result + "\nStatus: " + status); */
				if (status == "success") {
					switch (result.data.code) {
					case "0001":// 用户已存在
						$(".lv-error-one").html("手机号码已注册");
						$("#lv-sign-phone").removeClass("lv-border-grey");
						$("#lv-sign-phone").addClass("lv-border-red");
                        disableReg();
						break;
					case "0012":// 注册成功
						$(".lv-error-one").html("");
						$("#lv-sign-phone").removeClass("lv-border-red");
						$("#lv-sign-phone").addClass("lv-border-grey");
						telflag = true;
						check(telflag, pasflag, codeflag);
						break;
					}
				} else {
					alert("请求失败")
				}

			})
		} else {
			$(".lv-error-one").html("请输入正确的11位手机号码");
			$("#lv-sign-phone").removeClass("lv-border-grey");
			$("#lv-sign-phone").addClass("lv-border-red");
            disableReg();
		}
		

	})
	$(".lv-login-ipt2").focus(function() {
		$(".lv-login-lab2").addClass("am-text-xs");
	});
	$(".lv-login-ipt1").blur(function() {
		$(".lv-login-lab2").removeClass("am-text-xs");
	});
	$(".lv-login-ipt2").on('input', function() {
		$(".lv-login-lab2").addClass("am-text-xs");
		var password = $("#lv-sign-password").val();
		var reg = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$/;
		if (password.length < 6) {
            disableReg();
			return;
        }
		if (reg.test(password)) {
			$(".lv-login-lab2").addClass("am-text-xs");
			pasflag = true;
			$(".lv-password-error").html("")
			$("#lv-sign-password").removeClass("lv-border-red");
			$("#lv-sign-password").addClass("lv-border-grey");
			check(telflag, pasflag, codeflag);
		} else {
			$(".lv-login-lab2").addClass("am-text-xs");
			$(".lv-password-error").html("仅支持6-10位混合密码");
			$("#lv-sign-password").removeClass("lv-border-grey");
			$("#lv-sign-password").addClass("lv-border-red");
            disableReg();
		}
	});
	$(".lv-login-ipt3").focus(function() {
		$(".lv-login-lab3").addClass("am-text-xs");
	});
	$(".lv-login-ipt3").blur(function() {
		$(".lv-login-lab3").removeClass("am-text-xs");
	});
	$(".lv-login-ipt3").on('input',function() {
				$(".lv-login-lab3").addClass("am-text-xs");
				var yzm = $("#lv-sign-yzm").val();
				var reg = /^[1-9]$|^[1-9]\d{0,3}$/;
				if (yzm.length < 4) {
                    disableReg();
					return;
				}
				if (reg.test(yzm)) {
					$.get("/check/verifyCode/" + yzm, function(result, status) {
						/* alert("Data: " + result + "\nStatus: " + status); */
						if (status == "success") {
							switch (result.data.code) {
							case "0000":
								$(".lv-error-four").html("")
								$(".lv-login-lab3").addClass("am-text-xs");
								$("#lv-sign-yzm").removeClass("lv-border-red");
								$("#lv-sign-yzm").addClass("lv-border-grey");
								codeflag = true;
								check(telflag, pasflag, codeflag);
								break;
							case "0004":
								$(".lv-login-lab3").addClass("am-text-xs");
								$(".lv-error-four").html("验证码输入错误");
								$("#lv-sign-yzm").val("");
								$("#lv-sign-yzm").removeClass("lv-border-grey");
								$("#lv-sign-yzm").addClass("lv-border-red");
								$(".lv-reg-yzm").attr("src", "/reg/kaptcha?flag=" + Math.random());
                                disableReg();
								break;
							}
						} else {

						}
					});
				} else {
					$(".lv-login-lab3").addClass("am-text-xs");
					$("#lv-sign-yzm").val("");
					$(".lv-error-four").removeClass("am-hide");
					$(".lv-reg-yzm").attr("src", "/reg/kaptcha?flag=" + Math.random());
                    disableReg();
				}

			});

	// 校验三个变量是否为true

	function check(telflag, pasflag, codeflag) {
		if (telflag && pasflag && codeflag) {
			enableReg();
		}else{
			disableReg();
		}
	}

	function enableReg() {
        $("#lv-sign").removeClass("lv-bg-grey");
        $("#lv-sign").removeAttr("disabled");
        $("#lv-sign").addClass("lv-bg-green");
    }

	function disableReg() {
        $("#lv-sign").removeClass("lv-bg-green");
        $("#lv-sign").attr("disabled","disabled");
        $("#lv-sign").addClass("lv-bg-grey");
    }

	function regData(mobile, password, parent) {
		if (parent.length == 0) {
			return JSON.stringify({
				"mobile" : mobile,
				"password" : password,
			})
		}
		return JSON.stringify({
			"parent" : {
				"id" : parent
			},
			"mobile" : mobile,
			"password" : password,
		})
	}

	$('#lv-sign').click(
			function() {
				var id = $('#lv-sign-id').val();
				var username = $('#lv-sign-phone').val();
				var password = $("#lv-sign-password").val();
				var code = $('#lv-sign-yzm').val();
				if (username != "" && password != "" && code.length == 4) {
					$.ajax({
						type : "POST",
						url : "/reg/" + code,
						dataType : "JSON",
						contentType : "application/json; charset=utf-8",
						data : regData(username, password, id),
						beforeSend : function(XMLHttpRequest) {
						},

						success : function(result) {
							switch (result.data.code) {
							case "0000":
								location.href = "/login.html";
							case "0008":
								$(".lv-error-zero").removeClass("am-hide");
								$(".lv-reg-yzm").attr("src",
										"/reg/kaptcha?flag=" + Math.random());
								break;
							}
						}
					})
				} else {
					alert("请检查您的输入！");
				}
			})
});
