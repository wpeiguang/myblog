<form id="qForm" class="form-horizontal" method="post" action="update_config">
<#include "/admin/message.ftl">

    <div class="form-group">
        <label class="col-lg-2 control-label">TPG系统</label>
        <div class="col-lg-8">
            <textarea rows="3" class="form-control" name="tpg_cookies" class="form-control" placeholder="请输入TPG系统cookies">${configs['tpg_cookies'].value}</textarea>
        </div>
    </div>
    <div class="form-group">
        <label class="col-lg-2 control-label">前程无忧</label>
        <div class="col-lg-8">
            <textarea rows="3" class="form-control" id="h51" name="h51_cookies" class="form-control" placeholder="请输入前程无忧cookies">${configs['h51_cookies'].value}</textarea>
        </div>
    </div>
    <div class="form-group">
        <label class="col-lg-2 control-label">智联招聘</label>
        <div class="col-lg-8">
            <textarea rows="3" class="form-control" id="zhilian" name="zhilian_cookies" placeholder="请输入智联招聘cookies">${configs['zhilian_cookies'].value}</textarea>
        </div>
    </div>
    <div class="form-group">
        <label class="col-lg-2 control-label">ip地址</label>
        <div class="col-lg-8">
            <textarea rows="3" class="form-control" name="ip" placeholder="ip地址">${configs['ip'].value}</textarea>
        </div>
    </div>
    <div class="form-group">
        <div class="col-lg-9 col-lg-offset-3">
            <button type="submit" class="btn btn-primary btn-small">提交</button>
        </div>
    </div>
</form>
<script type="text/javascript">
    $(function () {
        var h51Cookie = $("#h51").val();
        var zhilianCookie = $("#zhilian").val();

        if(h51Cookie == ""){
            alert("前程无忧cookie已失效");
        }
        if(zhilianCookie == ""){
            alert("智联招聘cookie已失效");
        }
    })
</script>