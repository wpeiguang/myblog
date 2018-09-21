<#include "/admin/message.ftl">
<#include "/admin/utils/ui.ftl"/>
<meta http-equiv="refresh" content="600">
<@layout>
<div class="row">
    <div class="col-md-12 col-sm-12 col-xs-12">
        <div class="x_panel">
            <div class="x_title">
                <h2>推广管理</h2>
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
                        <label class="control-label">项目</label>
                        <select class="form-control" id="project" name="project" value="${post.project}">
                            <option value="0">请选择</option>
                            <option value="鱼矿池">鱼矿池</option>
                        </select>
                        <label style="width:20px"></label>
                    </div>
                    <div class="form-group">
                        <label class="control-label">推广码</label>
                        <input type="text" name="code" class="form-control" value="${post.code}" placeholder="请输入推广码">
                        <label style="width:20px"></label>
                    </div>
                    <div class="form-group">
                        <label class="control-label">时间间隔</label>
                        <input type="text" name="interval" class="form-control" value="${post.inteval}" placeholder="请输入时间间隔">
                        <label class="control-label">秒</label>
                        <label style="width:20px"></label>
                    </div>
                    <div class="form-group">
                        <label class="control-label">数量</label>
                        <input type="text" name="count" class="form-control" value="${post.amount}" placeholder="请输入推广数量">
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
                        <th>项目</th>
                        <th>推广码</th>
                        <th>时间间隔</th>
                        <th>数量</th>
                        <th>已完成量</th>
                        <th>剩余量</th>
                        <th align="center" width="200">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                        <#list page.content as row>
                        <tr>
                            <td>
                                <input type="checkbox" name="id" value="${row.id}">
                            </td>
                            <td>${row.project}</td>
                            <td>${row.code}</td>
                            <td>${row.inteval}</td>
                            <td>${row.amount}</td>
                            <td>${row.successCount}</td>
                            <td>${row.remainCount}</td>
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
        J.getJSON('${base}/admin/generalize/delete_task', J.param({'id': ids}, true), winReload);
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
            J.getJSON('${base}/admin/generalize/start_task', J.param({'id': that.attr('data-id')}, true), winReload);
        });

        $('#dataGrid a[data-action="stop"]').bind('click', function () {
            var that = $(this);
            J.getJSON('${base}/admin/generalize/stop_task', J.param({'id': that.attr('data-id')}, true), winReload);
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
    })
</script>
</@layout>