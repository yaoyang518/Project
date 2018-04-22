$(function () {
    showLoading("玩命加载中....");
    var token = $.AMUI.utils.cookie.get("token");
    
    //创建列表
    function createList(array) {
        for (var k = 0; k < array.length; k++) {
            var objet = array[k];
            var div =  '<a href="/user/payaccount/edit.html?id='+ objet.id + '"><div class=" am-text-center am-margin-top-sm lv-bank-bg gh-text-white am-cf lv-relative">';
            div = div + '<div class="lv-cash-bank">'+objet.bank+'</div>';
            div = div + '<div class="am-margin-top-xs x-text-gray lv-cash-cardNum">'+objet.cardNo+'</div>';
            div = div + '</div></a>';
            $("#bank").append(div);
        }
    }

    $.ajax({
        type: "GET",
        url: "/bankAccountApi/bankAccounts",
        dataType: "JSON",
        beforeSend: function (request) {
            request.setRequestHeader("token", token);
        },
        success: function (result) {
            var data = result.data
            switch (data.code) {
                case "0000":
                	createList(data.bankAccounts);
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