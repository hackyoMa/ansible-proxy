package com.github.ansibleproxy.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.*;
import org.apache.commons.exec.Executor;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

/**
 * ExecUtil
 *
 * @author hackyo
 * @since 1.0.0
 */
@Slf4j
public final class ExecUtil {

    private static final long TIMEOUT_BUFFER_MS = 1000L;
    private static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    public static void close() {
        EXECUTOR.shutdownNow();
    }

    public static ExecResult exec(String command, Duration execTimeout)
            throws IOException, ExecutionException, InterruptedException {
        return exec(CommandLine.parse(command), execTimeout);
    }

    public static ExecResult exec(CommandLine commandLine, Duration execTimeout)
            throws IOException, ExecutionException, InterruptedException {
        ConcurrentLinkedQueue<String> stdoutQueue = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<String> stderrQueue = new ConcurrentLinkedQueue<>();
        CompletableFuture<Integer> completable;
        try (OutputStream outputStream = new CollectingLogOutputStream(stdoutQueue);
             OutputStream errorOutputStream = new CollectingLogOutputStream(stderrQueue)) {
            completable = exec(commandLine, outputStream, errorOutputStream, execTimeout);
        }
        return new ExecResult(completable.get() == 0, List.copyOf(stdoutQueue), List.copyOf(stderrQueue));
    }

    private static CompletableFuture<Integer> exec(CommandLine commandLine, OutputStream outputStream,
                                                   OutputStream errorOutputStream, Duration execTimeout) {
        if (commandLine == null || execTimeout == null || execTimeout.isNegative()) {
            return CompletableFuture.completedFuture(-1);
        }

        ExecuteWatchdog watchdog = ExecuteWatchdog.builder().setTimeout(execTimeout).get();
        Executor executor = DefaultExecutor.builder().get();
        executor.setWatchdog(watchdog);
        executor.setStreamHandler(new PumpStreamHandler(outputStream, errorOutputStream));

        return CompletableFuture.supplyAsync(() -> {
                    try {
                        return executor.execute(commandLine);
                    } catch (ExecuteException e) {
                        log.error("Error executing command", e);
                        return e.getExitValue();
                    } catch (Exception e) {
                        log.error("Error executing command", e);
                        throw new CompletionException(e);
                    }
                }, EXECUTOR)
                .orTimeout(execTimeout.toMillis() + TIMEOUT_BUFFER_MS, TimeUnit.MILLISECONDS)
                .exceptionally(_ -> {
                    watchdog.destroyProcess();
                    return -1;
                });
    }

    public record ExecResult(boolean success, List<String> stdout, List<String> stderr) {
    }

    private static class CollectingLogOutputStream extends LogOutputStream {
        private final ConcurrentLinkedQueue<String> queue;

        CollectingLogOutputStream(ConcurrentLinkedQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        protected void processLine(String line, int logLevel) {
            queue.offer(line);
        }
    }

}
