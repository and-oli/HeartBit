package com.firebase.uidemo.auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.firebase.ui.auth.AuthUI;
import com.firebase.uidemo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private FirebaseUser user = null;
    private View view = null;
    private static final String TAG = "BottomNavActivity";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        setUp();

        Button signOut = view.findViewById(R.id.sign_out);
        signOut.setOnClickListener(this);
        Button delAccount = view.findViewById(R.id.delete_account);
        delAccount.setOnClickListener(this);
        FloatingActionButton newRecord = view.findViewById(R.id.fab);
        newRecord.setOnClickListener(this);
        Button myReminders = view.findViewById(R.id.my_reminders);
        myReminders.setOnClickListener(this);

        return view;
    }

    public void setUp() {

        TextView nameText = view.findViewById(R.id.user_display_name);
        nameText.setText(user.getDisplayName());

        TextView emailText = view.findViewById(R.id.user_email);
        emailText.setText(user.getEmail());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_out:
                AuthUI.getInstance()
                        .signOut(getActivity())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    startActivity(AuthUiActivity.createIntent(getActivity()));
                                    getActivity().finish();
                                } else {
                                    Log.w(TAG, "signOut:failure", task.getException());
                                    Snackbar.make(view, R.string.sign_out_failed, Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                break;
            case R.id.delete_account:
                new AlertDialog.Builder(getActivity())
                        .setMessage("Are you sure you want to delete this account?")
                        .setPositiveButton("Yes, nuke it!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteAccount();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                break;

            case R.id.fab:
                startActivity(new Intent(view.getContext(), NewRecordActivity.class));
                break;

            case R.id.my_reminders:
                startActivity(new Intent(view.getContext(), RemindersActivity.class));
                break;
        }
    }

    private void deleteAccount() {
        AuthUI.getInstance()
                .delete(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(AuthUiActivity.createIntent(getActivity()));
                            getActivity().finish();
                        } else {
                            Snackbar.make(view, R.string.delete_account_failed, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void onResume(){
        super.onResume();
        ((BottomNavActivity)getActivity()).setActionBarTitle("Profile");
    }
}
