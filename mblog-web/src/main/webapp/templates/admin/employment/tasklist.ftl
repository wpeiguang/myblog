<#include "/admin/message.ftl">
<#include "/admin/message.ftl">
<#include "/admin/utils/ui.ftl"/>
<meta http-equiv="refresh" content="600">
<@layout>
<div class="row">
    <div class="col-md-12 col-sm-12 col-xs-12">
        <div class="x_panel">
            <div class="x_title">
                <h2>任务列表</h2>
                <ul class="nav navbar-right panel_toolbox">
                <@shiro.hasPermission name="comments:edit">
                    <li><a href="javascrit:void(0);" data-action="batch_del">批量删除</a>
                    </li>
                </@shiro.hasPermission>
                </ul>
                <div class="clearfix"></div>
            </div>
            <div class="x_content">
                <form id="addForm" class="form-inline" method="post" action="add_task">
                    <input type="hidden" name="pn" value="${page.pageNo}"/>
                    <div class="form-group">
                        <label class="control-label">职位</label>
                        <input type="text" name="jobKey" class="form-control" value="${post.jobkey}" placeholder="请输入职位关键字">
                        <label style="width:20px"></label>
                    </div>
                    <div class="form-group">
                        <label class="control-label">排除关键字</label>
                            <input type="text" name="excludeKey" class="form-control" value="${post.excludeKey}" placeholder="请输入职位排除关键字">
                        <label style="width:20px"></label>
                    </div>
                    <div class="form-group">
                        <label class="control-label">学历</label>
                        <select class="form-control" id="degree" name="degree" value="${post.degree}">
                            <option value="硕士,211/985本科,211本科,985本科,二本（正式）,大学本科,硕士研究生,专科,大专">全部</option>
                            <option value="硕士,211/985本科,211本科,985本科,二本（正式）,三本,大学本科">本科</option>
                            <option value="硕士,211/985本科,211本科,985本科,二本（正式）,大学本科,硕士研究生,专科,大专">专科</option>
                        </select>
                        <label style="width:20px"></label>
                    </div>
                    <div class="form-group">
                        <label class="control-label">性别</label>
                        <select class="form-control" id="sex" name="sex" value="${post.sex}">
                            <option value="男女">全部</option>
                            <option value="男">男</option>
                            <option value="女">女</option>
                        </select>
                        <label style="width:20px"></label>
                    </div>
                    <div class="form-group">
                        <label class="control-label">工作年限</label>
                        <select class="form-control" id="workLimit" name="workLimit" value="${post.workLimit}">
                            <option value="0">无限制</option>
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                            <option value="4">4</option>
                            <option value="5">5</option>
                            <option value="6">6</option>
                            <option value="7">7</option>
                            <option value="8">8</option>
                            <option value="9">9</option>
                            <option value="10">10</option>
                        </select>
                        <label style="width:20px"></label>
                    </div>
                    <button type="submit" class="btn btn-default pull-right">添加</button>
                </form>
            </div>
            <div class="x_content">
                <table id="dataGrid" class="table table-striped table-bordered b-t">
                    <thead>
                    <tr>
                        <th width="50"><input type="checkbox" class="checkall">
                        </th>
                        <th>职位</th>
                        <th>排除关键字</th>
                        <th>学历</th>
                        <th>性别</th>
                        <th>工作年限</th>
                        <th>状态</th>
                        <th align="center" width="200">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list page.content as row>
                    <tr>
                        <td>
                            <input type="checkbox" name="id" value="${row.id}">
                        </td>
                        <td class="text-center">${row.jobKey}</td>
                        <td>${row.excludeKey}</td>
                        <td>${row.degree}</td>
                        <td>${row.sex}</td>
                        <td>${row.workLimit}</td>
                        <td>${row.status}</td>
                        <td class="text-center" align="left">
                            <a href="javascript:void(0);" class="btn btn-xs btn-white" data-id="${row.id}"
                               data-action="start">
                                <i class="fa fa-bitbucket"></i> 启动
                            </a>
                            <a href="javascript:void(0);" class="btn btn-xs btn-white" data-id="${row.id}"
                               data-action="stop">
                                <i class="fa fa-bitbucket"></i> 停止
                            </a>
                            <a href="javascript:void(0);" class="btn btn-xs btn-white" data-id="${row.id}"
                               data-action="delete">
                                <i class="fa fa-bitbucket"></i> 删除
                            </a>
                        </td>
                    </tr>
                    </#list>
                    </tbody>
                </table>
            <@pager "tasklist" page 5 />
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
            window.location.reload();
        } else {
            layer.msg(json.message, {icon: 2});
        }
    }

    function doDelete(ids) {
        J.getJSON('${base}/admin/employment/delete_task', J.param({'id': ids}, true), winReload);
    }

    $(function () {
        // 删除
        $('#dataGrid a[data-action="delete"]').bind('click', function () {
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

        $('#dataGrid a[data-action="start"]').bind('click', function () {
            var that = $(this);
            J.getJSON('${base}/admin/employment/start_task', J.param({'id': that.attr('data-id')}, true), winReload);
        });

        $('#dataGrid a[data-action="stop"]').bind('click', function () {
            var that = $(this);
            J.getJSON('${base}/admin/employment/stop_task', J.param({'id': that.attr('data-id')}, true), winReload);
        });


        $('a[data-action="batch_del"]').click(function () {
            var check_length = $("input[type=checkbox][name=id]:checked").length;

            if (check_length == 0) {
                layer.msg("请至少选择一项", {icon: 2});
                return false;
            }

            var ids = [];
            $("input[type=checkbox][name=id]:checked").each(function () {
                ids.push($(this).val());
            });

            layer.confirm('确定删除此项吗?', {
                btn: ['确定', '取消'], //按钮
                shade: false //不显示遮罩
            }, function () {
                doDelete(ids);
            }, function () {
            });
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
    })
</script>
</@layout>