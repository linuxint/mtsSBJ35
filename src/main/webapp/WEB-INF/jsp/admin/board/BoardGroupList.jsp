<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/ad_header.jsp" %>

    <script>
        var selectedNode = null;

        $(function () {
            $("#tree").dynatree({
                children: <c:out value="${treeStr}" escapeXml="false"/>,
                onActivate: TreenodeActivate
            });
            $("#tree").dynatree("getRoot").visit(function (node) {
                node.expand(true);
            });
            fn_groupNew();
        });

        function TreenodeActivate(node) {
            selectedNode = node;

            if (selectedNode == null || selectedNode.data.key == 0) return;
            $.ajax({
                url: "adBoardGroupRead",
                cache: false,
                data: {bgno: selectedNode.data.key}
            }).done(receiveData);
        }

        function receiveData(data) {
            $("#bgno").val(data.bgno);
            $("#bgname").val(data.bgname);
            $('input:radio[name="bgused"][value="' + data.bgused + '"]').prop('checked', true);
            $('input:radio[name="bgreadonly"][value="' + data.bgreadonly + '"]').prop('checked', true);
            $('input:radio[name="bgreply"][value="' + data.bgreply + '"]').prop('checked', true);
            $('input:radio[name="bgnotice"][value="' + data.bgnotice + '"]').prop('checked', true);
        }

        function fn_groupNew() {
            $("#bgno").val("");
            $("#bgname").val("");
            $('input:radio[name="bgused"][value="Y"]').prop('checked', true);
            $('input:radio[name="bgreadonly"][value="N"]').prop('checked', true);
            $('input:radio[name="bgreply"][value="Y"]').prop('checked', true);
            $('input:radio[name="bgnotice"][value="Y"]').prop('checked', true);
        }

        function fn_groupDelete(value) {
            if (selectedNode == null) {
                alert("<s:message code="msg.err.boardDelete"/>");
                return;
            }
            if (selectedNode.childList) {
                alert("<s:message code="msg.err.boardDelete4Child"/>");
                return;
            }

            if (!confirm("<s:message code="ask.Delete"/>")) return;
            $.ajax({
                url: "adBoardGroupDelete",
                cache: false,
                data: {bgno: selectedNode.data.key}
            }).done(function (response) {
                receiveData4Delete(response);
            });
        }

        function receiveData4Delete(response) {
            if(response === "OK") {
                alert("<s:message code="msg.boardDelete"/>");
                selectedNode.remove();
                selectedNode = null;
                fn_groupNew();
            } else {
                // JSON 응답 데이터를 그대로 alert
                alert(JSON.stringify(response, null, 2)); // Pretty Print 형식으로 출력
            }
        }

        function fn_groupSave() {
            if ($("#bgname").val() == "") {
                alert("<s:message code="msg.boardInputName"/>");
                return;
            }
            var pid = null;
            if (selectedNode != null) {
                pid = selectedNode.data.key;
            }

            if (!confirm("<s:message code="ask.Save"/>")) {
                return;
            }

            $.ajax({
                url: "adBoardGroupSave",
                cache: false,
                type: "POST",
                data: {
                    bgno: $("#bgno").val(), bgname: $("#bgname").val(), bgparent: pid,
                    bgused: $("input:radio[name=bgused]:checked").val(),
                    bgreadonly: $("input:radio[name=bgreadonly]:checked").val(),
                    bgreply: $("input:radio[name=bgreply]:checked").val(),
                    bgnotice: $("input:radio[name=bgnotice]:checked").val()
                }
            }).done(receiveData4Save);
        }

        function receiveData4Save(data) {
            if (selectedNode !== null && selectedNode.data.key === data.bgno) {
                selectedNode.data.title = data.bgname;
                selectedNode.render();
            } else {
                addNode(data.bgno, data.bgname);
            }

            alert("<s:message code="msg.boardSave"/>");
        }

        function addNode(nodeNo, nodeTitle) {
            var node = $("#tree").dynatree("getActiveNode");
            if (!node) {
                node = $("#tree").dynatree("getRoot");
            }
            var childNode = node.addChild({key: nodeNo, title: nodeTitle});
            node.expand();
            node.data.isFolder = true;
            node.tree.redraw();
        }
    </script>
</head>

<body>

<div id="wrapper">
    <jsp:include page="../../common/navigation.jsp"/>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header"><i class="fa fa-files-o fa-fw"></i> <s:message code="menu.board"/></h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <div class="panel panel-default col-lg-3">
                <div style="max-height:400px; overflow:auto;">
                    <div id="tree">
                    </div>
                </div>
            </div>

            <div class="panel panel-default col-lg-6">
                <div class="panel-body">
                    <div class="row form-group">
                        <button class="btn btn-outline btn-primary" onclick="fn_groupNew()"><s:message code="board.append"/></button>
                    </div>
                    <input name="bgno" id="bgno" type="hidden" value="">
                    <div class="row form-group">
                        <label class="col-lg-3"><s:message code="board.groupname"/></label>
                        <div class="col-lg-9">
                            <input name="bgname" id="bgname" type="text" maxlength="100" value="" class="form-control">
                        </div>
                    </div>
                    <div class="row form-group">
                        <label class="col-lg-3"><s:message code="board.used"/></label>
                        <div class="col-lg-9 checkbox-inline">
                            <label><input name="bgused" id="bgused" type="radio" checked="checked" value="Y"><s:message code="board.usedY"/></label>
                            <label><input name="bgused" id="bgused" type="radio" value="N"><s:message code="board.usedN"/></label>
                        </div>
                    </div>
                    <div class="row form-group">
                        <label class="col-lg-3"><s:message code="board.readonly"/></label>
                        <div class="col-lg-9 checkbox-inline">
                            <label><input name="bgreadonly" id="bgreadonly" type="radio" checked="checked" value="N"><s:message code="board.usedY"/></label>
                            <label><input name="bgreadonly" id="bgreadonly" type="radio" value="Y"><s:message code="board.usedN"/></label>
                        </div>
                    </div>
                    <div class="row form-group">
                        <label class="col-lg-3"><s:message code="common.btnReply"/></label>
                        <div class="col-lg-9 checkbox-inline">
                            <label><input name="bgreply" id="bgreply" type="radio" checked="checked" value="Y"><s:message code="board.usedY"/></label>
                            <label><input name="bgreply" id="bgreply" type="radio" value="N"><s:message code="board.usedN"/></label>
                        </div>
                    </div>
                    <div class="row form-group">
                        <label class="col-lg-3"><s:message code="common.notice"/></label>
                        <div class="col-lg-9 checkbox-inline">
                            <label><input name="bgnotice" id="bgnotice" type="radio" checked="checked" value="Y"><s:message code="board.usedY"/></label>
                            <label><input name="bgnotice" id="bgnotice" type="radio" value="N"><s:message code="board.usedN"/></label>
                        </div>
                    </div>

                    <div class="row form-group">
                        <button class="btn btn-outline btn-primary" onclick="fn_groupSave()"><s:message code="common.btnSave"/></button>
                        <button class="btn btn-outline btn-primary" onclick="fn_groupDelete()"><s:message code="common.btnDelete"/></button>
                    </div>
                </div>
            </div>
        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->
</body>

<%@include file="../inc/ad_footer.jsp" %>
