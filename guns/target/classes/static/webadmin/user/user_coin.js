

//对象初始化
var g_select_userId = 0;
var UserCoin = {
    id: "UserCoinTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

//初始化表格的列
UserCoin.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'ID', field: 'id', visible: true, align: 'center', valign: 'middle'},
        {title: '用户ID', field: 'userId', visible: true, align: 'center', valign: 'middle'},
        {title: '币名', field: 'coinName', visible: true, align: 'center', valign: 'middle'},
        {title: '地址', field: 'bindAddress', visible: true, align: 'center', valign: 'middle'},
        {title: '可用余额', field: 'availableBalance', visible: true, align: 'center', valign: 'middle'},
        {title: '冻结余额', field: 'freezeBalance', visible: true, align: 'center', valign: 'middle'},
        {title: '状态', field: 'status', visible: true, align: 'center', valign: 'middle', formatter: function (value, row, index){return 0 == value ? "启用" : "禁用";}}
    ];
};


$(function () {
    var defaultColunms = UserCoin.initColumn();
    var table = new BSTable(UserCoin.id, "/user/user_coin/list/" + g_select_userId, defaultColunms);
    table.setPaginationType("client");
    UserCoin.table = table.init();
});
