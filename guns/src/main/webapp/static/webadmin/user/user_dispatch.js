//初始化币种配置详情对话框
var DispatchInfoDlg = {
    coinInfoData : {}
};

//清除数据
DispatchInfoDlg.clearData = function() {
    this.coinInfoData = {};
}

//设置对话框中的数据
DispatchInfoDlg.set = function(key, val) {
    this.coinInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

//设置对话框中的数据
DispatchInfoDlg.get = function(key) {
    return $("#" + key).val();
}

//关闭此对话框
DispatchInfoDlg.close = function() {
    parent.layer.close(window.parent.User.layerIndex);
}

//收集数据
DispatchInfoDlg.collectData = function() {
    this.set('id')
        .set("userName")
        .set('coinName')
        .set('amount');
}

//拨币
DispatchInfoDlg.dispatchCoin = function() {
    this.clearData();
    this.collectData();

    this.set('coinName', $("#coinName").val());
    this.set('amount', $("#amount").val());

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/user/dispatch-coin", function(data){
        Feng.success("拨币成功!");
        DispatchInfoDlg.close();
    },function(data){
        Feng.error("拨币失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.coinInfoData);
    ajax.start();
}

$(function() {
});
