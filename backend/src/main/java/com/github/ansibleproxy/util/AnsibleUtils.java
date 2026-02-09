package com.github.ansibleproxy.util;

import com.github.ansibleproxy.ansible.entity.BaseRequest;
import com.github.ansibleproxy.ansible.entity.CopyRequest;
import com.github.ansibleproxy.ansible.entity.ScriptRequest;
import com.github.ansibleproxy.common.CredentialType;
import com.github.ansibleproxy.common.HttpException;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.util.UUID;

/**
 * AnsibleUtils
 *
 * @author hackyo
 * @since 2022/4/1
 */
@Log4j2
public final class AnsibleUtils {

    private static final String FILE_SEPARATOR = "/";
    private static final String PRIVATE_KEY_FILENAME = "private_key.pem";
    private static final String PROXY_PRIVATE_KEY_FILENAME = "proxy_private_key.pem";
    private static final Set<PosixFilePermission> PRIVATE_KEY_FILE_PERMISSION = PosixFilePermissions.fromString("rw-------");
    private static final String INVENTORY_FILENAME = "inventory.yaml";
    private static final String SCRIPT_FILENAME = "script";
    private static final String PLAYBOOK_FILENAME = "playbook.yaml";
    private static final String COPYFILE_FILENAME = "file.out";

    public static String taskFolderPath = "/opt/ansible_proxy/tasks";
    public static boolean cleanFile = true;

    public static void buildBase(BaseRequest request) {
        request.setTaskId(UUID.randomUUID().toString());
        log.debug("开始构建基础信息：{}", request.getTaskId());
        AnsibleUtils.buildTaskFolder(request);
        if (CredentialType.PRIVATE_KEY.equals(request.getCredentialType())) {
            AnsibleUtils.buildPrivateKeyFile(request, PRIVATE_KEY_FILENAME);
        }
        if (request.getProxy() != null) {
            request.getProxy().setTaskId(request.getTaskId());
            request.getProxy().setTaskFolder(request.getTaskFolder());
            if (CredentialType.PRIVATE_KEY.equals(request.getProxy().getCredentialType())) {
                AnsibleUtils.buildPrivateKeyFile(request.getProxy(), PROXY_PRIVATE_KEY_FILENAME);
            }
        }
        AnsibleUtils.buildInventoryFile(request);
        log.debug("结束构建基础信息：{}", request.getTaskId());
    }

    private static void buildTaskFolder(BaseRequest request) {
        log.debug("开始创建作业目录：{}", request.getTaskId());
        File taskFolder = new File(taskFolderPath + FILE_SEPARATOR + request.getTaskId());
        if (!taskFolder.mkdirs()) {
            log.error("创建作业目录失败：{}", request.getTaskId());
            throw new HttpException("创建作业目录失败：" + request.getTaskId());
        }
        request.setTaskFolder(taskFolder);
        log.debug("结束创建作业目录：{}", request.getTaskId());
    }

