<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>
    <script>
        window.onload = function () {
            ClassicEditor
                .create(document.querySelector('#brdmemo'), {
                    ckfinder: {
                        uploadUrl: 'upload4ckeditor'
                    }
                })
                .then(editor => {
                    console.log('CKEditor 5 initialized', editor);
                })
                .catch(error => {
                    console.error('CKEditor 5 load error', error);
                });
        };
        function fn_formSubmit() {
            CKEDITOR.instances["brdmemo"].updateElement();

            if (!chkInputValue("#brdtitle", "<s:message code="board.title"/>")) return false;
            if (!chkInputValue("#brdmemo", "<s:message code="board.contents"/>")) return false;

            $("#form1").submit();
        }
    </script>

</head>

<body>

<div id="wrapper">

    <jsp:include page="../common/navigation.jsp"/>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header"><i class="fa fa-files-o fa-fw"></i> <c:out value="${bgInfo.bgname}"/></h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <form id="form1" name="form1" role="form" action="boardSave" method="post" enctype="multipart/form-data" onsubmit="return fn_formSubmit();">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <div class="row form-group">
                            <label class="col-lg-1"><s:message code="board.title"/></label>
                            <div class="col-lg-9">
                                <input type="text" class="form-control" id="brdtitle" name="brdtitle" size="70" maxlength="250" value="<c:out value="${boardInfo.brdtitle}"/>">
                                <c:if test="${bgInfo.bgnotice=='Y'}">
                                    <label>
                                        <input type="checkbox" name="brdnotice" value="Y" <c:if test="${boardInfo.brdnotice=='Y'}">checked="checked"</c:if>/>
                                        <s:message code="common.notice"/>
                                    </label>
                                </c:if>
                            </div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-1"><s:message code="board.contents"/></label>
                            <div class="col-lg-9">
                                <textarea id="brdmemo" class="form-control" name="brdmemo" rows="10" cols="60"><c:out value="${boardInfo.brdmemo}"/></textarea>
                            </div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-1"><s:message code="board.file"/></label>
                            <div class="col-lg-9">
                                <c:forEach var="listview" items="${listview}" varStatus="status">
                                    <input type="checkbox" name="fileno" value="<c:out value="${listview.fileno}"/>">
                                    <a href="fileDownload?filename=<c:out value="${listview.filename}"/>&downname=<c:out value="${listview.realname }"/>">
                                        <c:out value="${listview.filename}"/></a> <c:out value="${listview.size2String()}"/><br/>
                                </c:forEach>

                                <input type="file" name="uploadfile" multiple="multiple"/>
                            </div>
                        </div>
                    </div>
                </div>
                <button class="btn btn-outline btn-primary"><s:message code="common.btnSave"/></button>
                <input type="hidden" name="bgno" value="<c:out value="${bgno}"/>">
                <input type="hidden" name="brdno" value="<c:out value="${boardInfo.brdno}"/>">
            </form>

        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->
</body>

<%@include file="../inc/footer.jsp" %>
