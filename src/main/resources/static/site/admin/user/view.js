$(function () {
	initHeader('user');
    var token = $.AMUI.utils.cookie.get("adminToken");
    var page = 1;
    var size = 10;
    var current = 0;
    var upAmount = 10000;

    $("body").append('<div id="modal-self" class="gh-hide"></div>');
    var params = parseURL(window.location.href);
    var id = params.id;
    if (typeof(params.tab) != "undefined") {
        current = params.tab;
    }

    if (typeof(params.id) == "undefined") {
        location.replace("list.html")
    }

    $(".gh-view").eq(current).siblings().removeClass("am-active");
    $(".gh-view").eq(current).addClass("am-active");
    $(".view-list").eq(current).siblings().addClass("gh-hide");
    $(".view-list").eq(current).removeClass("gh-hide");

    if (current == 0) {
        loadUser();
    } else if (current == 1) {
        loadScoreRecords();
    } else if (current == 2) {
        loadParent();
    } else if (current == 3) {
        loadJunior();
    } else if (current == 4) {
        loadBalance();
    }else if (current == 5) {
    	loadBitcoin();
    }else if(current == 6){
    	loadTicket()
    }

    // 点击根据i请求对应的页面
    $(".gh-view").click(function () {
        page = 1;
        current = $(this).index();
        $(".view-list").eq(current).siblings().addClass("gh-hide");
        $(".view-list").eq(current).removeClass("gh-hide");
        if (current == 0) {
            loadUser();
        } else if (current == 1) {
            loadScoreRecords();
        } else if (current == 2) {
            loadParent();
        } else if (current == 3) {
            loadJunior();
        } else if (current == 4) {
            loadBalance();
        }else if (current == 5) {
        	loadBitcoin();
        }else if(current == 6){
        	loadTicket()
        }
    })

    $("#save").click(function () {
        save()
    })


    // 请求信息
    function loadUser() {
        showLoading();
        $.ajax({
            type: "GET",
            url: "/back/user/" + id,
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            success: function (result) {
            	var data = result.data;
                if (data.code == "0000") {
                    var user = data.user;
                    loadHighLevel(user.userLevel);
                    if(user.replenish){
                    	$("#setReplenish").removeClass("gh-hide");
                    }
                    $("#ticketUsername").val(user.mobile);//调整消费券手机展示
                    $("#bitcoinUsername").val(user.mobile);//调整充值报单积分手机展示
                    $("#balanceUsername").val(user.mobile);//调整充值报单积分手机展示
                    $("#scoreUsername").val(user.mobile);//充值积分用户手机展示
                    $("#levelUsername").val(user.mobile);//调整级别用户手机展示
                    $("#replenishUsername").val(user.mobile);//补货调整用户手机展示
                    if(data.user.shopKeeper){
                    	 $("#level").val(user.userLevel+"(店主)");
                    }else{
                    	$("#level").val(user.userLevel);
                    }
                    
                    $("#level-now").val(user.userLevel);//调整级别的当前级别展示
                    $("#up-user").val(user.mobile);//调整上级用户手机展示
                    $("#up-up").val(user.parentId);//调整上级上级展示
                    $("#id").val(user.id);
                    $("#name").val(user.username);
                    $("#phone").val(user.mobile);
                    $("#code").val();
                    if (user.parentName) {
                        $("#uper").val(user.parentName);
                    } else {
                        $("#uper").val("无");
                    }
                    if (user.state == 1) {
                        $("#state").val("正常");
                    } else {
                        $("#state").val("锁定");
                    }
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }

    // 请求可调级别信息
    function loadHighLevel(level) {
        showLoading();
        $.ajax({
            type: "GET",
            url: "/back/high/level/" +id,
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "name": level
            },
            success: function (result) {
            	console.log(result);
                var data = result.data;
                if (data.code == "0000") {
                	upAmount = data.amount;
                    if (data.userLevels.length != 0) {
                        $("#setLevel").removeClass("gh-hide");
                        $("#levelType").empty();
                        for (var i = 0; i < data.userLevels.length; i++) {
                            var obj = data.userLevels[i];
                            $("#levelType").append('<option value="' + obj.value + '">' + obj.name + '</option>')
                        }
                        if( $("#levelType").val()!="6"){
                        	$("#amount-ipt").hide();
                        }
                    }
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }

    

    function loadScoreRecords() {
        showLoading();
        $.ajax({
            type: "GET",
            url: "/back/scoreRecords/" + id,
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "page": page,
                "size": size
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    var scoreRecord = data.scoreRecords;
                    $("#gh-admin-list").empty();
                    loadUser();
                    for (var i = 0; i < scoreRecord.length; i++) {
                        createScoreTr(scoreRecord[i])
                    }
                    if (page == 1) {
                        $(".pager").empty();
                        createPage(data.page);
                    }
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }

    function loadParent() {
        showLoading();
        loadUser();
        $.ajax({
            type: "GET",
            url: "/back/user/parent/" + id,
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "page": 1,
                "size": 100
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    var parent = data.parents;
                    $("#gh-up-list").empty();
                    if (parent.length != 0) {
                        $("#gh-up-header").removeClass("gh-hide");
                        for (var i = 0; i < parent.length; i++) {
                            createParentTr(parent[i])
                        }
                    } else {
                        $("#gh-up-date").removeClass("gh-hide");
                        $("#gh-up-header").addClass("gh-hide");
                    }

                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }

    function loadJunior() {
        showLoading();
        $.ajax({
            type: "GET",
            url: "/back/user/junior/" + id,
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "page": page,
                "size": size
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    var junior = data.juniors;
                    $("#gh-down-list").empty();
                    if (junior.length != 0) {
                        $("#gh-junior-header").removeClass("gh-hide");
                        for (var i = 0; i < junior.length; i++) {
                            createJuniorTr(junior[i])
                        }
                    } else {
                        $("#gh-junior-date").removeClass("gh-hide");
                        $("#gh-junior-header").addClass("gh-hide");
                    }

                    if (page == 1) {
                        createPage(data.page);
                    }
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }

    function loadBalance() {
        showLoading();
        loadUser();
        $.ajax({
            type: "GET",
            url: "/back/balanceRecords/" + id,
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "page": page,
                "size": size
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    var balance = data.balanceRecords;
                    $("#gh-balance-list").empty();
                    if (balance.length != 0) {
                        $("#gh-balance-header").removeClass("gh-hide");
                        for (var i = 0; i < balance.length; i++) {
                            createBalanceTr(balance[i])
                        }
                    } else {
                        $("#gh-balance-data").removeClass("gh-hide");
                        $("#gh-balance-header").addClass("gh-hide");
                    }
                    if (page == 1) {
                        createPage(data.page);
                    }
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }
    function loadBitcoin() {
        showLoading();
        loadUser();
        $.ajax({
            type: "GET",
            url: "/back/bitcoinRecords/" + id,
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "page": page,
                "size": size
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    var bitcoin = data.bitcoinRecords;
                    $("#gh-bitcoin-list").empty();
                    if (bitcoin.length != 0) {
                        $("#gh-bitcoin-header").removeClass("gh-hide");
                        for (var i = 0; i < bitcoin.length; i++) {
                            createBitcoinTr(bitcoin[i])
                        }
                    } else {
                        $("#gh-bitcoin-data").removeClass("gh-hide");
                        $("#gh-bitcoin-header").addClass("gh-hide");
                    }
                    if (page == 1) {
                        createPage(data.page);
                    }
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }
    //获取消费券列表
    function loadTicket() {
        showLoading();
        loadUser();
        $.ajax({
            type: "GET",
            url: "/back/ticketRecords/" + id,
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "page": page,
                "size": size
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    var ticket = data.ticketRecords;
                    $("#gh-ticket-list").empty();
                    if (ticket.length != 0) {
                        $("#gh-ticket-header").removeClass("gh-hide");
                        for (var i = 0; i < ticket.length; i++) {
                            createTicketTr(ticket[i])
                        }
                    } else {
                        $("#gh-ticket-data").removeClass("gh-hide");
                        $("#gh-ticket-header").addClass("gh-hide");
                    }
                    if (page == 1) {
                        createPage(data.page);
                    }
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }

    // 积分列表
    function createScoreTr(scoreRecord) {
        var tr = '<tr>';
        tr = tr + '<td>' + scoreRecord.scoreSource + '</td>';
        if (scoreRecord.minus) {
            tr = tr + '<td>减少</td>';
        } else {
            tr = tr + '<td>增加</td>';
        }

        tr = tr + '<td>' + scoreRecord.amount + '</td>';
        tr = tr + '<td>' + scoreRecord.score + '</td>';
        tr = tr + '<td>' + scoreRecord.frozen + '</td>';
        tr = tr + '<td>' + scoreRecord.total + '</td>';
        tr = tr + '<td>' + scoreRecord.createDate + '</td>';
        tr = tr + '</tr>';
        $("#gh-admin-list").append(tr);
    }

    // 上级列表
    function createParentTr(parent) {
        var flag = (parent.state == '1');
        var tr = '<tr>';
        tr = tr + '<td>' + parent.id + '</td>';
        tr = tr + '<td>' + parent.username + '</td>';
        tr = tr + '<td><a href="view.html?id=' + parent.id + '">' + parent.mobile + '</a></td>';
        tr = tr + '<td>' + parent.userLevel + '</td>';
        tr = tr + '<td>' + parent.createDate + '</td>';
        $("#gh-up-list").append(tr);
    }

    // 下级列表
    function createJuniorTr(junior) {
        var flag = (junior.state == '1');
        var tr = '<tr>';
        tr = tr + '<td>' + junior.id + '</td>';
        tr = tr + '<td>' + junior.username + '</td>';
        tr = tr + '<td><a href="view.html?id=' + junior.id + '">' + junior.mobile + '</a></td>';
        tr = tr + '<td>' + junior.userLevel + '</td>';
        tr = tr + '<td>' + junior.createDate + '</td>';
        $("#gh-down-list").append(tr);
    }

    // 余额列表
    function createBalanceTr(balance) {
        var tr = '<tr>';
        tr = tr + '<td>' + balance.balanceSource + '</td>';
        if (balance.minus) {
            tr = tr + '<td>减少</td>';
        } else {
            tr = tr + '<td>增加</td>';
        }

        tr = tr + '<td>' + balance.amount + '</td>';
        tr = tr + '<td>' + balance.balance + '</td>';
        tr = tr + '<td>' + balance.createDate + '</td>';
        tr = tr + '</tr>';
        $("#gh-balance-list").append(tr);
    }
    // 报单积分列表
    function createBitcoinTr(bitcoin) {
        var tr = '<tr>';
        tr = tr + '<td>' + bitcoin.remark + '</td>';
        if (bitcoin.minus) {
            tr = tr + '<td>减少</td>';
        } else {
            tr = tr + '<td>增加</td>';
        }
        tr = tr + '<td>' + bitcoin.amount + '</td>';
        tr = tr + '<td>' + bitcoin.total + '</td>';
        tr = tr + '<td>' + bitcoin.createDate + '</td>';
        tr = tr + '</tr>';
        $("#gh-bitcoin-list").append(tr);
    }
    
 // 消费券列表
    function createTicketTr(ticket) {
        var tr = '<tr>';
        tr = tr + '<td>' + ticket.remark + '</td>';
        if (ticket.minus) {
            tr = tr + '<td>减少</td>';
        } else {
            tr = tr + '<td>增加</td>';
        }

        tr = tr + '<td>' + ticket.amount + '</td>';
        tr = tr + '<td>' + ticket.total + '</td>';
        tr = tr + '<td>' + ticket.createDate + '</td>';
        tr = tr + '</tr>';
        $("#gh-ticket-list").append(tr);
    }

    function createPage(pageParams) {
        $("#scorePage").empty();
        $("#juniorPage").empty();
        $("#balancePage").empty();
        $("#bitcoinPage").empty();
        $("#ticketPage").empty();
        var html = '<div class="pager">' +
            '<span name="nav" style="cursor: pointer;">' +
            '<a class="navi">' +
            '<span style="color: #999">1</span>' +
            '</a>' +
            '</span>' +
            '</div>';
        if (current == 1) {
            $("#scorePage").append(html);
            $(".pager").pagination({
                recordCount: pageParams.total, // 总记录数
                pageSize: pageParams.size, // 一页记录数
                onPageChange: function (pageNumber) {
                    page = pageNumber;
                    loadScoreRecords();
                }
            });
        } else if (current == 3) {
            $("#juniorPage").append(html);
            $(".pager").pagination({
                recordCount: pageParams.total, // 总记录数
                pageSize: pageParams.size, // 一页记录数
                onPageChange: function (pageNumber) {
                    page = pageNumber;
                    loadJunior();
                }
            });
        } else if (current == 4) {
            $("#balancePage").append(html);
            $(".pager").pagination({
                recordCount: pageParams.total, // 总记录数
                pageSize: pageParams.size, // 一页记录数
                onPageChange: function (pageNumber) {
                    page = pageNumber;
                    loadBalance();
                }
            });
        }else if (current == 5) {
            $("#bitcoinPage").append(html);
            $(".pager").pagination({
                recordCount: pageParams.total, // 总记录数
                pageSize: pageParams.size, // 一页记录数
                onPageChange: function (pageNumber) {
                    page = pageNumber;
                    loadBitcoin();
                }
            });
        }else if (current == 6) {
            $("#ticketPage").append(html);
            $(".pager").pagination({
                recordCount: pageParams.total, // 总记录数
                pageSize: pageParams.size, // 一页记录数
                onPageChange: function (pageNumber) {
                    page = pageNumber;
                    loadTicket();
                }
            });
        }
    }

    function updateUser(requestParams) {
        showLoading("玩命加载中...");
        $.ajax({
            type: "PUT",
            url: "/back/user/edit",
            dataType: "JSON",
            contentType: "application/json; charset=utf-8",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: JSON.stringify(requestParams),
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    location.href = "/admin/user/list.html";
                    return;
                } else if(data.code == "0038"){
                	initWarning();
                	showWarning();
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }

    // 校验密码
    $("#code").on('input', function () {
        var password = $("#code").val();
        if (password.length < 6) {
            passwordFlag = false;
            return;
        }
        var reg = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$/;
        if (reg.test(password)) {
            passwordFlag = true;
        } else {
            passwordFlag = false;
        }
    })

    function save() {
        var name = $("#name").val();
        if (name == "") {
            $("#name").css("border", "1px solid red");
            $("#nameError").html("请输入名称");
            return;
        }
        var requestParams = {
            "username": name,
            "id": id
        }
        var code = $("#code").val();
        if (code != "") {
            requestParams.password = code;
        }
        updateUser(requestParams);
    }

    //调整级别

    $("#setLevel").click(function () {
        $("#LevelModal").modal('open');
        change();
    })

  //消费商显示输入金额框
    function change(){
    	$("#levelType").change(function(){
    		var name = $("#levelType").val();
    		if(name !=6){
    			$("#amount-ipt").hide();
    			$("#level-err").html("")
    		}else{
    			$("#amount-ipt").show();
    		}
    	  });
    }
    
    $("#levelSet").click(function () {
        setLevel();
    })

    $("#amount").on('input', function () {
    	var amount = $("#amount").val();
    	if(amount < upAmount){
    		$("#level-err").html("金额小于最低配置")
    	}else{
    		$("#level-err").html("")
    	}
    })
    function setLevel() {
        var levelType = $("#levelType").val();
        var amount = $("#amount").val();
        showLoading("玩命调整中...");
        if(levelType == 1){
        	 $.ajax({
                 type: "POST",
                 url: "/back/shopKeeper/" + id,
                 dataType: "JSON",
                 beforeSend: function (request) {
                     request.setRequestHeader("token", token);
                 },
                 data: {
                     "userId": id,
                 },
                 success: function (result) {
                     var data = result.data;
                     if (data.code == "0000") {
                         $("#LevelModal").modal('close');
                         location.replace("/admin/user/view.html?id=" + id + "&tab=0");
                     } else if (data.code == "0012") {
                         $("#level-err").html("用户不存在")
                         return;
                     } else if (data.code == "0037") {
                         $("#level-err").html("用户升级金额未配置")
                     }else if (data.code == "0042") {
                         $("#level-err").html("用户已是店主")
                     }else if (data.code == "0036") {
                         $("#level-err").html("用户等级错误")
                     }else if(data.code == "0038"){
                     	initWarning();
                     	showWarning();
                     }  else if (data.code == "0010") {
                         location.replace("/admin/login.html");
                         return;
                     } else {
                         alert(data.msg);
                     }
                     hideLoading();
                 }
             })
        }else{
        	$.ajax({
                type: "POST",
                url: "/back/userLevel/" + id,
                dataType: "JSON",
                beforeSend: function (request) {
                    request.setRequestHeader("token", token);
                },
                data: {
                    "userId": id,
                    "index": levelType,
                    "amount":amount,
                },
                success: function (result) {
                	console.log(result);
                    var data = result.data;
                    if (data.code == "0000") {
                        $("#LevelModal").modal('close');
                        location.replace("/admin/user/view.html?id=" + id + "&tab=0");
                    } else if (data.code == "0012") {
                        $("#level-err").html("用户不存在")
                        return;
                    } else if (data.code == "0037") {
                        $("#level-err").html("用户升级金额未配置")
                    }else if (data.code == "0035") {
                        $("#level-err").html("金额小于最低配置")
                    }else if (data.code == "0036") {
                        $("#level-err").html("用户等级错误")
                    }else if(data.code == "0038"){
                    	initWarning();
                    	showWarning();
                    }  else if (data.code == "0010") {
                        location.replace("/admin/login.html");
                        return;
                    } else {
                        alert(data.msg);
                    }
                    hideLoading();
                }
            })
        }
       
        
    }

    // 充值积分
    var scoreState = false;


    $("#confirm").click(function () {
        $("#scoreModal").modal('open');
    })
    $("#success-back").click(function () {
        location.replace("/admin/user/view.html?id=" + id + "&tab=1");
    })

    $("#score").on('input', function () {
        var reg = /(^[1-9]{1}[0-9]*$)|(^[0-9]*\.[0-9]{2}$)/
        var amount = $("#score").val();
        if (!reg.test(amount)) {
            $("#score").css("border", "1px solid red");
            $("#score-err").html("只能输入正数，最多保留2位小数")
        } else {
            $("#score").css("border", "1px solid #ccc");
            $("#score-err").html("")
            scoreState = true;
        }
        ;

    })

    $("#score-add").click(
        function () {
            if (scoreState) {
                recharge();
            }
        }
    )

    function recharge() {
        var amount = $("#score").val();
        var scoreType = $("#scoreType").val();
        var type = $("#Type").val();
        var minus = true;
        var frozen = true;
        if (type == "add") {
            minus = false;
        }
        if (scoreType == "score") {
            frozen = false;
        }
        showLoading("玩命充值中...");
        $.ajax({
            type: "POST",
            url: "/back/modify/score/" + id,
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "userId": id,
                "amount": amount,
                "frozen": frozen,
                "minus": minus
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    $("#scoreModal").modal('close');
                    $("#scoreModal-tips").modal('open');
                    $('#modal-self').removeClass("gh-hide");
                    $("#score-title").html("调整成功");
                    $("#score-warn").html("");
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else if (data.code == "0032") {
                    $("#scoreModal").modal('close');
                    $("#scoreModal-tips").modal('open');
                    $('#modal-self').removeClass("gh-hide");
                    $("#score-title").html("调整失败");
                    $("#score-warn").html("现有积分不足")
                }else if(data.code == "0038"){
                	initWarning();
                	showWarning();
                }  else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }

    // 调整上级


    $("#setUp").click(function () {
        $("#setUpModal").modal('open');
    })
    $("#setClose").click(function () {
        $("#setUpModal").modal('close');
        $('#modal-self').addClass("gh-hide");
    })
    $("#up-set").click(
        function () {
            setup();
        }
    )

    $("#partent-back").click(function () {
        $('#modal-self').addClass("gh-hide");
        location.replace("/admin/user/view.html?id=" + id + "&tab=2");
    })

    function setup() {
        var parentId = $("#parentId").val();
        if (parentId != "") {
            showLoading("玩命创建中...");
            $.ajax({
                type: "POST",
                url: "/back/modify/parent/" + id,
                dataType: "JSON",
                beforeSend: function (request) {
                    request.setRequestHeader("token", token);
                },
                data: {
                    "userId": id,
                    "parentId": parentId,
                },
                success: function (result) {
                    var data = result.data;
                    if (data.code == "0000") {
                        $("#setUpModal").modal('close');
                        $("#partentModal-tips").modal('open');
                        $('#modal-self').removeClass("gh-hide");
                        $("#partent-title").html("调整成功");
                    } else if (data.code == "0010") {
                        location.replace("/admin/login.html");
                        return;
                    } else if (data.code == "0028") {
                        $('#modal-self').removeClass("gh-hide");
                        $("#up-err").html("上级不能为自己")
                    } else if (data.code == "0029") {
                        $('#modal-self').removeClass("gh-hide");
                        $("#up-err").html("不能为循环上级")
                    } else if (data.code == "0012") {
                        $('#modal-self').removeClass("gh-hide");
                        $("#up-err").html("用户不存在")
                    } else if(data.code == "0038"){
                    	initWarning();
                    	showWarning();
                    } else if (data.code == "0010") {
                        location.replace("/admin/login.html");
                        return;
                    } else {
                        $('#modal-self').removeClass("gh-hide");
                        alert(data.msg);
                    }
                    hideLoading();
                }
            })
        } else {
            $("#up-err").html("上级不能为空")
        }
    }
  
  //充值余额
    var balanceState = false;


    $("#balance-add").click(function () {
        $("#balanceModal").modal('open');
    })
   
    
     $("#success-balance").click(function () {
        location.replace("/admin/user/view.html?id=" + id + "&tab=4");
    })

    $("#balance").on('input', function () {
        var reg = /(^[1-9]{1}[0-9]*$)|(^[0-9]*\.[0-9]{2}$)/
        var amount = $("#balance").val();
        if (!reg.test(amount)) {
            $("#balance").css("border", "1px solid red");
            $("#balance-err").html("只能输入正数，最多保留2位小数")
        } else {
            $("#balance").css("border", "1px solid #ccc");
            $("#balance-err").html("")
            balanceState = true;
        }
        ;

    })

    $("#balanceSet").click(
        function () {
            if (balanceState) {
            	balanceRecharge();
            }
        }
    )

    function balanceRecharge() {
        var amount = $("#balance").val();
        var type = $("#balanceType").val();
        var minus = true;
        var frozen = true;
        if (type == "add") {
            minus = false;
        }
        showLoading("玩命充值中...");
        $.ajax({
            type: "POST",
            url: "/back/recharge/balance",
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "id": id,
                "amount": amount,
                "minus": minus
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    $("#balanceModal").modal('close');
                    $("#balanceModal-tips").modal('open');
                    $('#modal-self').removeClass("gh-hide");
                    $("#balance-title").html("调整成功");
                    $("#balance-warn").html("");
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                }else if (data.code == "0049") {
                    $("#balanceModal").modal('close');
                    $("#balanceModal-tips").modal('open');
                    $('#modal-self').removeClass("gh-hide");
                    $("#balance-title").html("调整失败");
                    $("#balance-warn").html("余额不足")
                }else if(data.code == "0038"){
                	initWarning();
                	showWarning();
                }  else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }
    
    
    //充值报单积分
    var bitcoinState = false;


    $("#bitcoin-add").click(function () {
        $("#bitcoinModal").modal('open');
    })
   
    
     $("#success-bitcoin").click(function () {
        location.replace("/admin/user/view.html?id=" + id + "&tab=5");
    })

    $("#bitcoin").on('input', function () {
        var reg = /(^[1-9]{1}[0-9]*$)|(^[0-9]*\.[0-9]{2}$)/
        var amount = $("#bitcoin").val();
        if (!reg.test(amount)) {
            $("#bitcoin").css("border", "1px solid red");
            $("#bitcoin-err").html("只能输入正数，最多保留2位小数")
        } else {
            $("#bitcoin").css("border", "1px solid #ccc");
            $("#bitcoin-err").html("")
            scoreState = true;
        }
        ;

    })

    $("#bitcoinSet").click(
        function () {
            if (scoreState) {
                bitRecharge();
            }
        }
    )

    function bitRecharge() {
        var amount = $("#bitcoin").val();
        var type = $("#bitcoinType").val();
        var minus = true;
        var frozen = true;
        if (type == "add") {
            minus = false;
        }
        showLoading("玩命充值中...");
        $.ajax({
            type: "POST",
            url: "/back/recharge/bitcoin",
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "id": id,
                "amount": amount,
                "type": minus
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    $("#bitcoinModal").modal('close');
                    $("#bitcoinModal-tips").modal('open');
                    $('#modal-self').removeClass("gh-hide");
                    $("#bitcoin-title").html("调整成功");
                    $("#bitcoin-warn").html("");
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else if (data.code == "0032") {
                    $("#bitcoinModal").modal('close');
                    $("#bitcoinModal-tips").modal('open');
                    $('#modal-self').removeClass("gh-hide");
                    $("#bitcoin-title").html("调整失败");
                    $("#bitcoin-warn").html("现有积分不足")
                }else if (data.code == "0058") {
                    $("#bitcoinModal").modal('close');
                    $("#bitcoinModal-tips").modal('open');
                    $('#modal-self').removeClass("gh-hide");
                    $("#bitcoin-title").html("调整失败");
                    $("#bitcoin-warn").html("余额不足")
                }else if(data.code == "0038"){
                	initWarning();
                	showWarning();
                }  else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }
    
  //补货调整
    var replenishState = false;


    $("#setReplenish").click(function () {
        $("#replenishModal").modal('open');
    })
   
     $("#success-replenish").click(function () {
        location.replace("/admin/user/view.html?id=" + id + "&tab=0");
    })

    $("#replenish").on('input', function () {
        var reg = /(^[1-9]{1}[0-9]*$)|(^[0-9]*\.[0-9]{2}$)/
        var amount = $("#replenish").val();
        if (!reg.test(amount)) {
            $("#replenish").css("border", "1px solid red");
            $("#replenish-err").html("只能输入正数，最多保留2位小数")
        } else {
            $("#replenish").css("border", "1px solid #ccc");
            $("#replenish-err").html("")
            scoreState = true;
        }
        ;

    })

    $("#replenishSet").click(
        function () {
            if (scoreState) {
            	repRecharge();
            }
        }
    )

    function repRecharge() {
        var amount = $("#replenish").val();
        showLoading("玩命充值中...");
        $.ajax({
            type: "POST",
            url: "/back/replenish",
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "id": id,
                "amount": amount
            },
            success: function (result) {
                var data = result.data;
                if (data.code == "0000") {
                    $("#replenishModal").modal('close');
                    $("#replenishModal-tips").modal('open');
                    $('#modal-self').removeClass("gh-hide");
                    $("#replenish-title").html("调整成功");
                    $("#replenish-warn").html("");
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else if (data.code == "0032") {
                    $("#replenishModal").modal('close');
                    $("#replenishModal-tips").modal('open');
                    $('#modal-self').removeClass("gh-hide");
                    $("#replenish-title").html("调整失败");
                    $("#replenish-warn").html("现有积分不足")
                }else if(data.code == "0038"){
                	initWarning();
                	showWarning();
                }  else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }
    
    //调整消费券
    var ticketState = false;


    $("#ticket-add").click(function () {
        $("#ticketModal").modal('open');
    })
   
    
     $("#success-ticket").click(function () {
        location.replace("/admin/user/view.html?id=" + id + "&tab=6");
    })

    $("#ticket").on('input', function () {
        var reg = /(^[1-9]{1}[0-9]*$)|(^[0-9]*\.[0-9]{2}$)/
        var amount = $("#ticket").val();
        if (!reg.test(amount)) {
            $("#ticket").css("border", "1px solid red");
            $("#ticket-err").html("只能输入正数，最多保留2位小数")
        } else {
            $("#ticket").css("border", "1px solid #ccc");
            $("#ticket-err").html("")
            scoreState = true;
        }
        ;

    })

    $("#ticketSet").click(
        function () {
            if (scoreState) {
                ticketRecharge();
            }
        }
    )

    function ticketRecharge() {
        var amount = $("#ticket").val();
        console.log(amount);
        var type = $("#ticketType").val();
        var minus = true;
        var frozen = true;
        if (type == "add") {
            minus = false;
        }
        showLoading("玩命充值中...");
        $.ajax({
            type: "POST",
            url: "/back/recharge/ticket",
            dataType: "JSON",
            beforeSend: function (request) {
                request.setRequestHeader("token", token);
            },
            data: {
                "id": id,
                "amount": amount,
                "minus": minus
            },
            success: function (result) {
            	console.log(result);
                var data = result.data;
                if (data.code == "0000") {
                    $("#ticketModal").modal('close');
                    $("#ticketModal-tips").modal('open');
                    $('#modal-self').removeClass("gh-hide");
                    $("#ticket-title").html("调整成功");
                    $("#ticket-warn").html("");
                } else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                }else if (data.code == "0049") {
                    $("#ticketModal").modal('close');
                    $("#ticketModal-tips").modal('open');
                    $('#modal-self').removeClass("gh-hide");
                    $("#ticket-title").html("调整失败");
                    $("#ticket-warn").html("余额不足")
                }else if(data.code == "0038"){
                	initWarning();
                	showWarning();
                }  else if (data.code == "0010") {
                    location.replace("/admin/login.html");
                    return;
                } else {
                    alert(data.msg);
                }
                hideLoading();
            }
        })
    }
    
    

})