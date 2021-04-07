
package net.iGap.model;

import com.google.gson.annotations.SerializedName;

public class ConfigRes {
    @SerializedName("caption_length_max")
    public long captionLengthMax;
    @SerializedName("channel_add_member_limit")
    public long channelAddMemberLimit;
    @SerializedName("debug_mode")
    public boolean debugMode;
    @SerializedName("debugger")
    public String debugger;
    @SerializedName("default_timeout")
    public long defaultTimeout;
    @SerializedName("group_add_member_limit")
    public long groupAddMemberLimit;
    @SerializedName("max_file_size")
    public long maxFileSize;
    @SerializedName("message_length_max")
    public long messageLengthMax;
    @SerializedName("micro_services")
    public MicroServices microServices;
    @SerializedName("optimize_mode")
    public boolean optimizeMode;
    @SerializedName("websocket")
    public String webSocket;
}
