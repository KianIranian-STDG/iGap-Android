package net.iGap.model;

public class GoToShowMemberModel {
    private long roomId;
    private String role;
    private long userId;
    private String selectedRole;
    private boolean isNeedGetMemberList;

    public GoToShowMemberModel(long roomId, String role, long userId, String selectedRole, boolean isNeedGetMemberList) {
        this.roomId = roomId;
        this.role = role;
        this.userId = userId;
        this.selectedRole = selectedRole;
        this.isNeedGetMemberList = isNeedGetMemberList;
    }

    public long getRoomId() {
        return roomId;
    }

    public String getRole() {
        return role;
    }

    public long getUserId() {
        return userId;
    }

    public String getSelectedRole() {
        return selectedRole;
    }

    public boolean isNeedGetMemberList() {
        return isNeedGetMemberList;
    }
}
