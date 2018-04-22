$(function () {
    initHeader('config');
    var token = $.AMUI.utils.cookie.get("adminToken");

    loadData();
    change();

    function change() {
        $("#userLevel").change(function () {
            var name = $("#userLevel").val();
            if (name == 1) {
                $("#directAll").removeClass("am-hide");
            } else {
                $("#directAll").addClass("am-hide");
            }
        });
    }


    function loadData() {
        $.ajax({
            type: "GET",
            url: "/config/level/up",
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            success: function (result) {
                var data = result.data;
                if (data.levelUps.length == 0) {
                    $("#level-rule").html("规则未配置");
                } else {
                    for (var i = 0; i < data.levelUps.length; i++) {
                        var obj = data.levelUps[i];
                        if (i == 0) {
                            $("#level-rule").append('<div>当前升级规则：' + obj.rule + '</div>');
                        } else {
                            $("#level-rule").append('<div style="margin-left: 110px;">' + obj.rule + '</div>');
                        }
                    }
                }
                for (var i = 0; i < data.userLevels.length; i++) {
                    var obj = data.userLevels[i];
                    $("#userLevel").append('<option value="' + obj.value + '">' + obj.name + '</option>');
                }

                hideLoading();
            }
        })
    }

    var Num1 = false;
    var Num2 = false;
    var Num3 = false;

    $("#amount").on('input', function () {
        var amount = Number($("#amount").val());
        if (amount < 0) {
            $("#amount").css("border", "1px solid red");
            $("#amountError").html("金额不能小于0");
            return;
        } else {
            Num1 = true;
            $("#amount").css("border", "1px solid #ccc");
            $("#amountError").html("");
        }
    })
    $("#directAmount").on('input', function () {
        var directAmount = Number($("#directAmount").val());
        if (directAmount < 0) {
            $("#directAmount").css("border", "1px solid red");
            $("#directAmountError").html("金额不能小于0");
            return;
        } else {
            Num2 = true;
            $("#directAmount").css("border", "1px solid #ccc");
            $("#directAmountError").html("");
        }
    })

    $('#level-set').click(
        function () {
            var userLevel = $("#userLevel").val();
            var amount = Number($("#amount").val());
            var directAmount = Number($("#directAmount").val());

            if (userLevel == 1) {
                if (Num1 && Num2) {
                    showLoading();
                    $.ajax({
                        type: "POST",
                        url: "/config/level/up/" + userLevel,
                        dataType: "JSON",
                        contentType: "application/json; charset=utf-8",
                        beforeSend: function (request) {
                            request.setRequestHeader("token", token);
                        },
                        data: JSON.stringify({
                            "amount": amount,
                            "directAmount": directAmount
                        }),
                        success: function (result) {
                            var data = result.data;
                            if (data.code == "0000") {
                                location.reload();
                            } else if (data.code == "0038") {
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
            } else {
                showLoading();
                $.ajax({
                    type: "POST",
                    url: "/config/level/up/" + userLevel,
                    dataType: "JSON",
                    contentType: "application/json; charset=utf-8",
                    beforeSend: function (request) {
                        request.setRequestHeader("token", token);
                    },
                    data: JSON.stringify({
                        "amount": amount,
                        "directAmount": 0
                    }),
                    success: function (result) {
                        var data = result.data;
                        if (data.code == "0000") {
                            location.reload();
                        } else if (data.code == "0038") {
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


        });
})