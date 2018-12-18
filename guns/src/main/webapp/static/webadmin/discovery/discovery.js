

//对象初始化
var Discovery = {
    id: "DiscoveryTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

//初始化表格的列
Discovery.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'ID', field: 'id', visible: true, align: 'center', valign: 'middle'},
        {title: '用户名', field: 'userName', visible: true, align: 'center', valign: 'middle'},
        {title: '标题', field: 'title', visible: true, align: 'center', valign: 'middle'},
        //{title: '内容', field: 'content', visible: true, align: 'center', valign: 'middle', value: 'sdfsdfs'},
        {title: '状态', field: 'status', visible: true, align: 'center', valign: 'middle', formatter: function (value, row, index){
                switch (value){
                    case 1: return "发布审核";
                    case 2: return "审核通过";
                    case 3: return "审核失败";
                    default: return "未知";
                }
        }},
        {title: '时间', field: 'createTime', visible: true, align: 'center', valign: 'middle'},
        {title: '修改时间', field: 'lastTime', visible: true, align: 'center', valign: 'middle'}
    ];
};

//检查是否选中
Discovery.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        Discovery.seItem = selected[0];
        return true;
    }
};

//查询管理员登录日志列表
Discovery.search = function () {
    var queryData = {};
    queryData['mobile'] = $("#mobile").val();
    queryData['status'] = $("#status").val();
    Discovery.table.refresh({query: queryData});
};

Discovery.openDiscoveryDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/discovery/discovery_edit/' + Discovery.seItem.id
        });
        this.layerIndex = index;
        layer.full(index);
    }
};


$(function () {
    var defaultColunms = Discovery.initColumn();
    var table = new BSTable(Discovery.id, "/discovery/list", defaultColunms);
    //table.setPaginationType("client");
    Discovery.table = table.init();
});
