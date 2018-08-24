<#include "/admin/utils/ui.ftl"/>
<meta http-equiv="refresh" content="600">
<@layout>

<div class="row">
    <div class="col-md-12 col-sm-12 col-xs-12">
        <div class="x_panel">
            <div class="x_title">
                <h2>招聘管理</h2>
                <div class="clearfix"></div>
            </div>
            <div class="x_content">

                <div class="col-xs-1">
                    <!-- required for floating -->
                    <!-- Nav tabs -->
                    <ul class="nav nav-tabs tabs-left">
                        <li class="active"><a href="#tasklist" data-toggle="tab" aria-expanded="true">任务列表</a>
                        </li>
                        <li class=""><a href="#resumelist" data-toggle="tab" aria-expanded="false">简历列表</a>
                        </li>
                        <li class=""><a href="#set" data-toggle="tab" aria-expanded="false">设置</a>
                        </li>
                    </ul>
                </div>

                <div class="col-xs-11">
                    <!-- Tab panes -->
                    <div class="tab-content">
                        <div class="tab-pane active" id="tasklist">
                            <#include "/admin/employment/tasklist.ftl">
                        </div>
                        <div class="tab-pane" id="resumelist">
                            <#include "/admin/employment/resumelist.ftl">
                        </div>
                        <div class="tab-pane" id="set">
                            <#include "/admin/employment/set.ftl">
                        </div>
                    </div>
                </div>

                <div class="clearfix"></div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">

</script>
</@layout>