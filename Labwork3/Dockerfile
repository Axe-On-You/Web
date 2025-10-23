FROM quay.io/wildfly/wildfly:latest

COPY psql_jdbc_driver/postgresql-42.7.8.jar /opt/jboss/wildfly/standalone/deployments/
COPY build/libs/web_lab3-1.0.war /opt/jboss/wildfly/standalone/deployments/

EXPOSE 8080 9990

ARG ADMIN_NAME="admin"
ARG ADMIN_PASSWORD="admin"
RUN /opt/jboss/wildfly/bin/add-user.sh -u $ADMIN_NAME -p $ADMIN_PASSWORD -s -r ManagementRealm

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]