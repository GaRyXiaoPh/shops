

//对象初始化
var User = {
    id: "UserTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

//初始化表格的列
User.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'ID', field: 'id', visible: true, align: 'center', valign: 'middle'},
        {title: '用户名', field: 'userName', visible: true, align: 'center', valign: 'middle'},
        {title: '用户昵称', field: 'nickName', visible: true, align: 'center', valign: 'middle'},
        //{title: '真实姓名', field: 'realName', visible: true, align: 'center', valign: 'middle', value: 'sdfsdfs'},
        {title: '手机号', field: 'mobile', visible: true, align: 'center', valign: 'middle'},
        //{title: 'Email', field: 'email', visible: true, align: 'center', valign: 'middle'},
        {title: '注册时间', field: 'registerTime', visible: true, align: 'center', valign: 'middle'},
        {title: '登录时间', field: 'lastLoginTime', visible: true, align: 'center', valign: 'middle'},
        {title: '推荐码', field: 'leftInvite', visible: true, align: 'center', valign: 'middle'},
        {title: '是否商家', field: 'salesPermit', visible: true, align: 'center', valign: 'middle',formatter:function(value, row, index){
                switch (value){
                    case 0:
                        return "否";
                    case 1:
                        return "是";
                    default:
                        return "未知";
                }
            }},
        {title: '是否自品牌用户', field: 'brandPermit', visible: true, align: 'center', valign: 'middle',formatter:function(value, row, index){
                switch (value){
                    case 0:
                        return "否";
                    case 1:
                        return "是";
                    default:
                        return "未知";
                }
            }},
        {title: '是否全平台用户', field: 'globalPermit', visible: true, align: 'center', valign: 'middle',formatter:function(value, row, index){
                switch (value){
                    case 0:
                        return "否";
                    case 1:
                        return "是";
                    default:
                        return "未知";
                }
            }},
        // {title: '右区推荐码', field: 'rightInvite', visible: true, align: 'center', valign: 'middle'},
        {title: '状态', field: 'status', visible: true, align: 'center', valign: 'middle', formatter: function (value, row, index){return 0 == value ? "启用" : "禁用";}}
    ];
};

//检查是否选中
User.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        User.seItem = selected[0];
        return true;
    }
};

//查询管理员登录日志列表
User.search = function () {
    var queryData = {};
    queryData['mobile'] = $("#mobile").val();
    queryData['email'] = $("#email").val();
    User.table.refresh({query: queryData});
};

User.openUserEdit = function () {
    if (this.check()){
        var index = layer.open({
            type: 2,
            title: '用户信息',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/user/user_edit/' + User.seItem.id
        });
        this.layerIndex = index;
        layer.full(index);
    }
};
User.openUserBillList = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '资产流水',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/user/user_bill/' + User.seItem.id
        });
        this.layerIndex = index;
        layer.full(index);
    }
};
User.openUserCoinList = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '用户资产',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/user/user_coin/' + User.seItem.id
        });
        this.layerIndex = index;
        layer.full(index);
    }
};
User.openUserDispatch = function () {
    if (this.check()){
        var index = layer.open({
            type: 2,
            title: '用户资产',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/user/user-dispatch/' + User.seItem.id
        });
        this.layerIndex = index;
        layer.full(index);
    }
}
//提交修改
User.sellPermit = function() {

    if (this.check()) {
        var status = this.seItem.sellPermit;
        var action = status == 0 ? '开启' :'关闭';
        var id = this.seItem.id;
        Feng.confirm("是否"+action+"该用户销售权限?",function(){
            var ajax = new $ax(Feng.ctxPath + "/user/sell-permit", function (data) {
                Feng.success(action+"成功!");
                User.table.refresh();
            }, function (data) {
                Feng.error(action+"失败!" + data.responseJSON.message + "!");
            });
            ajax.set("id",id);
            ajax.set("status",status == 0 ? 1 : 0);
            ajax.start();
        });

    }
}

//提交修改
User.brandPermit = function() {

    if (this.check()) {
        var status = this.seItem.brandPermit;
        var action = status == 0 ? '开启' :'关闭';
        var id = this.seItem.id;
        Feng.confirm("是否"+action+"该用户自品牌权限?",function(){
            var ajax = new $ax(Feng.ctxPath + "/user/brand-permit", function (data) {
                Feng.success(action+"成功!");
                User.table.refresh();
            }, function (data) {
                Feng.error(action+"失败!" + data.responseJSON.message + "!");
            });
            ajax.set("id",id);
            ajax.set("status",status == 0 ? 1 : 0);
            ajax.start();
        });

    }
}

//提交修改
User.globalPermit = function() {

    if (this.check()) {
        var status = this.seItem.globalPermit;
        var action = status == 0 ? '开启' :'关闭';
        var id = this.seItem.id;
        Feng.confirm("是否"+action+"该用户全平台权限?",function(){
            var ajax = new $ax(Feng.ctxPath + "/user/global-permit", function (data) {
                Feng.success(action+"成功!");
                User.table.refresh();
            }, function (data) {
                Feng.error(action+"失败!" + data.responseJSON.message + "!");
            });
            ajax.set("id",id);
            ajax.set("status",status == 0 ? 1 : 0);
            ajax.start();
        });

    }
}


$(function () {
    var defaultColunms = User.initColumn();
    var table = new BSTable(User.id, "/user/list", defaultColunms);
    //table.setPaginationType("client");
    User.table = table.init();
});
