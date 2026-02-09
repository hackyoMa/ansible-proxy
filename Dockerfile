# syntax=docker/dockerfile:1
FROM hackyo/jre:25

LABEL maintainer="137120918@qq.com" version="20260204"

ENV JAVA_OPTS=""

RUN set -eux; \
    apt-get update; \
    apt-get install -y --no-install-recommends sshpass ansible; \
    mkdir -p /opt/ansible_proxy && chmod 777 /opt/ansible_proxy; \
    rm -rf /var/lib/apt/lists/* \

COPY ansible.cfg /etc/ansible/ansible.cfg
COPY ssh_config /etc/ssh/ssh_config
COPY backend/target/backend-1.0.0.jar /opt/ansible_proxy/app.jar

HEALTHCHECK --interval=10s --timeout=5s --start-period=30s --retries=3 CMD curl -fs -I -o /dev/null http://localhost:18880/ || exit 1

EXPOSE 18880

ENTRYPOINT ["container-init.sh"]
CMD java ${JAVA_OPTS} -jar /opt/ansible_proxy/app.jar
