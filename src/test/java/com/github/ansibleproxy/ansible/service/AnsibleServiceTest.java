package com.github.ansibleproxy.ansible.service;

import com.github.ansibleproxy.ansible.entity.*;
import com.github.ansibleproxy.common.CredentialType;
import com.github.ansibleproxy.util.Json;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * AnsibleServiceTest
 *
 * @author hackyo
 * @since 2022/4/1
 */
@SpringBootTest
class AnsibleServiceTest {

    private final AnsibleService ansibleService;
    private final BaseRequest baseRequest = new BaseRequest();

    @Autowired
    public AnsibleServiceTest(AnsibleService ansibleService) {
        this.ansibleService = ansibleService;

        baseRequest.setHost("123.60.96.195");
        baseRequest.setPort(6922);
        baseRequest.setUser("root");
        baseRequest.setCredentialType(CredentialType.PASSWORD);
        baseRequest.setPassword("1qaz@WSX#EDC");
        baseRequest.setTimeout(20);

        BaseRequest proxy = new BaseRequest();
        proxy.setHost("123.60.96.195");
        proxy.setPort(6922);
        proxy.setUser("root");
        proxy.setCredentialType(CredentialType.PASSWORD);
        proxy.setPassword("1qaz@WSX#EDC");

        baseRequest.setProxy(proxy);
    }

    @Test
    void script() {
        ScriptRequest request = Json.parseObject(Json.toJsonString(baseRequest), ScriptRequest.class);
        request.setExecutable("sh");
        request.setScript("#!/bin/bash\necho $1");
        request.setArgs("666");
        TaskResult taskResult = ansibleService.script(request);
        System.out.println(Json.toJsonString(taskResult));
    }

    @Test
    void copy() throws IOException {
        CopyRequest request = Json.parseObject(Json.toJsonString(baseRequest), CopyRequest.class);
        request.setDest("/root/test.txt");
        request.setMode("0666");
        request.setGroup("nginx");
        request.setOwner("nginx");
        MockMultipartFile file;
        try (FileInputStream fileInputStream = new FileInputStream("/opt/ansible_proxy/test.txt")) {
            file = new MockMultipartFile("test.txt", fileInputStream);
        }
        TaskResult taskResult = ansibleService.copy(file, request);
        System.out.println(Json.toJsonString(taskResult));
    }

    @Test
    void playbook() {
        PlaybookRequest request = Json.parseObject(Json.toJsonString(baseRequest), PlaybookRequest.class);
        request.setPlaybook("""
                ---
                - name: ping linux server
                  hosts: default-host
                  gather_facts: False
                  tasks:
                    - name: ping linux server
                      ansible.builtin.ping:""");
        TaskResult taskResult = ansibleService.playbook(request);
        System.out.println(Json.toJsonString(taskResult));
    }

}
