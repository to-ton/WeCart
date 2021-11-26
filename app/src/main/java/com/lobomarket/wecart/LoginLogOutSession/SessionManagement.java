package com.lobomarket.wecart.LoginLogOutSession;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "session";
    String SESSION_KEY = "session_id";
    String SESSION_KE_2 = "session_role";
    String SESSION_KE_3 = "session_username";


    public SessionManagement(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(Users users){
        //save session of user whenever user is logged in
        int id = users.getId();
        String role = users.getName();
        String username = users.getUsername();

        editor.putInt(SESSION_KEY, id)
                .putString(SESSION_KE_2, role)
                .putString(SESSION_KE_3, username)
                .commit();
    }

    public int getSession(){
        //return user id whose session is saved
        return sharedPreferences.getInt(SESSION_KEY, -1); //-1 is the default value of the shared preferences
    }

    public String getSession2(){
        return sharedPreferences.getString(SESSION_KE_2, "");
    }

    public String getSession3(){
        return sharedPreferences.getString(SESSION_KE_3, "");
    }

    public void removeSession(){
        //this will remove the current session_id and it will return -1 as the default value
        editor.putInt(SESSION_KEY, -1).commit();
    }
}
