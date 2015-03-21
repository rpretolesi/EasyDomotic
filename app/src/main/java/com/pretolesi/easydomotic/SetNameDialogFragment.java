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
    private AlertDialog m_AD;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String POSITION = "position";
    private static final String TITLE = "title";

    public static SetNameDialogFragment newInstance(int position, String strTitle) {
        SetNameDialogFragment fragment = new SetNameDialogFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putString(TITLE, strTitle);
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
            throw new ClassCastException("Activity must implement SetNameDialogFragmentCallbacks.");
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
     }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

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
                            EditText et = (EditText)m_AD.findViewById(R.id.id_et_room_name);
                            if (et != null) {
                                str = et.getText().toString();
                            }
                            position = getArguments().getInt(POSITION);
                            mCallbacks.onSetNameDialogFragmentClickListener(SetNameDialogFragment.this, position, getArguments().getString(TITLE), str);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SetNameDialogFragment.this.getDialog().cancel();
                    }
                });
        m_AD = builder.create();
        return m_AD;
    }
    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface SetNameDialogFragmentCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onSetNameDialogFragmentClickListener(DialogFragment dialog, int position, String strTitle,  String strName);
    }
}
