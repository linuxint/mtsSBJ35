<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>
    <script>

        function fn_formSubmit() {
            document.form1.submit();
        }

        function fnCheckAll() {
            var chk = $("#chkall").is(":checked");
            $('input:checkbox[name="checkRow"]').each(function () {
                this.checked = chk;
            })
        }


    </script>

</head>

<body>

<div id="wrapper">

    <jsp:include page="../common/navigation.jsp"/>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header"><i class="fa fa-envelope-o fa-fw"></i> 보낸 메일</h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <div class="col-lg-12">
                <button type="button" class="btn btn-default pull-right" onclick="form2.submit()">
                    <i class="fa fa-times fa-fw"></i> <s:message code="common.btnDelete"/></button>
            </div>
        </div>
        <!-- /.row -->
        <div class="panel panel-default">
            <div class="panel-body">
                <div class="listHead">
                    <div class="listHiddenField pull-right field130">보낸 날짜</div>
                    <div class="listHiddenField pull-right field130">받는 사람</div>
                    <div class="listHiddenField pull-left">
                        <input id="chkall" name="chkall" title="모두선택" onclick="fnCheckAll()" type="checkbox">
                    </div>
                    <div class="listTitle">제목</div>
                </div>

                <c:if test="${listview.size()==0}">
                    <div class="listBody height200">
                    </div>
                </c:if>

                <form role="form" id="form2" name="form2" method="post" action="sendMailsDelete">
                    <c:forEach var="listview" items="${listview}" varStatus="status">
                        <c:url var="link" value="sendMailRead">
                            <c:param name="emno" value="${listview.emno}"/>
                        </c:url>

                        <div class="listBody">
                            <div class="listHiddenField pull-right field130 textCenter"><c:out value="${listview.regdate}"/></div>
                            <div class="listHiddenField pull-right field130 textCenter"><c:out value="${listview.strTo}"/></div>
                            <div class="listTitle" title="<c:out value="${listview.emsubject}"/>">
                                <input type="checkbox" name="checkRow" value="<c:out value="${listview.emno}"/>"/> &nbsp;
                                <a href="${link}"><c:out value="${listview.emsubject}"/></a>
                            </div>
                        </div>
                    </c:forEach>
                </form>

                <br/>
                <form role="form" id="form1" name="form1" method="post">
                    <jsp:include page="../common/pagingforSubmit.jsp"/>

                    <div class="form-group">
                        <div class="checkbox col-lg-3 pull-left">
                            <label class="pull-right">
                                <input type="checkbox" name="searchType" value="emsubject" <c:if test="${fn:indexOf(searchVO.searchType, 'emsubject')!=-1}">checked="checked"</c:if>/>
                                제목
                            </label>
                            <label class="pull-right">
                                <input type="checkbox" name="searchType" value="emcontents" <c:if test="${fn:indexOf(searchVO.searchType, 'emcontents')!=-1}">checked="checked"</c:if>/>
                                내용
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
<!-- /#wrapper -->
</body>

<%@include file="../inc/footer.jsp" %>