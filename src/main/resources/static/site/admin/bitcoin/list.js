$(function() {
	initHeader('record');
	// 获取今天的日期
	var preMonth = new XDate();
	preMonth.addMonths(-1); // 上个月的今天
	var start = "" + new XDate(preMonth).toString('yyyy-MM-dd') + "";
	var end = "" + new XDate().toString('yyyy-MM-dd') + "";

	var token = $.AMUI.utils.cookie.get("adminToken");
	var page = 1;
	var size = 10;
	var phone = "";

	$("#phone").val(phone);
	$("#start").val(start);
	$("#end").val(end);
	loadData();

	$("#search").click(function() {
		start = $("#start").val();
		end = $("#end").val();
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
			type : "GET",
			url : "/back/bitcoinRecords",
			dataType : "JSON",
			beforeSend : function(request) {
				request.setRequestHeader("token", token);
			},
			data : {
				"start" : start,
				"end" : end,
				"page" : page,
				"mobile" : phone,
				"size" : size
			},
			success : function(result) {
				var data = result.data;
				if (data.code == "0000") {
					var bitcoinRecords = data.bitcoinRecords;
					if (page == 1) {
						createPage(data.page);
					}
					$("#dataError").html("");
					$("#gh-scoreRecord-list").empty();
					for (var i = 0; i < bitcoinRecords.length; i++) {
						createTr(bitcoinRecords[i])
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

	function createTr(bitcoinRecord) {
		var tr = '<tr>';
		tr = tr + '<td>' + bitcoinRecord.id + '</td>';
		tr = tr + '<td><a href="/admin/user/view.html?id='
				+ bitcoinRecord.userId + '">' + bitcoinRecord.username
				+ '</a></td>';
		tr = tr + '<td>' + bitcoinRecord.remark + '</td>';
		if (bitcoinRecord.minus) {
			tr = tr + '<td>减少</td>';
		} else {
			tr = tr + '<td>增加</td>';
		}

		tr = tr + '<td>' + bitcoinRecord.amount + '</td>';
		tr = tr + '<td>' + bitcoinRecord.total + '</td>';
		tr = tr + '<td>' + bitcoinRecord.createDate + '</td>';
		tr = tr + '</tr>';
		$("#gh-scoreRecord-list").append(tr);
	}

	function createPage(pageParams) {
		$(".pager").pagination({
			recordCount : pageParams.total, // 总记录数
			pageSize : pageParams.size, // 一页记录数
			onPageChange : function(pageNumber) {
				page = pageNumber;
				loadData();
			}
		});
	}

})