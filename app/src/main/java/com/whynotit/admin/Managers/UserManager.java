package com.whynotit.admin.Managers;

import android.view.View;

import com.whynotit.admin.Models.User;

/**
 * Created by Harzallah on 28/07/2016.
 */
public class UserManager {
    private static UserManager instance;
    private View userLayout = null;
    private User user;

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public View getUserLayout() {
        return userLayout;
    }

    public void setUserLayout(View userLayout) {
        this.userLayout = userLayout;
    }
}
