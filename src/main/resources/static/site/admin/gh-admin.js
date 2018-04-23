$(function () {
    var token = $.AMUI.utils.cookie.get("adminToken");
    if (token == null) {
        location.replace("/admin/login.html");
        return;
    }
 // 初始化footer  
    initFooter();
 // 初始化loading
    initLoading();
    
 //防止JS缓存   
    var t="20171207";  
    var scripts = $("script");  
    $.each(scripts,function(i,item){
      var src = item.src;
      item.setAttribute('src',src+"?t="+t); 
    　　});
})

function parseURL(url) {
    var res = {};
    if (url.indexOf("?") == -1) {
        return res;
    }
    url = url.split("?")[1];
    var para = url;
    var arr = [];
    if (url.indexOf("&") != -1) {
        para = url.split("&");
        for (var i = 0; i < para.length; i++) {
            arr = para[i].split("=");
            res[arr[0]] = arr[1];
        }
    } else {
        arr = para.split("=");
        res[arr[0]] = arr[1];
    }
    return res;
}


function initLoading() {
    var loading =   '<div class="am-modal am-modal-loading am-modal-no-btn" tabindex="-1" id="my-loading">'+
                        '<div class="am-modal-dialog">'+
                            '<div class="am-modal-hd">玩命加载中</div>'+
                            '<div class="am-modal-bd">'+
                                '<span class="am-icon-spinner am-icon-spin"></span>'+
                            '</div>'+
                        '</div>'+
                    '</div>';
    $("body").append(loading);
}

function showLoading(text) {
    if (text) {
        $(".am-modal-hd").html(text);
    }
    $("#my-loading").modal('open');
}

function hideLoading() {
    setTimeout('$("#my-loading").modal("close")', 600);
}

function initWarning() {
    var warning =   '<div class="am-modal am-modal-alert" tabindex="-1" id="my-warning">'+
                        '<div class="am-modal-dialog">'+
                            '<div class="am-modal-bd am-text-lg am-text-danger am-padding-top-lg">权限不足</div>'+
                            '<div class="am-modal-footer">'+
                                '<span class="am-modal-btn">确定</span>'+
                            '</div>' +
                        '</div>' +
                    '</div>';
    $("body").append(warning);
}

function showWarning(text) {
    if (text) {
        $(".am-modal-warn").html(text);
    }
    $("#my-warning").modal('open');
}

function hideWarning() {
    setTimeout('$("#my-warning").modal("close")', 600);
}



