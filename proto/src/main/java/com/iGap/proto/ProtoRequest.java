// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Request.proto

package com.iGap.proto;

public final class ProtoRequest {
  private ProtoRequest() {
  }
  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface RequestOrBuilder extends
      // @@protoc_insertion_point(interface_extends:proto.Request)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>optional string id = 1;</code>
     */
    java.lang.String getId();
    /**
     * <code>optional string id = 1;</code>
     */
    com.google.protobuf.ByteString getIdBytes();
  }
  /**
   * Protobuf type {@code proto.Request}
   */
  public static final class Request extends com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:proto.Request)
      RequestOrBuilder {
    // Use Request.newBuilder() to construct.
    private Request(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Request() {
      id_ = "";
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }
    private Request(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
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
      return com.iGap.proto.ProtoRequest.internal_static_proto_Request_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return com.iGap.proto.ProtoRequest.internal_static_proto_Request_fieldAccessorTable.ensureFieldAccessorsInitialized(com.iGap.proto.ProtoRequest.Request.class, com.iGap.proto.ProtoRequest.Request.Builder.class);
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
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!getIdBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, id_);
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
      if (!(obj instanceof com.iGap.proto.ProtoRequest.Request)) {
        return super.equals(obj);
      }
      com.iGap.proto.ProtoRequest.Request other = (com.iGap.proto.ProtoRequest.Request) obj;

      boolean result = true;
      result = result && getId().equals(other.getId());
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
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.iGap.proto.ProtoRequest.Request parseFrom(com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.iGap.proto.ProtoRequest.Request parseFrom(com.google.protobuf.ByteString data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.iGap.proto.ProtoRequest.Request parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.iGap.proto.ProtoRequest.Request parseFrom(byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.iGap.proto.ProtoRequest.Request parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    public static com.iGap.proto.ProtoRequest.Request parseFrom(java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.iGap.proto.ProtoRequest.Request parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    public static com.iGap.proto.ProtoRequest.Request parseDelimitedFrom(java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.iGap.proto.ProtoRequest.Request parseFrom(com.google.protobuf.CodedInputStream input) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    public static com.iGap.proto.ProtoRequest.Request parseFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() {
      return newBuilder();
    }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(com.iGap.proto.ProtoRequest.Request prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code proto.Request}
     */
    public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:proto.Request)
        com.iGap.proto.ProtoRequest.RequestOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
        return com.iGap.proto.ProtoRequest.internal_static_proto_Request_descriptor;
      }

      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return com.iGap.proto.ProtoRequest.internal_static_proto_Request_fieldAccessorTable.ensureFieldAccessorsInitialized(com.iGap.proto.ProtoRequest.Request.class, com.iGap.proto.ProtoRequest.Request.Builder.class);
      }

      // Construct using com.iGap.proto.ProtoRequest.Request.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        id_ = "";

        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
        return com.iGap.proto.ProtoRequest.internal_static_proto_Request_descriptor;
      }

      public com.iGap.proto.ProtoRequest.Request getDefaultInstanceForType() {
        return com.iGap.proto.ProtoRequest.Request.getDefaultInstance();
      }

      public com.iGap.proto.ProtoRequest.Request build() {
        com.iGap.proto.ProtoRequest.Request result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.iGap.proto.ProtoRequest.Request buildPartial() {
        com.iGap.proto.ProtoRequest.Request result = new com.iGap.proto.ProtoRequest.Request(this);
        result.id_ = id_;
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
        if (other instanceof com.iGap.proto.ProtoRequest.Request) {
          return mergeFrom((com.iGap.proto.ProtoRequest.Request) other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.iGap.proto.ProtoRequest.Request other) {
        if (other == com.iGap.proto.ProtoRequest.Request.getDefaultInstance()) return this;
        if (!other.getId().isEmpty()) {
          id_ = other.id_;
          onChanged();
        }
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
        com.iGap.proto.ProtoRequest.Request parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.iGap.proto.ProtoRequest.Request) e.getUnfinishedMessage();
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
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }


      // @@protoc_insertion_point(builder_scope:proto.Request)
    }

    // @@protoc_insertion_point(class_scope:proto.Request)
    private static final com.iGap.proto.ProtoRequest.Request DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.iGap.proto.ProtoRequest.Request();
    }

    public static com.iGap.proto.ProtoRequest.Request getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Request> PARSER = new com.google.protobuf.AbstractParser<Request>() {
      public Request parsePartialFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
        return new Request(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Request> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Request> getParserForType() {
      return PARSER;
    }

    public com.iGap.proto.ProtoRequest.Request getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor internal_static_proto_Request_descriptor;
  private static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_proto_Request_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }

  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;
  static {
    java.lang.String[] descriptorData = {
            "\n\rRequest.proto\022\005proto\"\025\n\007Request\022\n\n\002id\030" +
                    "\001 \001(\tB\036\n\016com.iGap.protoB\014ProtoRequestb\006p" +
                    "roto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_proto_Request_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_proto_Request_fieldAccessorTable = new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(internal_static_proto_Request_descriptor, new java.lang.String[]{"Id",});
  }

  // @@protoc_insertion_point(outer_class_scope)
}
