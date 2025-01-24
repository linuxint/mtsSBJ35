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
                url: "adMenuRead",
                cache: false,
                data: {mnuNo: selectedNode.data.key}
            }).done(receiveData);
        }

        function receiveData(data) {
            if(data===null) {
                return;
            }
            console.log(data);
            $("#mnuNo").val(data.mnuNo);
            $("#mnuParent").val(data.mnuParent);
            $("#mnuNm").val(data.mnuNm);
            $("#mnuType").val(data.mnuType);
            $("#mnuTarget").val(data.mnuTarget);
            $("#mnuFilenm").val(data.mnuFilenm);
            $("#mnuImgpath").val(data.mnuImgpath);
            $("#mnuCustom").val(data.mnuCustom);
            $("#mnuDesc").val(data.mnuDesc);
            $('input:radio[name="mnuDesktop"][value="' + data.mnuDesktop + '"]').prop('checked', true);
            $('input:radio[name="mnuMobile"][value="' + data.mnuMobile + '"]').prop('checked', true);
            $('input:radio[name="mnuExtnConnYn"][value="' + data.mnuExtnConnYn + '"]').prop('checked', true);
            $("#mnuStartHour").val(data.mnuStartHour);
            $("#mnuEndHour").val(data.mnuEndHour);
            $("#mnuOrder").val(data.mnuOrder);
        }

        function fn_groupNew() {
            parentMenuKey = selectedNode!=null ? selectedNode.parent.data.key : "";
            $("#mnuNo").val("");
            $("#mnuParent").val(parentMenuKey);
            $("#mnuNm").val("");
            $("#mnuType").val("");
            $("#mnuTarget").val("");
            $("#mnuFilenm").val("");
            $("#mnuImgpath").val("");
            $("#mnuCustom").val("");
            $("#mnuDesc").val("");
            $('input:radio[name="mnuDesktop"][value="N"]').prop('checked', true);
            $('input:radio[name="mnuMobile"][value="N"]').prop('checked', true);
            $('input:radio[name="mnuExtnConnYn"][value="N"]').prop('checked', true);
            $("#mnuStartHour").val("");
            $("#mnuEndHour").val("");
            $("#mnuOrder").val("");
        }

        function fn_subGroupNew() {
            menuKey = selectedNode!=null ? selectedNode.data.key : "";
            $("#mnuNo").val("");
            $("#mnuParent").val(menuKey);
            $("#mnuNm").val("");
            $("#mnuType").val("");
            $("#mnuTarget").val("");
            $("#mnuFilenm").val("");
            $("#mnuImgpath").val("");
            $("#mnuCustom").val("");
            $("#mnuDesc").val("");
            $('input:radio[name="mnuDesktop"][value="N"]').prop('checked', true);
            $('input:radio[name="mnuMobile"][value="N"]').prop('checked', true);
            $('input:radio[name="mnuExtnConnYn"][value="N"]').prop('checked', true);
            $("#mnuStartHour").val("");
            $("#mnuEndHour").val("");
            $("#mnuOrder").val("");
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
                url: "adMenuDelete",
                cache: false,
                data: {mnuNo: selectedNode.data.key}
            }).done(receiveData4Delete);
        }

        function receiveData4Delete(data) {
            alert("<s:message code="msg.boardDelete"/>");
            selectedNode.remove();
            selectedNode = null;
            fn_groupNew();
        }

        function fn_groupSave() {
            if (!chkInputValue("#mnuNm", "<s:message code="common.menuName"/>")) return;

            var pid = null;
            if (selectedNode != null) pid = selectedNode.data.key;

            if (!confirm("<s:message code="ask.Save"/>")) return;

            $.ajax({
                url: "adMenuSave",
                cache: false,
                type: "POST",
                data: {
                    mnuNo: $("#mnuNo").val(),
                    mnuParent: $("#mnuParent").val(),
                    mnuNm: $("#mnuNm").val(),
                    mnuType: $("#mnuType").val(),
                    mnuTarget: $("#mnuTarget").val(),
                    mnuFilenm: $("#mnuFilenm").val(),
                    mnuImgpath: $("#mnuImgpath").val(),
                    mnuCustom: $("#mnuCustom").val(),
                    mnuDesc: $("#mnuDesc").val(),
                    mnuDesktop: $("input:radio[name=mnuDesktop]:checked").val(),
                    mnuMobile: $("input:radio[name=mnuMobile]:checked").val(),
                    mnuExtnConnYn: $("input:radio[name=mnuExtnConnYn]:checked").val(),
                    mnuStartHour: $("#mnuStartHour").val(),
                    mnuEndHour: $("#mnuEndHour").val(),
                    mnuOrder: $("#mnuOrder").val()
                }
            }).done(receiveData4Save);
        }

        function receiveData4Save(data) {
            if (selectedNode !== null && selectedNode.data.key === data.mnuNo) {
                selectedNode.data.title = data.mnuNm;
                selectedNode.render();
            } else {
                addNode(data.mnuNo, data.mnuNm);
            }

            alert("<s:message code="msg.boardSave"/>");
        }

        function addNode(nodeNo, nodeTitle) {
            var node = $("#tree").dynatree("getActiveNode");
            if (!node) node = $("#tree").dynatree("getRoot");
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
                <h1 class="page-header"><i class="fa fa-sitemap fa-fw"></i> <s:message code="menu.menu"/></h1>
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
                        <button class="btn btn-outline btn-primary" onclick="fn_subGroupNew()"><s:message code="menu.subappend"/></button>
                        <button class="btn btn-outline btn-primary" onclick="fn_groupNew()"><s:message code="menu.append"/></button>
                    </div>
                    <div class="row form-group">
                        <label class="col-lg-3">메뉴No</label>
                        <div class="col-lg-9">
                            <input name="mnuNo" id="mnuNo" type="readonly" value="">
                        </div>
                    </div>
                    <div class="row form-group">
                        <label class="col-lg-3">상위메뉴No</label>
                        <div class="col-lg-9">
                            <input name="mnuParent" id="mnuParent" style="width: 300px;" type="text" maxlength="100" value="" class="form-control">
                        </div>
                    </div>

                    <div class="row form-group">
                        <label class="col-lg-3"><s:message code="common.menuName"/></label>
                        <div class="col-lg-9">
                            <input name="mnuNm" id="mnuNm" style="width: 300px;" type="text" maxlength="100" value="" class="form-control">
                        </div>
                    </div>
                    <div class="row form-group">
                        <label class="col-lg-3">메뉴타입</label>
                        <div class="col-lg-9">
                            <input name="mnuType" id="mnuType" style="width: 300px;" type="text" maxlength="100" value="" class="form-control">
                        </div>
                    </div>
                    <div class="row form-group">
                        <label class="col-lg-3">메뉴링크</label>
                        <div class="col-lg-9">
                            <input name="mnuTarget" id="mnuTarget" style="width: 300px;" type="text" maxlength="100" value="" class="form-control">
                        </div>
                    </div>
                    <div class="row form-group">
                        <label class="col-lg-3">파일명</label>
                        <div class="col-lg-9">
                            <input name="mnuFilenm" id="mnuFilenm" style="width: 300px;" type="text" maxlength="100" value="" class="form-control">
                        </div>
                    </div>
                    <div class="row form-group">
                        <label class="col-lg-3">이미지경로</label>
                        <div class="col-lg-9">
                            <input name="mnuImgpath" id="mnuImgpath" style="width: 300px;" type="text" maxlength="100" value="" class="form-control">
                        </div>
                    </div>
                    <div class="row form-group">
                        <label class="col-lg-3">메뉴설명</label>
                        <div class="col-lg-9">
                            <textarea class="form-control" id="mnuDesc" name="mnuDesc"></textarea>
                        </div>
                    </div>
                    <div class="row form-group">
                        <label class="col-lg-3">커스텀태그</label>
                        <div class="col-lg-9">
                            <input name="mnuCustom" id="mnuCustom" style="width: 300px;" type="text" maxlength="100" value="" class="form-control">
                        </div>
                    </div>
                    <div class="row form-group">
                        <label class="col-lg-3">데스크탑사용메뉴</label>
                        <div class="col-lg-9 checkbox-inline">
                            <label><input name="mnuDesktop" id="mnuDesktop" type="radio" checked="checked" value="Y"><s:message code="board.usedY"/></label>
                            <label><input name="mnuDesktop" id="mnuDesktop" type="radio" value="N"><s:message code="board.usedN"/></label>
                        </div>
                    </div>
                    <div class="row form-group">
                        <label class="col-lg-3">모바일사용메뉴</label>
                        <div class="col-lg-9 checkbox-inline">
                            <label><input name="mnuMobile" id="mnuMobile" type="radio" checked="checked" value="Y"><s:message code="board.usedY"/></label>
                            <label><input name="mnuMobile" id="mnuMobile" type="radio" value="N"><s:message code="board.usedN"/></label>
                        </div>
                    </div>
                    <div class="row form-group">
                        <label class="col-lg-3">인증구분코드</label>
                        <div class="col-lg-9">
                            <input name="mnuCertType" id="mnuCertType" style="width: 300px;" type="text" maxlength="100" value="" class="form-control">
                        </div>
                    </div>
                    <div class="row form-group">
                        <label class="col-lg-3">외부연결구분</label>
                        <div class="col-lg-9 checkbox-inline">
                            <label><input name="mnuExtnConnYn" id="mnuExtnConnYn" type="radio" checked="checked" value="Y"><s:message code="board.usedY"/></label>
                            <label><input name="mnuExtnConnYn" id="mnuExtnConnYn" type="radio" value="N"><s:message code="board.usedN"/></label>
                        </div>
                    </div>
                    <div class="row form-group">
                        <label class="col-lg-3">메뉴사용시간</label>
                        <div class="col-lg-9">
                            <input name="mnuStartHour" id="mnuStartHour" style="width: 100px;" type="text" maxlength="10" value="" class="form-control">
                            ~<input name="mnuEndHour" id="mnuEndHour" style="width: 100px;" type="text" maxlength="10" value="" class="form-control">
                        </div>
                    </div>
                    <div class="row form-group">
                        <label class="col-lg-3">정렬순서</label>
                        <div class="col-lg-9">
                            <input name="mnuOrder" id="mnuOrder" style="width: 300px;" type="text" maxlength="100" value="" class="form-control">
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