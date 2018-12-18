//初始化币种配置详情对话框
var CoinInfoDlg = {
    coinInfoData : {}
};

//清除数据
CoinInfoDlg.clearData = function() {
    this.coinInfoData = {};
}

//设置对话框中的数据
CoinInfoDlg.set = function(key, val) {
    this.coinInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

//设置对话框中的数据
CoinInfoDlg.get = function(key) {
    return $("#" + key).val();
}

//关闭此对话框
CoinInfoDlg.close = function() {
    parent.layer.close(window.parent.Advertise.layerIndex);
}

//收集数据
CoinInfoDlg.collectData = function() {
    this.set('name')
        .set('symbol')
        .set('category')
        .set('displayName')
        .set('displayNameAll')
        .set('image')
        .set('icon')
        .set('serverAddress')
        .set('serverPort')
        .set('serverUser')
        .set('serverPassword')
        .set('contractAddress')
        .set('transferMaxAmount')
        .set('transferMinAmount')
        .set('transferFeeRate')
        .set('maximumAmountDay')
        .set('maximumNumberDay')
        .set('withdrawFeeRate')
        .set('coinBase');
}

//提交添加
CoinInfoDlg.addSubmit = function() {
    this.clearData();
    this.collectData();

    this.set('image', $("#image1").attr('src'));
    this.set('icon', $("#icon1").attr('src'));

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/coin/add", function(data){
        Feng.success("添加成功!");
        window.parent.Advertise.table.refresh();
        CoinInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.coinInfoData);
    ajax.start();
}

//提交修改
CoinInfoDlg.editSubmit = function() {
    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/coin/update", function(data){
        Feng.success("修改成功!");
        window.parent.Advertise.table.refresh();
        CoinInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.coinInfoData);
    ajax.start();
}

$(function() {
    //当image输入有变动时
    $("#image").change(function () {
        var file = $("#image")[0].files[0];
        if (file!=null){
            var formData = new FormData();
            formData.append("upload",file);
            $.ajax({
                url : Feng.ctxPath + "/assist/img/upload",
                type : 'POST',
                data :formData,
                processData : false,
                contentType : false,
                success: function(data) {
                    $("#image1").attr('src',data);
                    $("#image1").show();
                },
                error:function (data) {
                    console.log(data)
                }
            });
        }
    });
    $("#icon").change(function () {
        var file=$("#icon")[0].files[0];
        if (file!=null){
            var formData = new FormData();
            formData.append("upload", file);
            $.ajax({
                url:Feng.ctxPath+"/assist/img/upload",
                type:'POST',
                data:formData,
                processData:false,
                contentType:false,
                success:function (data) {
                    $("#icon1").attr('src',data);
                    $("#icon1").show();
                },
                error:function (data) {
                    console.log(data)
                }
            })
        }
    });
});
