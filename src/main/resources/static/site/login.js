$(function () {
    initLoading();
    // 查看密码
    $(".eyes").click(
        function () {
            if ($("input[password='1']").length == 1) {
                $("input[password='1']").attr('type', "text").attr(
                    "password", 2);
                $(".lv-eye-open").attr("src", "img/eye-close.png");
            } else {
                $("input[password='2']").attr('type', "password").attr(
                    "password", 1);
                $(".lv-eye-open").attr("src", "img/eye-open.png");
            }
        }
    );
    // 焦点事件

    var telflag = false;
    var pasflag = false;
    checkCode();
    checkPhone();
    
    function checkPhone(){
    	var tel = $('#lv-phone').val();
        var reg = /^0?1[3|4|5|7|8][0-9]\d{8}$/;
        if(tel !=""){
        	if (reg.test(tel)) {
                $.get("/check/mobile/" + tel, function (result, status) {
                    if (status == "success") {
                        switch (result.data.code) {
                            case "0001":// 用户已存在
                                $(".lv-error-one").html("");
                                $(".lv-login-lab1").addClass("am-text-xs");
                                $("#lv-phone").removeClass("lv-border-red");
                                $("#lv-phone").addClass("lv-border-grey");
                                telflag = true;
                                check(telflag, pasflag);
                                break;
                            case "0012"://用户不存在
                                $(".lv-error-one").html("手机号码未注册");
                                $(".lv-login-lab1").addClass("am-text-xs");
                                $("#lv-phone").removeClass("lv-border-grey");
                                $("#lv-phone").addClass("lv-border-red");
                                break;
                        }
                    } else {
                        alert("请求失败")
                    }
                });
            } else {
                $(".lv-error-one").html("手机格式不正确");
                $("#lv-phone").removeClass("lv-border-grey");
                $("#lv-phone").addClass("lv-border-red");
                disableLogin();
            }
        }
    	
    }
    
    function checkCode(){
    	  var password = $("#lv-password").val();
          var reg = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$/;
          if (password.length < 6) {
              disableLogin();
              return;
          }
          if (reg.test(password)) {
              $(".lv-login-lab2").addClass("am-text-xs");
              $(".lv-password-error").html("");
              $("#lv-password").removeClass("lv-border-red");
              $("#lv-password").addClass("lv-border-grey");
              pasflag = true;
              check(telflag, pasflag);
          } else {
              $(".lv-login-lab2").addClass("am-text-xs");
              $(".lv-password-error").html("密码格式不正确");
              $("#lv-password").removeClass("lv-border-grey");
              $("#lv-password").addClass("lv-border-red");
              disableLogin();
          }
    }

    $(".lv-login-ipt1").focus(function () {
        $(".lv-login-lab1").addClass("am-text-xs");
    });
    $(".lv-login-ipt1").blur(function () {
        $(".lv-login-lab1").removeClass("am-text-xs");
        var tel = $('#lv-phone').val();
        var reg = /^0?1[3|4|5|8|7][0-9]\d{8}$/;
        if (reg.test(tel)) {
            $.get("/check/mobile/" + tel, function (result, status) {
                if (status == "success") {
                    switch (result.data.code) {
                        case "0001":// 用户已存在
                            $(".lv-error-one").html("");
                            $(".lv-login-lab1").addClass("am-text-xs");
                            $("#lv-phone").removeClass("lv-border-red");
                            $("#lv-phone").addClass("lv-border-grey");
                            telflag = true;
                            check(telflag, pasflag);
                            break;
                        case "0012"://用户不存在
                            $(".lv-error-one").html("手机号码未注册");
                            $(".lv-login-lab1").addClass("am-text-xs");
                            $("#lv-phone").removeClass("lv-border-grey");
                            $("#lv-phone").addClass("lv-border-red");
                            break;
                    }
                } else {
                    alert("请求失败")
                }
            });
        } else {
            $(".lv-error-one").html("手机格式不正确");
            $("#lv-phone").removeClass("lv-border-grey");
            $("#lv-phone").addClass("lv-border-red");
            disableLogin();
        }

    });

    $(".lv-login-ipt1").on('input', function () {
        $(".lv-login-lab1").addClass("am-text-xs");
        var tel = $('#lv-phone').val();
        var reg = /^0?1[3|4|5|8|7][0-9]\d{8}$/;
        if (tel.length < 11) {
            disableLogin();
            return;
        }
        if (reg.test(tel)) {
            $.get("/check/mobile/" + tel, function (result, status) {
                if (status == "success") {
                    switch (result.data.code) {
                        case "0001":// 用户已存在
                            $(".lv-error-one").html("");
                            $(".lv-login-lab1").addClass("am-text-xs");
                            $("#lv-phone").removeClass("lv-border-red");
                            $("#lv-phone").addClass("lv-border-grey");
                            telflag = true;
                            check(telflag, pasflag);
                            break;
                        case "0012"://用户不存在
                            $(".lv-error-one").html("手机号码未注册");
                            $(".lv-login-lab1").addClass("am-text-xs");
                            $("#lv-phone").removeClass("lv-border-grey");
                            $("#lv-phone").addClass("lv-border-red");
                            break;
                    }
                } else {
                    alert("请求失败")
                }
            });
        } else {
            $(".lv-error-one").html("手机格式不正确");
            $("#lv-phone").removeClass("lv-border-grey");
            $("#lv-phone").addClass("lv-border-red");
            disableLogin();
        }
    });
    $(".lv-login-ipt2").focus(function () {
        $(".lv-login-lab2").addClass("am-text-xs");
    });
    $(".lv-login-ipt2").blur(function () {
        $(".lv-login-lab2").removeClass("am-text-xs");
    });
    $(".lv-login-ipt2").on('input', function () {
        $(".lv-login-lab2").addClass("am-text-xs");
        var password = $("#lv-password").val();
        var reg = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$/;
        if (password.length < 6) {
            disableLogin();
            return;
        }
        if (reg.test(password)) {
            $(".lv-login-lab2").addClass("am-text-xs");
            $(".lv-password-error").html("");
            $("#lv-password").removeClass("lv-border-red");
            $("#lv-password").addClass("lv-border-grey");
            pasflag = true;
            check(telflag, pasflag);
        } else {
            $(".lv-login-lab2").addClass("am-text-xs");
            $(".lv-password-error").html("密码格式不正确");
            $("#lv-password").removeClass("lv-border-grey");
            $("#lv-password").addClass("lv-border-red");
            disableLogin();
        }
    });

    // 校验二个变量是否为true

    function check(telflag, pasflag) {
        if (telflag && pasflag) {
            enableLogin();
        }else{
            disableLogin();
        }
    }

    // 保存账号密码到cookie
    function setCookie(cookieName, cookieVal, lasttime) {
        // cookieName cookieValue
        // expires 过期时间 path /
        var time = new Date().getTime();// 获取当前的日期时间
        // 480分钟过期
        if (lasttime) {
            time += lasttime;
        } else {
            time += 8 * 60 * 1000;
        }
        time = new Date(time);
        // 再加上一个编码
        document.cookie = cookieName + "=" + cookieVal + ";expires=" + time
            + ';path=/';
    }

    $('#lv-login').click(
        function () {
            var username = $('#lv-phone').val();
            var password = $("#lv-password").val();
            if (telflag && pasflag) {
                showLoading("登录中...")
                $.ajax({
                    type: "POST",
                    url: "/login/user",
                    dataType: "JSON",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify({
                        "mobile": username,
                        "password": password,
                    }),
                    success: function (result) {
                        if (result.data.code == 0000) {
                            $.AMUI.utils.cookie.set("token", result.data.token, 8 * 60 * 1000, "/");//设置路径为根目录
                        }
                        switch (result.data.code) {
                            case "0000":
                                location.href = "/user/index.html";
                                $("#lv-password").removeClass("lv-border-grey");
                                $("#lv-password").addClass("lv-border-grey");
                                break;
                            case "0009":
                                $(".lv-password-error").html("密码错误");
                                $("#lv-password").removeClass("lv-border-red");
                                $("#lv-password").addClass("lv-border-red");
                                break;
                            case "0027":
                            	 $(".lv-error-one").html("该账号已被锁定");
                                 $("#lv-phone").removeClass("lv-border-grey");
                                 $("#lv-phone").addClass("lv-border-red");
                                break;
                        }
                        hideLoading();
                    }
                })
            } else {
                alert("请检查您的输入！");
            }
        });

    function enableLogin() {
        $("#lv-login").removeClass("lv-bg-grey");
        $("#lv-login").addClass("lv-bg-green");
        $("#lv-login").removeAttr("disabled");
    }

    function disableLogin() {
        $("#lv-login").removeClass("lv-bg-green");
        $("#lv-login").addClass("lv-bg-grey");
        $("#lv-login").attr("disabled","disabled");
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
});
