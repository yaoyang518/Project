$(function () {
    var token = $.AMUI.utils.cookie.get("token");
    var idBank = location.search.split("bankAccountId=", 2)[1];
    var idAli = location.search.split("id=", 2)[1];
    console.log(idBank);
    console.log(idAli);
    var payoutConfig = 0;

    if (typeof(idBank) == "undefined") {
        url = "/bankAccountApi/default?bankAccountId=";
    } else {
        url = "/bankAccountApi/default?bankAccountId=" + idBank;
        $(".cash-header").eq(1).removeClass("am-active");
        $(".cash-header").eq(0).addClass("am-active");
        $(".am-tab-panel").eq(0).addClass("am-active");
        $(".am-tab-panel").eq(0).addClass("am-in");
        $(".am-tab-panel").eq(1).removeClass("am-active");
        $(".am-tab-panel").eq(1).removeClass("am-in");
    }
    if (typeof(idAli) == "undefined") {
        Aliurl = "/aliPayAccountApi/default?id=";
    } else {
        Aliurl = "/aliPayAccountApi/default?id=" + idAli;
        $(".cash-header").eq(0).removeClass("am-active");
        $(".cash-header").eq(1).addClass("am-active");
        $(".am-tab-panel").eq(0).removeClass("am-active");
        $(".am-tab-panel").eq(0).removeClass("am-in");
        $(".am-tab-panel").eq(1).addClass("am-active");
        $(".am-tab-panel").eq(1).addClass("am-in");

    }
    //创建列表

    loadBank();
    loadAlipay();

    function loadBank() {
        showLoading();
        $.ajax({
            type: "GET",
            url: url,
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            success: function (result) {
                console.log(result);
                var data = result.data;
                if (data.code == "0000") {
                    $("#bankNull").hide();
                    $("#Bank-all").show();
                    var percent = data.ticketPercent * 100;
                    $("#cardNum").html(data.cardNo.substr(data.cardNo.length - 4, 4));
                    $("#bank").html(data.bank);
                    $("#cash-rule1").html("提现金额必须是" + result.data.payoutConfig + "的倍数")
                    $("#cash-rule2").html("提现金额按" + percent + "%转化消费券")
                    idBank = data.id;
                    percent = data.ticketPercent;
                    payoutConfig = result.data.payoutConfig;
                } else if (data.code == "0050") {
                    $(".cash-header").eq(0).removeClass("am-active");
                    $(".cash-header").eq(1).addClass("am-active");
                    $(".am-tab-panel").eq(0).removeClass("am-active");
                    $(".am-tab-panel").eq(0).removeClass("am-in");
                    $(".am-tab-panel").eq(1).addClass("am-active");
                    $(".am-tab-panel").eq(1).addClass("am-in");
                    $("#bankNull").show();
                    $("#Bank-all").hide();
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

    function loadAlipay() {
        showLoading();
        $.ajax({
            type: "GET",
            url: Aliurl,
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            success: function (result) {
                console.log(result);
                var data = result.data;
                if (data.code == "0000") {
                    $("#alipayNull").hide();
                    $("#Alipay-all").show();
                    var percent = data.ticketPercent * 100;
                    $("#AliNum").html("支付宝账号：" + data.accountNumber);
                    $("#Alipay").html(data.name);
                    $("#Ali-rule1").html("提现金额必须是" + result.data.payoutConfig + "的倍数")
                    $("#Ali-rule2").html("提现金额按" + percent + "%转化消费券")
                    idAli = data.id;
                    percent = data.ticketPercent;
                    payoutConfig = result.data.payoutConfig;
                } else if (data.code == "0066") {
                    $(".cash-header").eq(1).removeClass("am-active");
                    $(".cash-header").eq(0).addClass("am-active");
                    $(".am-tab-panel").eq(0).addClass("am-active");
                    $(".am-tab-panel").eq(0).addClass("am-in");
                    $(".am-tab-panel").eq(1).removeClass("am-active");
                    $(".am-tab-panel").eq(1).removeClass("am-in");
                    $("#alipayNull").show();
                    $("#Alipay-all").hide();
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

    $("#cashAmount").on('input', function () {
        var cashAmount = $("#cashAmount").val();
        var r = payoutConfig;
        if (cashAmount % r == 0) {
            $.ajax({
                type: "GET",
                url: "/userApi/info",
                dataType: "JSON",
                beforeSend: function (request) {
                    request.setRequestHeader("token", token);
                },
                success: function (result) {
                    var data = result.data;
                    switch (data.code) {
                        case "0000":
                            if (data.balance < cashAmount) {
                                $("#cashErr").html("余额不足")
                            } else {
                                $("#cashErr").html("")
                            }
                            break;
                        case "0056":
                            $("#cashErr").html("暂时无法提现");
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

                }
            })
        } else {
            $("#cashErr").html("提现金额需为" + payoutConfig + "的倍数")
        }

    });
    $("#AliAmount").on('input', function () {
        var cashAmount = $("#AliAmount").val();
        var r = payoutConfig;
        if (cashAmount % r == 0) {
            $.ajax({
                type: "GET",
                url: "/userApi/info",
                dataType: "JSON",
                beforeSend: function (request) {
                    request.setRequestHeader("token", token);
                },
                success: function (result) {
                    var data = result.data;
                    switch (data.code) {
                        case "0000":
                            if (data.balance < cashAmount) {
                                $("#AliErr").html("余额不足")
                            } else {
                                $("#AliErr").html("")
                            }
                            break;
                        case "0056":
                            $("#AliErr").html("暂时无法提现");
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
                }
            })
        } else {
            $("#AliErr").html("提现金额需为" + payoutConfig + "的倍数")
        }
    });


    $('#cash-out').click(
        function () {
            var amount = $("#cashAmount").val();
            var r = payoutConfig;
            if (amount != "") {
                if (amount % r == 0) {
                    showLoading();
                    $.ajax({
                        type: "POST",
                        url: "/userApi/payoutRecord/apply?id=" + idBank + "&amount=" + amount + "&type=bankcard",
                        contentType: "application/json; charset=utf-8",
                        beforeSend: function (request) {
                            request.setRequestHeader("token", token);
                        },
                        success: function (result) {
                            var data = result.data;
                            if (data.code == "0000") {
                                location.replace("/user/cashSuccess.html");
                            }
                            if (data.code == "0049") {
                                $("#cashErr").html("余额不足")
                            }
                            if (data.code == "0045") {
                                $("#cashErr").html("提现金额需为" + payoutConfig + "的倍数")
                            } else if (data.code == "0010" || data.code == "0027") {
                                location.replace("login.html");
                                return;
                            }
                            hideLoading();
                        }
                    })
                } else {
                    $("#cashErr").html("提现金额需为" + payoutConfig + "的倍数")
                }
            } else {
                $("#cashErr").html("提现金额不能为空")
            }


        });

    $('#Ali-out').click(
        function () {
            var amount = $("#AliAmount").val();
            var r = payoutConfig;
            if (amount != "") {
                if (amount % r == 0) {
                    showLoading();
                    $.ajax({
                        type: "POST",
                        url: "/userApi/payoutRecord/apply?id=" + idAli + "&amount=" + amount + "&type=alipay",
                        contentType: "application/json; charset=utf-8",
                        beforeSend: function (request) {
                            request.setRequestHeader("token", token);
                        },
                        success: function (result) {
                            console.log(result);
                            var data = result.data;
                            if (data.code == "0000") {
                                location.replace("/user/cashSuccess.html");
                            }
                            if (data.code == "0049") {
                                $("#AliErr").html("余额不足")
                            }
                            if (data.code == "0045") {
                                $("#AliErr").html("提现金额需为" + payoutConfig + "的倍数")
                            } else if (data.code == "0010") {
                                location.replace("/login.html");
                                return;
                            }
                            hideLoading();
                        }
                    })
                } else {
                    $("#AliErr").html("提现金额需为" + payoutConfig + "的倍数")
                }
            } else {
                $("#AliErr").html("提现金额不能为空")
            }


        });


})