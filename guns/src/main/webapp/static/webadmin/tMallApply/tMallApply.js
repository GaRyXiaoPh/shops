/**
 * 商家审核管理初始化
 */
var TMallApply = {
    id: "TMallApplyTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
TMallApply.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            {title: '', field: 'id', visible: false, align: 'center', valign: 'middle'},
            {title: '申请的用户', field: 'userId', visible: false, align: 'center', valign: 'middle'},
            {title: '联系人', field: 'contacts', visible: true, align: 'center', valign: 'middle'},
            {title: '联系人电话', field: 'phone', visible: true, align: 'center', valign: 'middle'},
            {title: '申请时间', field: 'addTime', visible: true, align: 'center', valign: 'middle'},
            {title: '审核时间', field: 'updateTime', visible: true, align: 'center', valign: 'middle'},
            {title: '状态', field: 'status', visible: true, align: 'center', valign: 'middle', formatter: function (value, row, index){
                switch (value){
                    case 0:
                        return "申请中";
                    case 1:
                        return "已驳回";
                    case 2:
                        return "已通过";
                    default:
                        return "未知";
                }
            }
            }
    ];
};

/**
 * 检查是否选中
 */
TMallApply.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        TMallApply.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加商家审核
 */
TMallApply.openAddTMallApply = function () {
    var index = layer.open({
        type: 2,
        title: '添加商家审核',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/tMallApply/tMallApply_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看商家审核详情
 */
TMallApply.openTMallApplyDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '商家审核详情',
            // area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/tMallApply/tMallApply_update/' + TMallApply.seItem.id
        });
        this.layerIndex = index;
        layer.full(index);
    }
};

// /**
//  * 删除商家审核
//  */
// TMallApply.update = function () {
//     if (this.check()) {
//         var ajax = new $ax(Feng.ctxPath + "/tMallApply/update/"+  TMallApply.seItem.id, function (data) {
//             Feng.success("操作成功!");
//             TMallApply.table.refresh();
//         }, function (data) {
//             Feng.error("操作失败!" + data.responseJSON.message + "!");
//         });
//         ajax.start();
//     }
// };

/**
 * 查询商家审核列表
 */
TMallApply.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    TMallApply.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = TMallApply.initColumn();
    var table = new BSTable(TMallApply.id, "/tMallApply/list", defaultColunms);
    // table.setPaginationType("client");
    TMallApply.table = table.init();
});
