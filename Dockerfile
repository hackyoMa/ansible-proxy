# syntax=docker/dockerfile:1
FROM hackyo/jre:21

LABEL maintainer="137120918@qq.com" version="20251027"

ENV JAVA_OPTS=""

RUN set -eux; \
    apt-get update; \
    apt-get install -y --no-install-recommends sshpass ansible; \
    apt-get autoremove -y; \
    apt-get clean; \
    rm -rf /var/lib/apt/lists/*
COPY ansible.cfg /etc/ansible/ansible.cfg
COPY ssh_config /etc/ssh/ssh_config
COPY target/ansible-proxy-1.0.0.jar /opt/app/app.jar

HEALTHCHECK --interval=10s --timeout=5s --start-period=30s --retries=3 CMD curl -f http://localhost:8080/doc/index.html || exit 1
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar /opt/app/app.jar"]