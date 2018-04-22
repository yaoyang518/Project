$(function () {
    initHeader('user');
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
        if (start == "" || end == "") {
            $("#dataError").html("日期格式错误")
        } else {
            page = 1;// 从第一页检索
            loadData();
        }
    })


    function loadData() {
        showLoading();
        $.ajax({
            type: "GET",
            url: '/back/qualification/users',
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
                "size": size
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    var users = data.users;
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
                } else if (data.code == "0014") {
                    $("#dataError").html("日期格式错误")
                } else {
                    alert(data.msg);
                }
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
        tr = tr + '<td>' + user.username + '</td>';
        tr = tr + '<td>' + user.mobile + '</td>';
        tr = tr + '<td>' + user.createDate + '</td>';
        tr = tr + '<td>' + user.userLevel + '</td>';
        tr = tr + '<td>' + user.qualification + '</td>';
        tr = tr + '<td>'
            + '<button class="gh-btn-blue gh-btn-sm gh-margin-left-sm qual-yes" id="qualification' + user.id + '">确定</button>'
            + '</td>';
        tr = tr + '</tr>';
        $("#gh-user-list").append(tr);
        // 事件绑定
        $("#qualification" + user.id).click(function () {
            setqualId(user.id);
        });

        function setqualId(id) {
            $("#optqualId").val(id);
        }

        $("#gh-user-list").on("click", ".qual-yes", function () {
            $("#qualModal").modal('open');
        })

        $("#qual-set").click(function () {
            showLoading();
            $.ajax({
                type: "POST",
                url: "/back/qualification/" + $("#optqualId").val(),
                beforeSend: function (request) {
                    request.setRequestHeader("token", token);
                },
                success: function (result) {
                    var data = result.data;
                    if (data.code == "0000") {
                        location.href = "/admin/user/qualification.html";
                        return;
                    } else if (data.code == "0010") {
                        location.replace("/admin/login.html");
                        return;
                    } else {
                        alert(data.msg);
                    }
                    hideLoading();
                }
            })
        })

    }
})