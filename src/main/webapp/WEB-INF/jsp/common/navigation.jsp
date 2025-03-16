<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sql" uri="jakarta.tags.sql" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="x" uri="jakarta.tags.xml" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>

<nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
    <div class="navbar-header">
        <a class="navbar-brand" href="index"><s:message code="common.projectTitle"/></a>
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>

        <ul class="nav navbar-top-links navbar-right">
            <!-- /.dropdown -->
            <c:if test="${alertcount>0}">
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#" onclick="showAlertList()">
                        <i class="fa fa-bell fa-fw"></i> <i class="fa fa-caret-down"></i>
                        <div class="msgAlert"><c:out value="${alertcount}"/></div>
                    </a>
                    <script>
                        function showAlertList() {
                            $.ajax({
                                url: "alertList4Ajax",
                                dataType: "html",
                                type: "post",
                                success: function (result) {
                                    if (result !== "") {
                                        $("#alertlist").html(result);
                                    }
                                }
                            })
                        }
                    </script>
                    <ul id="alertlist" class="dropdown-menu dropdown-alerts">
                    </ul>
                    <!-- /.dropdown-alerts -->
                </li>
            </c:if>
            <!-- /.dropdown -->
            <li class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                    <i class="fa fa-user fa-fw"></i> <i class="fa fa-caret-down"></i>
                </a>
                <ul class="dropdown-menu dropdown-user">
                    <li><a href="memberForm"><i class="fa fa-user fa-fw"></i> <c:out value="${sessionScope.usernm}"/></a></li>
                    <li><a href="searchMember"><i class="fa fa-users fa-fw"></i> <s:message code="menu.users"/></a></li>
                    <li class="divider"></li>
                    <li><a href="memberLogout"><i class="fa fa-sign-out fa-fw"></i> Logout</a>
                    </li>
                </ul>
                <!-- /.dropdown-user -->
            </li>
            <!-- /.dropdown -->
        </ul>
        <!-- /.navbar-top-links -->
    </div>
    <!-- /.navbar-header -->

    <div class="navbar-default sidebar" role="navigation">
        <div class="sidebar-nav navbar-collapse">
            <ul class="nav" id="side-menu">
                <li class="sidebar-search">
                    <form id="searchForm" name="searchForm" method="post" action="boardList">
                        <input type="hidden" name="searchType" value="brdtitle,brdmemo">
                        <div class="input-group custom-search-form">
                            <input class="form-control" type="text" name="globalKeyword" id="globalKeyword" placeholder="Search...">
                            <span class="input-group-btn">
                                        <button class="btn btn-default" type="button" onclick="fn_search()">
                                            <i class="fa fa-search"></i>
                                        </button>
                                    </span>
                        </div>
                    </form>
                    <script>
                        function fn_search() {
                            if ($("#globalKeyword").val() !== "") {
                                $("#searchForm").submit();
                            }
                        }
                    </script>                            <!-- /input-group -->
                </li>
                <li>
                    <a href="boardList"><i class="fa fa-files-o fa-fw"></i> <s:message code="board.boardName"/></a>
                </li>
                <li>
                    <a href="search"><i class="fa fa-search fa-fw"></i> <s:message code="search.total"/></a>
                </li>
                <li>
                    <a href="projectList"><i class="fa fa-tasks fa-fw"></i> <s:message code="project.title"/></a>
                </li>
                <li>
                    <a href="#"><i class="fa fa-edit fa-fw"></i> <s:message code="electronic.payment"/><span class="fa arrow"></span></a>
                    <ul class="nav nav-second-level">
                        <li>
                            <a href="signDocTypeList"> <s:message code="electronic.draft"/></a>
                        </li>
                        <li>
                            <a href="signListTobe"> <s:message code="electronic.approved"/></a>
                        </li>
                        <li>
                            <a href="signListTo"> <s:message code="electronic.paid"/></a>
                        </li>
                    </ul>
                </li>
                <li>
                    <a href="schList"><i class="fa fa-calendar fa-fw"></i> <s:message code="schedule.management"/></a>
                </li>
                <li>
                    <a href="#"><i class="fa fa-envelope-o fa-fw"></i> <s:message code="mail.title"/><span class="fa arrow"></span></a>
                    <ul class="nav nav-second-level">
                        <li>
                            <a href="mailForm"> <s:message code="mail.new"/></a>
                        </li>
                        <li>
                            <a href="receiveMails"> <s:message code="mail.receive"/></a>
                        </li>
                        <li>
                            <a href="sendMails"> <s:message code="mail.send"/></a>
                        </li>
                    </ul>
                </li>
                <li>
                    <a href="#"><i class="fa fa-music fa-fw"></i> <s:message code="sample.title"/><span class="fa arrow"></span></a>
                    <ul class="nav nav-second-level">
                        <li>
                            <a href="sample1"><s:message code="sample.1"/></a>
                        </li>
                        <li>
                            <a href="sample2"><s:message code="sample.2"/></a>
                        </li>
                        <li>
                            <a href="sample3"><s:message code="sample.3"/></a>
                        </li>
                        <li>
                            <a href="sample4"><s:message code="sample.4"/></a>
                        </li>
                        <li>
                            <a href="crudList"><s:message code="sample.5"/></a>
                        </li>
                        <li>
                            <a href="chkList"><s:message code="sample.6"/></a>
                        </li>
                    </ul>
                </li>

                <c:if test='${sessionScope.userrole == "ADMIN"}'>
                    <li>
                        <a href="#"> <s:message code="menu.admin"/></a>
                    </li>
                    <li>
                        <a href="adBoardGroupList"><i class="fa fa-files-o fa-fw"></i> <s:message code="menu.board"/></a>
                    </li>
                    <li>
                        <a href="adMenuList"><i class="fa fa-files-o fa-fw"></i> <s:message code="menu.menu"/></a>
                    </li>
                    <li>
                        <a href="#"><i class="fa fa-sitemap fa-fw"></i> <s:message code="menu.organ"/><span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level">
                            <li>
                                <a href="adDept"><s:message code="menu.dept"/></a>
                            </li>
                            <li>
                                <a href="adUser"><s:message code="menu.user"/></a>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <a href="adSignDocTypeList"><i class="fa fa-edit fa-fw"></i> <s:message code="electronic.approval"/></a>
                    </li>
                    <li>
                        <a href="adCodeList"><i class="fa fa-gear fa-fw"></i> <s:message code="menu.code"/></a>
                    </li>
                </c:if>
                <c:if test='${sessionScope.userrole == "ADMIN"}'>
                    <li>
                        <a href="#"><i class="fa fa-sitemap fa-fw"></i> <s:message code="develop.title"/><span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level">
                            <li>
                                <a href="devLogView" target="_blank"><s:message code="develop.title1"/></a>
                            </li>
                            <li>
                                <a href="/apidoc/index.html" target="_blank"><s:message code="develop.title2"/></a>
                            </li>
                            <li>
                                <a href="/thymeleaftest" target="_blank"><s:message code="develop.title3"/></a>
                            </li>
                            <li>
                                <a href="/fileAllIndex" ><s:message code="develop.title4"/></a>
                            </li>
                            <li>
                                <a href="/pdfdraw" target="_blank"><s:message code="develop.title5"/></a>
                            </li>
                            <li>
                                <a href="/dependencySearch" target="_blank"><s:message code="develop.title6"/></a>
                            </li>
                            <li>
                                <a href="/qrdraw" target="_blank"><s:message code="develop.title7"/></a>
                            </li>
                            <li>
                                <a href="/tableLayout" target="_blank"><s:message code="develop.title8"/></a>
                            </li>
                            <li>
                                <a href="/mapaddr" target="_blank"><s:message code="develop.title9"/></a>
                            </li>
                            <li>
                                <a href="/test/1" target="_blank"><s:message code="develop.title10"/></a>
                            </li>
                            <li>
                                <a href="/test/2" target="_blank"><s:message code="develop.title10"/></a>
                            </li>
                        </ul>
                    </li>
                </c:if>
</ul>
        </div>
        <!-- /.sidebar-collapse -->
    </div>
    <!-- /.navbar-static-side -->
</nav>