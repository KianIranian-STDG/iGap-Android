// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Response.proto

package net.iGap.proto;

public final class ProtoResponse {
  private ProtoResponse() {
  }

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface ResponseOrBuilder extends
          // @@protoc_insertion_point(interface_extends:proto.Response)
          com.google.protobuf.MessageOrBuilder {

    /**
     * <code>optional string id = 1;</code>
     */
    java.lang.String getId();
    /**
     * <code>optional string id = 1;</code>
     */
    com.google.protobuf.ByteString getIdBytes();

    /**
     * <code>optional uint32 timestamp = 2;</code>
     */
    int getTimestamp();
  }
  /**
   * Protobuf type {@code proto.Response}
   */
  public static final class Response extends com.google.protobuf.GeneratedMessageV3 implements
          // @@protoc_insertion_point(message_implements:proto.Response)
          ResponseOrBuilder {
    // Use Response.newBuilder() to construct.
    private Response(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Response() {
      id_ = "";
      timestamp_ = 0;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }

    private Response(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!input.skipField(tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              java.lang.String s = input.readStringRequireUtf8();

              id_ = s;
              break;
            }
            case 16: {

              timestamp_ = input.readUInt32();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(e).setUnfinishedMessage(this);
      } finally {
        makeExtensionsImmutable();
      }
    }

    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return net.iGap.proto.ProtoResponse.internal_static_proto_Response_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return net.iGap.proto.ProtoResponse.internal_static_proto_Response_fieldAccessorTable.ensureFieldAccessorsInitialized(net.iGap.proto.ProtoResponse.Response.class, net.iGap.proto.ProtoResponse.Response.Builder.class);
    }

    public static final int ID_FIELD_NUMBER = 1;
    private volatile java.lang.Object id_;
    /**
     * <code>optional string id = 1;</code>
     */
    public java.lang.String getId() {
      java.lang.Object ref = id_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        id_ = s;
        return s;
      }
    }
    /**
     * <code>optional string id = 1;</code>
     */
    public com.google.protobuf.ByteString getIdBytes() {
      java.lang.Object ref = id_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
        id_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int TIMESTAMP_FIELD_NUMBER = 2;
    private int timestamp_;
    /**
     * <code>optional uint32 timestamp = 2;</code>
     */
    public int getTimestamp() {
      return timestamp_;
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
      if (!getIdBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, id_);
      }
      if (timestamp_ != 0) {
        output.writeUInt32(2, timestamp_);
      }
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!getIdBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, id_);
      }
      if (timestamp_ != 0) {
        size += com.google.protobuf.CodedOutputStream.computeUInt32Size(2, timestamp_);
      }
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof net.iGap.proto.ProtoResponse.Response)) {
        return super.equals(obj);
      }
      net.iGap.proto.ProtoResponse.Response other = (net.iGap.proto.ProtoResponse.Response) obj;

      boolean result = true;
      result = result && getId().equals(other.getId());
      result = result && (getTimestamp() == other.getTimestamp());
      return result;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptorForType().hashCode();
      hash = (37 * hash) + ID_FIELD_NUMBER;
      hash = (53 * hash) + getId().hashCode();
      hash = (37 * hash) + TIMESTAMP_FIELD_NUMBER;
      hash = (53 * hash) + getTimestamp();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static net.iGap.proto.ProtoResponse.Response parseFrom(com.google.protobuf.ByteString data) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }

    public static net.iGap.proto.ProtoResponse.Response parseFrom(com.google.protobuf.ByteString data, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }

    public static net.iGap.proto.ProtoResponse.Response parseFrom(byte[] data) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }

    public static net.iGap.proto.ProtoResponse.Response parseFrom(byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }

    public static net.iGap.proto.ProtoResponse.Response parseFrom(java.io.InputStream input) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
    }

    public static net.iGap.proto.ProtoResponse.Response parseFrom(java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static net.iGap.proto.ProtoResponse.Response parseDelimitedFrom(java.io.InputStream input) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }

    public static net.iGap.proto.ProtoResponse.Response parseDelimitedFrom(java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static net.iGap.proto.ProtoResponse.Response parseFrom(com.google.protobuf.CodedInputStream input) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
    }

    public static net.iGap.proto.ProtoResponse.Response parseFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() {
      return newBuilder();
    }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(net.iGap.proto.ProtoResponse.Response prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code proto.Response}
     */
    public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
            // @@protoc_insertion_point(builder_implements:proto.Response)
            net.iGap.proto.ProtoResponse.ResponseOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
        return net.iGap.proto.ProtoResponse.internal_static_proto_Response_descriptor;
      }

      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return net.iGap.proto.ProtoResponse.internal_static_proto_Response_fieldAccessorTable.ensureFieldAccessorsInitialized(net.iGap.proto.ProtoResponse.Response.class, net.iGap.proto.ProtoResponse.Response.Builder.class);
      }

      // Construct using net.iGap.proto.ProtoResponse.Response.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        id_ = "";

        timestamp_ = 0;

        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
        return net.iGap.proto.ProtoResponse.internal_static_proto_Response_descriptor;
      }

      public net.iGap.proto.ProtoResponse.Response getDefaultInstanceForType() {
        return net.iGap.proto.ProtoResponse.Response.getDefaultInstance();
      }

      public net.iGap.proto.ProtoResponse.Response build() {
        net.iGap.proto.ProtoResponse.Response result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public net.iGap.proto.ProtoResponse.Response buildPartial() {
        net.iGap.proto.ProtoResponse.Response result = new net.iGap.proto.ProtoResponse.Response(this);
        result.id_ = id_;
        result.timestamp_ = timestamp_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }

      public Builder setField(com.google.protobuf.Descriptors.FieldDescriptor field, Object value) {
        return (Builder) super.setField(field, value);
      }

      public Builder clearField(com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }

      public Builder clearOneof(com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }

      public Builder setRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }

      public Builder addRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof net.iGap.proto.ProtoResponse.Response) {
          return mergeFrom((net.iGap.proto.ProtoResponse.Response) other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(net.iGap.proto.ProtoResponse.Response other) {
        if (other == net.iGap.proto.ProtoResponse.Response.getDefaultInstance()) return this;
        if (!other.getId().isEmpty()) {
          id_ = other.id_;
          onChanged();
        }
        if (other.getTimestamp() != 0) {
          setTimestamp(other.getTimestamp());
        }
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
        net.iGap.proto.ProtoResponse.Response parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (net.iGap.proto.ProtoResponse.Response) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private java.lang.Object id_ = "";
      /**
       * <code>optional string id = 1;</code>
       */
      public java.lang.String getId() {
        java.lang.Object ref = id_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          id_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string id = 1;</code>
       */
      public com.google.protobuf.ByteString getIdBytes() {
        java.lang.Object ref = id_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
          id_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string id = 1;</code>
       */
      public Builder setId(java.lang.String value) {
        if (value == null) {
          throw new NullPointerException();
        }
  
        id_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string id = 1;</code>
       */
      public Builder clearId() {

        id_ = getDefaultInstance().getId();
        onChanged();
        return this;
      }
      /**
       * <code>optional string id = 1;</code>
       */
      public Builder setIdBytes(com.google.protobuf.ByteString value) {
        if (value == null) {
          throw new NullPointerException();
        }
        checkByteStringIsUtf8(value);
        
        id_ = value;
        onChanged();
        return this;
      }

      private int timestamp_;
      /**
       * <code>optional uint32 timestamp = 2;</code>
       */
      public int getTimestamp() {
        return timestamp_;
      }
      /**
       * <code>optional uint32 timestamp = 2;</code>
       */
      public Builder setTimestamp(int value) {

        timestamp_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional uint32 timestamp = 2;</code>
       */
      public Builder clearTimestamp() {

        timestamp_ = 0;
        onChanged();
        return this;
      }

      public final Builder setUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }

      public final Builder mergeUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }


      // @@protoc_insertion_point(builder_scope:proto.Response)
    }

    // @@protoc_insertion_point(class_scope:proto.Response)
    private static final net.iGap.proto.ProtoResponse.Response DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new net.iGap.proto.ProtoResponse.Response();
    }

    public static net.iGap.proto.ProtoResponse.Response getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Response> PARSER = new com.google.protobuf.AbstractParser<Response>() {
      public Response parsePartialFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
        return new Response(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Response> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Response> getParserForType() {
      return PARSER;
    }

    public net.iGap.proto.ProtoResponse.Response getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor internal_static_proto_Response_descriptor;
  private static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internal_static_proto_Response_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }

  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;
  static {
    java.lang.String[] descriptorData = {
            "\n\016Response.proto\022\005proto\")\n\010Response\022\n\n\002i" +
                    "d\030\001 \001(\t\022\021\n\ttimestamp\030\002 \001(\rB\037\n\016net.iGap.p" +
                    "rotoB\rProtoResponseb\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
      public com.google.protobuf.ExtensionRegistry assignDescriptors(com.google.protobuf.Descriptors.FileDescriptor root) {
        descriptor = root;
        return null;
      }
    };
    com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new com.google.protobuf.Descriptors.FileDescriptor[]{
    }, assigner);
    internal_static_proto_Response_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_proto_Response_fieldAccessorTable = new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(internal_static_proto_Response_descriptor, new java.lang.String[]{"Id", "Timestamp",});
  }

  // @@protoc_insertion_point(outer_class_scope)
}
