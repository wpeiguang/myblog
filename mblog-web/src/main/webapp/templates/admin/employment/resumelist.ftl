<#include "/admin/message.ftl">
<#include "/admin/utils/ui.ftl"/>
<meta http-equiv="refresh" content="600">
<@layout>
<div class="row">
    <div class="col-md-12 col-sm-12 col-xs-12">
        <div class="x_panel">
            <div class="x_title">
                <h2>简历列表</h2>
                <ul class="nav navbar-right panel_toolbox">
                    <#--<@shiro.hasPermission name="comments:edit">-->
                        <#--<li><a href="javascrit:void(0);" data-action="batch_del">批量删除</a>-->
                        <#--</li>-->
                    <#--</@shiro.hasPermission>-->
                </ul>
                <div class="clearfix"></div>
            </div>
            <div class="x_content">
                <#--<form id="qForm" class="form-inline">-->
                    <#--<input type="hidden" name="pn" value="${resumes.mobile}"/>-->
                    <#--<div class="form-group">-->
                        <#--<input type="text" name="key" class="form-control" value="${key}" placeholder="请输入手机号">-->
                    <#--</div>-->
                    <#--<button type="submit" class="btn btn-default">搜索</button>-->
                <#--</form>-->
            </div>
            <div class="x_content">
                <table id="dataGrid" class="table table-striped table-bordered b-t">
                    <thead>
                    <tr>
                        <th width="50"><input type="checkbox" class="checkall">
                        </th>
                        <th>姓名</th>
                        <th>职位</th>
                        <th>手机号</th>
                        <th>简历ID</th>
                        <th>锁定日期</th>
                        <th>来源</th>
                        <th width="200">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                        <#list resumes.content as row>
                        <tr>
                            <td>
                                <input type="checkbox" name="id" value="${row.id}">
                            </td>
                            <td>${row.name}</td>
                            <td>${row.job}</td>
                            <td>${row.mobile}</td>
                            <td>${row.resumeId}</td>
                            <td>${row.lockDate}</td>
                            <td>${row.source}</td>
                            <td class="text-center" align="center">
                                <a href="javascript:void(0);" class="btn btn-xs btn-white" data-id="${row.id}"
                                   data-action="delete_resume">
                                    <i class="fa fa-bitbucket"></i> 删除
                                </a>
                            </td>
                        </tr>
                        </#list>
                    </tbody>
                </table>
                <@pager "resumelist" resumes 5 />
            </div>
        </div>
    </div>
</div>
<div style="display: none;">
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
        <label class="col-lg-2 control-label">51job是否已人工验证</label>
        <div class="col-lg-8">
            <textarea rows="3" class="form-control" id="verify" placeholder="verify">${configs['verify'].value}</textarea>
        </div>
    </div>
</div>
<script type="text/javascript">
    var J = jQuery;

    function winReload(json) {
        if (json.code >= 0) {
            if (json.message != null && json.message != '') {
                layer.msg(json.message, {icon: 1});
            }
            <#--window.location.href = "${base}/admin/employment/";-->
            self.location.reload();
        } else {
            layer.msg(json.message, {icon: 2});
        }
    }

    function doDelete(ids) {
        J.getJSON('${base}/admin/employment/delete_resume', J.param({'id': ids}, true), winReload);
    }

    $(function () {
        // 删除
        $('#dataGrid a[data-action="delete_resume"]').bind('click', function () {
            var that = $(this);
            layer.confirm('确定删除此项吗?', {
                btn: ['确定', '取消'], //按钮
                shade: false //不显示遮罩
            }, function () {
                doDelete(that.attr('data-id'));
            }, function () {
            });
            return false;
        });

        var h51Cookie = $("#h51").val();
        var zhilianCookie = $("#zhilian").val();
        var verify = $("#verify").val();
        if(h51Cookie == ""){
            alert("前程无忧cookie已失效");
        }
        if(zhilianCookie == ""){
            alert("智联招聘cookie已失效");
        }
        if(verify == "1"){
            alert("51job简历查看需要人工验证码");
        }
    });
</script>
</@layout>