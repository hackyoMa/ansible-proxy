# syntax=docker/dockerfile:latest
FROM hackyo/jre:21
LABEL maintainer="137120918@qq.com" version="20241030"

RUN apt update -y && apt install -y sshpass ansible && apt autoremove -y && apt clean
COPY ansible.cfg /etc/ansible/ansible.cfg
COPY ssh_config /etc/ssh/ssh_config
COPY target/ansible-proxy-1.0.0.jar /opt/app/app.jar

HEALTHCHECK --interval=10s --timeout=5s --start-period=30s --retries=3 CMD curl -f http://localhost:8080/actuator/health || exit 1
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]