function initAdmin() {
    // 初始化管理员
    var adminName = $.AMUI.utils.cookie.get("adminName");
    var html = '<div class="gh-bg-top">' +
                    '<div class="gh-padding-vertical-xs  gh-admin-main am-cf">' +
                        '<a href="/admin/index.html" class="gh-text-white lv-text-bold am-fl gh-text-xl ">教师管理后台</a>' +
                            '<div class="am-fr gh-margin-top-xxs">' +
                                '<span class="gh-text-white" id="admin-user">'+adminName+'</span> ' +
                                '<span class="gh-text-white" id="admin-out">退出系统</span>' +
                            '</div>' +
                    '</div>' +
                '</div>';
    $("body").prepend(html);
    // 退出系统
    $("#admin-out").click(function () {
        $.AMUI.utils.cookie.unset("adminToken", "/");
        $.AMUI.utils.cookie.unset("adminName", "/");
        location.replace("/admin/login.html");
    });
}
function initHeader(tab) {
    var header =
                '<div class="gh-bg">' +
                    '<div class="container">' +
                        '<ul class="mainnav">' +
                            '<li class="am-dropdown nav-tab" data-am-dropdown>';
                            if(tab == 'user'){
                            	header = header +
                                    '<a class="am-dropdown-toggle gh-text-black" data-am-dropdown-toggle test>' +
                                        '<img src="/img/admin-user-black.png" /><span>用户</span>' +
                                    '</a>';
                            }else{
                            	header = header +
                                    '<a class="am-dropdown-toggle" data-am-dropdown-toggle>' +
                                        '<img src="/img/admin-user-grey.png" /><span>用户</span>' +
                                    '</a>';
                            }
                             header = header +  '<ul class="am-dropdown-content">' +
                                    '<li><a href="/admin/user/list.html">用户列表</a></li>' +
                                    '<li><a href="/admin/user/qualification.html">资格列表</a></li>' +
                                '</ul>' +
                            '</li>' +
                            '<li class="am-dropdown nav-tab am-hide" data-am-dropdown>' +
                                '<a class="am-dropdown-toggle" data-am-dropdown-toggle>' +
                                    '<img src="/img/admin-setting-grey.png" /><span>商品</span>' +
                                '</a>' +
                                '<ul class="am-dropdown-content">' +
                                    '<li><a href="/admin/product/catalog.html">商品分类</a></li>' +
                                '</ul>' +
                            '</li>' +
                            '<li class="am-dropdown nav-tab" data-am-dropdown>';
                             if(tab == "record"){
                            	 header = header +
                                     '<a class="am-dropdown-toggle gh-text-black" data-am-dropdown-toggle>' +
                                        '<img src="/img/admin-score-black.png" /><span>记录</span>' +
                                     '</a>';
                             }else{
                            	 header = header +
                                     '<a class="am-dropdown-toggle" data-am-dropdown-toggle>' +
                                        '<img src="/img/admin-score-grey.png" /><span>记录</span>' +
                                     '</a>';
                             }
                             header = header   +
                                '<ul class="am-dropdown-content">' +
                                    '<li><a href="/admin/treasure/list.html">积分记录</a></li>' +
                                    '<li><a href="/admin/bitcoin/list.html">报单积分记录</a></li>' +
                                    '<li><a href="/admin/replenish/list.html">补货记录</a></li>' +
                                    '<li><a href="/admin/recharge/list.html">充值记录</a></li>' +
                                '</ul>' +
                            '</li>' +
                            '<li class="am-dropdown nav-tab" data-am-dropdown>';
                             if(tab == "cash"){
                            	header= header +
                                    '<a class="am-dropdown-toggle gh-text-black" data-am-dropdown-toggle>' +
                                        '<img src="/img/admin-setting-black.png" /><span>提现</span>' +
                                    '</a>';
                             }else{
                            	 header= header +
                                     '<a class="am-dropdown-toggle" data-am-dropdown-toggle>' +
                                        '<img src="/img/admin-setting-grey.png" /><span>提现</span>' +
                                     '</a>';
                             }
                             header = header  +
                                '<ul class="am-dropdown-content">' +
                                    '<li><a href="/admin/payout/records.html">提现申请</a></li>' +
                                '</ul>' +
                            '</li>' +
                             '<li class="am-dropdown nav-tab" data-am-dropdown>';
                            if(tab == "school"){
                                header= header +
                                    '<a class="am-dropdown-toggle gh-text-black" data-am-dropdown-toggle>' +
                                    '<img src="/img/admin-setting-black.png" /><span>学校</span>' +
                                    '</a>';
                            }else{
                                header= header +
                                    '<a class="am-dropdown-toggle" data-am-dropdown-toggle>' +
                                    '<img src="/img/admin-setting-grey.png" /><span>学校</span>' +
                                    '</a>';
                            }
                            header = header  +
                                '<ul class="am-dropdown-content">' +
                                '<li><a href="/admin/school/list.html">学校列表</a></li>' +
                                '</ul>' +
                                '</li>' +
                            '<li class="am-dropdown nav-tab" data-am-dropdown>';
                             if(tab == "config"){
                            	 header = header  +
                                     '<a class="am-dropdown-toggle gh-text-black" data-am-dropdown-toggle> ' +
                                        '<img src="/img/admin-setting-black.png" /><span>配置</span>' +
                                     '</a>';
                             }else{
                            	 header = header  +
                                     '<a class="am-dropdown-toggle" data-am-dropdown-toggle> ' +
                                        '<img src="/img/admin-setting-grey.png" /><span>配置</span>' +
                                     '</a>';
                             }
                             header = header   +
                                '<ul class="am-dropdown-content">' +
                                    '<li><a href="/admin/config/percent.html">转化率</a></li>' +
                                    '<li><a href="/admin/config/rule.html">规则</a></li>' +
                                    '<li><a href="/admin/config/levelUp.html">级别</a></li>' +
                                    '<li><a href="/admin/config/payoutsetting.html">提现</a></li>' +
                                    '<li><a href="/admin/config/ticket.html">消费券</a></li>' +
                                    '<li><a href="/admin/config/replenish.html">补货</a></li>' +
                                '</ul>' +
                            '</li>' +
                            '<li class="am-dropdown nav-tab" data-am-dropdown>';
                             if(tab == "admin"){
                            	 header = header +
                                 '<a class="am-dropdown-toggle gh-text-black" data-am-dropdown-toggle> ' +
                                     '<img src="/img/admin-setting-black.png" /><span>系统</span>' +
                                 '</a>';
                             }else{
                            	 header = header +
                                 '<a class="am-dropdown-toggle" data-am-dropdown-toggle> ' +
                                     '<img src="/img/admin-setting-grey.png" /><span>系统</span>' +
                                 '</a>';
                             }
                             header = header +
                                '<ul class="am-dropdown-content">' +
                                    '<li><a href="/admin/admin/list.html">管理员</a></li>' +
                                    '<li><a href="/admin/role/list.html">角色</a></li>' +
                                    '<li><a href="/admin/permission/list.html">权限</a></li>' +
                                '</ul>' +
                            '</li>' +
                        '</ul>' +
                    '</div>' +
                '</div>';
    $("body").prepend(header);
    initAdmin();
    $('[data-am-dropdown]').dropdown();
}

function initFooter(tab) {
    var bottom =
    	'<div class="gh-admin-footer am-cf">' +
    	'<hr>' + 
    	'<span class="am-fr gh-margin-bottom-sm gh-margin-right-sm">'+
        	'©2017浙江耀阳科技发展有限公司'+
        '</span>'+
        '</div>';
    $("body").append(bottom);
}