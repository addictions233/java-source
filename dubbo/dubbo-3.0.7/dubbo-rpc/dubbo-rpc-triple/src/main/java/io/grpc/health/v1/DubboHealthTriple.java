    package io.grpc.health.v1;

import org.apache.dubbo.common.stream.StreamObserver;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.PathResolver;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.ServerService;
import org.apache.dubbo.rpc.TriRpcStatus;
import org.apache.dubbo.rpc.model.MethodDescriptor;
import org.apache.dubbo.rpc.model.ServiceDescriptor;
import org.apache.dubbo.rpc.model.StubMethodDescriptor;
import org.apache.dubbo.rpc.model.StubServiceDescriptor;
import org.apache.dubbo.rpc.stub.BiStreamMethodHandler;
import org.apache.dubbo.rpc.stub.ServerStreamMethodHandler;
import org.apache.dubbo.rpc.stub.StubInvocationUtil;
import org.apache.dubbo.rpc.stub.StubInvoker;
import org.apache.dubbo.rpc.stub.StubMethodHandler;
import org.apache.dubbo.rpc.stub.StubSuppliers;
import org.apache.dubbo.rpc.stub.UnaryStubMethodHandler;

import com.google.protobuf.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.concurrent.CompletableFuture;

public final class DubboHealthTriple {

    public static final String SERVICE_NAME = Health.SERVICE_NAME;

    private static final StubServiceDescriptor serviceDescriptor = new StubServiceDescriptor(SERVICE_NAME,Health.class);

    static {
        StubSuppliers.addSupplier(SERVICE_NAME, DubboHealthTriple::newStub);
        StubSuppliers.addSupplier(Health.JAVA_SERVICE_NAME,  DubboHealthTriple::newStub);
        StubSuppliers.addDescriptor(SERVICE_NAME, serviceDescriptor);
        StubSuppliers.addDescriptor(Health.JAVA_SERVICE_NAME, serviceDescriptor);
    }

    @SuppressWarnings("all")
    public static Health newStub(Invoker<?> invoker) {
        return new HealthStub((Invoker<Health>)invoker);
    }

    private static final StubMethodDescriptor checkMethod = new StubMethodDescriptor("Check",
    io.grpc.health.v1.HealthCheckRequest.class, io.grpc.health.v1.HealthCheckResponse.class, serviceDescriptor, MethodDescriptor.RpcType.UNARY,
    obj -> ((Message) obj).toByteArray(), obj -> ((Message) obj).toByteArray(), io.grpc.health.v1.HealthCheckRequest::parseFrom,
    io.grpc.health.v1.HealthCheckResponse::parseFrom);

    /**
         * <pre>
         *  Performs a watch for the serving status of the requested service.
         *  The server will immediately send back a message indicating the current
         *  serving status.  It will then subsequently send a new message whenever
         *  the service&#39;s serving status changes.
         * 
         *  If the requested service is unknown when the call is received, the
         *  server will send a message setting the serving status to
         *  SERVICE_UNKNOWN but will &#42;not&#42; terminate the call.  If at some
         *  future point, the serving status of the service becomes known, the
         *  server will send a new message with the service&#39;s serving status.
         * 
         *  If the call terminates with status UNIMPLEMENTED, then clients
         *  should assume this method is not supported and should not retry the
         *  call.  If the call terminates with any other status (including OK),
         *  clients should retry the call with appropriate exponential backoff.
         * </pre>
         */
    private static final StubMethodDescriptor watchMethod = new StubMethodDescriptor("Watch",
    io.grpc.health.v1.HealthCheckRequest.class, io.grpc.health.v1.HealthCheckResponse.class, serviceDescriptor, MethodDescriptor.RpcType.SERVER_STREAM,
    obj -> ((Message) obj).toByteArray(), obj -> ((Message) obj).toByteArray(), io.grpc.health.v1.HealthCheckRequest::parseFrom,
    io.grpc.health.v1.HealthCheckResponse::parseFrom);




    public static class HealthStub implements Health{
        private final Invoker<Health> invoker;

        public HealthStub(Invoker<Health> invoker) {
            this.invoker = invoker;
        }

