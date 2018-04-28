$(function () {
    initHeader('school');
	//获取今天的日期
    var preMonth = new XDate();
    preMonth.addMonths(-1); //上个月的今天
    var start = ""+new XDate(preMonth).toString('yyyy-MM-dd')+"";
    var end =  ""+new XDate().toString('yyyy-MM-dd')+"";
	
    var token = $.AMUI.utils.cookie.get("adminToken");
    var page = 1;
    var size = 10;
    var username = "";
    var mobile = "";
    var name = "";
    var phone = "";
    
    

    $("#name").val(name);
    $("#phone").val(phone);
    $("#start").val(start);
    $("#end").val(end);
    loadData();

    $("#search").click(function () {
        start = $("#start").val();
        end = $("#end").val();
        name = $("#name").val();
        phone = $("#phone").val();
        if(start==""||end==""){
        	$("#dataError").html("日期格式错误")
        }else{
        	 page = 1;// 从第一页检索
             loadData();
        }
    })

    function loadData() {
        showLoading();
        $.ajax({
            type: "GET",
            url: '/schoolApi/listSchool',
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "username": name,
                "mobile": phone,
                "start": start,
                "end": end,
                "page": page,
                "size": size,
            },
            success: function (result) {
                var data = result.data;
                console.log(data);
                var users = data.schools;
                $("#gh-user-list").empty();
                for (var i = 0; i < users.length; i++) {
                    createTr(users[i])
                }
                if (page == 1) {
                    createPage(data.page);
                }
                /*if (data.code == "0000") {
                    var users = data.schools;
                    $("#gh-user-list").empty();
                    for (var i = 0; i < users.length; i++) {
                        createTr(users[i])
                    }
                    if (page == 1) {
                        createPage(data.page);
                    }
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else if(data.code == "0014"){
                	
                }else {
                    alert(data.msg);
                }*/
                hideLoading();
            }
        })
    }

    function createPage(pageParams) {
        $(".pager").pagination({
            recordCount: pageParams.total, // 总记录数
            pageSize: pageParams.size, // 一页记录数
            onPageChange: function (pageNumber) {
                page = pageNumber;
                loadData();
            }
        });
    }

    function createTr(user) {
        var flag = (user.state == '1');
        var tr = '<tr>';
        tr = tr + '<td>' + user.id + '</td>';
        tr = tr + '<td>' + user.name + '</td>';
        /*tr = tr + '<td>' + user.mobile + '</td>';
        if (user.shopKeeper){
        	tr = tr + '<td>' + user.userLevel + '(店主)</td>';
        }else{
        	 tr = tr + '<td>' + user.userLevel + '</td>';
        }
       
        if (user.parentName) {
            tr = tr
                + '<td><a href="/admin/user/view.html?id='
                + user.parentId + '">' + user.parentName + '</a></td>';
        } else {
            tr = tr + '<td>无</td>';
        }

        tr = tr + '<td>' + user.juniorCount + '</td>';
        tr = tr + '<td>' + user.createDate + '</td>';
        if (flag) {
            tr = tr + '<td id="userState' + user.id + '">正常</td>';
        } else {
            tr = tr + '<td id="userState' + user.id + '">锁定</td>';
        }
        tr = tr
            + '<td>'
            + '<a class="gh-btn-blue gh-btn-sm" href="/admin/treasure/recharge.html?id='
            + user.id
            + '">充值</a>'
            + '<a class="gh-btn-blue gh-btn-sm gh-margin-left-sm" href="/admin/user/view.html?id='
            + user.id
            + '">查看</a>';
        if (flag) {
            tr = tr +
                '<button class="gh-btn-red gh-btn-sm gh-margin-left-sm" id="toggleUser' + user.id + '">锁定</button>';
        } else {
            tr = tr +
                '<button class="gh-btn-blue gh-btn-sm gh-margin-left-sm" id="toggleUser' + user.id + '">解锁</button>';
        }
        tr = tr + '</td>';*/

        tr = tr + '</tr>';
        $("#gh-user-list").append(tr);
        // 事件绑定
        $("#toggleUser" + user.id).click(function () {
            toggle(user.id);
        });
    }

    function toggle(id) {
        showLoading();
        var flag = ($("#toggleUser" + id).html().indexOf("解锁") != -1);
        $.ajax({
            type: "PUT",
            url: "/back/user/available/" + id,
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
                        $("#toggleUser" + id).removeClass("gh-btn-blue");
                        $("#toggleUser" + id).addClass("gh-btn-red");
                        $("#toggleUser" + id).html("锁定");
                        $("#userState" + id).html("正常");
                    } else {
                        $("#toggleUser" + id).removeClass("gh-btn-red");
                        $("#toggleUser" + id).addClass("gh-btn-blue");
                        $("#toggleUser" + id).html("解锁");
                        $("#userState" + id).html("锁定");
                    }
                }else if(data.code == "0038"){
                	initWarning();
                	showWarning();
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

})