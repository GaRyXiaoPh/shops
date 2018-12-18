/**
 * 对象初始化
 */
var Advertise = {
    id: "AdvertiseTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};



/**
 * 初始化表格的列
 */
Advertise.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'ID', field: 'id', visible: false, align: 'center', valign: 'middle'},
        {title: '广告名称', field: 'name', visible: true, align: 'center', valign: 'middle'},
        {title: '广告位置', field: 'positionStr', visible: true, align: 'center', valign: 'middle'},
        {title: '开始时间', field: 'startTime', visible: true, align: 'center', valign: 'middle'},
        {title: '结束时间', field: 'endTime', visible: true, align: 'center', valign: 'middle'},
        {title: '状态', field: 'status', visible: true, align: 'center', valign: 'middle',
            formatter: function (value, row, index){return "SHOW" == value ? "上线" : "下线";}},
        {title: '广告图片', field: 'url', visible: true, align: 'center', valign: 'middle'},
        {title: '广告链接', field: 'link', visible: true, align: 'center', valign: 'middle'},
        {title: '备注', field: 'remark', visible: true, align: 'center', valign: 'middle'},
        {title: '创建时间', field: 'createTime', visible: true, align: 'center', valign: 'middle'},
        {title: '更新时间', field: 'lastTime', visible: true, align: 'center', valign: 'middle'},
        {title: '语言版本', field: 'locale', visible: true, align: 'center', valign: 'middle',
            formatter: function (value, row, index){return "zh_CN" == value ? "中文" : "英文";}},
    ];
};

/**
 * 检查是否选中
 */
Advertise.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        Advertise.seItem = selected[0];
        return true;
    }
};


/**
 * 点击添加币种配置
 */
Advertise.openAddAdvertise = function () {
    var index = layer.open({
        type: 2,
        title: '添加币种配置',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/advertise/advertise_add'
    });
    this.layerIndex = index;
    layer.full(index);
};

/**
 * 打开查看币种配置详情
 */
Advertise.openAdvertiseDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '币种配置详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/advertise/advertise_update/' + Advertise.seItem.id
        });
        this.layerIndex = index;
        layer.full(index);
    }
};

/**
 * 删除币种配置
 */
Advertise.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/advertise/delete", function (data) {
            if (data.code==201) {
                Feng.error("删除失败!" + data.message );
            }else{
                Feng.success("删除成功!");
                Advertise.table.refresh();
            }
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("id",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询公告列表
 */
Advertise.search = function () {
    var queryData = {};
    queryData['status'] = $("#status").val();
    queryData['position'] = $("#position").val();
    Advertise.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = Advertise.initColumn();
    var table = new BSTable(Advertise.id, "/advertise/list", defaultColunms);
    Advertise.table = table.init();
});

