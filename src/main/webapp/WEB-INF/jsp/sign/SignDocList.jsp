<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>
    <script>
        function fn_formSubmit() {
            document.form1.submit();
        }
    </script>

</head>

<body>

<div id="wrapper">

    <jsp:include page="../common/navigation.jsp"/>

    <form role="form" id="form1" name="form1" method="post">
        <div id="page-wrapper">
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header"><i class="fa fa-edit fa-fw"></i> <s:message code="electronic.paid"/></h1>
                </div>
            </div>

            <div class="row">
                <div class="col-lg-12">
                    <label><input name="searchExt1" id="searchExt1" type="radio" value="sign" onclick="fn_formSubmit()" <c:if test='${searchVO.searchExt1=="sign"}'>checked</c:if>> 진행중</label>
                    <label><input name="searchExt1" id="searchExt1" type="radio" value="signed" onclick="fn_formSubmit()" <c:if test='${searchVO.searchExt1=="signed"}'>checked</c:if>> 완료</label>
                </div>
            </div>

            <!-- /.row -->
            <div class="panel panel-default guStyle2">
                <div class="panel-body">
                    <div class="listHead">
                        <div class="listHiddenField pull-left field60"><s:message code="board.no"/></div>
                        <div class="listHiddenField pull-right field100">종류</div>
                        <div class="listHiddenField pull-right field100"><s:message code="crud.regdate"/></div>
                        <div class="listHiddenField pull-right field100"><s:message code="crud.usernm"/></div>
                        <div class="listHiddenField pull-right field100">상태</div>
                        <div class="listTitle"><s:message code="crud.crtitle"/></div>
                    </div>

                    <c:if test="${listview.size()==0}">
                        <div class="listBody height200">
                        </div>
                    </c:if>

                    <c:forEach var="listview" items="${listview}" varStatus="status">
                        <c:url var="link" value="signDocRead">
                            <c:param name="docno" value="${listview.docno}"/>
                        </c:url>

                        <div class="listBody">
                            <div class="listHiddenField pull-left field60 textCenter"><c:out value="${searchVO.totRow-((searchVO.page-1)*searchVO.displayRowCount + status.index)}"/></div>
                            <div class="listHiddenField pull-right field100 textCenter"><c:out value="${listview.dttitle}"/></div>
                            <div class="listHiddenField pull-right field100 textCenter"><c:out value="${listview.chgdate}"/></div>
                            <div class="listHiddenField pull-right field100 textCenter"><c:out value="${listview.usernm}"/></div>
                            <div class="listHiddenField pull-right field100 textCenter"><c:out value="${listview.docstatus}"/></div>
                            <div class="listTitle" title="<c:out value="${listview.doctitle}"/>">
                                <a href="${link}"><c:out value="${listview.doctitle}"/></a>
                            </div>
                        </div>
                    </c:forEach>

                    <br/>
                    <jsp:include page="../common/pagingforSubmit.jsp"/>

                    <div class="form-group">
                        <div class="checkbox col-lg-3 pull-left">
                            <label class="pull-right">
                                <input type="checkbox" name="searchType" value="doctitle" <c:if test="${fn:indexOf(searchVO.searchType, 'doctitle')!=-1}">checked="checked"</c:if>/>
                                제목
                            </label>
                            <label class="pull-right">
                                <input type="checkbox" name="searchType" value="doccontents" <c:if test="${fn:indexOf(searchVO.searchType, 'doccontents')!=-1}">checked="checked"</c:if>/>
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
                </div>
            </div>
            <!-- /.row -->
        </div>
    </form>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->
</body>

<%@include file="../inc/footer.jsp" %>