$(function () {
	 initHeader('cash');
    var token = $.AMUI.utils.cookie.get("adminToken");
    var id = location.search.split("id=", 2)[1];

	loadDetail();
    // 请求信息
    function loadDetail() {
        showLoading();
        $.ajax({
            type: "GET",
            url: "/back/payoutRecord/"+id,
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            success: function (result) {
            	console.log(result);
                var payoutRecord = result.data.payoutRecord[0];
                $("#cashAll").append('用户<a href="/admin/user/view.html?id='
                    + payoutRecord.id +'">'+payoutRecord.mobile+"</a>于"+payoutRecord.applyDate+'发起提现申请，申请金额<span class="am-text-success">'+payoutRecord.amount+'</span>元，需付款<span class="am-text-success">'+payoutRecord.cash+"</span>元。<br/>");
                if(payoutRecord.payMethod=="支付宝"){
                	$("#cashAll").append('账号:<span class="am-text-success">'+payoutRecord.username+"</span>，"+'<span class="am-text-success">'+payoutRecord.cardNo+'</span>'+"<br/>")
                }else{
                	$("#cashAll").append('账号:<span class="am-text-success">'+payoutRecord.username+"</span>，"+payoutRecord.bank+"-"+payoutRecord.branch+"，"+'<span class="am-text-success">'+payoutRecord.cardNo+'</span>'+"<br/>")
                }
                if(payoutRecord.applyStatus=="申请中"){
                	$("#cashAll").append("请及时处理:<br/>");
                	$(".cash-tag").show();
                }else if(payoutRecord.applyStatus=="已拒绝"){
                	$(".cash-tag").hide();
                	$("#cashAll").append(payoutRecord.updateDate+'<span class="am-text-success">已拒绝</span>，拒绝理由：'+payoutRecord.remark+"<br/>");
                }else if(payoutRecord.applyStatus=="已通过"){
                	$(".cash-tag").hide();
                	$("#cashAll").append(payoutRecord.updateDate+'<span class="am-text-success">  已打款</span>，交易流水   '+payoutRecord.tradeNo+"<br/>");
                }
                
                
                hideLoading();
            }
        })
    }
    	
    $(".record-yes").click(function(){
        $("#recordModal").modal('open');
    })
     $(".record-no").click(function(){
        $("#recordModal2").modal('open');
    })
    
    
    $("#record-set").click(function () {
    	var tradeNo = $("#recordlsh").val();
         $.ajax({
             type: "PUT",
             url: "/back/payoutRecord/approve/" +id+"?tradeNo="+tradeNo,
             contentType: "application/json; charset=utf-8",
             beforeSend: function (request) {
                 request.setRequestHeader("token", token);
             },
             success: function (result) {
            	 console.log(result);
                 var data = result.data;
                 if (data.code == "0000") {
                	 window.location.reload();
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
             url: "/back/payoutRecord/reject/" +id+"?remark="+remark,
             contentType: "application/json; charset=utf-8",
             beforeSend: function (request) {
                 request.setRequestHeader("token", token);
             },
             success: function (result) {
                 var data = result.data;
                 if (data.code == "0000") {
                     window.location.reload();
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