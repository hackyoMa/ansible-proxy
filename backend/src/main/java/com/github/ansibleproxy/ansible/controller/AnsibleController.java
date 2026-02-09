package com.github.ansibleproxy.ansible.controller;

import com.github.ansibleproxy.ansible.entity.CopyRequest;
import com.github.ansibleproxy.ansible.entity.PlaybookRequest;
import com.github.ansibleproxy.ansible.entity.ScriptRequest;
import com.github.ansibleproxy.ansible.entity.TaskResult;
import com.github.ansibleproxy.ansible.service.AnsibleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Ansible代理执行
 *
 * @author hackyo
 * @since 2022/4/1
 */
@RestController
@RequestMapping("/ansible")
public class AnsibleController {

    private final AnsibleService ansibleService;

    @Autowired
    public AnsibleController(AnsibleService ansibleService) {
        this.ansibleService = ansibleService;
    }

    /**
     * 执行脚本模块
     *
     * @param request 脚本执行参数
     * @return 执行结果
     */
    @PostMapping("/script")
    public TaskResult script(@RequestBody ScriptRequest request) {
        return ansibleService.script(request);
    }

    /**
     * 复制文件模块
     *
     * @param file    复制的文件
     * @param request 复制文件参数
     * @return 复制结果
     */
    @PostMapping("/copy")
    public TaskResult copy(@RequestPart("file") MultipartFile file,
                           @RequestPart("params") CopyRequest request) {
        return ansibleService.copy(file, request);
    }

    /**
     * 执行Playbook模块
     *
     * @param request Playbook执行参数
     * @return 执行结果
     */
    @PostMapping("/playbook")
    public TaskResult playbook(@RequestBody PlaybookRequest request) {
        return ansibleService.playbook(request);
    }

}
