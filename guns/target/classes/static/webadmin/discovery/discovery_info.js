var DiscoveryInfoDlg = {
    coinInfoData : {}
};

//清除数据
DiscoveryInfoDlg.clearData = function() {
    this.coinInfoData = {};
}

/**
 * 设置对话框中的数据
 * @param key 数据的名称
 * @param val 数据的具体值
 */
DiscoveryInfoDlg.set = function(key, val) {
    this.coinInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 * @param key 数据的名称
 * @param val 数据的具体值
 */
DiscoveryInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
DiscoveryInfoDlg.close = function() {
    parent.layer.close(window.parent.Discovery.layerIndex);
}

/**
 * 收集数据
 */
DiscoveryInfoDlg.collectData = function() {
    this.set('id')
        .set('userName')
        .set('category')
        .set('title')
        .set('content')
        .set('status')
        .set('createTime')
        .set('lastTime');
}

//审核通过
DiscoveryInfoDlg.editSubmitPass = function() {
    this.clearData();
    this.collectData();
    console.log(g_select_id);
    console.log(DiscoveryInfoDlg.get("id"));

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/discovery/discovery-pass/"+g_select_id, function(data){
        Feng.success("提交成功!");
        window.parent.Discovery.table.refresh();
        DiscoveryInfoDlg.close();
    },function(data){
        Feng.error("提交失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.coinInfoData);
    ajax.start();
}
//审核失败
DiscoveryInfoDlg.editSubmitFail = function() {
    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/discovery/discovery-fail/"+g_select_id, function(data){
        Feng.success("提交成功!");
        window.parent.Discovery.table.refresh();
        DiscoveryInfoDlg.close();
    },function(data){
        Feng.error("提交失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.coinInfoData);
    ajax.start();
}
DiscoveryInfoDlg.delete = function () {
    this.clearData();
    this.collectData();

    var ajax = new $ax(Feng.ctxPath+"/discovery/discovery-delete/"+g_select_id, function (data) {
        Feng.success("删除成功!");
        window.parent.Discovery.table.refresh();
        DiscoveryInfoDlg.close();
    },function (data) {
        Feng.error("提交失败!" + data.responseJSON.message + "!");
    })
    ajax.set(this.coinInfoData);
    ajax.start();
}

$(function() {
});


