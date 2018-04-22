$(function () {
    showLoading("玩命加载中....");
    var token = $.AMUI.utils.cookie.get("token");

    //创建列表
    function createList(array) {
        for (var k = 0; k < array.length; k++) {
            var objet = array[k];
            var div =  '<div class="am-cf am-padding-vertical-sm "><div style="width:15rem;" class="am-fl"><div class="am-text-sm jm-second-gray am-text-truncate">';
            div = div + objet.remark;
            div = div + '</div><div class="x-text-gray am-text-xs">';
            div = div + objet.createDate;
            if(objet.minus){
            	div = div + '</div></div><div class="am-fr"><div style="line-height: 3rem" class="lv-text-green lv-text-lg lv-text-bold">'+'-';
            }else{
            	div = div + '</div></div><div class="am-fr"><div style="line-height: 3rem" class="gh-text-red lv-text-lg lv-text-bold">'+'+';
            }
            div = div + objet.amount;
            div = div + '</div></div></div>';
            $(".gh-ticket-list").append(div);
        }
    }

    // 下拉刷新
    // 页数
    var page = 0;
    // 每页展示10个
    var size = 10;
    // dropload
    $(".content").dropload({
        scrollArea: window,
        loadDownFn: function (me) {
            page++;
            // 拼接HTML
            var result = '';
            $.ajax({
                type: 'GET',
                url: '/balanceRecordApi/ticketRecords?page=' + page + '&size=' + size,
                dataType: 'json',
                beforeSend: function (request) {
                    request.setRequestHeader("token", token);
                },
                success: function (result) {
                    var array = result.data.ticketRecords;
                    $("#ticket").html(result.data.ticketTotal);
                     if (result.data.code == "0010" || result.data.code == "0027") {
                        location.replace("/login.html");
                        return;
                    }
                    if (array.length <= 0) {
                        me.lock();// 锁定
                        me.noData();// 无数据
                    }
                    // 为了测试，延迟1秒加载
                    setTimeout(function () {
                        // 插入数据到页面，放到最后面
                        createList(array);
                        // 每次数据插入，必须重置
                        me.resetload();
                    }, 1000);
                    hideLoading();
                },
                error: function (xhr, type) {
                    console.log('分页加载 Ajax error!');
                    // 即使加载出错，也得重置
                    me.resetload();
                }
            });
        }
    })

})