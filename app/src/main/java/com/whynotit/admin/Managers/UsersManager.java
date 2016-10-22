package com.whynotit.admin.Managers;

import com.whynotit.admin.Models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harzallah on 27/07/2016.
 */
public class UsersManager {
    private static UsersManager instance;
    private List<User> userList = new ArrayList<>();

    public static UsersManager getInstance() {
        if (instance == null) {
            instance = new UsersManager();
        }
        return instance;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
