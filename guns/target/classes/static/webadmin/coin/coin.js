/**
 * 对象初始化
 */
var Coin = {
    id: "CoinTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
Coin.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'ID', field: 'id', visible: false, align: 'center', valign: 'middle'},
        {title: '币种名称', field: 'name', visible: true, align: 'center', valign: 'middle'},
        {title: '币种符号', field: 'symbol', visible: false, align: 'center', valign: 'middle'},
        {title: '币种类别', field: 'category', visible: true, align: 'center', valign: 'middle'},
        {title: '默认显示名称', field: 'displayName', visible: false, align: 'center', valign: 'middle'},
        {title: '多语言的显示名', field: 'displayNameAll', visible: false, align: 'center', valign: 'middle'},
        {title: '显示图片', field: 'image', visible: false, align: 'center', valign: 'middle'},
        {title: '符号图片', field: 'icon', visible: false, align: 'center', valign: 'middle'},
        {title: '排序', field: 'sort', visible: false, align: 'center', valign: 'middle'},
        {title: '服务器地址', field: 'serverAddress', visible: true, align: 'center', valign: 'middle'},
        {title: '服务器端口', field: 'serverPort', visible: true, align: 'center', valign: 'middle'},
        {title: '用户名', field: 'serverUser', visible: false, align: 'center', valign: 'middle'},
        {title: '密码', field: 'serverPassword', visible: false, align: 'center', valign: 'middle'},
        {title: '合约地址', field: 'contractAddress', visible: false, align: 'center', valign: 'middle'},
        {title: '创建时间', field: 'createTime', visible: true, align: 'center', valign: 'middle'},
        {title: '修改时间', field: 'lastTime', visible: false, align: 'center', valign: 'middle'},
        {title: '主账户地址', field: 'coinBase', visible: false, align: 'center', valign: 'middle'},
        {title: '状态', field: 'status', visible: true, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
Coin.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        Coin.seItem = selected[0];
        return true;
    }
};


/**
 * 点击添加币种配置
 */
Coin.openAddCoin = function () {
    var index = layer.open({
        type: 2,
        title: '添加币种配置',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/coin/coin_add'
    });
    this.layerIndex = index;
    layer.full(index);
};

/**
 * 打开查看币种配置详情
 */
Coin.openCoinDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '币种配置详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/coin/coin_update/' + Coin.seItem.id
        });
        this.layerIndex = index;
        layer.full(index);
    }
};

/**
 * 删除币种配置
 */
Coin.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/coin/delete", function (data) {
            Feng.success("删除成功!");
            Coin.table.refresh();
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
Coin.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    Coin.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = Coin.initColumn();
    var table = new BSTable(Coin.id, "/coin/list", defaultColunms);
    table.setPaginationType("client");
    Coin.table = table.init();
});

