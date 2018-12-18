//初始化币种配置详情对话框
var AdvertiseInfoDlg = {
    advertiseInfoData : {}
};

//清除数据
AdvertiseInfoDlg.clearData = function() {
    this.advertiseInfoData = {};
}

//设置对话框中的数据
AdvertiseInfoDlg.set = function(key, val) {
    this.advertiseInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

//设置对话框中的数据
AdvertiseInfoDlg.get = function(key) {
    return $("#" + key).val();
}

//关闭此对话框
AdvertiseInfoDlg.close = function() {
    parent.layer.close(window.parent.Advertise.layerIndex);
}



//收集数据
AdvertiseInfoDlg.collectData = function() {
    this.set('id')
        .set('name')
        .set('position')
        .set('startTime')
        .set('endTime')
        .set('status')
        .set('image')
        .set('link')
        .set('remark')
        .set('createTime')
        .set('lastTime')
        .set('locale');
}

//提交添加
AdvertiseInfoDlg.addSubmit = function() {
    this.clearData();
    this.collectData();

    this.set('url', $("#image").attr('src'));

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/advertise/add", function(data){
        if (data.code==201) {
            Feng.error("添加失败!" + data.message + "!");
        }else {
            Feng.success("添加成功!");
            window.parent.Advertise.table.refresh();
            AdvertiseInfoDlg.close();
        }
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.advertiseInfoData);
    ajax.start();
}

//提交修改
AdvertiseInfoDlg.editSubmit = function() {
    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/advertise/update", function(data){
        Feng.success("修改成功!");
        window.parent.Advertise.table.refresh();
        AdvertiseInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.advertiseInfoData);
    ajax.start();
}

$(function() {
    //当image输入有变动时
    $("#url").change(function () {
        var file = $("#url")[0].files[0];
        if (file!=null){
            var formData = new FormData();
            formData.append("file",file);
            $.ajax({
                url : Feng.ctxPath + "/user/upload",
                type : 'POST',
                data :formData,
                processData : false,
                contentType : false,
                success: function(data) {
                    $("#image").attr('src',data);
                    $("#image").show();
                },
                error:function (data) {
                    console.log(data)
                }
            });
        }
    });

});
