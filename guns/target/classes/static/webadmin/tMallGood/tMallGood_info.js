/**
 * 初始化商品列表详情对话框
 */
var TMallGoodInfoDlg = {
    tMallGoodInfoData : {}
};

/**
 * 清除数据
 */
TMallGoodInfoDlg.clearData = function() {
    this.tMallGoodInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
TMallGoodInfoDlg.set = function(key, val) {
    this.tMallGoodInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
TMallGoodInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
TMallGoodInfoDlg.close = function() {
    parent.layer.close(window.parent.TMallGood.layerIndex);
}

/**
 * 收集数据
 */
TMallGoodInfoDlg.collectData = function() {
    this
    .set('id')
    .set('userId')
    .set('name')
    .set('detail')
    .set('price')
    .set('coinName')
    .set('stock')
    .set('sellerWechat')
    .set('sellerMobile')
    .set('shopAddress')
    .set('status')
    .set('createTime')
    .set('updateTime')
    .set('isDelete')
    .set('salesVolume')
    .set('categoryId')
    .set('cny');
}

/**
 * 提交添加
 */
TMallGoodInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/tMallGood/add", function(data){
        Feng.success("添加成功!");
        window.parent.TMallGood.table.refresh();
        TMallGoodInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.tMallGoodInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
TMallGoodInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/tMallGood/update", function(data){
        Feng.success("修改成功!");
        window.parent.TMallGood.table.refresh();
        TMallGoodInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.tMallGoodInfoData);
    ajax.start();
}

$(function() {

});
