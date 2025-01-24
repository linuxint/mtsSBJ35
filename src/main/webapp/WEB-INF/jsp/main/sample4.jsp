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

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header"><i class="fa fa-files-o fa-fw"></i> 게시판</h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <div class="col-lg-12">
                <button type="button" class="btn btn-default pull-right" onclick="fn_moveToURLbyForm('form1', 'sample4Excel', '엑셀 다운로드를 ')">
                    <i class="fa fa-file-excel-o fa-fw"></i> <s:message code="common.excel"/></button>
            </div>
        </div>
        <!-- /.row -->
        <div class="panel panel-default">
            <div class="panel-body">
                <div class="listHead">
                    <div class="listHiddenField pull-left field60"><s:message code="board.no"/></div>
                    <div class="listHiddenField pull-right field60"><s:message code="board.attach"/></div>
                    <div class="listHiddenField pull-right field60"><s:message code="board.hitCount"/></div>
                    <div class="listHiddenField pull-right field100"><s:message code="board.date"/></div>
                    <div class="listHiddenField pull-right field100"><s:message code="board.writer"/></div>
                    <div class="listTitle"><s:message code="board.title"/></div>
                </div>

                <c:if test="${listview.size()==0}">
                    <div class="listBody height200">
                    </div>
                </c:if>
                <c:forEach var="listview" items="${listview}" varStatus="status">
                    <c:url var="link" value="boardRead">
                        <c:param name="bgno" value="${listview.bgno}"/>
                        <c:param name="brdno" value="${listview.brdno}"/>
                    </c:url>

                    <div class="listBody">
                        <div class="listHiddenField pull-left field60">
                            <c:out value="${searchVO.totRow-((searchVO.page-1)*searchVO.displayRowCount + status.index)}"/>
                        </div>

                        <div class="listHiddenField pull-right field60">
                            <c:if test="${listview.filecnt>0}">
                                <i class="fa fa-download fa-fw" title="<c:out value="${listview.filecnt}"/>"></i>
                            </c:if>
                        </div>
                        <div class="listHiddenField pull-right field60 textCenter"><c:out value="${listview.brdhit}"/></div>
                        <div class="listHiddenField pull-right field100 textCenter"><c:out value="${listview.regdate}"/></div>
                        <div class="listHiddenField pull-right field100 textCenter"><a href="list4User?userno=<c:out value="${listview.userno}"/>"><c:out value="${listview.brdwriter}"/></a></div>
                        <div class="listTitle" title="<c:out value="${listview.brdtitle}"/>">
                            <a href="${link}" <c:if test="${listview.brdnotice=='Y'}">class="notice"</c:if>><c:out value="${listview.brdtitle}"/></a>
                            <c:if test="${listview.replycnt>0}">
                                (<c:out value="${listview.replycnt}"/>)
                            </c:if>
                        </div>
                        <div class="showField text-muted small">
                            <c:out value="${listview.brdwriter}"/>
                            <c:out value="${listview.regdate}"/>
                            <i class="fa fa-eye fa-fw"></i> <c:out value="${listview.brdhit}"/>
                            <c:if test="${listview.filecnt>0}">
                                <i class="fa fa-download fa-fw" title="<c:out value="${listview.filecnt}"/>"></i>
                            </c:if>
                        </div>
                    </div>

                </c:forEach>

                <br/>
                <form role="form" id="form1" name="form1" method="post">
                    <jsp:include page="../common/pagingforSubmit.jsp"/>

                    <div class="form-group">
                        <div class="checkbox col-lg-3 pull-left">
                            <label class="pull-right">
                                <input type="checkbox" name="searchType" value="brdmemo" <c:if test="${fn:indexOf(searchVO.searchType, 'brdmemo')!=-1}">checked="checked"</c:if>/>
                                <s:message code="board.contents"/>
                            </label>
                            <label class="pull-right">
                                <input type="checkbox" name="searchType" value="brdtitle" <c:if test="${fn:indexOf(searchVO.searchType, 'brdtitle')!=-1}">checked="checked"</c:if>/>
                                <s:message code="board.title"/>
                            </label>
                            <label class="pull-right">
                                <input type="checkbox" name="searchType" value="usernm" <c:if test="${fn:indexOf(searchVO.searchType, 'usernm')!=-1}">checked="checked"</c:if>/>
                                <s:message code="board.writer"/>
                            </label>
                        </div>
                        <div class="input-group custom-search-form col-lg-3">
                            <input class="form-control" placeholder="Search..." type="text" name="searchKeyword" value='<c:out value="${searchVO.searchKeyword}"/>'>
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