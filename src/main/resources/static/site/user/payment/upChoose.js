$(function () {
    showLoading("玩命加载中....");
    var token = $.AMUI.utils.cookie.get("token");

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
                	if(data.shopKeeper){
                		$("#up-shoper").addClass("am-hide");
                	}
                	if(!data.upgrade){
                		$("#up-consumer").addClass("am-hide");
                	}
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