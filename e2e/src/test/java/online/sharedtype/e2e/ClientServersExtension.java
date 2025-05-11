package online.sharedtype.e2e;

import online.sharedtype.processor.domain.TargetCodeType;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstancePreConstructCallback;
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Stack;

final class ClientServersExtension implements TestInstancePreConstructCallback, TestInstancePreDestroyCallback {
    private static final ObjectRemoteClientCaller caller = new ObjectRemoteClientCaller();
    private volatile Process process;

    @Override
    public void preConstructTestInstance(TestInstanceFactoryContext testInstanceFactoryContext, ExtensionContext extensionContext) throws Exception {
        ProcessBuilder builder = new ProcessBuilder("../misc/start-client-servers.sh");
        builder.redirectErrorStream(true);
        System.out.println("Executing server starting script...");
        process = builder.start();
        try {
            waitForServers();
            System.out.println("Servers started.");
        } catch (Exception e){
            System.err.println("Failed to start servers: " + e.getMessage());
            preDestroyTestInstance(extensionContext);
        }
    }

    @Override
    public void preDestroyTestInstance(ExtensionContext context) throws Exception {
        recursivelyStopAll();
        dumpOutput();
    }

    private void waitForServers() {
        var retryLeft = 10;
        while (retryLeft > 0) {
            try {
                Thread.sleep(1000);
                if (caller.isHealthy(TargetCodeType.GO)) {
                    return;
                }
            } catch (Exception e) {
                System.out.println("Error checking server health: " + e);
                System.out.println("Retrying...");
            }
            retryLeft--;
        }
        throw new RuntimeException("Servers timeout.");
    }

    private void recursivelyStopAll() {
        Stack<ProcessHandle> stack = new Stack<>();
        stack.push(process.toHandle());
        while (!stack.isEmpty()) {
            ProcessHandle p = stack.pop();
            System.out.printf("Destroying process %s%n", p.pid());
            p.destroy();
            p.children().forEach(stack::push);
        }
    }

    private void dumpOutput() throws Exception {
        BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) {
                break;
            }
            System.out.println(line);
        }
    }
}
