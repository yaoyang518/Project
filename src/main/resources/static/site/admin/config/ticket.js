$(function () {
	 initHeader('config');
    var token = $.AMUI.utils.cookie.get("adminToken");

    loadData();

    function loadData() {
        $.ajax({
            type: "GET",
            url: "/config/ticketPercent",
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            success: function (result) {
                var data = result.data;
                switch (data.code) {
                    case "0000":
                        for (var i = 0; i < data.ticketPercents.length; i++) {
                            var obj = result.data.ticketPercents[i];
                            if(obj.value == data.ticketPercent){
                                $("#ticket-select").append('<option selected value="' + obj.value + '">' + obj.name + '</option>');
                            }else{
                                $("#ticket-select").append('<option value="' + obj.value + '">' + obj.name + '</option>');
                            }
                        }
                        if(data.ticketPercent == "消费劵未配置"){
                        	$("#config-ticket").html(data.ticketPercent);
                        }else{
                        	  $("#config-ticket").html(data.ticketPercent + "%");
                        }
                      
                        break;
                    case "0020":
                        $("#config-ticket").html(data.msg);
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
        var percent = $("#ticket-select").val();
        var percentNow = $("#config-ticket").html().replace("%","");
        if (percent != percentNow) {
            showLoading();
            $.ajax({
                type: "POST",
                url: "/config/ticketPercent/save?percent=" + percent,
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
        	$("#ticket-select").css("border", "1px solid red");
			$("#ticketErr").html("不得与当前转化率相同");
        }
    })

})