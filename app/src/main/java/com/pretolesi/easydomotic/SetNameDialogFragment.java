package com.pretolesi.easydomotic;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by RPRETOLESI on 20/03/2015.
 */
public class SetNameDialogFragment extends DialogFragment {

    private SetNameDialogFragmentCallbacks mCallbacks;
    EditText et;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String POSITION = "section_number";

    public static SetNameDialogFragment newInstance(int position) {
        SetNameDialogFragment fragment = new SetNameDialogFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public SetNameDialogFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (SetNameDialogFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        et = (EditText) getActivity().findViewById(R.id.id_et_room_name);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
/*
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Message 1")

                .setPositiveButton("Fire", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
*/
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.set_name_dialog_fragment, null))
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (mCallbacks != null) {
                            String str = "";
                            int position;
                            if (et != null) {
                                str = et.getText().toString();
                            }
                            position = getArguments().getInt(POSITION);
                            mCallbacks.onSetNameDialogFragmentClickListener(SetNameDialogFragment.this, position, str);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SetNameDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface SetNameDialogFragmentCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onSetNameDialogFragmentClickListener(DialogFragment dialog, int position,  String strName);
    }
}
