/**
 * 初始化订单列表详情对话框
 */
var TMallOrderInfoDlg = {
    tMallOrderInfoData : {}
};

/**
 * 清除数据
 */
TMallOrderInfoDlg.clearData = function() {
    this.tMallOrderInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
TMallOrderInfoDlg.set = function(key, val) {
    this.tMallOrderInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
TMallOrderInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
TMallOrderInfoDlg.close = function() {
    parent.layer.close(window.parent.TMallOrder.layerIndex);
}

/**
 * 收集数据
 */
TMallOrderInfoDlg.collectData = function() {
    this
    .set('id')
    .set('userId')
    .set('sellerId')
    .set('goodId')
    .set('count')
    .set('addressId')
    .set('totalPrice')
    .set('status')
    .set('createTime')
    .set('updateTime')
    .set('isDelete')
    .set('receiverName')
    .set('receiverMobile')
    .set('provinceId')
    .set('cityId')
    .set('areaId')
    .set('detailAddr')
    .set('price')
    .set('coinName')
    .set('returnStatus')
    .set('returnReason')
    .set('reputation');
}

/**
 * 确认收货
 */
TMallOrderInfoDlg.confirm = function() {
    var that = this;

    Feng.confirm("是否确认收货该订单?",function(){
        that.clearData();
        that.collectData();

        //确认收货
        var ajax = new $ax(Feng.ctxPath + "/tMallOrder/confirm", function(data){
            Feng.success("确认收货成功!");
            window.parent.TMallOrder.table.refresh();
            TMallOrderInfoDlg.close();
        },function(data){
            Feng.error("确认收货失败!" + data.responseJSON.message + "!");
        });
        ajax.set("id",that.tMallOrderInfoData.id);
        ajax.start();
    });

}

/**
 * 确认退货
 */
TMallOrderInfoDlg.return = function() {
    var that = this;
    Feng.confirm("是否确认退货该订单?",function(){
        that.clearData();
        that.collectData();

        //确认收货
        var ajax = new $ax(Feng.ctxPath + "/tMallOrder/return", function(data){
            Feng.success("确认退货成功!");
            window.parent.TMallOrder.table.refresh();
            TMallOrderInfoDlg.close();
        },function(data){
            Feng.error("确认退货失败!" + data.responseJSON.message + "!");
        });
        ajax.set("id",that.tMallOrderInfoData.id);
        ajax.start();
    });

}

/**
 * 确认退货
 */
TMallOrderInfoDlg.cancel = function() {
    var that = this;

    Feng.confirm("是否确认取消该订单?",function(){
        that.clearData();
        that.collectData();
        //确认收货
        var ajax = new $ax(Feng.ctxPath + "/tMallOrder/cancel", function(data){
            Feng.success("取消订单成功!");
            window.parent.TMallOrder.table.refresh();
            TMallOrderInfoDlg.close();
        },function(data){
            Feng.error("取消订单失败!" + data.responseJSON.message + "!");
        });
        ajax.set("id",that.tMallOrderInfoData.id);
        ajax.start();
    });


}

/**
 * 提交修改
 */
TMallOrderInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/tMallOrder/update", function(data){
        Feng.success("修改成功!");
        window.parent.TMallOrder.table.refresh();
        TMallOrderInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.tMallOrderInfoData);
    ajax.start();
}

$(function() {
    var val = $("#statusValue").val();
    $("#status").val(val);
});
