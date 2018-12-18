/**
 * 商品列表管理初始化
 */
var TMallGood = {
    id: "TMallGoodTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};
var categories = [];



/**
 * 初始化表格的列
 */
TMallGood.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            {title: '', field: 'id', visible: false, align: 'center', valign: 'middle'},
            {title: '卖家ID', field: 'userId', visible: false, align: 'center', valign: 'middle'},
            {title: '发布人用户名', field: 'userName', visible: true, align: 'center', valign: 'middle'},
            {title: '分类', field: 'categoryId', visible: true, align: 'center', valign: 'middle',formatter:function(value, row, index){
                    var value =  categories.filter(function (value1) {
                        return value == value1.id
                    });
                    if(value.length > 0 && value[0].categoryName){
                        return value[0].categoryName;
                    }
                    return null;

                }},
            {title: '商品名称', field: 'name', visible: true, align: 'center', valign: 'middle'},
            {title: '商品详情', field: 'detail', visible: false, align: 'center', valign: 'middle'},
            {title: '商品价格', field: 'price', visible: false, align: 'center', valign: 'middle'},
            {title: '计价的币种', field: 'coinName', visible: false, align: 'center', valign: 'middle'},
            {title: '库存', field: 'stock', visible: true, align: 'center', valign: 'middle'},
            {title: '销量', field: 'salesVolume', visible: true, align: 'center', valign: 'middle'},

            {title: '是否在售', field: 'status', visible: true, align: 'center', valign: 'middle',formatter:function(value, row, index){
                    switch (value){
                        case 0:
                            return "否";
                        case 1:
                            return "是";
                        default:
                            return "未知";
                    }
                }},
            {title: '创建时间', field: 'createTime', visible: false, align: 'center', valign: 'middle'},
            {title: '修改时间', field: 'updateTime', visible: false, align: 'center', valign: 'middle'},
            {title: '是否删除', field: 'isDelete', visible: true, align: 'center', valign: 'middle',formatter:function(value, row, index){
                    switch (value){
                        case 0:
                            return "否";
                        case 1:
                            return "<text style='color: red'>是</text>";
                        default:
                            return "未知";
                    }
                }},
            {title: '价值', field: 'cny', visible: true, align: 'center', valign: 'middle'},
            {title: '卖家微信号', field: 'sellerWechat', visible: true, align: 'center', valign: 'middle'},
            {title: '卖家手机号', field: 'sellerMobile', visible: true, align: 'center', valign: 'middle'},
            {title: '线下店铺地址', field: 'shopAddress', visible: true, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
TMallGood.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        TMallGood.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加商品列表
 */
TMallGood.openAddTMallGood = function () {
    var index = layer.open({
        type: 2,
        title: '添加商品列表',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/tMallGood/tMallGood_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看商品列表详情
 */
TMallGood.openTMallGoodDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '商品列表详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/tMallGood/tMallGood_update/' + TMallGood.seItem.id
        });
        this.layerIndex = index;
        layer.full(index)
    }
};
/**
 * 删除商品列表
 */
TMallGood.editStatus = function () {

    if (this.check()) {
        var status = this.seItem.status;
        var action = status == 0 ? '上架' :'下架';
        var id = this.seItem.id;
        Feng.confirm("是否"+action+"该商品?",function(){
            var ajax = new $ax(Feng.ctxPath + "/tMallGood/edit-status", function (data) {
                Feng.success(action+"成功!");
                TMallGood.table.refresh();
            }, function (data) {
                Feng.error(action+"失败!" + data.responseJSON.message + "!");
            });
            ajax.set("goodId",id);
            ajax.set("type",status == 0 ? 2 : 0);
            ajax.start();
        });

    }
};

/**
 * 删除商品列表
 */
TMallGood.delete = function () {
    if (this.check()) {
        var isDelete = this.seItem.isDelete;
        var action = isDelete == 0 ? '删除' :'恢复';
        var id = this.seItem.id;
        Feng.confirm("是否"+action+"该商品?",function(){
            var ajax = new $ax(Feng.ctxPath + "/tMallGood/edit-status", function (data) {
                Feng.success(action+"成功!");
                TMallGood.table.refresh();
            }, function (data) {
                Feng.error(action+"失败!" + data.responseJSON.message + "!");
            });
            ajax.set("goodId",id);
            ajax.set("type",isDelete == 0 ? 1 : 3);
            ajax.start();
        });
    }
};

/**
 * 查询商品列表列表
 */
TMallGood.search = function () {
    var queryData = {};
    var userName = $("#userName").val();
    queryData['userName'] =  userName.length > 0 ? userName : null ;
    var goodName = $("#goodName").val();
    queryData['goodName'] =  goodName.length > 0 ? goodName : null ;
    queryData['status'] = $("#status").val();
    queryData['isDelete'] = $("#isDelete").val();
    TMallGood.table.refresh({query: queryData});
};

$(function () {
    var ajax = new $ax(Feng.ctxPath + "/tMallGood/categories", function (data) {
        categories = data.content;
        var defaultColunms = TMallGood.initColumn();
        var table = new BSTable(TMallGood.id, "/tMallGood/list", defaultColunms);
        // table.setPaginationType("client");
        TMallGood.table = table.init();
    });
    ajax.type='get';
    ajax.start();

});
