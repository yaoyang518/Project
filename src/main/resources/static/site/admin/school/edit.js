$(function() {
	var token = $.AMUI.utils.cookie.get("adminToken");
	var mobileFlag = false;
	var passwordFlag = false;
	var parentFlag = false;
	initHeader('school');
    loadAddress();
    loadCity(1);
    loadDistrict(1);
    change();
    function loadAddress() {
        showLoading();
        $.ajax({
            type : "GET",
            url : "/addressApi/listProvince",
            dataType : "JSON",
            beforeSend : function(request) {
                request.setRequestHeader("token", token);
            },
            data : {},
            success : function(result) {
                console.log(result);
                var data = result.data;
				createProvince(data.provinces);
                hideLoading();
            }
        })
    }
    function loadCity(item) {
        showLoading();
        $.ajax({
            type : "GET",
            url : "/addressApi/listCity",
            dataType : "JSON",
            beforeSend : function(request) {
                request.setRequestHeader("token", token);
            },
            data : {provinceId:item},
            success : function(result) {
                console.log(result);
                var data = result.data;
                createCity(data.cities);
                hideLoading();
            }
        })
    }
    function loadDistrict(item) {
        showLoading();
        $.ajax({
            type : "GET",
            url : "/addressApi/listDistrict",
            dataType : "JSON",
            beforeSend : function(request) {
                request.setRequestHeader("token", token);
            },
            data : {cityId:item},
            success : function(result) {
                console.log(result);
                var data = result.data;
                createArea(data.districts);
                hideLoading();
            }
        })
    }
    function createProvince(item) {
        $("#province-selec").empty();
        for (var i = 0; i < item.length; i++) {
            var obj = item[i];
			$("#province-select").append('<option value="' + obj.id + '">' + obj.name + '</option>');
        }
    }
    function createCity(item) {
        $("#city-select").empty();
        $("#district-select").empty();
        for (var i = 0; i < item.length; i++) {
            var obj = item[i];
            $("#city-select").append('<option value="' + obj.id + '">' + obj.name + '</option>');
        }
        var city = $("#city-select").val();
        loadDistrict(city);
    }
    function createArea(item) {
        $("#district-select").empty();
        for (var i = 0; i < item.length; i++) {
            var obj = item[i];
            $("#district-select").append('<option value="' + obj.id + '">' + obj.name + '</option>');
        }
    }
    function change() {
        $("#province-select").change(function () {
            var province = $("#province-select").val();
            console.log(province);
            loadCity(province)
        });
        $("#city-select").change(function () {
            var city = $("#city-select").val();
            loadDistrict(city)
        });
    }
	/*$("#parent").on('input',function() {
		var parent = $("#parent").val();
		if (parent != "") {
			$.ajax({
				type : "GET",
				url : "/back/user/" + parent,
				dataType : "JSON",
				beforeSend : function(request) {
					request.setRequestHeader("token", token);
				},
				success : function(result) {
					var data = result.data;
					if (data.code == "0012") {
						$("#parent").css("border", "1px solid red");
						$("#parentError").html("上级不存在");
						parentFlag = false;
					} else if (data.code == "0010") {
	                    location.replace("/admin/login.html");
	                    return;
	                } else {
						$("#parent").css("border", "1px solid #ccc");
						$("#parentError").html("");
						parentFlag = true;
					}
				}
			})
		} else {
			$("#parent").css("border", "1px solid #ccc");
			$("#parentError").html("");
			parentFlag = true;
		}
	})
	
	$("#mobile").blur(function(){
		var parent = $("#mobile").val();
		if (parent == ""){
			$("#mobile").css("border", "1px solid red");
			$("#mobileError").html("手机号不能为空");
			parentFlag = false;
		}
	});

	$("#mobile").on("input", function() {
		var mobile = $("#mobile").val();
		if (mobile.length < 11) {
			$("#mobileError").html("");
			mobileFlag = false;
			return;
		}

		var reg = /^0?1[3|4|5|8|7][0-9]\d{8}$/;
		if (reg.test(mobile)) {
			$.get("/check/mobile/" + mobile, function(result) {
				if (result.data.code == "0001") {
					$("#mobile").css("border", "1px solid red");
					$("#mobileError").html("手机号已注册");
					mobileFlag = false;
				} else {
					$("#mobile").css("border", "1px solid #ccc");
					$("#mobileError").html("");
					mobileFlag = true;
				}
			});
		} else {
			$("#mobile").css("border", "1px solid red");
			$("#mobileError").html("手机号格式错误");
			mobileFlag = false;
		}
	})

	$("#password").blur(function(){
		var parent = $("#password").val();
		if (parent == ""){
			$("#password").css("border", "1px solid red");
			$("#passwordError").html("密码不能为空");
			parentFlag = false;
		}
	});
	
	$("#password").on('input', function() {
		var password = $("#password").val();
		if (password.length < 6) {
			passwordFlag = false;
			return;
		}
		var reg = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$/;
		if (reg.test(password)) {
			$("#password").css("border", "1px solid #ccc");
			$("#passwordError").html("");
			passwordFlag = true;
		} else {
			$("#password").css("border", "1px solid red");
			$("#passwordError").html("密码为6-10位混合密码");
			passwordFlag = false;
		}
	})
	
	$("#save").click(function() {
		if(mobileFlag&passwordFlag){
			save();
		}
	})
	
	$("#addUser-back").click(function(){
		location.replace("/admin/user/list.html")
	})*/

    $("#save").click(function() {
        save();
    });
    $("#addUser-back").click(function(){
        location.replace("/admin/school/list.html")
    })

	function addUser(params) {
		showLoading("玩命创建用户中...");
		$.ajax({
			type : "POST",
			url : "/schoolApi/createSchool",
			dataType : "JSON",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(params),
			beforeSend : function(request) {
				request.setRequestHeader("token", token);
			},
			success : function(result) {
				var data = result.data;
                hideLoading();
                $("#userModal-tips").modal('open');
                $("#user-title").html("创建成功");
                $("#user-warn").html("");
                return;
				/*if (data.code == "0000") {
					 hideLoading();
	               	 $("#userModal-tips").modal('open');
	               	 $("#user-title").html("创建成功");
	               	 $("#user-warn").html("");
					return;
				}else if(data.code == "0038"){
                	initWarning();
                	showWarning();
                }  else if (data.code == "0010") {
					location.replace("/admin/login.html");
					return;
				} else {
					alert(data.msg);
				}*/
				hideLoading();
			}
		})
	}
	function save() {
		var district = $("#district-select").val();
		var address = $("#address").val();

		var params = {
			"districtId" : district,
            "name" : address,
		}
        console.log(params);
		addUser(params);
	}

})