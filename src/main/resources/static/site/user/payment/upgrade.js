$(function () {
    var token = $.AMUI.utils.cookie.get("token");
    //创建列表

    loadUP();

    function loadUP() {
        showLoading();
        $.ajax({
            type: "GET",
            url: "/userApi/shopkeeperInfo",
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    if (data.bitcoin < data.price) {
                        $("#upErr").html("报单积分不足" + data.price + ",无法升级");
                        $('#up').removeClass("lv-bg-green");
                        $('#up').addClass("lv-bg-grey");
                        $('#up').attr("disabled", true);
                    } else {
                        $('#up').attr("disabled", false);
                        $('#up').removeClass("lv-bg-grey");
                        $('#up').addClass("lv-bg-green");
                        $("#upErr").html("")
                    }
                    $("#username").html(data.username);
                    $("#id").html(data.id);
                    $("#price").html(data.price);
                } else if (data.code == "0010" || data.code == "0027") {
                    location.replace("/login.html");
                    return;
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }

    $('#up').click(
        function () {
            showLoading();
            $.ajax({
                type: "POST",
                url: "/userApi/shopkeeper",
                contentType: "application/json; charset=utf-8",
                beforeSend: function (request) {
                    request.setRequestHeader("token", token);
                },
                success: function (result) {
                    var data = result.data;
                    if (data.code == "0000") {
                        location.replace("/user/payment/upSuccess.html");
                    }else if (data.code == "0035") {
                        $("#upErr").html("报单积分不足")
                    } else if (data.code == "0010" || data.code == "0027") {
                        location.replace("/login.html");
                        return;
                    }
                    hideLoading();
                }
            })
        })
})