        @Override
        public io.grpc.health.v1.HealthCheckResponse check(io.grpc.health.v1.HealthCheckRequest request){
            return StubInvocationUtil.unaryCall(invoker, checkMethod, request);
        }

        @Override
        public void check(io.grpc.health.v1.HealthCheckRequest request, StreamObserver<io.grpc.health.v1.HealthCheckResponse> responseObserver){
            StubInvocationUtil.unaryCall(invoker, checkMethod , request, responseObserver);
        }

            /**
         * <pre>
         *  Performs a watch for the serving status of the requested service.
         *  The server will immediately send back a message indicating the current
         *  serving status.  It will then subsequently send a new message whenever
         *  the service&#39;s serving status changes.
         * 
         *  If the requested service is unknown when the call is received, the
         *  server will send a message setting the serving status to
         *  SERVICE_UNKNOWN but will &#42;not&#42; terminate the call.  If at some
         *  future point, the serving status of the service becomes known, the
         *  server will send a new message with the service&#39;s serving status.
         * 
         *  If the call terminates with status UNIMPLEMENTED, then clients
         *  should assume this method is not supported and should not retry the
         *  call.  If the call terminates with any other status (including OK),
         *  clients should retry the call with appropriate exponential backoff.
         * </pre>
         */
        @Override
        public void watch(io.grpc.health.v1.HealthCheckRequest request, StreamObserver<io.grpc.health.v1.HealthCheckResponse> responseObserver){
            StubInvocationUtil.serverStreamCall(invoker, watchMethod , request, responseObserver);
        }


    }

    public static abstract class HealthImplBase implements Health, ServerService<Health> {

        @Override
        public final Invoker<Health> getInvoker(URL url) {
            PathResolver pathResolver = url.getOrDefaultFrameworkModel()
            .getExtensionLoader(PathResolver.class)
            .getDefaultExtension();
            Map<String,StubMethodHandler<?, ?>> handlers = new HashMap<>();

            pathResolver.addNativeStub( "/" + SERVICE_NAME + "/Check" );
            pathResolver.addNativeStub( "/" + SERVICE_NAME + "/Watch" );

            BiConsumer<io.grpc.health.v1.HealthCheckRequest, StreamObserver<io.grpc.health.v1.HealthCheckResponse>> checkFunc = this::check;
            handlers.put(checkMethod.getMethodName(), new UnaryStubMethodHandler<>(checkFunc));

            handlers.put(watchMethod.getMethodName(), new ServerStreamMethodHandler<>(this::watch));



            return new StubInvoker<>(this, url, Health.class, handlers);
        }


        @Override
        public io.grpc.health.v1.HealthCheckResponse check(io.grpc.health.v1.HealthCheckRequest request){
            throw unimplementedMethodException(checkMethod);
        }


            /**
         * <pre>
         *  Performs a watch for the serving status of the requested service.
         *  The server will immediately send back a message indicating the current
         *  serving status.  It will then subsequently send a new message whenever
         *  the service&#39;s serving status changes.
         * 
         *  If the requested service is unknown when the call is received, the
         *  server will send a message setting the serving status to
         *  SERVICE_UNKNOWN but will &#42;not&#42; terminate the call.  If at some
         *  future point, the serving status of the service becomes known, the
         *  server will send a new message with the service&#39;s serving status.
         * 
         *  If the call terminates with status UNIMPLEMENTED, then clients
         *  should assume this method is not supported and should not retry the
         *  call.  If the call terminates with any other status (including OK),
         *  clients should retry the call with appropriate exponential backoff.
         * </pre>
         */
        @Override
        public void watch(io.grpc.health.v1.HealthCheckRequest request, StreamObserver<io.grpc.health.v1.HealthCheckResponse> responseObserver){
            throw unimplementedMethodException(watchMethod);
        }



        @Override
        public final ServiceDescriptor getServiceDescriptor() {
            return serviceDescriptor;
        }
        private RpcException unimplementedMethodException(StubMethodDescriptor methodDescriptor) {
            return TriRpcStatus.UNIMPLEMENTED.withDescription(String.format("Method %s is unimplemented",
                "/" + serviceDescriptor.getInterfaceName() + "/" + methodDescriptor.getMethodName())).asException();
        }
    }

}
