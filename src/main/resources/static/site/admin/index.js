$(function () {
    var token = $.AMUI.utils.cookie.get("adminToken");
    if (token == null) {
        location.replace("/admin/login.html");
        return;
    }
    showLoading();
    initHeader('index');
    $.ajax({
        type: "GET",
        url: "/back/admin",
        dataType: "JSON",
        beforeSend: function (request) {
            request.setRequestHeader("token", token);
        },
        success: function (result) {
            var code = result.data.code;
            if (code == "0000") {
                $.AMUI.utils.cookie.set("adminName", result.data.name, 8 * 60 * 1000, "/");//设置路径为根目录
                $("#admin-user").html(result.data.name);
                $("#admin-percent").html("当前转化率：" + result.data.percent + "‰");
                $("#admin-rule").html("当前充值规则：" + result.data.rule);
                $("#ticket-rule").html(result.data.ticketPercent);
                $("#replenish-rule").html(result.data.replenishPercent);
                $("#payout-rule").html(result.data.payoutConfig);
                for (var i = 0; i < result.data.levelUps.length; i++) {
                    var obj = result.data.levelUps[i];
                    if (i == 0) {
                        $("#admin-level-rule").append('<div>当前升级规则：' + obj.rule + '</div>');
                    } else {
                        $("#admin-level-rule").append('<div style="margin-left: 140px;">' + obj.rule + '</div>');
                    }
                }
            } else if (code == "0012" || code == "0010") {
                location.replace("/admin/login.html");
            }
            hideLoading();
        }
    })

})