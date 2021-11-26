package com.lobomarket.wecart.LoginSignUpScreen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.R;

import hari.bounceview.BounceView;

public class WelcomeScreenFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_welcome_screen, container, false);
    }

    private Button login, register;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);

        login = view.findViewById(R.id.btnToRegForm);
        register = view.findViewById(R.id.btnToLoginForm);
        BounceView.addAnimTo(login);
        BounceView.addAnimTo(register);




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_welcomeScreenFragment_to_loginFormFragment);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_welcomeScreenFragment_to_registerForm);
            }
        });


    }
}