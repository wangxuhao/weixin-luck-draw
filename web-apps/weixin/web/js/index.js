void function($){
	$(function(){  
		var box_width=document.body.clientWidth,//获取body宽度
		    box_height=document.documentElement.clientHeight;//获取页面可见高度
		$('#main-container').width(box_width);
		$('#main-container').height(box_height);
		
		// 获取奖项接口
		$.post('getAllPrizeItem.action',function(data){
			var items = JSON.parse(data);
			var html=_.template($("#prizeItemList-template").html());
			$('.prizeItems').append(html({items:items}));
		});
	});
}(window.jQuery);
