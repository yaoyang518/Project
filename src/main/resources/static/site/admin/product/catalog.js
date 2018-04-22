$(function () {
	
	
	
	$("#catalog-add").click(function(){
		var title = $("#catalog-title").val();
		var li = '<li class="am-panel am-padding-horizontal-sm">';
		 	li = li+ "<a>";
		 	li = li + '<i class="am-icon-table am-margin-left-sm"></i> '+title+'<button class="gh-padding-vertical-xs gh-btn-blue am-fr catalog-second">添加二级分类</button> <input type="text" class="am-form-field gh-search am-text-sm am-margin-right-sm catalog-ipt  am-fr"></a>';
		 	li = li + '</li>';
	        if(title !=""){
	        	 $("#collapase-nav").append(li);
	        }
	})
	
	$("#collapase-nav").on("click",".catalog-second",function(){
		var title = $(this).parent().find('input').val();
		var list =  $(this).parent();
		list.append('<div class="am-margin-horizontal-lg am-margin-top-lg">'+title+'</div>');
	});
	
	
	
	
	
	
})