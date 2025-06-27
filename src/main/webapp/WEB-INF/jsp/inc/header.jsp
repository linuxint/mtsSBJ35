<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sql" uri="jakarta.tags.sql" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="x" uri="jakarta.tags.xml" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<c:set var="timestamps" value="20250604"/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title><s:message code="common.pageTitle"/></title>
    <link href="<c:url value='/css/sb-admin/bootstrap.min.css?v=${timestamps}'/>" rel="stylesheet">
    <link href="<c:url value='/css/sb-admin/metisMenu.min.css?v=${timestamps}'/>" rel="stylesheet">
    <link href="<c:url value='/css/sb-admin/sb-admin-2.css?v=${timestamps}'/>" rel="stylesheet">
    <link href="<c:url value='/css/sb-admin/font-awesome.min.css?v=${timestamps}'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/js/dynatree/ui.dynatree.css?v=${timestamps}'/>" rel="stylesheet" id="skinSheet"/>
    <link href="<c:url value='/js/fullcalendar5/main.css?v=${timestamps}'/>" rel='stylesheet'/>
    <link href="<c:url value='/js/datepicker/datepicker.css?v=${timestamps}'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/sb-admin/morris.css?v=${timestamps}'/>" rel="stylesheet">
    <link href="<c:url value='/css/sign.css?v=${timestamps}'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/index.css?v=${timestamps}'/>" rel="stylesheet">
    <link href="<c:url value='/js/w2ui/w2ui.min.css?v=${timestamps}'/>" rel="stylesheet">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

    <script src="<c:url value='/js/jquery-2.2.3.min.js?v=${timestamps}'/>"></script>
    <script src="<c:url value='/js/jquery-ui.js?v=${timestamps}'/>"></script>
    <script src="<c:url value='/js/easyui/jquery.easyui.min.js?v=${timestamps}'/>"></script>
    <script src="<c:url value='/js/w2ui/w2ui.min.js?v=${timestamps}'/>"></script>

    <script src="<c:url value='/js/dynatree/jquery.dynatree.js?v=${timestamps}'/>"></script>
    <script src="<c:url value='/css/sb-admin/bootstrap.min.js?v=${timestamps}'/>"></script>
    <script src="<c:url value='/css/sb-admin/metisMenu.min.js?v=${timestamps}'/>"></script>
    <script src="<c:url value='/css/sb-admin/sb-admin-2.js?v=${timestamps}'/>"></script>
    <script src="<c:url value='/js/fullcalendar5/main.js?v=${timestamps}'/>"></script>
    <script src="<c:url value='/js/fullcalendar5/locales-all.js?v=${timestamps}'/>"></script>
    <script src="<c:url value='/js/datepicker/bootstrap-datepicker.js?v=${timestamps}'/>"></script>
    <script src="<c:url value='/js/ckeditor5/ckeditor.js?v=${timestamps}'/>"></script>
    <script src="<c:url value='/css/sb-admin/raphael-min.js?v=${timestamps}'/>"></script>
    <script src="<c:url value='/css/sb-admin/morris.min.js?v=${timestamps}'/>"></script>
    <script src="<c:url value='/js/mts.js?v=${timestamps}'/>"></script>