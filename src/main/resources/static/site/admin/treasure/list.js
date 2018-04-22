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

	$("#start").val(start);
	$("#end").val(end);
	loadData();

	$("#search").click(function() {
		start = $("#start").val();
		end = $("#end").val();
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
			url : "/back/scoreRecords",
			dataType : "JSON",
			beforeSend : function(request) {
				request.setRequestHeader("token", token);
			},
			data : {
				"start" : start,
				"end" : end,
				"page" : page,
				"size" : size
			},
			success : function(result) {
				var data = result.data;
				if (data.code == "0000") {
					var scoreRecords = data.scoreRecords;
					$("#score-canuse").html(data.score);
					$("#score-frozen").html(data.frozenScore);
					$("#score-total").html(data.totalScore);
					if (page == 1) {
						createPage(data.page);
					}
					$("#dataError").html("");
					$("#gh-scoreRecord-list").empty();
					for (var i = 0; i < scoreRecords.length; i++) {
						createTr(scoreRecords[i])
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

	function createTr(scoreRecord) {
		var tr = '<tr>';
		tr = tr + '<td><a href="/admin/user/view.html?id=' + scoreRecord.userId
				+ '">' + scoreRecord.username + '</a></td>';
		tr = tr + '<td>' + scoreRecord.scoreSource + '</td>';
		if (scoreRecord.minus) {
			tr = tr + '<td>减少</td>';
		} else {
			tr = tr + '<td>增加</td>';
		}

		tr = tr + '<td>' + scoreRecord.amount + '</td>';
		tr = tr + '<td>' + scoreRecord.score + '</td>';
		tr = tr + '<td>' + scoreRecord.frozen + '</td>';
		tr = tr + '<td>' + scoreRecord.total + '</td>';
		tr = tr + '<td>' + scoreRecord.createDate + '</td>';
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