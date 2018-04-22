$(function () {
	 initHeader('admin');
    var token = $.AMUI.utils.cookie.get("adminToken");
    var page = 1;
    var size = 10;
    loadData();

    function loadData() {
        showLoading();
        $.ajax({
            type: "GET",
            url: '/back/roles',
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
                    var roles = data.roles;
                    $("#gh-role-list").empty();
                    for (var i = 0; i < roles.length; i++) {
                        createTr(roles[i])
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

    function createTr(role) {
        var flag = role.available;
        var tr = '<tr>';
        tr = tr + '<td>' + role.id + '</td>';
        tr = tr + '<td>' + role.role + '</td>';
        tr = tr + '<td>' + role.createDate + '</td>';
        tr = tr + '<td>' + role.description + '</td>';
        if (flag) {
            tr = tr + '<td id="roleState' + role.id + '">正常</td>';
        } else {
            tr = tr + '<td id="roleState' + role.id + '">锁定</td>';
        }

        tr = tr + '<td>' +
            '<a href="/admin/role/edit.html?id=' + role.id + '" class="gh-btn-blue gh-btn-sm  pull-left">编辑</a>';
        if (flag) {
            tr = tr +
                '<button class="gh-btn-red gh-btn-sm gh-margin-left-sm" id="toggleRole' + role.id + '">锁定</button>';
        } else {
            tr = tr +
                '<button class="gh-btn-blue gh-btn-sm gh-margin-left-sm" id="toggleRole' + role.id + '">解锁</button>';
        }
        tr = tr + '</td>';
        tr = tr + '</tr>';
        $("#gh-role-list").append(tr);
        //事件绑定
        $("#toggleRole" + role.id).click(function () {
            toggle(role.id);
        });
    }

    function toggle(id) {
        showLoading();
        var flag = ($("#toggleRole" + id).html().indexOf("解锁") != -1);
        $.ajax({
            type: "PUT",
            url: "/back/role/available/" + id,
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
                        $("#toggleRole" + id).removeClass("gh-btn-blue");
                        $("#toggleRole" + id).addClass("gh-btn-red");
                        $("#toggleRole" + id).html("锁定");
                        $("#roleState" + id).html("正常");
                    } else {
                        $("#toggleRole" + id).removeClass("gh-btn-red");
                        $("#toggleRole" + id).addClass("gh-btn-blue");
                        $("#toggleRole" + id).html("解锁");
                        $("#roleState" + id).html("锁定");
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