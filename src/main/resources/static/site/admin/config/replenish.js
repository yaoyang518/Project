$(function () {
	 initHeader('config');
    var token = $.AMUI.utils.cookie.get("adminToken");

    loadData();

    function loadData() {
        $.ajax({
            type: "GET",
            url: "/config/replenishPercent",
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            success: function (result) {
                var data = result.data;
                switch (data.code) {
                    case "0000":
                        for (var i = 0; i < data.replenishPercens.length; i++) {
                            var obj = result.data.replenishPercens[i];
                            if(obj.value == data.percent){
                                $("#replenish-select").append('<option selected value="' + obj.value + '">' + obj.name + '</option>');
                            }else{
                                $("#replenish-select").append('<option value="' + obj.value + '">' + obj.name + '</option>');
                            }
                        }
                        if(data.replenishPercen=="补货率未配置"){
                        	$("#config-replenish").html(data.replenishPercen);
                        }else{
                        	  $("#config-replenish").html(data.replenishPercen + "%");
                        }
                      
                        break;
                    case "0020":
                        $("#config-replenish").html(data.msg);
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
        var percent = $("#replenish-select").val();
        var percentNow = $("#config-replenish").html().replace("%","");
        if (percent != percentNow) {
            showLoading();
            $.ajax({
                type: "POST",
                url: "/config/replenishPercent/save?percent=" + percent,
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
        	$("#replenish-select").css("border", "1px solid red");
			$("#replenishError").html("不得与当前转化率相同");
        }
    })

})