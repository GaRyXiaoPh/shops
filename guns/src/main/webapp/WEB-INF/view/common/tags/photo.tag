@/*
    头像参数的说明:
    name : 名称
    id : 头像的id
@*/
<div class="form-group">
    <label class="col-sm-3 control-label">${name}</label>
    @if(isNotEmpty(avatarImg)){
        <div class="col-sm-4">
            <div id="${id}PreId">
                <div><img height="200px" src="${avatarImg}"></div>
            </div>
        </div>
    @}else{
        <div class="col-sm-9">
            <input class="form-control" id="${id}" name="${id}"
                   readonly="true"
                   disabled="true"
                   value="无"
            >
        </div>
    @}
    @if(isNotEmpty(upbutton) && upbutton == 'true'){
    <div class="col-sm-2">
        <div class="head-scu-btn upload-btn" id="${id}BtnId">
            <i class="fa fa-upload"></i>&nbsp;上传
        </div>
    </div>
    @}

    <input type="hidden" id="${id}" value="${avatarImg!}"/>
</div>
@if(isNotEmpty(underline) && underline == 'true'){
    <div class="hr-line-dashed"></div>
@}


