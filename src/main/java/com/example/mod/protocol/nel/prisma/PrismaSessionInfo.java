package com.example.mod.protocol.nel.prisma;

import com.google.gson.annotations.SerializedName;


public class PrismaSessionInfo {
    @SerializedName("port")
    public int port;
    @SerializedName("neteaseEntityID")
    public String entityId;
    @SerializedName("roleName")
    public String username;
    @SerializedName("neteaseToken")
    public String token;
    @SerializedName("roleUUID")
    public String playerUuid;
    @SerializedName("roleGameUUID")
    public String playerGameUuid = "";
    @SerializedName("roleID")
    public String serverEntityId;
}
