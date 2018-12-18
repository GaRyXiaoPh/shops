

//对象初始化
var Check = {
    id: "CheckTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};



Check.funStatus = function (value, row, index) {
    switch (value){
        case 0: return "正在申请";
        case 1: return "审核通过";
        case 2: return "审核失败";
        case 3: return "节点确认";
    }
}

//初始化表格的列
Check.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'ID', field: 'id', visible: true, align: 'center', valign: 'middle'},
        {title: '用户名', field: 'userName', visible: true, align: 'center', valign: 'middle'},
        {title: '币种名称', field: 'coinName', visible: true, align: 'center', valign: 'middle'},
        {title: '类型', field: 'type', visible: true, align: 'center', valign: 'middle', formatter: function (value, row, index){return 0 == value ? "转账" : "提币";}},
        {title: '地址', field: 'address', visible: true, align: 'center', valign: 'middle'},
        {title: '数量', field: 'amount', visible: true, align: 'center', valign: 'middle'},
        {title: '手续费', field: 'fee', visible: true, align: 'center', valign: 'middle'},
        {title: '状态', field: 'status', visible: true, align: 'center', valign: 'middle', formatter: function (value, row, index){
            switch (value){
                case 'APPLYING': return "正在申请";
                case 'PASSED': return "审核通过";
                case 'FAILED': return "审核失败";
                case 'CONFIRM': return "节点确认";
            }
        }},
        {title: '时间', field: 'sendTime', visible: true, align: 'center', valign: 'middle'},
        {title: '操作时间', field: 'lastTime', visible: true, align: 'center', valign: 'middle'}
    ];
};

//检查是否选中
Check.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        Check.seItem = selected[0];
        return true;
    }
};

//查询管理员登录日志列表
Check.search = function () {
    var queryData = {};
    queryData['mobile'] = $("#mobile").val();
    queryData['address'] = $("#address").val();
    queryData['status'] = $("#status").val();
    Check.table.refresh({query: queryData});
};

Check.openCheckEdit = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '转账审核',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/trans/check/check_update/' + Check.seItem.id
        });
        this.layerIndex = index;
        layer.full(index);
    }
};

Check.openCheckPass =function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/trans/check/check_pass/"+ Check.seItem.id, function (data) {
            Feng.success("提交成功!");
            Check.table.refresh();
        }, function (data) {
            Feng.error("提交失败!" + data.responseJSON.message + "!");
        });
        ajax.set("id",this.seItem.id);
        ajax.start();
    }
}
Check.openCheckFail =function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/trans/check/check_fail/"+ Check.seItem.id, function (data) {
            Feng.success("提交成功!");
            Check.table.refresh();
        }, function (data) {
            console.log(JSON.stringify(data));
            Feng.error("提交失败!" + data.responseJSON.message + "!");
        });
        ajax.set("id",this.seItem.id);
        ajax.start();
    }
}

$(function () {
    var defaultColunms = Check.initColumn();
    var table = new BSTable(Check.id, "/trans/check/list", defaultColunms);
    //table.setPaginationType("client");
    Check.table = table.init();
});
