set LIBIAS_HOME=.
set LIBIAS_VERSION=dev

mvn -Dspring-boot.run.profiles=localhost -Djavax.servlet.request.encoding=UTF-8 -Dfile.encoding=UTF-8 spring-boot:run
