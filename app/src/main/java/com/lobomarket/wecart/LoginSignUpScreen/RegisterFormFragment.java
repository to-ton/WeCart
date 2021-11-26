package com.lobomarket.wecart.LoginSignUpScreen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lobomarket.wecart.R;

import hari.bounceview.BounceView;

public class RegisterFormFragment extends Fragment {

    public static InputFilter EMOJI_FILTER = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int index = start; index < end; index++) {

                int type = Character.getType(source.charAt(index));

                if (type == Character.SURROGATE) {
                    return "";
                }
            }
            return null;
        }
    };

    private String blockCharacterSet = "'\"’";

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_register_form, container, false);
    }

    private Button back, register;
    private NavController navController;
    private NavOptions navOptions;
    private EditText txtFnam, txtUname, txtPass, txtContact, txtEmail;
    String emailPattern = "[a-zA-Z0-9._+-]+@[a-z]+\\.+[a-z]+";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            back = view.findViewById(R.id.btnRegBack);
            BounceView.addAnimTo(back);
            register = view.findViewById(R.id.btnRegister);
            BounceView.addAnimTo(register);
            navController = Navigation.findNavController(view);
            txtFnam = view.findViewById(R.id.txtFullname);
            txtUname = view.findViewById(R.id.txtRegUsername);
            txtPass = view.findViewById(R.id.txtRegPassword);
            txtContact = view.findViewById(R.id.txtContactNumber);
            txtEmail = view.findViewById(R.id.txtEmail);

            txtUname.setFilters(new InputFilter[]{EMOJI_FILTER, filter});

            try{
                txtFnam.setText(getArguments().getString("fullName"));
                txtUname.setText(getArguments().getString("username"));
                txtPass.setText(getArguments().getString("password"));
                txtContact.setText(getArguments().getString("contact"));
                txtEmail.setText(getArguments().getString("email"));
            } catch (NullPointerException e){
                Log.v("TEST", "Error: " + e.toString());
            }


            navOptions = new NavOptions.Builder().setPopUpTo(R.id.welcomeScreenFragment, true)
                    .setEnterAnim(R.anim.slide_left_to_right)
                    .setExitAnim(R.anim.wait_anim)
                    .setPopEnterAnim(R.anim.wait_anim)
                    .setPopExitAnim(R.anim.slide_l2r_reverse)
                    .build();

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    navController.navigate(R.id.action_registerForm_to_welcomeScreenFragment2, null, navOptions);
                }
            });

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle bundle = new Bundle();
                    bundle.putString("fullName", txtFnam.getText().toString());
                    bundle.putString("username", txtUname.getText().toString());
                    bundle.putString("password", txtPass.getText().toString());
                    bundle.putString("contact", txtContact.getText().toString());
                    bundle.putString("email", txtEmail.getText().toString());


                    if(txtFnam.getText().length() != 0 && txtUname.getText().length() != 0 &&
                            txtPass.getText().length() != 0 && txtContact.getText().length() != 0 &&
                            txtEmail.getText().length() != 0){
                        if(txtContact.getText().length() == 11 || txtContact.getText().length() == 13){
                            if (txtEmail.getText().toString().trim().matches(emailPattern)) {
                                txtEmail.setError(null);
                                navController.navigate(R.id.action_registerForm_to_locationFormFragment2, bundle);
                            } else {
                                txtEmail.setError("Invalid email address");
                            }
                        } else {
                            txtContact.setError("Enter a valid number");
                        }

                    } else {
                        txtFnam.setError("Required");
                        txtUname.setError("Required");
                        txtPass.setError("Required");
                        txtContact.setError("Required");
                        txtEmail.setError("Required");
                    }


                }
            });
        } catch (Exception e) {
            Log.e("Register form", "exception", e);
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }


}