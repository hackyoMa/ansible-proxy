package com.github.ansibleproxy.ansible.service;

import com.github.ansibleproxy.ansible.entity.CopyRequest;
import com.github.ansibleproxy.ansible.entity.PlaybookRequest;
import com.github.ansibleproxy.ansible.entity.ScriptRequest;
import com.github.ansibleproxy.ansible.entity.TaskResult;
import com.github.ansibleproxy.common.TaskProperties;
import com.github.ansibleproxy.util.AnsibleUtils;
import com.github.ansibleproxy.util.ProcessUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * AnsibleService
 *
 * @author hackyo
 * @since 2022/4/1
 */
@Service
public class AnsibleService {

    @Autowired
    public AnsibleService(TaskProperties taskProperties) {
        AnsibleUtils.taskFolderPath = taskProperties.getFolder();
        AnsibleUtils.cleanFile = taskProperties.getCleanFile();
        ProcessUtils.ansibleDebug = taskProperties.getDebug();
    }

    public TaskResult script(ScriptRequest request) {
        try {
            AnsibleUtils.buildBase(request);
            AnsibleUtils.buildScriptFile(request);
            AnsibleUtils.buildScriptPlaybookFile(request);
            return ProcessUtils.execPlaybook(request.getTimeout(),
                    request.getPlaybookFile().getAbsolutePath(),
                    request.getInventoryFile().getAbsolutePath());
        } finally {
            AnsibleUtils.cleanTaskFile(request.getTaskId());
        }
    }

    public TaskResult copy(MultipartFile file, CopyRequest request) {
        try {
            AnsibleUtils.buildBase(request);
            AnsibleUtils.buildCopyFile(file, request);
            AnsibleUtils.buildCopyPlaybookFile(request);
            return ProcessUtils.execPlaybook(request.getTimeout(),
                    request.getPlaybookFile().getAbsolutePath(),
                    request.getInventoryFile().getAbsolutePath());
        } finally {
            AnsibleUtils.cleanTaskFile(request.getTaskId());
        }
    }

    public TaskResult playbook(PlaybookRequest request) {
        try {
            AnsibleUtils.buildBase(request);
            AnsibleUtils.buildPlaybookFile(request, request.getPlaybook());
            return ProcessUtils.execPlaybook(request.getTimeout(),
                    request.getPlaybookFile().getAbsolutePath(),
                    request.getInventoryFile().getAbsolutePath());
        } finally {
            AnsibleUtils.cleanTaskFile(request.getTaskId());
        }
    }

}
