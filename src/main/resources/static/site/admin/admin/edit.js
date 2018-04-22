$(function () {
	 initHeader('admin');
    var token = $.AMUI.utils.cookie.get("adminToken");
    var params = parseURL(window.location.href);
    var id = params.id;
    var update = (typeof(id) != "undefined");
    var roleIds = [];

    if (update) {
        $("#action").html("编辑管理员")
    } else {
        $("#action").html("添加管理员")
    }

    $("#save").click(
        function () {
            save()
        }
    )

   
    
    if (update) {
        showLoading();
        $.ajax({
            type: "GET",
            url: "/back/admin/" + id,
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    $("#name").val(data.name);
                    $("#userName").val(data.username);
                    $("#description").val(data.description);
                    var roles = data.roles;
                    for (var i = 0; i < roles.length; i++) {
                    	createRoleCheckbox(roles[i]);
                    }
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }else{
    	loadRoles();
    }
    
    function loadRoles() {
        showLoading();
        $.ajax({
            type: "GET",
            url: '/back/roles',
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "page": 1,
                "size": 100
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                	var roles = data.roles;
                    for (var i = 0; i < roles.length; i++) {
                    	createRoleCheckbox(roles[i]);
                    }
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                }
                hideLoading();
            }
        })
    }
    
    function createRoleCheckbox(role) {
        var label = '<label class=" am-checkbox-inline" >';
        if (role.checked) {
            roleIds.push(role.id);
            label = label + '<input id="roles' + role.id + '" type="checkbox"  value="' + role.id + '" checked>';
        } else {
            label = label + '<input id="roles' + role.id + '" type="checkbox"  value="' + role.id + '">';
        }
        label = label + role.role;
        label = label + '</label>';
        $("#roles-list").append(label);
        $("#roles" + role.id).click(function () {
            selectRole(role.id);
        })
    }

    function selectRole(id) {
        if ($.inArray(id, roleIds) == -1) {
            roleIds.push(id);
        } else {
            roleIds.splice($.inArray(id, roleIds), 1);
        }
    }
    
   
    $("#name").on('input',function() {
   	 var name = $("#name").val();
   	 if(name !=""){
   		$("#name").css("border","1px solid #ccc");
    	$("#nameError").html("");
   	 }
   })
   $("#userName").on('input',function() {
	   var userName = $("#userName").val();
   	 if(userName !=""){
   		$("#userName").css("border","1px solid #ccc");
    	$("#userNameError").html("");
   	 }
   })
   $("#password").on('input',function() {
	   var password = $("#password").val();
   	 if(password !=""){
   		$("#password").css("border","1px solid #ccc");
    	$("#passwordError").html("");
   	 }
   })
    
    
    function save() {
        var name = $("#name").val();
        if (name == "") {
        	$("#name").css("border","1px solid red");
        	$("#nameError").html("请输入管理员名称");
            return;
        }else{
        	$("#name").css("border","1px solid #ccc");
        	$("#nameError").html("");
        }
        var userName = $("#userName").val();
        if (userName == "") {
        	$("#userName").css("border","1px solid red");
        	$("#userNameError").html("请输入账号");
            return;
        }else{
        	$("#userName").css("border","1px solid #ccc");
        	$("#userNameError").html("");
        }
       
        var roles = [];
        for (var i = 0; i < roleIds.length; i++) {
            var obj = {
                "id":roleIds[i]
            }
            roles.push(obj);
        }
        var password = $("#password").val();
        if (password != "") {
        	$("#password").css("border","1px solid #ccc");
        	$("#passwordError").html("");
        }else{
        	$("#password").css("border","1px solid red");
        	$("#passwordError").html("请输入密码");
            return;
        }
        var description = $("#description").val();
        var requestParams = {
            "name": name,
            "username": userName,
            "password":password,
            "description": description,
            "roles": roles
        }
       
        var requestUrl = "/back/admin/save";
        if (update) {
            requestParams.id = id;
            requestUrl = "/back/admin/update";
        }
        showLoading("玩命加载中...");
        $.ajax({
            type: "POST",
            url: requestUrl,
            dataType: "JSON",
            contentType: "application/json; charset=utf-8",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: JSON.stringify(requestParams),
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    location.href = "/admin/admin/list.html";
                    return;
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                }else if(data.code == "0023"){
                	hideLoading();
                	$("#userNameError").html("登录名已存在");
                	return;
                }else if(data.code == "0038"){
                	initWarning();
                	showWarning();
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }

})