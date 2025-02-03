<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/ad_header.jsp" %>
    <script>
        function fn_formSubmit() {
            document.form1.submit();
        }

        function fn_uploadFile() {
            // 파일 업로드를 위한 FormData 객체 준비
            const formData = new FormData(document.getElementById('uploadForm'));

            // 파일 입력 값 확인
            const fileInput = document.getElementById('fileInput');
            if (!fileInput.value) {
                alert("<s:message code='common.file.notselected'/>");
                return;
            }

            // AJAX를 이용해 파일 업로드 실행
            $.ajax({
                url: '/api/v1/code/upload', // 파일 업로드 API 엔드포인트
                type: 'POST',
                data: formData,
                processData: false, // FormData 사용 시 false로 설정
                contentType: false, // FormData 사용 시 false로 설정
                success: function (response) {
                    alert(response); // 서버에서 반환된 메시지 출력
                    $('#uploadModal').modal('hide'); // 모달 닫기
                    location.reload(); // 페이지 새로고침
                },
                error: function (error) {
                    console.error(error);
                    alert("<s:message code='common.upload.error'/>");
                }
            });
        }

    </script>

</head>

<body>

<div id="wrapper">

    <jsp:include page="../../common/navigation.jsp"/>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header"><i class="fa fa-gear fa-fw"></i> <s:message code="common.codecd"/></h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <div class="col-lg-12">

                <button type="button" class="btn btn-default pull-right" onclick="fn_moveToURL('adCodeForm')">
                    <i class="fa fa-edit fa-fw"></i> <s:message code="common.codecd"/>
                </button>

                <!-- 새로운 엑셀 다운로드 버튼 -->
                <button type="button" class="btn btn-default pull-right" onclick="fn_moveToURL('adCodeListExcel')">
                    <i class="fa fa-download fa-fw"></i> <s:message code="common.excel.download"/>
                </button>

                <button type="button" class="btn btn-primary pull-right" data-toggle="modal" data-target="#uploadModal">
                    <i class="fa fa-upload fa-fw"></i> <s:message code="common.excel.upload"/>
                </button>

            </div>
        </div>
        <!-- /.row -->
        <div class="panel panel-default">
            <div class="panel-body">
                <div class="listHead">
                    <div class="listHiddenField pull-left field60"><s:message code="common.classno"/></div>
                    <div class="listHiddenField pull-left field100"><s:message code="common.codecd"/></div>
                    <div class="listTitle"><s:message code="common.codenm"/></div>
                </div>

                <c:if test="${listview.size()==0}">
                    <div class="listBody height200">
                    </div>
                </c:if>
                <c:forEach var="listview" items="${listview}" varStatus="status">
                    <c:url var="link" value="adCodeRead">
                        <c:param name="classno" value="${listview.classno}"/>
                        <c:param name="codecd" value="${listview.codecd}"/>
                    </c:url>

                    <div class="listBody">
                        <div class="listHiddenField pull-left field60 textCenter"><c:out value="${listview.classno}"/></div>
                        <div class="listHiddenField pull-left field100 textCenter"><c:out value="${listview.codecd}"/></div>
                        <div class="listTitle" title="<c:out value="${listview.codenm}"/>">
                            <a href="${link}"><c:out value="${listview.codenm}"/></a>
                        </div>
                    </div>
                </c:forEach>

                <br/>
                <form role="form" id="form1" name="form1" method="post">
                    <jsp:include page="../../common/pagingforSubmit.jsp"/>

                    <div class="form-group">
                        <div class="checkbox col-lg-3 pull-left">
                            <label class="pull-right">
                                <input type="checkbox" name="searchType" value="codenm" <c:if test="${fn:indexOf(searchVO.searchType, 'codenm')!=-1}">checked="checked"</c:if>/>
                                <s:message code="common.codenm"/>
                            </label>
                        </div>
                        <div class="input-group custom-search-form col-lg-3">
                            <input class="form-control" placeholder="Search..." type="text" name="searchKeyword"
                                   value='<c:out value="${searchVO.searchKeyword}"/>'>
                            <span class="input-group-btn">
                                    <button class="btn btn-default" onclick="fn_formSubmit()">
                                        <i class="fa fa-search"></i>
                                    </button>
                                </span>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>

<!-- 파일 업로드 레이어(모달) -->
<div class="modal fade" id="uploadModal" tabindex="-1" role="dialog" aria-labelledby="uploadModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <!-- 모달 헤더 -->
            <div class="modal-header">
                <h5 class="modal-title" id="uploadModalLabel"><s:message code="common.excel.upload"/></h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <!-- 모달 바디 -->
            <div class="modal-body">
                <form id="uploadForm" method="post" enctype="multipart/form-data">
                    <div class="form-group">
                        <label for="fileInput"><s:message code="common.file.select"/></label>
                        <input type="file" class="form-control" id="fileInput" name="file" />
                    </div>
                </form>
            </div>
            <!-- 모달 푸터 -->
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal"><s:message code="common.cancel"/></button>
                <button type="button" class="btn btn-primary" onclick="fn_uploadFile()"><s:message code="common.upload"/></button>
            </div>
        </div>
    </div>
</div>

<!-- /#wrapper -->
</body>

<%@include file="../inc/ad_footer.jsp" %>