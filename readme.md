## SpringBoot 3.4 + MyBatis 3 + Oracle/MariaDB based web project template ##
This sample is a web project template created based on SpringBoot 3.4 + MyBatis 3 + Oracle/MariaDB (Maven).

We plan to grow into a small product in the future by implementing various functions that are often used in web development in advance.

A more detailed explanation can be obtained from [here] (http://forest71.tistory.com/78).
The framework has changed based on the referenced source.
The Oracle version may have errors in some functions after DB conversion.

3 projects shared on the blog have been merged into one.
project9 - 3> Multi Bulletin Board
pms9 - 2) project management
groupware9 - 1) Electronic payment

### Key Implementation Features ###
#### 1) Electronic payment
- Schedule: Monthly schedule
- Payment: drafting, documents to be approved, documents to be approved
- E-mail: new mail, received mail, sent mail
- notice board
#### 2) Project management
- Create assignments and assign them individually
- View task information (task, schedule, user-centered)
- Enter individual task progress
#### 3) Multi-Bulletin Board, User/Group Management
- Multi-Bulletin Board (Infinite Comments, Likes, etc.)
- Member function: All pages are accessible only to members. Login/Logout function (save log). Member management, etc.
- Security function: Divided into general users (U) and administrators (A), general users cannot access the admin page.
- User selection: department, user selection function (popup)
- Sample date selection and chart usage
- Excel download (jXLS) sample
- Multilingual processing
- Design: Bootstrap-based responsive web application (SB-Admin)
- Handling common error pages (404, 500)
- Log processing (logback, log4jdbc)

#### Source Provided
- CrossOrigin - DevkbilApplication.java, WebMvcConfig.java - cancel remark
- Banner Custom - mts-banner.txt, application.properties, DevkbilApplication.java

### Major LIBs ###
- JQuery-2.2.3
- CKEditor 4.5.10
- SB-Admin 2, morris v0.5.0, DatePicker
- DynaTree 1.2.4
- jQuery EasyUI 1.4.3
- FullCalendar v5

### Development Environment ###
    Programming Language - Java 17
    IDE - intelliJ
    DB - Oracle/MariaDB
    Framework - MyBatis, SpringBoot 3.4
    Build Tool - Maven

### installation ###
- Create a database (mts) in OracleDB (user_database_oracle.sql) and create tables and data by executing tables_oracle.sql and tableData_oracle.sql.
- Create a database (mts) in MariaDB (user_database_myriadb.sql) and create tables and data by executing tables_myriadb.sql and tableData_myriadb.sql.
- Enter appropriate connection information in applicationContext.xml.
- Run mts from Tomcat or Intellij/Eclipse
- Connect to https://localhost:8443/
- ID/PW: admin/admin, user1/user1, user2/user2 ...
  The Oracle PW source is not changed, so the PW is entered as an ID.


### oracle11g configuration ###
    0. docker image download
      docker pull linuxint/oraclexe11g
    1. docker run
      docker run --name oracle11g -d -p 1521:1521 linuxint/oracle11g

### elasticsearch configuration ###
    0. docker image download
      docker pull elasticsearch:7.17.6
    1. docker run
      docker run -p 9200:9200 -p 9300:9300 --name elasticsearch -e "discovery.type=single-node" linuxint/elasticsearch
    2. user password set
      ./docker exec -t elasticsearch /bin/bash
      ./bin/elasticsearch-setup-passwords interactive
           ~ password : manager

              Initiating the setup of passwords for reserved users elastic,apm_system,kibana,kibana_system,logstash_system,beats_system,remote_monitoring_user.
              You will be prompted to enter passwords as the process progresses.
              Please confirm that you would like to continue [y/N]y
              
              Enter password for [elastic]:
              Reenter password for [elastic]:
              Enter password for [apm_system]:
              Reenter password for [apm_system]:
              Enter password for [kibana_system]:
              Reenter password for [kibana_system]:
              Enter password for [logstash_system]:
              Reenter password for [logstash_system]:
              Enter password for [beats_system]:
              Reenter password for [beats_system]:
              Enter password for [remote_monitoring_user]:
              Reenter password for [remote_monitoring_user]:
              Changed password for user [apm_system]
              Changed password for user [kibana_system]
              Changed password for user [kibana]
              Changed password for user [logstash_system]
              Changed password for user [beats_system]
              Changed password for user [remote_monitoring_user]
              Changed password for user [elastic]

    3. docker exec -it elasticsearch /bin/bash
    4. Install nori, a morphological analyzer provided by default in Elasticsearch       ./elasticsearch/bin/elasticsearch-plugin install analysis-nori
    5. dictionary copy
      ./elasticsearch/stopwords.txt, synonym.txt, userdict.txt -> elasticsearch/config 
      docker cp stopwords.txt elasticsearch:/usr/share/elasticsearch/config/
      docker cp synonyms.txt elasticsearch:/usr/share/elasticsearch/config/
      docker cp userdict.txt elasticsearch:/usr/share/elasticsearch/config/
    6. index create
      curl -XPUT localhost:9200/mts -d @index_board.json -H "Content-Type: application/json"

### james configuration ###
    1. docker pull apache/james:demo-latest
    2. docker run -p "465:465" -p "993:993" --name james apache/james:demo-latest
      IMAP port : 993
      SMTP port : 465
      user : user01@james.local
      passwd : 1234

### network configuration ###
    1.
    0. Create a network
      > docker network create somenetwork
    1. Check network information
      > docker network inspect somenetwork
    2. Connect the network to the container1
      > docker network connect somenetwork elasticsearch
    3. Network Connections to Containers2
      > docker network connect somenetwork oracle11g
    4. Check network information
      > docker network inspect somenetwork
    5. Check network connection
      > docker exec oracle11g ping elasticsearch
      PING elasticsearch (172.19.0.2) 56(84) bytes of data.
      64 bytes from elasticsearch.somenetwork (172.19.0.2): icmp_seq=1 ttl=64 time=0.091 ms
      64 bytes from elasticsearch.somenetwork (172.19.0.2): icmp_seq=2 ttl=64 time=0.076 ms
      64 bytes from elasticsearch.somenetwork (172.19.0.2): icmp_seq=6 ttl=64 time=0.048 ms

### License ###
MIT
