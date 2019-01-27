package com.ding.myrpc.server.util;

import com.ding.myrpc.common.bean.RequestInfoClass;
import com.ding.myrpc.common.bean.ResponseInfoClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * Rpc服务端工具
 *
 */
public class RpcServerUtil {

    private final static Logger LOG = LoggerFactory.getLogger(RpcServerUtil.class);

    private static final int nThreads = Runtime.getRuntime().availableProcessors() * 2;
    private static ExecutorService handlerPool = Executors.newFixedThreadPool(nThreads);

    public static void buildRpcServer(final Object service, final int port) throws IOException {

        ServerSocket serverSocket = new ServerSocket(port);
        LOG.info("Server listen {}", port);

        while (true) {
            Socket socket = serverSocket.accept();
            LOG.info("Create a channel");

            handlerPool.submit(new Handler(service, socket));
        }
    }

    static class Handler implements Runnable{

        private Object service;
        private Socket socket;

        public Handler(Object service, Socket socket) {
            this.service = service;
            this.socket = socket;
        }

        @Override
        public void run() {

            ObjectInputStream in = null;
            ObjectOutputStream out = null;
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                Object requestObject = in.readObject();
                if (requestObject instanceof RequestInfoClass) {
                    RequestInfoClass requestInfoClass = (RequestInfoClass) requestObject;
                    Method method = service.getClass().getMethod(requestInfoClass.getMethodName(), requestInfoClass.getParameterTypes());

                    ResponseInfoClass responseInfoClass = new ResponseInfoClass();
                    responseInfoClass.setRequestId(requestInfoClass.getRequestId());
                    try {
                        Object result = method.invoke(service, requestInfoClass.getParameters());
                        responseInfoClass.setData(result);
                    } catch (Exception e) {
                        responseInfoClass.setCause(e);
                    }

                    out.writeObject(responseInfoClass);
                    out.flush();
                }

            } catch (IOException | NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                    in.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
