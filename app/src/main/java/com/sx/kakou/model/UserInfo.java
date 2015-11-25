package com.sx.kakou.model;

/**
 * Created by mglory on 2015/11/9.
 */
public class UserInfo {
    private int UserID;
    private int RoleID;
    private String UserName;
    private String RoleName;

    public UserInfo() {
    }

    public UserInfo(String userName, int userID, int roleID, String roleName) {
        UserName = userName;
        UserID = userID;
        RoleID = roleID;
        RoleName = roleName;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getRoleID() {
        return RoleID;
    }

    public void setRoleID(int roleID) {
        RoleID = roleID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getRoleName() {
        return RoleName;
    }

    public void setRoleName(String roleName) {
        RoleName = roleName;
    }
}
