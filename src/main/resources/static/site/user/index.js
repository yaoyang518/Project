$(function () {
    showLoading("玩命加载中....");
    var token = $.AMUI.utils.cookie.get("token");
    //清除cookie，退出登录
    $("#logout").click(function () {
        $.AMUI.utils.cookie.unset("token", "/");
        location.href = ("/login.html");
    })

    $.ajax({
        type: "GET",
        url: "/userApi/info",
        dataType: "JSON",
        beforeSend: function (request) {
            request.setRequestHeader("token", token);
        },
        success: function (result) {
            console.log(result);
            var data = result.data;
            switch (data.code) {
                case "0000":
                    if (data.shopKeeper) {
                        $("#uer-level2").html("店主");
                        $("#uer-level2").addClass("user-level-second");
                    }
                    if (data.shopKeeper && !data.upgrade) {
                        $("#up").addClass("am-hide");

                    }
                    if (data.bitcoin == "0") {
                        $("#bitcoin-block").hide();
                    }
                    $("#uer-level1").html(data.UserLevelName);
                    $("#uer-level1").addClass("user-level-first");
                    $("#user-phone").html(data.username);
                    $("#user-id").html("ID:" + data.id);
                    if (data.parent == null) {
                        $("#user-up").html("上级:无");
                    } else {
                        $("#user-up").html("上级:" + data.parent.id);
                    }
                    $("#balance").html(data.balance);
                    $("#frozen-score").html(data.frozenScore);
                    $("#score").html(data.score);
                    $("#bank").html(data.bankAcountTotal);
                    $("#bitcoin").html(data.bitcoin);
                    $("#ticket").html(data.ticket);
                    $("#Alipay").html(data.aliPayAcountTotal);
                    break;
                case "0010":
                    location.replace("/login.html");
                    break;
                case "0027":
                    location.replace("/login.html");
                    break;
                default:
                    alert(data.msg);
            }
            hideLoading();
        }
    })

})