$(function () {
    var token = $.AMUI.utils.cookie.get("token");
    if (token == null) {
        location.replace("/login.html");
        return;
    }

    // 初始化loading
    initLoading();

    // 防止JS缓存
    var t = "20171208";
    var scripts = $("script");
    $.each(scripts, function (i, item) {
        var src = item.src;
        item.setAttribute('src', src + "?t=" + t);
    });
})

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
    if (text) {
        $(".am-modal-hd").html(text);
    }
    $("#my-loading").modal('open');
}

function hideLoading() {
    $("#my-loading").modal("close");
}