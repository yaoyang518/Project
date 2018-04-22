$(function () {
	 initHeader('admin');
    var token = $.AMUI.utils.cookie.get("adminToken");
    var page = 1;
    var size = 10;
    var username = "";

    $("#username").val(username);
    loadData();

    $("#search").click(function () {
        username = $("#username").val();
        loadData();
    })


    function loadData() {
        showLoading("玩命加载中...");
        $.ajax({
            type: "GET",
            url: "/back/admins",
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "username": username,
                "page": page,
                "size": size
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    var admins = data.admins;
                    $("#gh-admin-list").empty();
                    for (var i = 0; i < admins.length; i++) {
                        createTr(admins[i])
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
        var flag = ($("#toggleAdmin" + id).html().indexOf("解锁") != -1);
        $.ajax({
            type: "PUT",
            url: "/back/admin/available/" + id,
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
                        $("#toggleAdmin" + id).removeClass("gh-btn-blue");
                        $("#toggleAdmin" + id).addClass("gh-btn-red");
                        $("#toggleAdmin" + id).html("锁定");
                        $("#adminState" + id).html("正常");
                    } else {
                        $("#toggleAdmin" + id).removeClass("gh-btn-red");
                        $("#toggleAdmin" + id).addClass("gh-btn-blue");
                        $("#toggleAdmin" + id).html("解锁");
                        $("#adminState" + id).html("锁定");
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

    function createTr(admin) {
        var flag = (admin.state == '1');
        var tr = '<tr>';
        tr = tr + '<td>' + admin.id + '</td>';
        tr = tr + '<td>' + admin.name + '</td>';
        tr = tr + '<td>' + admin.username + '</td>';
        if (flag) {
            tr = tr + '<td id="adminState' + admin.id + '">正常</td>';
        } else {
            tr = tr + '<td id="adminState' + admin.id + '">锁定</td>';
        }
        tr = tr + '<td>' + admin.roleName + '</td>';
        tr = tr + '<td>' + admin.createDate + '</td>';
        tr = tr + '<td>' +
            '<a href="/admin/admin/edit.html?id=' + admin.id + '"><button class="gh-btn-blue gh-margin-left-sm ">编辑</button></a>';
        if (flag) {
            tr = tr +
                '<button class="gh-btn-red gh-margin-left-sm" id="toggleAdmin' + admin.id + '">锁定</button>';
        } else {
        	if(admin.id !=1){
        		  tr = tr +
                  '<button class="gh-btn-blue gh-margin-left-sm" id="toggleAdmin' + admin.id + '">解锁</button>';
        	}
          
        }
        tr = tr + '</td>';
        tr = tr + '</tr>';
        $("#gh-admin-list").append(tr);
        // 事件绑定
        if(admin.id !=1){
        	 $("#toggleAdmin" + admin.id).click(function () {
                 toggle(admin.id);
             });
        }
       
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