    private static void buildPrivateKeyFile(BaseRequest request, String privateKeyFilename) {
        log.debug("开始创建私钥文件：{}", request.getTaskId());
        File privateKeyFile = new File(request.getTaskFolder().getAbsolutePath() + FILE_SEPARATOR + privateKeyFilename);
        try (FileOutputStream fos = new FileOutputStream(privateKeyFile)) {
            fos.write(request.getPrivateKey().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("创建私钥文件失败：{} {}", request.getTaskId(), e.getMessage());
            throw new HttpException("创建私钥文件失败：" + request.getTaskId() + " " + e.getMessage());
        }
        log.debug("开始授权私钥文件：{}", request.getTaskId());
        try {
            Files.setPosixFilePermissions(privateKeyFile.toPath(), PRIVATE_KEY_FILE_PERMISSION);
        } catch (IOException e) {
            log.error("授权私钥文件失败：{} {}", request.getTaskId(), e.getMessage());
            throw new HttpException("授权私钥文件失败" + request.getTaskId() + " " + e.getMessage());
        }
        log.debug("结束授权私钥文件：{}", request.getTaskId());
        request.setPrivateKeyFile(privateKeyFile);
        log.debug("结束创建私钥文件：{}", request.getTaskId());
    }

    private static void buildInventoryFile(BaseRequest request) {
        log.debug("开始创建Inventory文件：{}", request.getTaskId());
        File inventoryFile = new File(request.getTaskFolder().getAbsolutePath() + FILE_SEPARATOR + INVENTORY_FILENAME);
        try (FileOutputStream fos = new FileOutputStream(inventoryFile)) {
            fos.write(getInventory(request).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("创建Inventory文件失败：{} {}", request.getTaskId(), e.getMessage());
            throw new HttpException("创建Inventory文件失败" + request.getTaskId() + " " + e.getMessage());
        }
        request.setInventoryFile(inventoryFile);
        log.debug("结束创建Inventory文件：{}", request.getTaskId());
    }

    public static void buildScriptFile(ScriptRequest request) {
        log.debug("开始创建脚本文件：{}", request.getTaskId());
        String scriptFileName = request.getTaskFolder().getAbsolutePath() + FILE_SEPARATOR + SCRIPT_FILENAME + ".";
        if (StringUtils.hasText(request.getScriptExt())) {
            scriptFileName += request.getScriptExt();
        } else {
            scriptFileName += "out";
        }
        File scriptFile = new File(scriptFileName);
        try (FileOutputStream fos = new FileOutputStream(scriptFile)) {
            fos.write(request.getScript().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("创建脚本文件失败：{} {}", request.getTaskId(), e.getMessage());
            throw new HttpException("创建脚本文件失败" + request.getTaskId() + " " + e.getMessage());
        }
        request.setScriptFile(scriptFile);
        log.debug("结束创建脚本文件：{}", request.getTaskId());
    }

    public static void buildScriptPlaybookFile(ScriptRequest request) {
        String playbook = "---" +
                "\n- name: Run a script" +
                "\n  hosts: default-host" +
                "\n  tasks:" +
                "\n    - name: Run a script" +
                "\n      ansible.builtin.script:" +
                "\n      args:" +
                "\n        executable: '" + request.getExecutable() + "'" +
                "\n        cmd: '" + request.getScriptFile().getAbsolutePath();
        if (StringUtils.hasText(request.getArgs())) {
            playbook += " " + request.getArgs();
        }
        playbook += "'";
        buildPlaybookFile(request, playbook);
    }

    public static void buildCopyFile(MultipartFile file, CopyRequest request) {
        log.debug("开始创建复制文件：{}", request.getTaskId());
        File copyFile = new File(request.getTaskFolder().getAbsolutePath() + FILE_SEPARATOR + COPYFILE_FILENAME);
        try (FileOutputStream fos = new FileOutputStream(copyFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("创建复制文件失败：{} {}", request.getTaskId(), e.getMessage());
            throw new HttpException("创建复制文件失败" + request.getTaskId() + " " + e.getMessage());
        }
        request.setCopyFile(copyFile);
        log.debug("结束创建复制文件：{}", request.getTaskId());
    }

    public static void buildCopyPlaybookFile(CopyRequest request) {
        String playbook = """
                ---
                - name: Copy file
                  hosts: default-host
                  tasks:
                    - name: Copy file
                """;
        if (Boolean.TRUE.equals(request.getIsWindows())) {
            playbook += "      ansible.windows.win_copy:";
        } else {
            playbook += "      ansible.builtin.copy:";
        }
        playbook += "\n        src: '" + request.getCopyFile().getAbsolutePath() + "'"
                + "\n        dest: '" + request.getDest() + "'";
        if (StringUtils.hasText(request.getMode())) {
            playbook += "\n        mode: '" + request.getMode() + "'";
        }
        if (StringUtils.hasText(request.getGroup())) {
            playbook += "\n        group: '" + request.getGroup() + "'";
        }
        if (StringUtils.hasText(request.getOwner())) {
            playbook += "\n        owner: '" + request.getOwner() + "'";
        }
        buildPlaybookFile(request, playbook);
    }

    public static void buildPlaybookFile(BaseRequest request, String playbook) {
        log.debug("开始创建Playbook文件：{}", request.getTaskId());
        File playbookFile = new File(request.getTaskFolder().getAbsolutePath() + FILE_SEPARATOR + PLAYBOOK_FILENAME);
        try (FileOutputStream fos = new FileOutputStream(playbookFile)) {
            fos.write(playbook.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("创建Playbook文件失败：{} {}", request.getTaskId(), e.getMessage());
            throw new HttpException("创建Playbook文件失败" + request.getTaskId() + " " + e.getMessage());
        }
        request.setPlaybookFile(playbookFile);
        log.debug("结束创建Playbook文件：{}", request.getTaskId());
    }

    public static void cleanTaskFile(String taskId) {
        try {
            if (!cleanFile) {
                return;
            }
            if (!StringUtils.hasText(taskId)) {
                return;
            }
            log.debug("开始清理文件：{}", taskId);
            File taskFolder = new File(taskFolderPath + FILE_SEPARATOR + taskId);
            boolean delete = deleteFile(taskFolder);
            log.debug("结束清理文件: {} {}", taskId, delete);
        } catch (Exception e) {
            log.error("清理文件失败: {} {}", taskId, e.getMessage());
        }
    }

    private static String getInventory(BaseRequest request) {
        boolean isWindows = Boolean.TRUE.equals(request.getIsWindows());
        String inventory = "all:" +
                "\n  hosts:" +
                "\n    default-host:" +
                "\n      ansible_host: '" + request.getHost() + "'" +
                "\n      ansible_port: " + request.getPort() +
                "\n      ansible_user: '" + request.getUser() + "'";
        if (CredentialType.PRIVATE_KEY.equals(request.getCredentialType()) && !isWindows) {
            inventory += "\n      ansible_ssh_private_key_file: '" + request.getPrivateKeyFile().getAbsolutePath() + "'";
        } else {
            inventory += "\n      ansible_password: '" + request.getPassword().replace("'", "''") + "'";
        }
        if (Boolean.TRUE.equals(request.getBecome())) {
            inventory += "\n      ansible_become: true";
        } else {
            inventory += "\n      ansible_become: false";
        }
        BaseRequest proxy = request.getProxy();
        if (proxy != null && !isWindows) {
            inventory += "\n      ansible_ssh_common_args: '-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o ProxyCommand=\"";
            if (CredentialType.PRIVATE_KEY.equals(proxy.getCredentialType())) {
                inventory += "ssh -W %h:%p -i ''" + proxy.getPrivateKeyFile().getAbsolutePath() + "''";
            } else {
                inventory += "sshpass -p ''" + proxy.getPassword() + "'' ssh -W %h:%p";
            }
            inventory += " -p " + proxy.getPort() + " ''" + proxy.getUser() + "''@''" + proxy.getHost() + "''\"'";
        }
        if (isWindows) {
            inventory += "\n      ansible_connection: winrm";
            inventory += "\n      ansible_winrm_transport: ntlm";
            inventory += "\n      ansible_winrm_server_cert_validation: ignore";
        }
        return inventory;
    }

    private static boolean deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteFile(f);
                }
            }
        }
        return file.delete();
    }

}
