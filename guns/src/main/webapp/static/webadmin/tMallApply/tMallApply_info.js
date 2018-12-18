/**
 * 初始化商家审核详情对话框
 */
var TMallApplyInfoDlg = {
    tMallApplyInfoData : {}
};

/**
 * 清除数据
 */
TMallApplyInfoDlg.clearData = function() {
    this.tMallApplyInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
TMallApplyInfoDlg.set = function(key, val) {
    this.tMallApplyInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
TMallApplyInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
TMallApplyInfoDlg.close = function() {
    parent.layer.close(window.parent.TMallApply.layerIndex);
}

/**
 * 收集数据
 */
TMallApplyInfoDlg.collectData = function() {
    this
    .set('id')
    .set('userId')
    .set('contacts')
    .set('phone')
    .set('addTime')
    .set('updateTime')
    .set('status');
}

/**
 * 提交添加
 */
TMallApplyInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/tMallApply/add", function(data){
        Feng.success("添加成功!");
        window.parent.TMallApply.table.refresh();
        TMallApplyInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.tMallApplyInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
TMallApplyInfoDlg.editSubmit = function(status) {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/tMallApply/update", function(data){
        Feng.success("操作成功!");
        window.parent.TMallApply.table.refresh();
        TMallApplyInfoDlg.close();
    },function(data){
        Feng.error("操作失败!" + data.responseJSON.message + "!");
    });
    this.tMallApplyInfoData['status'] = status;

    ajax.set(this.tMallApplyInfoData);
    ajax.start();
}

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
$(function() {
    var val = $("#statusValue").val();
    $("#status").val(val);

});
