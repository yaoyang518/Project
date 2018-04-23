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

    $('#download').click(function () {
        start = $("#start").val();
        end = $("#end").val();
        name = $("#name").val();
        phone = $("#phone").val();
        showLoading();
        $.ajax({
            type: "GET",
            url: '/back/download/user',
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
                if (data.code == "0000") {
                    var title = [
                        {"value": "ID", "type": "ROW_HEADER_HEADER", "datatype": "string"},
                        {"value": "昵称", "type": "ROW_HEADER_HEADER", "datatype": "string"},
                        {"value": "手机号", "type": "ROW_HEADER_HEADER", "datatype": "string"},
                        {"value": "用户等级", "type": "ROW_HEADER_HEADER", "datatype": "string"},
                        {"value": "上级", "type": "ROW_HEADER_HEADER", "datatype": "string"},
                        {"value": "下级", "type": "ROW_HEADER_HEADER", "datatype": "string"},
                        {"value": "注册时间", "type": "ROW_HEADER_HEADER", "datatype": "string"},
                        {"value": "状态", "type": "ROW_HEADER_HEADER", "datatype": "string"}
                    ];
                    JSONToExcelConvertor(data.users, "用户列表", title);
                }  else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                }else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })

    });

    function JSONToExcelConvertor(JSONData, FileName, ShowLabel) {
        //先转化json
        var arrData = typeof JSONData != 'object' ? JSON.parse(JSONData) : JSONData;
        var excel = '<table>';
        //设置表头
        var row = "<tr>";
        for (var i = 0, l = ShowLabel.length; i < l; i++) {
            row += "<td>" + ShowLabel[i].value + '</td>';
        }
        //换行
        excel += row + "</tr>";
        //设置数据
        for (var i = 0; i < arrData.length; i++) {
            var row = "<tr>";
            var obj = arrData[i];
            row += '<td>' + obj.id + '</td>';
            row += '<td>' + obj.username + '</td>';
            row += '<td>' + obj.mobile + '</td>';
            if (obj.shopKeeper){
            	row += '<td>' + obj.userLevel + '(店主)</td>';
            }else{
            	row += '<td>' + obj.userLevel + '</td>';
            }
            row += '<td>' + obj.parent + '</td>';
            row += '<td>' + obj.junior + '</td>';
            row += '<td>' + obj.createDate + '</td>';
            row += '<td>' + obj.state + '</td>';
            excel += row + "</tr>";
        }

        excel += "</table>";

        var excelFile = "<html xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:x='urn:schemas-microsoft-com:office:excel' xmlns='http://www.w3.org/TR/REC-html40'>";
        excelFile += '<meta http-equiv="content-type" content="application/vnd.ms-excel; charset=UTF-8">';
        excelFile += '<meta http-equiv="content-type" content="application/vnd.ms-excel';
        excelFile += '; charset=UTF-8">';
        excelFile += "<head>";
        excelFile += "<!--[if gte mso 9]>";
        excelFile += "<xml>";
        excelFile += "<x:ExcelWorkbook>";
        excelFile += "<x:ExcelWorksheets>";
        excelFile += "<x:ExcelWorksheet>";
        excelFile += "<x:Name>";
        excelFile += FileName;
        excelFile += "</x:Name>";
        excelFile += "<x:WorksheetOptions>";
        excelFile += "<x:DisplayGridlines/>";
        excelFile += "</x:WorksheetOptions>";
        excelFile += "</x:ExcelWorksheet>";
        excelFile += "</x:ExcelWorksheets>";
        excelFile += "</x:ExcelWorkbook>";
        excelFile += "</xml>";
        excelFile += "<![endif]-->";
        excelFile += "</head>";
        excelFile += "<body>";
        excelFile += excel;
        excelFile += "</body>";
        excelFile += "</html>";


        var uri = 'data:application/vnd.ms-excel;charset=utf-8,' + encodeURIComponent(excelFile);

        var link = document.createElement("a");
        link.href = uri;

        link.style = "visibility:hidden";
        link.download = FileName + ".xls";

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }


    function loadData() {
        showLoading();
        $.ajax({
            type: "GET",
            url: '/back/users',
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
                } else if(data.code == "0014"){
                	
                }else {
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
        tr = tr + '<td>' + user.id + '</td>';
        tr = tr + '<td>' + user.username + '</td>';
        tr = tr + '<td>' + user.mobile + '</td>';
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
        tr = tr + '</td>';

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