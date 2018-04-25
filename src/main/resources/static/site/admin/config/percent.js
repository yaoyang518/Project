$(function () {
    initHeader('config');
    var token = $.AMUI.utils.cookie.get("adminToken");

    loadData();

    function loadData() {
        $.ajax({
            type: "GET",
            url: "/config/percent",
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            success: function (result) {
                console.log(result);
                var data = result.data;
                switch (data.code) {
                    case "0000":
                        for (var i = 0; i < data.percents.length; i++) {
                            var obj = result.data.percents[i];
                            if(obj.value == data.percent){
                                $("#percent-select").append('<option selected value="' + obj.value + '">' + obj.name + '</option>');
                            }else{
                                $("#percent-select").append('<option value="' + obj.value + '">' + obj.name + '</option>');
                            }
                        }
                        $("#config-percent").html(data.percent + "‰");
                        break;
                    case "0020":
                        $("#config-percent").html(data.msg);
                        break;
                    case "0010":
                    	location.replace("/admin/login.html");
                    	break;
                    default:
                        alert(data.msg);
                }
                hideLoading();
            }
        })
    }

    $('#confirm').click(function () {
        var percent = $("#percent-select").val();
        var percentNow = $("#config-percent").html().replace("‰","");
        if (percent != percentNow) {
            showLoading();
            $.ajax({
                type: "POST",
                url: "/config/percent/save?percent=" + percent,
                dataType: "JSON",
                beforeSend: function (request) {
                    request.setRequestHeader("token", token);
                },
                success: function (result) {
                    switch (result.data.code) {
                        case "0000":
                            location.reload();
                            break;
                        case "0010":
                            location:"/admin/login.html";
                            break;
                        case "0038":
                            initWarning();
                            showWarning();
                            break;
                        default:
                            alert(result.data.msg);
                    }
                    hideLoading();
                }
            })
        } else {
        	$("#percent-select").css("border", "1px solid red");
			$("#parentError").html("不得与当前转化率相同");
        }
    })

})