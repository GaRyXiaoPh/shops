/**
 * 订单列表管理初始化
 */
var TMallOrder = {
    id: "TMallOrderTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
TMallOrder.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            // {title: '主键', field: 'id', visible: true, align: 'center', valign: 'middle'},
            // {title: '买家ID', field: 'buyerId', visible: false, align: 'center', valign: 'middle'},
            {title: '买家', field: 'buyerName', visible: true, align: 'center', valign: 'middle'},
            // {title: '卖家ID', field: 'sellerId', visible: false, align: 'center', valign: 'middle'},
            {title: '卖家', field: 'sellerName', visible: true, align: 'center', valign: 'middle'},
            // {title: '商品ID', field: 'goodId', visible: false, align: 'center', valign: 'middle'},
            {title: '商品', field: 'name', visible: true, align: 'center', valign: 'middle'},
            {title: '购买数量', field: 'count', visible: true, align: 'center', valign: 'middle'},
            // {title: '收货地址ID', field: 'addressId', visible: true, align: 'center', valign: 'middle'},
            {title: '合计总金额', field: 'totalPrice', visible: true, align: 'center', valign: 'middle'},
            {title: '付款币种', field: 'coinName', visible: true, align: 'center', valign: 'middle'},
            //1:待发货；2:已发货；3:已完成,4取消订单,5：退货中，6：已退货
            {title: '状态', field: 'status', visible: true, align: 'center', valign: 'middle',formatter:function(value, row, index){
                    switch (value){
                        case 1:
                            return "待发货";
                        case 2:
                            return "已发货";
                        case 3:
                            return "已完成";
                        case 4:
                            return "已取消";
                        case 5:
                            return "退货中";
                        case 6:
                            return "已退货";
                        default:
                            return "未知";
                    }
                }},
            {title: '下单时间', field: 'createTime', visible: true, align: 'center', valign: 'middle'},
            // {title: '更新时间', field: 'updateTime', visible: true, align: 'center', valign: 'middle'},
            // {title: '删除标记:0未删除，1已删除', field: 'isDelete', visible: true, align: 'center', valign: 'middle'},
            // {title: '收货人姓名', field: 'receiverName', visible: true, align: 'center', valign: 'middle'},
            // {title: '收货人手机号', field: 'receiverMobile', visible: true, align: 'center', valign: 'middle'},
            // {title: '省名', field: 'provinceId', visible: true, align: 'center', valign: 'middle'},
            // {title: '城市', field: 'cityId', visible: true, align: 'center', valign: 'middle'},
            // {title: '区县', field: 'areaId', visible: true, align: 'center', valign: 'middle'},
            // {title: '详细地址', field: 'detailAddr', visible: true, align: 'center', valign: 'middle'},
            // {title: '单价', field: 'price', visible: true, align: 'center', valign: 'middle'},
            // {title: '0:正常，1：退货中，2：已退货', field: 'returnStatus', visible: true, align: 'center', valign: 'middle'},
            // {title: '退货原因', field: 'returnReason', visible: true, align: 'center', valign: 'middle'},
            // {title: '评价：0好评，1中评，2差评', field: 'reputation', visible: true, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
TMallOrder.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        TMallOrder.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加订单列表
 */
TMallOrder.openAddTMallOrder = function () {
    var index = layer.open({
        type: 2,
        title: '添加订单列表',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/tMallOrder/tMallOrder_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看订单列表详情
 */
TMallOrder.openTMallOrderDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '订单列表详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/tMallOrder/tMallOrder_update/' + TMallOrder.seItem.id
        });
        this.layerIndex = index;
        layer.full(index);
    }
};

/**
 * 删除订单列表
 */
TMallOrder.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/tMallOrder/delete", function (data) {
            Feng.success("删除成功!");
            TMallOrder.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("tMallOrderId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询订单列表列表
 */
TMallOrder.search = function () {
    var queryData = {};
    queryData['buyer'] = $("#buyer").val();
    queryData['seller'] = $("#seller").val();
    queryData['goodName'] = $("#goodName").val();
    queryData['status'] = $("#status").val();
    TMallOrder.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = TMallOrder.initColumn();
    var table = new BSTable(TMallOrder.id, "/tMallOrder/list", defaultColunms);
    // table.setPaginationType("client");
    TMallOrder.table = table.init();
});
