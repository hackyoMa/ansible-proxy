package com.github.ansibleproxy.util;

import com.github.ansibleproxy.ansible.entity.TaskResult;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ProcessUtils
 *
 * @author hackyo
 * @since 2022/4/1
 */
@Log4j2
public final class ProcessUtils {

    public static boolean ansibleDebug = false;

    public static TaskResult execPlaybook(long timeout, String playbookFilePath, String inventoryFilePath) {
        Long startTime = System.currentTimeMillis();
        log.debug("开始执行任务：{} {} {} {}", startTime, timeout, playbookFilePath, inventoryFilePath);

        boolean success = false;
        List<String> stdout = Collections.synchronizedList(new ArrayList<>());
        List<String> stderr = Collections.synchronizedList(new ArrayList<>());

        Process process = null;
        InputStream errorStream = null;
        InputStream inputStream = null;
        Thread errReadOutThread = null;
        Thread outReadOutThread = null;

        ProcessBuilder processBuilder;
        if (ansibleDebug) {
            processBuilder = new ProcessBuilder("ansible-playbook", playbookFilePath, "-i", inventoryFilePath, "-vvv");
        } else {
            processBuilder = new ProcessBuilder("ansible-playbook", playbookFilePath, "-i", inventoryFilePath);
        }
        log.debug("构建任务成功：{} {} {} {} {}", startTime, timeout, playbookFilePath, inventoryFilePath, Json.toJsonString(processBuilder.command()));

        try {
            process = processBuilder.start();
            log.debug("下发任务成功：{} {} {} {}", startTime, timeout, playbookFilePath, inventoryFilePath);

            errorStream = process.getErrorStream();
            InputStreamReader errorStreamReader = new InputStreamReader(errorStream, StandardCharsets.UTF_8);
            inputStream = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            errReadOutThread = Thread.startVirtualThread(() -> {
                try {
                    BufferedReader bufferedReader = new BufferedReader(errorStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stderr.add(line);
                    }
                } catch (Exception e) {
                    log.error("读取错误流失败：{} {} {} {} {}", startTime, timeout, playbookFilePath, inventoryFilePath, e.getMessage());
                    stderr.add("读取错误流失败: " + e.getMessage());
                }
            });

            outReadOutThread = Thread.startVirtualThread(() -> {
                try {
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stdout.add(line);
                    }
                } catch (Exception e) {
                    log.error("读取输入流失败：{} {} {} {} {}", startTime, timeout, playbookFilePath, inventoryFilePath, e.getMessage());
                    stderr.add("读取输入流失败: " + e.getMessage());
                }
            });

            if (process.waitFor(timeout, TimeUnit.SECONDS)) {
                log.debug("任务执行成功：{} {} {} {}", startTime, timeout, playbookFilePath, inventoryFilePath);
                success = true;
            } else {
                log.error("任务执行超时：{} {} {} {}", startTime, timeout, playbookFilePath, inventoryFilePath);
                stderr.add("任务执行超时");
            }
        } catch (Exception e) {
            log.error("任务执行失败：{} {} {} {} {}", startTime, timeout, playbookFilePath, inventoryFilePath, e.getMessage());
            stderr.add("任务执行失败: " + e.getMessage());
        } finally {
            if (errorStream != null) {
                try {
                    errorStream.close();
                    log.debug("关闭错误流成功：{} {} {} {}", startTime, timeout, playbookFilePath, inventoryFilePath);
                } catch (Exception e) {
                    log.error("关闭错误流失败：{} {} {} {} {}", startTime, timeout, playbookFilePath, inventoryFilePath, e.getMessage());
                    stderr.add("关闭错误流失败: " + e.getMessage());
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                    log.debug("关闭输入流成功：{} {} {} {}", startTime, timeout, playbookFilePath, inventoryFilePath);
                } catch (Exception e) {
                    log.error("关闭输入流失败：{} {} {} {} {}", startTime, timeout, playbookFilePath, inventoryFilePath, e.getMessage());
                    stderr.add("关闭输入流失败: " + e.getMessage());
                }
            }
            if (errReadOutThread != null) {
                try {
                    errReadOutThread.interrupt();
                    log.debug("关闭错误流读取线程成功：{} {} {} {}", startTime, timeout, playbookFilePath, inventoryFilePath);
                } catch (Exception e) {
                    log.error("关闭错误流读取线程失败：{} {} {} {} {}", startTime, timeout, playbookFilePath, inventoryFilePath, e.getMessage());
                    stderr.add("关闭错误流读取线程失败: " + e.getMessage());
                }
            }
            if (outReadOutThread != null) {
                try {
                    outReadOutThread.interrupt();
                    log.debug("关闭输出流读取线程成功：{} {} {} {}", startTime, timeout, playbookFilePath, inventoryFilePath);
                } catch (Exception e) {
                    log.error("关闭输出流读取线程失败：{} {} {} {} {}", startTime, timeout, playbookFilePath, inventoryFilePath, e.getMessage());
                    stderr.add("关闭输出流读取线程失败: " + e.getMessage());
                }
            }
            if (process != null) {
                try {
                    String processPid = String.valueOf(process.pid());
                    new ProcessBuilder("pkill", "-9", "-P", processPid).start();
                    log.debug("任务结束成功：{} {} {} {}", startTime, timeout, playbookFilePath, inventoryFilePath);
                } catch (Exception e) {
                    log.error("任务结束失败：{} {} {} {} {}", startTime, timeout, playbookFilePath, inventoryFilePath, e.getMessage());
                    stderr.add("任务结束失败: " + e.getMessage());
                }
            }
        }

        Long endTime = System.currentTimeMillis();
        log.debug("结束执行任务：{} {} {} {} {}", startTime, endTime, timeout, playbookFilePath, inventoryFilePath);
        return new TaskResult(startTime, endTime, success, stdout, stderr);
    }

}
