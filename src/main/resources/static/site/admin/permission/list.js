$(function () {
	 initHeader('admin');
    var token = $.AMUI.utils.cookie.get("adminToken");
    var page = 1;
    var size = 10;

    loadData();

    function loadData() {
        $.ajax({
            type: "GET",
            url: "/back/permissions",
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "page": page,
                "size": size
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    var permissions = data.permissions;
                    $("#permission-list").empty();
                    for (var i = 0; i < permissions.length; i++) {
                        createTr(permissions[i])
                    }
                    if (page == 1) {
                        createPage(data.page);
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
    }


    function toggle(id) {
        showLoading("玩命加载中...");
        var flag = ($("#togglePermission" + id).html().indexOf("解锁") != -1);
        $.ajax({
            type: "PUT",
            url: "/back/permission/available/" + id,
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "flag": flag
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    if (flag) {
                        $("#togglePermission" + id).removeClass("gh-btn-blue");
                        $("#togglePermission" + id).addClass("gh-btn-red");
                        $("#togglePermission" + id).html("锁定");
                        $("#permissionState" + id).html("正常");
                    } else {
                        $("#togglePermission" + id).removeClass("gh-btn-red");
                        $("#togglePermission" + id).addClass("gh-btn-blue");
                        $("#togglePermission" + id).html("解锁");
                        $("#permissionState" + id).html("锁定");
                    }
                }else if(data.code == "0038"){
                	initWarning();
                	showWarning();
                }  else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }


    function createTr(permission) {
        var flag = permission.available;
        var tr = '<tr>';
        tr = tr + '<td>' + permission.id + '</td>';
        tr = tr + '<td>' + permission.name + '</td>';
        tr = tr + '<td>' + permission.createDate + '</td>';
        tr = tr + '<td>' + permission.resourceType + '</td>';
        tr = tr + '<td>' + permission.permission + '</td>';

        if (permission.available) {
            tr = tr + '<td id="permissionState' + permission.id + '">正常</td>';
        } else {
            tr = tr + '<td id="permissionState' + permission.id + '">锁定</td>';
        }
        tr = tr + '<td>' +
            '<a href="/admin/permission/edit.html?id=' + permission.id + '" ><button class="gh-btn-blue">编辑</button></a>';
        if (flag) {
            tr = tr +
                '<button class="gh-btn-red gh-margin-left-sm" id="togglePermission' + permission.id + '">锁定</button>';
        } else {
            tr = tr +
                '<button class="gh-btn-blue gh-margin-left-sm" id="togglePermission' + permission.id + '">解锁</button>';
        }
        tr = tr + '</td>';
        tr = tr + '</tr>';
        $("#permission-list").append(tr);
        //事件绑定
        $("#togglePermission" + permission.id).click(function () {
            toggle(permission.id);
        });
    }

    function createPage(pageParams) {
        $(".pager").pagination({
            recordCount: pageParams.total,       //总记录数
            pageSize: pageParams.size,           //一页记录数
            onPageChange: function (pageNumber) {
                page = pageNumber;
                loadData();
            }
        });
    }

})