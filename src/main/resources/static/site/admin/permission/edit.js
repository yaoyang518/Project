$(function () {
	 initHeader('admin');
    var token = $.AMUI.utils.cookie.get("adminToken");
    var params = parseURL(window.location.href);
    var id = params.id;
    var update = (typeof(id) != "undefined");
    var roleIds = [];

    if (update) {
        $("#action").html("编辑权限")
    } else {
        $("#action").html("添加权限")
    }

    if (update) {
        showLoading("玩命加载中...");
        $.ajax({
            type: "GET",
            url: "/back/permission/" + id,
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    $("#name").val(data.permission.name);
                    $("#resourceType").val(data.permission.resourceType);
                    /*  $("#url").val(data.permission.url);*/
                    $("#permission").val(data.permission.permission);
                    $("#description").val(data.permission.description);
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

    $("#save").click(
        function () {
            save()
        }
    )

    $("#name").on('input', function () {
        var name = $("#name").val();
        if (name != "") {
            $("#nameError").html("");
            $("#name").css("border", "1px solid #ccc");
        }
    })
    $("#url").on('input', function () {
        var url = $("#url").val();
        if (url != "") {
            $("#urlError").html("");
            $("#url").css("border", "1px solid #ccc");
        }
    })
    $("#permission").on('input', function () {
        var permission = $("#permission").val();
        if (permission != "") {
            $("#permissionError").html("");
            $("#permission").css("border", "1px solid #ccc");
        }
    })

    function save() {
        var name = $("#name").val();
        if (name == "") {
            $("#nameError").html("请输入权限名称");
            $("#name").css("border", "1px solid red");
            return;
        }
        var resourceType = $("#resourceType").val();
        var url = $("#url").val();
        var permission = $("#permission").val();
        if (permission == "") {
            $("#permissionError").html("请输入权限代码");
            $("#permission").css("border", "1px solid red");
            return;
        }
        var description = $("#description").val();
        var requestParams = {
            "name": name,
            "resourceType": resourceType,
            "url": url,
            "permission": permission,
            "description": description
        }

        var requestUrl = "/back/permission/add";
        if (update) {
            requestParams.id = id;
            requestUrl = "/back/permission/edit";
        }
        showLoading("玩命加载中...");
        $.ajax({
            type: "PUT",
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
                    location.href = "list.html"
                    return;
                }else if(data.code == "0038"){
                	initWarning();
                	showWarning();
                }  else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else if (data.code == "0026") {
                    hideLoading();
                    $("#permission").css("border", "1px solid red")
                    $("#permissionError").html("权限代码已存在");
                    return;
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }

})