#!/bin/bash
mvn clean verify sonar:sonar \                                                                           ✔  00:45:30  
  -Dsonar.projectKey=mtsSBJ35 \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=sqp_2793fab7358c39c3c416aca59c7e1b0ce59debe6
