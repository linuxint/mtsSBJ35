<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/ad_header.jsp" %>

    <script>
        window.onload = function () {
            CKEDITOR.replace('dtcontents', {'filebrowserUploadUrl': 'upload4ckeditor'});
        }

        function fn_formSubmit() {
            CKEDITOR.instances["dtcontents"].updateElement();

            if (!chkInputValue("#dttitle", "문서양식명")) return false;

            $("#form1").submit();
        }
    </script>
</head>
<body>

<div id="wrapper">

    <jsp:include page="../../common/navigation.jsp"/>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header"><i class="fa fa-edit fa-fw"></i> 결재문서양식</h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <form id="form1" name="form1" role="form" action="adSignDocTypeSave" method="post">
                <input type="hidden" name="dtno" value="<c:out value="${signInfo.dtno}"/>">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <div class="row form-group">
                            <label class="col-lg-2">문서양식명</label>
                            <div class="col-lg-8">
                                <input type="text" class="form-control" id="dttitle" name="dttitle" maxlength="255"
                                       value="<c:out value="${signInfo.dttitle}"/>">
                            </div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-2">문서양식내용</label>
                            <div class="col-lg-8">
                                <textarea class="form-control" id="dtcontents" name="dtcontents"><c:out value="${signInfo.dtcontents}"/></textarea>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
            <button class="btn btn-outline btn-primary" onclick="fn_formSubmit();"><s:message code="common.btnSave"/></button>
            <c:if test="${signInfo.dtno!=null}">
                <button class="btn btn-outline btn-primary" onclick="fn_moveToURL('adSignDocTypeDelete?dtno=<c:out value="${signInfo.dtno}"/>', '<s:message code="common.btnDelete"/>')"><s:message code="common.btnDelete"/></button>
            </c:if>
        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->
</body>

<%@include file="../inc/ad_footer.jsp" %>