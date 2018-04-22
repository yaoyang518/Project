$(function () {
	 initHeader('admin');
    var token = $.AMUI.utils.cookie.get("adminToken");
    var params = parseURL(window.location.href);
    var id = params.id;
    var update = (typeof(id) != "undefined");
    var permissionIds = [];

    if (update) {
        $("#action").html("编辑角色")
    } else {
        $("#action").html("添加角色")
    }

    if (update) {
        showLoading("玩命加载中...");
        $.ajax({
            type: "GET",
            url: "/back/role/" + id,
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    $("#role").val(data.role.role);
                    $("#description").val(data.role.description);
                    for (var i = 0; i < data.permissions.length; i++) {
                        createPermissionCheckbox(data.permissions[i]);
                    }
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                }else if(data.code == "0033"){
                	hideLoading();
                	$("#roleError").html("角色名已存在");
                	return;
                }else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    } else {
        showLoading("玩命加载中...");
        $.ajax({
            type: "GET",
            url: "/back/permissions",
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "page": 1,
                "size": 1000
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    var permissions = data.permissions;
                    for (var i = 0; i < permissions.length; i++) {
                        createPermissionCheckbox(permissions[i]);
                    }
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                }
                hideLoading();
            }
        })
    }

    function createPermissionCheckbox(permission) {
    	var label = '<label class="am-checkbox-inline" >';
        if (permission.checked) {
            permissionIds.push(permission.id);
            label = label + '<input id="permission' + permission.id + '" type="checkbox" value="' + permission.id + '" checked>';
        } else {
            label = label + '<input id="permission' + permission.id + '" type="checkbox" value="' + permission.id + '" >';
        }

        label = label + permission.name;
        label = label + '</label>';
        $("#permission-list").append(label);
        $("#permission" + permission.id).click(function () {
            selectPermission(permission.id);
        })
    }

    function selectPermission(id) {
        if ($.inArray(id, permissionIds) == -1) {
            permissionIds.push(id);
        } else {
            permissionIds.splice($.inArray(id, permissionIds), 1);
        }
    }

    $("#save").click(
        function () {
            save()
        }
    )

    function save() {
        var role = $("#role").val();
        if (role == "") {
        	$("#role").css("border","1px solid red");
        	$("#roleError").html("请输入角色名称");
            return;
        }else{
        	$("#role").css("border","1px solid #ccc");
        	$("#role").html("");
        }
        var permissions = [];
        for (var i = 0; i < permissionIds.length; i++) {
            var obj = {
                "id": permissionIds[i]
            }
            permissions.push(obj);
        }

        var description = $("#description").val();
        var requestParams = {
            "role": role,
            "description": description,
            "permissions": permissions
        }

        var requestUrl = "/back/role/add";
        var requestType = "POST";
        if (update) {
            requestType = "PUT"
            requestParams.id = id;
            requestUrl = "/back/role/edit";
        }

        showLoading("玩命加载中...");
        $.ajax({
            type: requestType,
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
                    location.href = "/admin/role/list.html"
                    return;
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                }else if(data.code == "0038"){
                	initWarning();
                	showWarning();
                } else if (data.code == "0033") {
               	 hideLoading();
               	 $("#role").css("border","1px solid red")
                 $("#roleError").html("角色已存在");
                  return;
              }  else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }

})