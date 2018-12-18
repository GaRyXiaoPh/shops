/**
 * 对象初始化
 */
var CoinRate = {
    id: "CoinTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
CoinRate.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'ID', field: 'id', visible: false, align: 'center', valign: 'middle'},
        {title: '币种名称', field: 'coinName', visible: true, align: 'center', valign: 'middle'},
        {title: '兑换币名称', field: 'changeName', visible: false, align: 'center', valign: 'middle'},
        {title: '汇率', field: 'rate', visible: true, align: 'center', valign: 'middle'},
    ];
};

/**
 * 检查是否选中
 */
CoinRate.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        CoinRate.seItem = selected[0];
        return true;
    }
};


/**
 * 点击添加币种配置
 */
CoinRate.openAddCoin = function () {
    var index = layer.open({
        type: 2,
        title: '添加币种汇率',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/coin/rate/coin_add'
    });
    this.layerIndex = index;
    layer.full(index);
};

/**
 * 打开查看币种配置详情
 */
CoinRate.openCoinDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '币种配置详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/coin/rate/coin_update/' + CoinRate.seItem.id
        });
        this.layerIndex = index;
        layer.full(index);
    }
};

/**
 * 删除币种配置
 */
CoinRate.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/coin/rate/delete", function (data) {
            Feng.success("删除成功!");
            CoinRate.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("id",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询币种配置列表
 */
CoinRate.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    CoinRate.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = CoinRate.initColumn();
    var table = new BSTable(CoinRate.id, "/coin/rate/list", defaultColunms);
    table.setPaginationType("client");
    CoinRate.table = table.init();
});

