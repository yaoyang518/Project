$(function () {
	 initHeader('config');
    var token = $.AMUI.utils.cookie.get("adminToken");

    loadData();

    function loadData() {
        $.ajax({
            type: "GET",
            url: "/config/level",
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            success: function (result) {
                var data = result.data;
                if (data.rule == "充值积分规则未配置") {
                    $("#rule-first").html(result.data.rule);
                    $("#rule-second").html(result.data.rule);
                   /* $("#rule-third").html(result.data.rule);*/

                    for (var i = 0; i < data.multiple.length; i++) {
                        var obj = data.multiple[i];
                        $(".rule-select").append('<option value="' + obj.value + '">' + obj.name + '</option>');
                    }
                    for (var i = 0; i < data.level.length; i++) {
                        var obj = data.level[i];
                        $(".rule-money-select").append('<option value="' + obj.value + '">' + obj.name + '</option>');
                    }

                } else {
                    var n = data.rule.split("；");
                    for (var i = 0; i < n.length; i++) {
                        var x = n[i].split("，");
                        if (i == 0) {
                            $("#rule-nav-second").html(x[0]);
                            $("#rule-nav-first").html(data.firstMultiple);
                        } else if (i == 1) {
                            $("#rule-nav-third").html(data.secondMultiple);
                            $("#rule-nav-fourth").html(x[0]);
                        }
                    }

                    for (var i = 0; i < data.level.length; i++) {
                        var obj = data.level[i];
                        if (obj.value == data.firstLevel) {
                            $("#rule-first-level").append('<option selected value="' + obj.value + '">' + obj.name + '</option>');
                        } else {
                            $("#rule-first-level").append('<option value="' + obj.value + '">' + obj.name + '</option>');
                        }
                    }


                    for (var i = 0; i < data.multiple.length; i++) {
                        var obj = data.multiple[i];
                        if (obj.value == data.firstMultiple) {
                            $("#rule-first-multiple").append('<option selected value="' + obj.value + '">' + obj.name + '</option>');
                        } else {
                            $("#rule-first-multiple").append('<option value="' + obj.value + '">' + obj.name + '</option>');
                        }
                    }

                    for (var i = 0; i < data.multiple.length; i++) {
                        var obj = data.multiple[i];
                        if (obj.value == data.secondMultiple) {
                            $("#rule-second-multiple").append('<option selected value="' + obj.value + '">' + obj.name + '</optionselected>');
                        } else {
                            $("#rule-second-multiple").append('<option value="' + obj.value + '">' + obj.name + '</option>');
                        }
                    }


                }


                hideLoading();
            }
        })
    }


    $('#rule-percent-set').click(
        function () {
            var firstLevel = Number($("#rule-first-level").val());
            var firstMultiple = Number($("#rule-first-multiple").val());
            var secondMultiple = Number($("#rule-second-multiple").val());
           
            showLoading();
            $.ajax({
                type: "PUT",
                url: "/config/level/save",
                dataType: "JSON",
                contentType: "application/json; charset=utf-8",
                beforeSend: function (request) {
                    request.setRequestHeader("token", token);
                },
                data: JSON.stringify({
                    "firstLevel": firstLevel,
                    "firstMultiple": firstMultiple,
                    "secondMultiple": secondMultiple,
                }),
                success: function (result) {
                    var data = result.data;
                    if (data.code == "0000") {
                        location.reload();
                    }else if(data.code == "0038"){
                    	initWarning();
                    	showWarning();
                    }  else if (data.code == "0010") {
                        location.replace("/admin/login.html");
                        return;
                    } else {
                        alert(data.msg);
                    }
                    hideLoading();
                }
            })
        });
})