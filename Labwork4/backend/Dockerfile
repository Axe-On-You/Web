FROM quay.io/wildfly/wildfly:latest

# Добавляем аргументы сборки, чтобы они были доступны во время RUN
ARG DB_NAME
ARG DB_USER
ARG DB_PASSWORD

COPY psql_jdbc_driver/postgresql-42.7.8.jar /opt/jboss/wildfly/standalone/deployments/
COPY build/libs/*.war /opt/jboss/wildfly/standalone/deployments/backend.war
COPY datasource-config.cli /opt/jboss/wildfly/bin/

# Передаем аргументы в переменные окружения для процесса сборки
RUN DB_NAME=${DB_NAME} DB_USER=${DB_USER} DB_PASSWORD=${DB_PASSWORD} \
    /opt/jboss/wildfly/bin/jboss-cli.sh --commands="embed-server --std-out=echo, run-batch --file=/opt/jboss/wildfly/bin/datasource-config.cli" && \
    rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history

EXPOSE 8080 9990

ARG ADMIN_NAME="admin"
ARG ADMIN_PASSWORD="admin"
RUN /opt/jboss/wildfly/bin/add-user.sh -u $ADMIN_NAME -p $ADMIN_PASSWORD -s -r ManagementRealm

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]