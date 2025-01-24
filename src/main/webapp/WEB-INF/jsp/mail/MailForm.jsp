<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>
    <script>
        window.onload = function () {
            CKEDITOR.replace('emcontents', {'filebrowserUploadUrl': 'upload4ckeditor'});
        }

        function fn_formSubmit() {
            if (!chkInputValue("#strTo", "받는 사람 이메일 주소")) return false;
            if (!chkInputValue("#emsubject", "제목")) return false;

            CKEDITOR.instances["emcontents"].updateElement();
            if (!chkInputValue("#emcontents", "내용")) return false;

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
                <h1 class="page-header"><i class="fa fa-envelope-o fa-fw"></i> 메일작성</h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <form id="form1" name="form1" role="form" action="mailSave" method="post" onsubmit="return fn_formSubmit();">
                <input type="hidden" name="emno" value="<c:out value="${mailInfo.emno}"/>">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <div class="row form-group">
                            <label class="col-lg-2">보내는 사람</label>
                            <div class="col-lg-4">
                                <select id="emfrom" name="emfrom" class="form-control">
                                    <c:forEach var="listview" items="${mailInfoList}" varStatus="status">
                                        <option value="${listview.emino}">${listview.usernm} &lt;${listview.emiuser}&gt;</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-2">받는 사람</label>
                            <div class="col-lg-10">
                                <input type="text" class="form-control" id="strTo" name="strTo">
                            </div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-2">참조</label>
                            <div class="col-lg-10">
                                <input type="text" class="form-control" id="strCc" name="strCc">
                            </div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-2">숨은 참조</label>
                            <div class="col-lg-10">
                                <input type="text" class="form-control" id="strBcc" name="strBcc">
                            </div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-2">제목</label>
                            <div class="col-lg-10">
                                <input type="text" class="form-control" id="emsubject" name="emsubject" maxlength="255" value="<c:out value="${mailInfo.emsubject}"/>">
                            </div>
                        </div>
                        <div class="row form-group">
                            <div class="col-lg-12">
                                <textarea class="form-control" id="emcontents" name="emcontents"><c:out value="${mailInfo.emcontents}"/></textarea>
                            </div>
                        </div>
                    </div>
                </div>
                <button class="btn btn-outline btn-primary">보내기</button>
            </form>

        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->
</body>

<%@include file="../inc/footer.jsp" %>
