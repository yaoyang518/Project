$(function () {
    showLoading("玩命加载中....");
    var token = $.AMUI.utils.cookie.get("token");
    
    //创建列表
    function createList(array) {
        for (var k = 0; k < array.length; k++) {
            var objet = array[k];
            var div =  '<a href="/user/aliPay/edit.html?id='+ objet.id + '"><div class=" am-text-center am-margin-top-sm lv-alipay-bg gh-text-white  am-cf lv-relative">';
            div = div + '<div class="lv-cash-ali ">支付宝账号：'+objet.accountNumber+'</div>';
            if(objet.asDefault){
            	div = div + '<div class="am-margin-top-xs x-text-gray lv-default-alipay">默认</div>';
            }
            div = div + '</div></a>';
            $("#alipay").append(div);
        }
    }

    $.ajax({
        type: "GET",
        url: "/aliPayAccountApi/aliPayAccounts",
        dataType: "JSON",
        beforeSend: function (request) {
            request.setRequestHeader("token", token);
        },
        success: function (result) {
        	console.log(result);
            var data = result.data
            switch (data.code) {
                case "0000":
                	createList(data.aliPayAccounts);
                    break;
                case "0050":
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