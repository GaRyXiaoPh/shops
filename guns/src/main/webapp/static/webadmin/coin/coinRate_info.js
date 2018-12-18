/**
 * 初始化币种配置详情对话框
 */
var CoinRateInfoDlg = {
    coinInfoData : {}
};

/**
 * 清除数据
 */
CoinRateInfoDlg.clearData = function() {
    this.coinInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
CoinRateInfoDlg.set = function(key, val) {
    this.coinInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
CoinRateInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
CoinRateInfoDlg.close = function() {
    parent.layer.close(window.parent.CoinRate.layerIndex);
}

/**
 * 收集数据
 */
CoinRateInfoDlg.collectData = function() {
    this.set('id')
        .set('coinName')
        .set('changeName')
        .set('rate')
        .set('createTime')
        .set('lastTime')
        .set('status');
}

/**
 * 提交添加
 */
CoinRateInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/coin/rate/add", function(data){
        Feng.success("添加成功!");
        window.parent.CoinRate.table.refresh();
        CoinRateInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.coinInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
CoinRateInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/coin/rate/update", function(data){
        Feng.success("修改成功!");
        window.parent.CoinRate.table.refresh();
        CoinRateInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.coinInfoData);
    ajax.start();
}

$(function() {

});
