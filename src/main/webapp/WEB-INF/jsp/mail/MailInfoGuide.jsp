<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>

</head>

<body>

<div id="wrapper">

    <jsp:include page="../common/navigation.jsp"/>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header"><i class="fa fa-envelope-o fa-fw"></i> 메일정보관리</h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <div class="panel panel-default">
                <div class="panel-body">
                    <div class="alert alert-info">
                        등록된 메일 서버 정보가 없습니다.<p>&nbsp;</p>
                        먼저, 서버 정보를 등록해 주세요 <p>&nbsp;</p> <a href="mailInfoList" class="alert-link">메일 정보 등록하러 가기</a>.
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

<%@include file="../inc/footer.jsp" %>
