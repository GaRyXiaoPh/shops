/**
 * 初始化币种配置详情对话框
 */
var ConfigInfoDlg = {
    coinInfoData : {}
};

/**
 * 清除数据
 */
ConfigInfoDlg.clearData = function() {
    this.coinInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ConfigInfoDlg.set = function(key, val) {
    this.coinInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ConfigInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
ConfigInfoDlg.close = function() {
    parent.layer.close(window.parent.Config.layerIndex);
}

/**
 * 收集数据
 */
ConfigInfoDlg.collectData = function() {
    this.set('confName')
        .set('confValue')
        .set('comment');
}

/**
 * 提交添加
 */
ConfigInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/params/config/add", function(data){
        Feng.success("添加成功!");
        window.parent.Config.table.refresh();
        ConfigInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.coinInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
ConfigInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/params/config/update", function(data){
        Feng.success("修改成功!");
        window.parent.Config.table.refresh();
        ConfigInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.coinInfoData);
    ajax.start();
}



var LevelInfoDlg = {
    coinInfoData : {}
};
LevelInfoDlg.clearData = function() {
    this.coinInfoData = {};
}
LevelInfoDlg.set = function(key, val) {
    this.coinInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}
LevelInfoDlg.get = function(key) {
    return $("#" + key).val();
}
LevelInfoDlg.close = function() {
    parent.layer.close(window.parent.Level.layerIndex);
}
LevelInfoDlg.collectData = function() {
    this.set('level').set('minAmount').set('maxAmount').set('rate').set('consume');
}
LevelInfoDlg.addSubmit = function() {
    this.clearData();
    this.collectData();
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/params/level/add", function(data){
        Feng.success("添加成功!");
        window.parent.Level.table.refresh();
        LevelInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.coinInfoData);
    ajax.start();
}
LevelInfoDlg.editSubmit = function() {
    this.clearData();
    this.collectData();
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/params/level/update", function(data){
        Feng.success("修改成功!");
        window.parent.Level.table.refresh();
        LevelInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.coinInfoData);
    ajax.start();
}


$(function() {

});
