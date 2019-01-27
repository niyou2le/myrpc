package com.ding.myrpc.client.util;

import com.ding.myrpc.common.bean.RequestInfoClass;
import com.ding.myrpc.common.bean.ResponseInfoClass;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.UUID;

/**
 *
 * Rpc客户端工具类
 *
 */
public class RpcClientUtil {

    public static <T> T buildRpcClient(final Class<T> cls, final String host, final int port) {

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallback(new CreateMethodInterceptor(host, port));
        T t = (T) enhancer.create();
        return t;
    }

    static class CreateMethodInterceptor implements MethodInterceptor {

        final String host;
        final int port;

        public CreateMethodInterceptor(String host, int port) {
            this.host = host;
            this.port = port;
        }

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
    }
}
