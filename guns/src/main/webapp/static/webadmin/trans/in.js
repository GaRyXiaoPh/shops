

//对象初始化
var In = {
    id: "InTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

//初始化表格的列
In.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'ID', field: 'id', visible: true, align: 'center', valign: 'middle'},
        {title: '用户名', field: 'userName', visible: true, align: 'center', valign: 'middle'},
        {title: '币种名称', field: 'coinName', visible: true, align: 'center', valign: 'middle'},
        {title: '类型', field: 'type', visible: true, align: 'center', valign: 'middle', formatter: function (value, row, index){return 0 == value ? "转账" : "充币";}},
        {title: '地址', field: 'address', visible: true, align: 'center', valign: 'middle'},
        {title: '数量', field: 'amount', visible: true, align: 'center', valign: 'middle'},
        {title: '手续费', field: 'fee', visible: true, align: 'center', valign: 'middle'},
        {title: '状态', field: 'status', visible: true, align: 'center', valign: 'middle', formatter: function (value, row, index){ return "成功"; }},
        {title: '时间', field: 'receivedTime', visible: true, align: 'center', valign: 'middle'},
    ];
};

//检查是否选中
In.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        In.seItem = selected[0];
        return true;
    }
};

//查询管理员登录日志列表
In.search = function () {
    var queryData = {};
    queryData['mobile'] = $("#mobile").val();
    queryData['address'] = $("#address").val();
    queryData['status'] = $("#status").val();
    In.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = In.initColumn();
    var table = new BSTable(In.id, "/trans/in/list", defaultColunms);
    //table.setPaginationType("client");
    In.table = table.init();
});
