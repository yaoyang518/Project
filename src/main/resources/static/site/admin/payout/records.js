$(function () {
	 initHeader('cash');
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
    var cardNo = "";
    var phone = "";
    var applyStatus ="";

    $("#card").val(cardNo);
    $("#phone").val(phone);
    $("#start").val(start);
    $("#end").val(end);
    loadData();

    $("#search").click(function () {
        start = $("#start").val();
        end = $("#end").val();
        cardNo = $("#card").val();
        phone = $("#phone").val();
        applyStatus =  $("#select  option:selected").val();
        if(start==""||end==""){
        	$("#dataError").html("日期格式错误")
        }else{
        	 page = 1;// 从第一页检索
             loadData();
        }

    })

    $('#download').click(function () {
        start = $("#start").val();
        applyStatus =  $("#select  option:selected").val();
        end = $("#end").val();
        card = $("#card").val();
        phone = $("#phone").val();
        showLoading();
        $.ajax({
            type: "GET",
            url: '/back/payoutRecords',
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "username": name,
                "mobile": phone,
                "start": start,
                "end": end,
                "page": 1,
                "size": 5000,
                "applyStatus":applyStatus,
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    var title = [
                        {"value": "账号", "type": "ROW_HEADER_HEADER", "datatype": "string"},
                        {"value": "申请金额", "type": "ROW_HEADER_HEADER", "datatype": "string"},
                        {"value": "金额", "type": "ROW_HEADER_HEADER", "datatype": "string"},
                        {"value": "收款人账号", "type": "ROW_HEADER_HEADER", "datatype": "string"},
                        {"value": "开户网点", "type": "ROW_HEADER_HEADER", "datatype": "string"},
                        {"value": "开户银行", "type": "ROW_HEADER_HEADER", "datatype": "string"},
                        {"value": "收款人", "type": "ROW_HEADER_HEADER", "datatype": "string"},
                        {"value": "申请时间", "type": "ROW_HEADER_HEADER", "datatype": "string"},
                        {"value": "状态", "type": "ROW_HEADER_HEADER", "datatype": "string"},
                        {"value": "流水", "type": "ROW_HEADER_HEADER", "datatype": "string"},
                        {"value": "拒绝理由", "type": "ROW_HEADER_HEADER", "datatype": "string"}
                    ];
                    JSONToExcelConvertor(data.payoutRecords, "提现列表", title);
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
            row += '<td>' + obj.mobile + '</td>';
            row += '<td>' + obj.amount + '</td>';
            if(obj.cash==undefined){
            	row += '<td></td>';
            }else{
            	row += '<td>' + obj.cash + '</td>';
            }
            row += '<td>' + obj.cardNo + '</td>';
            if(obj.branch==undefined){
            	row += '<td></td>';
            }else{
            	row += '<td>' + obj.branch + '</td>';
            }
            if(obj.bank==undefined){
            	 row += '<td></td>';
            }else{
            	 row += '<td>' + obj.bank + '</td>';
            }
            row += '<td>' + obj.username + '</td>';
            row += '<td>' + obj.applyDate + '</td>';
            row += '<td>' + obj.applyStatus + '</td>';
            if(obj.tradeNo==undefined){
            	  row += '<td></td>';
            }else{
            	  row += '<td>' + obj.tradeNo + '</td>';
            }
            if(obj.remark==undefined){
          	  row += '<td></td>';
            }else{
        	  row += '<td>' + obj.remark + '</td>';
            }
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
        applyStatus =  $("#select  option:selected").val();
        $.ajax({
            type: "GET",
            url: '/back/payoutRecords',
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "cardNo": cardNo,
                "mobile": phone,
                "start": start,
                "end": end,
                "page": page,
                "size": size,
                "applyStatus":applyStatus,
            },
            success: function (result) {
            	console.log(result);
                var data = result.data;
                if (data.code == "0000") {
                    var payoutRecords = data.payoutRecords;
                    $("#gh-user-list").empty();
                    for (var i = 0; i < payoutRecords.length; i++) {
                        createTr(payoutRecords[i])
                    }
                    if (page == 1) {
                        createPage(data.page);
                    }
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else if(data.code == "0014"){
                	$("#dataError").html("日期格式错误")
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

    function createTr(payoutRecord) {
        var flag = payoutRecord.applyStatus == "申请中";
        var tr = '<tr>';
        tr = tr + '<td>' + payoutRecord.mobile + '</td>';
        tr = tr + '<td>' + payoutRecord.amount + '</td>';
        if(payoutRecord.cash == undefined){
        	tr = tr + '<td></td>';
        }else{
        	tr = tr + '<td>' + payoutRecord.cash + '</td>';
        }
        tr = tr + '<td>' + payoutRecord.cardNo + '</td>';
        tr = tr + '<td>' + payoutRecord.username + '</td>';
        tr = tr + '<td>' + payoutRecord.applyDate + '</td>';
        tr = tr + '<td>' + payoutRecord.applyStatus + '</td>';
        tr = tr + '<td>';
        if(flag){
        	 tr = tr
        	  + '<button class="gh-btn-blue gh-btn-sm gh-margin-left-sm record-yes" data-toggle="modal" id="toggleRecord' + payoutRecord.id + '">通过</button>'
              + '<button class="gh-btn-blue gh-btn-sm gh-margin-left-sm record-no" data-toggle="modal" id="togglejujue' + payoutRecord.id + '">拒绝</button>';
        }
        tr = tr
   	 	+ '<a class="am-btn-success gh-btn-sm gh-margin-left-sm" href="/admin/payout/detail.html?id=' + payoutRecord.id + '">详情</a>';
        tr = tr + '</td>';

        tr = tr + '</tr>';
        $("#gh-user-list").append(tr);
        // 事件绑定
        $("#toggleRecord" + payoutRecord.id).click(function () {
            setrecordId(payoutRecord.id);
        });
     // 事件绑定
        $("#togglejujue" + payoutRecord.id).click(function () {
            setrecordId(payoutRecord.id);
        });
    }

	function  setrecordId(id){
		$("#optrecordId").val(id);
	}
	
	
    $("#gh-user-list").on("click", ".record-yes", function(){
        $("#recordModal").modal('open');
    })
     $("#gh-user-list").on("click", ".record-no", function(){
        $("#recordModal2").modal('open');
    })
    
    
    $("#record-set").click(function () {
    	var tradeNo = $("#recordlsh").val();
         $.ajax({
             type: "PUT",
             url: "/back/payoutRecord/approve/" +$("#optrecordId").val()+"?tradeNo="+tradeNo,
             contentType: "application/json; charset=utf-8",
             beforeSend: function (request) {
                 request.setRequestHeader("token", token);
             },
             success: function (result) {
                 var data = result.data;
                 if (data.code == "0000") {
                     location.href = "/admin/payout/records.html";
                     return;
                 } else if(data.code == "0052"){
                	 $("#recordErr").html("流水账号不能为空")
                 }
                 else if(data.code == "0051"){
                	 $("#recordErr").html("该记录已处理")
                 }else if (data.code == "0010") {
                     location.replace("/admin/login.html");
                     return;
                 } else {
                     alert(data.msg);
                 }
             }
         })
    })
    
     $("#jujue-set").click(function () {
    	var remark = $("#recordjujue").val();
         $.ajax({
             type: "PUT",
             url: "/back/payoutRecord/reject/" +$("#optrecordId").val()+"?remark="+remark,
             contentType: "application/json; charset=utf-8",
             beforeSend: function (request) {
                 request.setRequestHeader("token", token);
             },
             success: function (result) {
                 var data = result.data;
                 if (data.code == "0000") {
                     location.href = "/admin/payout/records.html";
                     return;
                 } else if(data.code == "0053"){
                	 $("#jujueErr").html("拒绝理由不能为空")
                 }else if(data.code == "0051"){
                	 $("#jujueErr").html("该记录已处理")
                 } else if (data.code == "0010") {
                     location.replace("/admin/login.html");
                     return;
                 } else {
                     alert(data.msg);
                 }
             }
         })
    })
    
    
    
   

})