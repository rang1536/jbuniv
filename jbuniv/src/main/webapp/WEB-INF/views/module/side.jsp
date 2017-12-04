<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>전북대 총동창회DB 관리자</title>

    <!-- Bootstrap Core CSS -->
    <link href="resources/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- MetisMenu CSS -->
    <link href="resources/vendor/metisMenu/metisMenu.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="resources/dist/css/sb-admin-2.css" rel="stylesheet">

    <!-- Morris Charts CSS -->
    <link href="resources/vendor/morrisjs/morris.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="resources/vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>

<body>

    <div id="wrapper">

        <!-- Navigation -->
        <nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#">JB-UNIV VER 1.0</a>
            </div>
            <!-- /.navbar-header -->

            <ul class="nav navbar-top-links navbar-right">
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                        <i class="fa fa-envelope fa-fw"></i> <i class="fa fa-caret-down"></i>
                    </a>
                    <ul class="dropdown-menu dropdown-messages">
                        <li>
                            <a href="#">
                                <div>
                                    <strong>윤재호</strong>
                                    <span class="pull-right text-muted">
                                        <em>11-17</em>
                                    </span>
                                </div>
                                <div>어깨동무 원형데이터 업로드완료.</div>
                            </a>
                        </li>
                        <!-- 추가메세지 -->
                        <li class="divider"></li>
                        <li>
                            <a href="#">
                                <div>
                                    <strong>윤재호</strong>
                                    <span class="pull-right text-muted">
                                        <em>11-15</em>
                                    </span>
                                </div>
                                <div>테이터분석시작</div>
                            </a>
                        </li> 
                        
                    </ul>
                    <!-- /.dropdown-messages -->
                </li>
                <!-- /.dropdown -->
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                        <i class="fa fa-tasks fa-fw"></i> <i class="fa fa-caret-down"></i>
                    </a>
                    <ul class="dropdown-menu dropdown-tasks">
                        <li>
                            <a href="#">
                                <div>
                                    <p>
                                        <strong>어깨동무 원형DB입력</strong>
                                        <span class="pull-right text-muted">100% Complete</span>
                                    </p>
                                    <div class="progress progress-striped active">
                                        <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%">
                                            <span class="sr-only">100% Complete (success)</span>
                                        </div>
                                    </div>
                                </div>
                            </a>
                        </li>
                        <li class="divider"></li>
                        <li>
                            <a href="#">
                                <div>
                                    <p>
                                        <strong>발전제단 DB입력</strong>
                                        <span class="pull-right text-muted">20% Complete</span>
                                    </p>
                                    <div class="progress progress-striped active">
                                        <div class="progress-bar progress-bar-info" role="progressbar" aria-valuenow="20" aria-valuemin="0" aria-valuemax="100" style="width: 20%">
                                            <span class="sr-only">20% Complete</span>
                                        </div>
                                    </div>
                                </div>
                            </a>
                        </li>
                        <li class="divider"></li>
                        <li>
                            <a href="#">
                                <div>
                                    <p>
                                        <strong>두개DB 비교정제</strong>
                                        <span class="pull-right text-muted">5% Complete</span>
                                    </p>
                                    <div class="progress progress-striped active">
                                        <div class="progress-bar progress-bar-warning" role="progressbar" aria-valuenow="5" aria-valuemin="0" aria-valuemax="100" style="width: 5%">
                                            <span class="sr-only">5% Complete (warning)</span>
                                        </div>
                                    </div>
                                </div>
                            </a>
                        </li>
                        <li class="divider"></li>
                        <li>
                            <a href="#">
                                <div>
                                    <p>
                                        <strong>DB일원화 및 무결성확보</strong>
                                        <span class="pull-right text-muted">0% Complete</span>
                                    </p>
                                    <div class="progress progress-striped active">
                                        <div class="progress-bar progress-bar-danger" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%">
                                            <span class="sr-only">0% Complete (danger)</span>
                                        </div>
                                    </div>
                                </div>
                            </a>
                        </li>
                        <li class="divider"></li>
                        <li>
                            <a class="text-center" href="#">
                                <strong>진행상황</strong>
                            </a>
                        </li>
                    </ul>
                    <!-- /.dropdown-tasks -->
                </li>
                <!-- /.dropdown -->
                <!--<li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                        <i class="fa fa-bell fa-fw"></i> <i class="fa fa-caret-down"></i>
                    </a>
                    <ul class="dropdown-menu dropdown-alerts">
                        <li>
                            <a href="#">
                                <div>
                                    <i class="fa fa-comment fa-fw"></i> New Comment
                                    <span class="pull-right text-muted small">4 minutes ago</span>
                                </div>
                            </a>
                        </li>
                        <li class="divider"></li>
                        <li>
                            <a href="#">
                                <div>
                                    <i class="fa fa-twitter fa-fw"></i> 3 New Followers
                                    <span class="pull-right text-muted small">12 minutes ago</span>
                                </div>
                            </a>
                        </li>
                        <li class="divider"></li>
                        <li>
                            <a href="#">
                                <div>
                                    <i class="fa fa-envelope fa-fw"></i> Message Sent
                                    <span class="pull-right text-muted small">4 minutes ago</span>
                                </div>
                            </a>
                        </li>
                        <li class="divider"></li>
                        <li>
                            <a href="#">
                                <div>
                                    <i class="fa fa-tasks fa-fw"></i> New Task
                                    <span class="pull-right text-muted small">4 minutes ago</span>
                                </div>
                            </a>
                        </li>
                        <li class="divider"></li>
                        <li>
                            <a href="#">
                                <div>
                                    <i class="fa fa-upload fa-fw"></i> Server Rebooted
                                    <span class="pull-right text-muted small">4 minutes ago</span>
                                </div>
                            </a>
                        </li>
                        <li class="divider"></li>
                        <li>
                            <a class="text-center" href="#">
                                <strong>See All Alerts</strong>
                                <i class="fa fa-angle-right"></i>
                            </a>
                        </li>
                    </ul> -->
                    <!-- /.dropdown-alerts -->
                </li>
                <!-- /.dropdown -->
               <!-- 상단마이페이지 메뉴 
               <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                        <i class="fa fa-user fa-fw"></i> <i class="fa fa-caret-down"></i>
                    </a>
                    <ul class="dropdown-menu dropdown-user">
                        <li><a href="#"><i class="fa fa-user fa-fw"></i> User Profile</a>
                        </li>
                        <li><a href="#"><i class="fa fa-gear fa-fw"></i> Settings</a>
                        </li>
                        <li class="divider"></li>
                        <li><a href="login.html"><i class="fa fa-sign-out fa-fw"></i> Logout</a>
                        </li>
                    </ul>
                    
                </li> -->
                <!-- /.dropdown -->
            </ul>
            <!-- 상단네비바 종료 -->

            <div class="navbar-default sidebar" role="navigation">
                <div class="sidebar-nav navbar-collapse">
                    <ul class="nav" id="side-menu">
                        <li class="sidebar-search">
                            <div class="input-group custom-search-form">
                                <input type="text" class="form-control" placeholder="미구현">
                                <span class="input-group-btn">
                                <button class="btn btn-default" type="button">
                                    <i class="fa fa-search"></i>
                                </button>
                            </span>
                            </div>
                            <!-- /input-group -->
                        </li>
                       
                        <li>
                            <a href="addUser"><i class="fa fa-edit fa-fw"></i> 등록</a>
                        </li>
                       
                        <li>
                            <a href="#"><i class="fa fa-sitemap fa-fw"></i> 목록<span class="fa arrow"></span></a>
                            <ul class="nav nav-second-level">
                            	<li>
                                    <a href="#">단과 <span class="fa arrow"></span></a>
                                    <ul class="nav nav-third-level">
                                       <!--  <li>
                                            <a href="userList?relationType=1&relation1=공공인재학부">공공인재학부</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=1&relation1=지미카터국제학부">지미카터국제학부</a>
                                        </li> -->
                                        <li>
                                            <a href="userList?relationType=1&relation1=0">간호대학</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=1&relation1=1">공과대학</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=1&relation1=2">농업생명과학대학</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=1&relation1=3">사범대학</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=1&relation1=4">사회과학대학</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=1&relation1=5">상과대학</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=1&relation1=6">생활과학대학</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=1&relation1=7">수의과대학</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=1&relation1=8">예술대학</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=1&relation1=9">인문대학</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=1&relation1=10">자연과학대학</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=1&relation1=11">환경생명자원대학</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=1&relation1=12">의과대학</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=1&relation1=13">법과대학</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=1&relation1=14">치과대학</a>
                                        </li>
                                    </ul>
                                    <!-- /.nav-third-level -->
                                </li>
                                <li>
                                    <a href="#">일반대학원 <span class="fa arrow"></span></a>
                                    <ul class="nav nav-third-level">
                                    	<li>
                                            <a href="userList?relationType=2&relation1=0">농업개발대</a>
                                        </li>
                                    </ul>
                                </li>
                             	<li>
                                    <a href="#">특수대학원 <span class="fa arrow"></span></a>
                                    <ul class="nav nav-third-level">
                                    	<li>
                                            <a href="userList?relationType=3&relation1=0">경영대학원</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=3&relation1=1">교육대학원</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=3&relation1=2">법무대학원</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=3&relation1=3">보건대학원</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=3&relation1=4">산업기술대학원</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=3&relation1=5">생명자원과학대학원</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=3&relation1=6">정보과학대학원</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=3&relation1=7">행정대학원</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=3&relation1=8">환경대학원</a>
                                        </li>
                                    </ul>
                                </li>
                                <li>
                                    <a href="#">전문대학원 <span class="fa arrow"></span></a>
                                    <ul class="nav nav-third-level">
                                    	<li>
                                            <a href="userList?relationType=4&relation1=0">법학전문대학원</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=4&relation1=1">의학전문대학원</a>
                                        </li>
                                        <li>
                                            <a href="userList?relationType=4&relation1=2">치의학전문대학원</a>
                                        </li>
                                    </ul>
                                </li>
                                <li>
                                    <a href="userList?relationType=5&relation1=0">MOT과정 </a>
                                </li>
                                <li>
                                    <a href="userList?relationType=6&relation1=0">최고위과정</a>
                                </li>
                            </ul>
                            <!-- /.nav-second-level -->
                        </li>
                        
                    </ul>
                </div>
                <!-- /.sidebar-collapse -->
            </div>
            <!-- /.navbar-static-side -->
        </nav>
<!--사이드메뉴 종료  -->

       
        <!-- /#page-wrapper -->

    </div>
    <!-- /#wrapper -->

    <!-- jQuery -->
    <script src="resources/vendor/jquery/jquery.min.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="resources/vendor/bootstrap/js/bootstrap.min.js"></script>

    <!-- Metis Menu Plugin JavaScript -->
    <script src="resources/vendor/metisMenu/metisMenu.min.js"></script>

    <!-- Morris Charts JavaScript -->
    <script src="resources/vendor/raphael/raphael.min.js"></script>
    <script src="resources/vendor/morrisjs/morris.min.js"></script>
    <script src="resources/data/morris-data.js"></script>

    <!-- Custom Theme JavaScript -->
    <script src="resources/dist/js/sb-admin-2.js"></script>

</body>

</html>
