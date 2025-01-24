<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>
    <script>

        $(function () {
            <c:if test="${save eq 'OK'}">
            alert("저장되었습니다.");
            </c:if>

            $("#photofile").change(function () {
                readImage(this);
            });
        });

        function readImage(input) {
            if (input.files && input.files[0]) {
                var reader = new FileReader();
                reader.onload = function (e) {
                    $('#previewImg').attr('src', e.target.result);
                }
                reader.readAsDataURL(input.files[0]);
            }
        }

        function fn_formSubmit() {
            if (!chkInputValue("#usernm", "<s:message code="common.name"/>")) return false;

            $("#form1").submit();
        }

        function fn_changePW() {
            $("#userpw").val("");
            $("#userpw2").val("");
            $('#myModal').modal("show");
        }

        function fn_changePWSave() {
            if (!chkInputValue("#userpw", "<s:message code="common.password"/>")) return false;
            if (!chkInputValue("#userpw2", "<s:message code="common.passwordRe"/>")) return false;
            if ($("#userpw").val() !== $("#userpw2").val()) {
                alert("<s:message code="msg.err.noMatchPW"/>");
                return false;
            }

            $.ajax({
                url: "changePWSave",
                type: "post",
                data: {userpw: $("#userpw").val()},
                success: function (result) {
                    if (result === "OK") {
                        alert("<s:message code="msg.changeComplete"/>");
                    }
                }
            })
            $("#myModal").modal("hide");
        }
    </script>
</head>

<body>

<div id="wrapper">
    <jsp:include page="../common/navigation.jsp"/>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header"><i class="fa fa-user fa-fw"></i> <s:message code="menu.profile"/></h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <div class="col-lg-7">
                <form id="form1" name="form1" role="form" action="userSave" method="post" enctype="multipart/form-data">
                    <div class="row form-group">
                        <div class="col-lg-1"></div>
                        <label class="col-lg-2"><s:message code="common.id"/></label>
                        <div class="col-lg-5">
                            <input type="text" class="form-control" id="userid" name="userid"
                                   maxlength="20" readonly="readonly" value="<c:out value="${userInfo.userid}"/>">
                        </div>
                    </div>
                    <div class="row form-group">
                        <div class="col-lg-1"></div>
                        <label class="col-lg-2"><s:message code="common.name"/></label>
                        <div class="col-lg-8">
                            <input type="text" class="form-control" id="usernm" name="usernm" maxlength="20" value="<c:out value="${userInfo.usernm}"/>">
                        </div>
                    </div>
                    <div class="row form-group">
                        <div class="col-lg-1"></div>
                        <div class="col-lg-2"><label><s:message code="common.photo"/></label></div>
                        <c:if test="${ !empty  userInfo.photo}">
                            <div class="col-sm-3">
                                <img id="previewImg" style="width:100%; height: 120px; max-width: 100px;" src="fileDownload?downname=<c:out value="${userInfo.photo}"/>">
                            </div>
                        </c:if>
                        <div class="col-lg-5">
                            <input type="file" name="photofile" id="photofile" accept='image/*'/>
                        </div>
                    </div>
                </form>
            </div>

        </div>
        <button type="button" class="btn btn-primary" onclick="fn_formSubmit()"><s:message code="common.btnSave"/></button>
        <button type="button" class="btn btn-primary" onclick="fn_changePW()"><s:message code="common.changePassword"/></button>
        <button type="button" class="btn btn-default" onclick="fn_moveToURL('mailInfoList')">메일 정보 등록</button>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->

<div id="myModal" style="display: none;" class="modal fade bs-example-modal-sm" role="dialog" tabindex="-1" aria-labelledby="mySmallModalLabel">
    <div class="modal-dialog modal-sm" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="mySmallModalLabel"><s:message code="common.changePassword"/></h4>
            </div>
            <div class="modal-body">
                <div class="row form-group">
                    <div class="col-lg-4"><label><s:message code="common.password"/></label></div>
                    <div class="col-sm-8">
                        <input type="password" class="form-control" id="userpw" name="userpw" maxlength="20">
                    </div>
                </div>
                <div class="row form-group">
                    <div class="col-lg-4"><label><s:message code="common.passwordRe"/></label></div>
                    <div class="col-sm-8">
                        <input type="password" class="form-control" id="userpw2" name="userpw2" maxlength="20">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal" id="close"><s:message code="common.btnClose"/></button>
                <button type="button" class="btn btn-primary" onclick="fn_changePWSave()"><s:message code="common.btnSave"/></button>
            </div>
        </div>
    </div>
</div>
</body>

<%@include file="../inc/footer.jsp" %>