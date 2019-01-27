package com.ding.myrpc.util;

import com.ding.myrpc.bean.RequestInfoClass;
import com.ding.myrpc.bean.ResponseInfoClass;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RpcBuilder {

    private static final int nThreads = Runtime.getRuntime().availableProcessors() * 2;
    private static ExecutorService handlerPool = Executors.newFixedThreadPool(nThreads);

    public static <T> T buildRpcClient(final Class<T> cls, final String host, final int port) {

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                Object returnData = null;

                Socket socket = null;
                ObjectInputStream in = null;
                ObjectOutputStream out = null;

                try {
                    socket = new Socket(host, port);
                    in = new ObjectInputStream(socket.getInputStream());
                    out = new ObjectOutputStream(socket.getOutputStream());


                    RequestInfoClass request = RequestInfoClass.builder()
                            .requestId(UUID.randomUUID().toString())
                            .className(method.getDeclaringClass().getName())
                            .methodName(method.getName())
                            .parameterTypes(method.getParameterTypes())
                            .parameters(args)
                            .build();

                    out.writeObject(request);
                    out.flush();

                    Object responseObject = in.readObject();
                    if (responseObject instanceof ResponseInfoClass) {
                        ResponseInfoClass responseInfoClass = (ResponseInfoClass) responseObject;
                        if (responseInfoClass.isError()) {
                            throw responseInfoClass.getCause();
                        } else {
                            returnData = responseInfoClass.getData();
                        }
                    }

                } finally {
                    out.close();
                    in.close();
                    socket.close();
                }

                return returnData;
            }
        });
        T t = (T) enhancer.create();
        return t;
    }

    public static void buildRpcServer(final Object service, final int port) throws IOException {

        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {
            Socket socket = serverSocket.accept();
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

