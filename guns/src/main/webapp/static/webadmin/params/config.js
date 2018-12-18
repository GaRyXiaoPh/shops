

//对象初始化
var Config = {
    id: "ConfigTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

//初始化表格的列
Config.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'ID', field: 'id', visible: true, align: 'center', valign: 'middle'},
        {title: '参数名', field: 'confName', visible: true, align: 'center', valign: 'middle'},
        {title: '参数值', field: 'confValue', visible: true, align: 'center', valign: 'middle'},
        {title: '备注', field: 'comment', visible: true, align: 'center', valign: 'middle'}
    ];
};

//检查是否选中
Config.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        Config.seItem = selected[0];
        return true;
    }
};


//点击添加币种配置
Config.openAddCoin = function () {
    var index = layer.open({
        type: 2,
        title: '添加参数配置',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/params/config/config_add'
    });
    this.layerIndex = index;
    layer.full(index);
};

//打开查看币种配置详情
Config.openCoinDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '参数配置详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/params/config/config_update/' + Config.seItem.id
        });
        this.layerIndex = index;
        layer.full(index);
    }
};

//删除币种配置
Config.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/params/config/delete", function (data) {
            Feng.success("删除成功!");
            Config.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("id",this.seItem.id);
        ajax.start();
    }
};

//查询币种配置列表
Config.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    Config.table.refresh({query: queryData});
};

var Level = {
    id: "LevelTable",
    seItem: null,
    table: null,
    layerIndex: -1
};
Level.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'ID', field: 'id', visible: true, align: 'center', valign: 'middle'},
        {title: '等级', field: 'level', visible: true, align: 'center', valign: 'middle'},
        {title: '最小值', field: 'minAmount', visible: true, align: 'center', valign: 'middle'},
        {title: '最大值', field: 'maxAmount', visible: true, align: 'center', valign: 'middle'},
        {title: '社区比例', field: 'rate', visible: true, align: 'center', valign: 'middle'},
        {title: '挖矿消耗ENG11', field: 'consume', visible: true, align: 'center', valign: 'middle'},
    ];
};
Level.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        Level.seItem = selected[0];
        return true;
    }
};
Level.openAddCoin = function () {
    var index = layer.open({
        type: 2,
        title: '添加参数配置',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/params/level/level_add'
    });
    this.layerIndex = index;
    layer.full(index);
};
Level.openCoinDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '参数配置详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/params/level/level_update/' + Level.seItem.id
        });
        this.layerIndex = index;
        layer.full(index);
    }
};
Level.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/params/level/delete", function (data) {
            Feng.success("删除成功!");
            Level.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("id",this.seItem.id);
        ajax.start();
    }
};



$(function () {
    var defaultColunms = Config.initColumn();
    var table = new BSTable(Config.id, "/params/config/list", defaultColunms);
    table.setPaginationType("client");
    Config.table = table.init();

    var defaultLevel = Level.initColumn();
    var tableLevel = new BSTable(Level.id, "/params/level/list", defaultLevel);
    tableLevel.setPaginationType("client");
    Level.table = tableLevel.init();
});

