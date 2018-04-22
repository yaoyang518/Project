$(function () {
	 initHeader('user');
    var token = $.AMUI.utils.cookie.get("adminToken");
    var params = parseURL(window.location.href);
    var id = params.id;

    $.ajax({
        type: "GET",
        url: "/back/user/" + id,
        dataType: "JSON",
        beforeSend: function (request) {
            request.setRequestHeader("token", token);
        },
        success: function (result) {
            var data = result.data;
            if (data.code == "0000") {
                $("#username").val(data.user.username);
                $("#rechargeMobile").html(data.user.mobile);
                $("#mobile").val(data.user.mobile);
            } else if (data.code == "0010") {
                location.replace("/admin/login.html");
                return;
            } else {
                alert(data.msg);
            }
            hideLoading();
        }
    })

    $("#confirm").click(function () {
            var amount = $("#amount").val();
            if (amount < 10000) {
               $("#amount").css("border","1px solid red ");
               $("#amountError").html("充值金额最少为1万元");
                return;
            }
            $("#amount").css("border","1px solid #ccc ");
            $("#amountError").html("");
            $("#rechargeAmount").html(amount + "元");
            $("#rechargeModal").modal('open');
        }
    )


    $("#recharge").click(
        function () {
            $("#rechargeModal").modal('close');
            recharge();
        }
    )

    function recharge() {
        var amount = $("#amount").val();
        showLoading("玩命充值中...");
        $.ajax({
            type: "POST",
            url: "/back/recharge",
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "id": id,
                "amount": amount
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                	 $("#rechargrModal-tips").modal('open');
                	 $("#recharge-title").html("充值成功")
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