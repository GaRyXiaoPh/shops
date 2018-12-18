

//对象初始化
var Trans = {
    id: "TransCheckTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

//初始化表格的列
Trans.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'ID', field: 'id', visible: true, align: 'center', valign: 'middle'},
        {title: '用户名', field: 'userName', visible: true, align: 'center', valign: 'middle'},
        {title: '用户昵称', field: 'nickName', visible: true, align: 'center', valign: 'middle'},
        {title: '真实姓名', field: 'realName', visible: true, align: 'center', valign: 'middle', value: 'sdfsdfs'},
        {title: '手机号', field: 'mobile', visible: true, align: 'center', valign: 'middle'},
        {title: 'Email', field: 'email', visible: true, align: 'center', valign: 'middle'},
        {title: '注册时间', field: 'registerTime', visible: true, align: 'center', valign: 'middle'},
        {title: '登录时间', field: 'lastLoginTime', visible: true, align: 'center', valign: 'middle'},
        {title: '左区推荐码', field: 'leftInvite', visible: true, align: 'center', valign: 'middle'},
        {title: '右区推荐码', field: 'rightInvite', visible: true, align: 'center', valign: 'middle'},
        {title: '状态', field: 'status', visible: true, align: 'center', valign: 'middle', formatter: function (value, row, index){return 0 == value ? "启用" : "禁用";}}
    ];
};

//检查是否选中
Trans.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        Trans.seItem = selected[0];
        return true;
    }
};

//查询管理员登录日志列表
Trans.search = function () {
    var queryData = {};
    queryData['mobile'] = $("#mobile").val();
    queryData['email'] = $("#email").val();
    Trans.table.refresh({query: queryData});
};

Trans.openUserEdit = function () {
    alert("暂时不开放");
};
Trans.openUserBillList = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '资产流水',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/user/user_bill/' + Trans.seItem.id
        });
        this.layerIndex = index;
        layer.full(index);
    }
};
Trans.openUserCoinList = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '用户资产',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/user/user_coin/' + Trans.seItem.id
        });
        this.layerIndex = index;
        layer.full(index);
    }
};



$(function () {
    var defaultColunms = Trans.initColumn();
    var table = new BSTable(Trans.id, "/trans/list", defaultColunms);
    //table.setPaginationType("client");
    Trans.table = table.init();
});
