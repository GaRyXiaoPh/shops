//初始化币种配置详情对话框
var UserInfoDlg = {
    coinInfoData : {}
};

//清除数据
UserInfoDlg.clearData = function() {
    this.coinInfoData = {};
}

//设置对话框中的数据
UserInfoDlg.set = function(key, val) {
    this.coinInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

//设置对话框中的数据
UserInfoDlg.get = function(key) {
    return $("#" + key).val();
}

//关闭此对话框
UserInfoDlg.close = function() {
    parent.layer.close(window.parent.User.layerIndex);
}

//收集数据
UserInfoDlg.collectData = function() {
    this.set('id')
        .set('nickName')
        .set('mobile')
        .set('email');
}

//提交修改
UserInfoDlg.editSubmit = function() {
    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/user/update", function(data){
        Feng.success("修改成功!");
        window.parent.User.table.refresh();
        UserInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.coinInfoData);
    ajax.start();
}

$(function() {

});
