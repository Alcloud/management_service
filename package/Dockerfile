FROM openjdk:8-jre-alpine

MAINTAINER Fraunhofer FOKUS "ehealth@fokus.fraunhofer.de"

# java app associated files
ADD ./files/bin/notificationmanagementservice.jar /tmp/notificationmanagementservice.jar

# start script associated files
ADD ./files/scripts/start.sh /tmp/start.sh
RUN chmod +x /tmp/start.sh

# confd associated files
ADD ./files/confd/bin/confd /usr/local/bin/confd
RUN chmod +x /usr/local/bin/confd
ADD ./files/confd/templates /etc/confd/templates
ADD ./files/confd/conf.d /etc/confd/conf.d


# Run REST Client
CMD ["sh", "/tmp/start.sh"]