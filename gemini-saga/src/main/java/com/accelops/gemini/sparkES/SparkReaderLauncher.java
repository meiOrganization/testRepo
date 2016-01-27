package com.accelops.gemini.sparkES;

import com.accelops.libra.communication.CommunicationType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static com.accelops.libra.communication.CommunicationType.*;

/**
 * Created by kai.zhang on 1/19/2016.
 */
public class SparkReaderLauncher {

    public static final int LAUNCHER_PORT = 7919;
    private static final Log log = LogFactory.getLog(SparkReaderLauncher.class);
    private static SparkReaderLauncher instance;
    private volatile boolean needExit = false;
    private ServerSocketChannel server = null;
    private IngestionJobManager ingestionJobManager = null;
    private final ByteBuffer resultBuffer = ByteBuffer.allocate(1024);

    protected SparkReaderLauncher() {
        ingestionJobManager = new IngestionJobManager();
    }

    public static SparkReaderLauncher getInstance() {
        if(instance == null) {
            synchronized (SparkReaderLauncher.class) {
                if(instance == null)
                    instance = new SparkReaderLauncher();
            }
        }
        return instance;
    }


    public static void main(final String[] args) {

        log.info("SparkReaderLauncher is starting...");

        SparkReaderLauncher launcher = SparkReaderLauncher.getInstance();
        if(!launcher.initialize()) {
            log.error("Failed to initialize SparkReaderLauncher");
            return;
        }

        launcher.run();

    }

    public boolean initialize() {

        resultBuffer.order(ByteOrder.LITTLE_ENDIAN);

        try {
            server = ServerSocketChannel.open().bind(new InetSocketAddress(LAUNCHER_PORT));
            server.configureBlocking(false);
        } catch (IOException e) {
            log.error("Failed to bind port: " + LAUNCHER_PORT);
            return false;
        }

        if(!ingestionJobManager.initialize())
            return false;

        return true;

    }

    public void run() {

        try {
            log.info("Launcher started! Running...");
            // 4 bytes: event type id
            // 4 bytes: message length
            // 4 bytes: process hash (never use)
            // 4 bytes: event seq id (never use)
            ByteBuffer signalBuffer = ByteBuffer.allocate(16);
            signalBuffer.order(ByteOrder.LITTLE_ENDIAN);
            while(!needExit) {
                signalBuffer.clear();
                if (!readFromSocketChannel(server, signalBuffer))
                    Thread.sleep(1000l);
            }

        } catch (IOException | InterruptedException e) {
        }

    }

    private boolean readFromSocketChannel(final ServerSocketChannel server, final ByteBuffer signalBuffer) throws IOException {

        SocketChannel socketClient = server.accept();
        if(socketClient != null) {
            int count = socketClient.read(signalBuffer);
            if(count <= 0) {
                return true;
            }
            signalBuffer.flip();
            int type = signalBuffer.getInt();
            int length = signalBuffer.getInt();
            String request = null;

            if(length != 0) {
                ByteBuffer requestBuffer = ByteBuffer.allocate(length);
                requestBuffer.order(ByteOrder.nativeOrder());
                count = 0;
                while (count < length) {
                    count += socketClient.read(requestBuffer);
                }
                requestBuffer.flip();
                request = new String(requestBuffer.array(), 0, length);
            }

            handleRequest(type, request);
            socketClient.write(resultBuffer);
        } else {
            return false;
        }

        return true;

    }

    private void handleRequest(int type, String requestCmd) {

        CommunicationType cType = getTypeFromId(type);
        switch (cType) {
            case EXPORT_DATA: {
                ingestionJobManager.addJob(requestCmd, resultBuffer);
                break;
            }
            case EXPORT_DATA_STATUS: {
                ingestionJobManager.getStatus(requestCmd, resultBuffer);
                break;
            }
            case STOP_PROCESS: {
                break;
            }
            default:
                log.warn("Unknown request type: " + type);
        }

    }

    public void launch() {
        SparkAppHandle handle;
        try {
            handle = new SparkLauncher()
                    .setAppResource("/home/admin/gemini-saga-1.0.jar")
                    .setMainClass("com.accelops.gemini.sparkES.SparkSeqTestReader")
                    .setMaster("spark://ip-10-0-0-248:7077")
                    .setConf(SparkLauncher.DRIVER_MEMORY, "8g")
                    .addAppArgs("")
                    .startApplication();

            while(!handle.getState().isFinal()) {
                System.out.println("Job running: "  + handle.getState().name());
                Thread.sleep(3000);
            }

            System.out.println("Job finished: " + handle.getState().name());

            Thread.sleep(10000);

            System.out.println("Exit");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
