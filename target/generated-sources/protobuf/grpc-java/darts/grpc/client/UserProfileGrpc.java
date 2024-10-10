package darts.grpc.client;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 *endpoint point to send the message through
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.51.0)",
    comments = "Source: client/user_profile.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class UserProfileGrpc {

  private UserProfileGrpc() {}

  public static final String SERVICE_NAME = "grpc.details.UserProfile";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<darts.grpc.client.UserProfileOuterClass.Profile,
      darts.grpc.client.UserProfileOuterClass.Response> getAddProfileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AddProfile",
      requestType = darts.grpc.client.UserProfileOuterClass.Profile.class,
      responseType = darts.grpc.client.UserProfileOuterClass.Response.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<darts.grpc.client.UserProfileOuterClass.Profile,
      darts.grpc.client.UserProfileOuterClass.Response> getAddProfileMethod() {
    io.grpc.MethodDescriptor<darts.grpc.client.UserProfileOuterClass.Profile, darts.grpc.client.UserProfileOuterClass.Response> getAddProfileMethod;
    if ((getAddProfileMethod = UserProfileGrpc.getAddProfileMethod) == null) {
      synchronized (UserProfileGrpc.class) {
        if ((getAddProfileMethod = UserProfileGrpc.getAddProfileMethod) == null) {
          UserProfileGrpc.getAddProfileMethod = getAddProfileMethod =
              io.grpc.MethodDescriptor.<darts.grpc.client.UserProfileOuterClass.Profile, darts.grpc.client.UserProfileOuterClass.Response>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AddProfile"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  darts.grpc.client.UserProfileOuterClass.Profile.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  darts.grpc.client.UserProfileOuterClass.Response.getDefaultInstance()))
              .setSchemaDescriptor(new UserProfileMethodDescriptorSupplier("AddProfile"))
              .build();
        }
      }
    }
    return getAddProfileMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static UserProfileStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserProfileStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserProfileStub>() {
        @java.lang.Override
        public UserProfileStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserProfileStub(channel, callOptions);
        }
      };
    return UserProfileStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static UserProfileBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserProfileBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserProfileBlockingStub>() {
        @java.lang.Override
        public UserProfileBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserProfileBlockingStub(channel, callOptions);
        }
      };
    return UserProfileBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static UserProfileFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserProfileFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserProfileFutureStub>() {
        @java.lang.Override
        public UserProfileFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserProfileFutureStub(channel, callOptions);
        }
      };
    return UserProfileFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   *endpoint point to send the message through
   * </pre>
   */
  public static abstract class UserProfileImplBase implements io.grpc.BindableService {

    /**
     */
    public void addProfile(darts.grpc.client.UserProfileOuterClass.Profile request,
        io.grpc.stub.StreamObserver<darts.grpc.client.UserProfileOuterClass.Response> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAddProfileMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getAddProfileMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                darts.grpc.client.UserProfileOuterClass.Profile,
                darts.grpc.client.UserProfileOuterClass.Response>(
                  this, METHODID_ADD_PROFILE)))
          .build();
    }
  }

  /**
   * <pre>
   *endpoint point to send the message through
   * </pre>
   */
  public static final class UserProfileStub extends io.grpc.stub.AbstractAsyncStub<UserProfileStub> {
    private UserProfileStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserProfileStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserProfileStub(channel, callOptions);
    }

    /**
     */
    public void addProfile(darts.grpc.client.UserProfileOuterClass.Profile request,
        io.grpc.stub.StreamObserver<darts.grpc.client.UserProfileOuterClass.Response> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAddProfileMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   *endpoint point to send the message through
   * </pre>
   */
  public static final class UserProfileBlockingStub extends io.grpc.stub.AbstractBlockingStub<UserProfileBlockingStub> {
    private UserProfileBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserProfileBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserProfileBlockingStub(channel, callOptions);
    }

    /**
     */
    public darts.grpc.client.UserProfileOuterClass.Response addProfile(darts.grpc.client.UserProfileOuterClass.Profile request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddProfileMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   *endpoint point to send the message through
   * </pre>
   */
  public static final class UserProfileFutureStub extends io.grpc.stub.AbstractFutureStub<UserProfileFutureStub> {
    private UserProfileFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserProfileFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserProfileFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<darts.grpc.client.UserProfileOuterClass.Response> addProfile(
        darts.grpc.client.UserProfileOuterClass.Profile request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAddProfileMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ADD_PROFILE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final UserProfileImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(UserProfileImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ADD_PROFILE:
          serviceImpl.addProfile((darts.grpc.client.UserProfileOuterClass.Profile) request,
              (io.grpc.stub.StreamObserver<darts.grpc.client.UserProfileOuterClass.Response>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class UserProfileBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    UserProfileBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return darts.grpc.client.UserProfileOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("UserProfile");
    }
  }

  private static final class UserProfileFileDescriptorSupplier
      extends UserProfileBaseDescriptorSupplier {
    UserProfileFileDescriptorSupplier() {}
  }

  private static final class UserProfileMethodDescriptorSupplier
      extends UserProfileBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    UserProfileMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (UserProfileGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new UserProfileFileDescriptorSupplier())
              .addMethod(getAddProfileMethod())
              .build();
        }
      }
    }
    return result;
  }
}
