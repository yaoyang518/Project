$(function () {
    initLoading();
    //监听回车键
    document.onkeyup = function (event) {
        var event = event || window.event;
        if (event.keyCode == 13) {
            login();
        }
    }
    //点击绑定
    $('#lv-login').click(
        function () {
            login();
        }
    );

    //登录方法
    function login() {
        $(".lv-account-error").addClass("am-hide");
        $(".lv-password-error").addClass("am-hide")
        var username = $('#gh-account').val();
        var password = $("#gh-password").val();
        if (username == "") {
            $(".lv-account-error").removeClass("am-hide").html("请输入用户名");
            return;
        }
        if (password == "") {
            $(".lv-password-error").removeClass("am-hide").html("请输入密码");
            return;
        }
        if (username != "" && password != "") {
            showLoading("玩命登录中...");
            $.ajax({
                type: "POST",
                url: "/login/admin",
                dataType: "JSON",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify({
                    "username": username,
                    "password": password,
                }),
                success: function (result) {
                    var code = result.data.code;
                    switch (code) {
                        case "0000":
                            $.AMUI.utils.cookie.set("adminToken", result.data.token, 8 * 60 * 1000, "/");//设置路径为根目录
                            location.href = "/admin/index.html";
                            break;
                        case "0009":
                            $(".lv-password-error").removeClass("am-hide").html("密码错误");
                            break;
                        case "0041":
                            $(".lv-account-error").removeClass("am-hide").html("该账号已被锁定");
                            break;
                        case "0012":
                            $(".lv-account-error").removeClass("am-hide").html("用户不存在");
                            break;
                    }
                    hideLoading();
                }
            })
        }

    }


    function initLoading() {
        $("body").append('<!-- loading -->' +
            '<div class="am-modal am-modal-loading am-modal-no-btn" tabindex="-1" id="my-loading">' +
            '<div class="am-modal-dialog">' +
            '<div class="am-modal-hd">正在载入...</div>' +
            '<div class="am-modal-bd">' +
            '<span class="am-icon-spinner am-icon-spin"></span>' +
            '</div></div></div>'
        );
    }

    function showLoading(text) {
        $(".am-modal-hd").html(text);
        $("#my-loading").modal('open');
    }

    function hideLoading() {
        $("#my-loading").modal("close");
    }
})
