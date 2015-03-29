package com.pretolesi.easydomotic.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 *
 */
public class OkDialogFragment extends DialogFragment {

    private OkDialogFragmentCallbacks mCallbacks;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String POSITION = "position";
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String OK_BTN_TEXT = "ok_btn_text";

    public static OkDialogFragment newInstance(int position, String strTitle, String strMessage, String strOkButtonText) {
        OkDialogFragment fragment = new OkDialogFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putString(TITLE, strTitle);
        args.putString(MESSAGE, strMessage);
        args.putString(OK_BTN_TEXT, strOkButtonText);
        fragment.setArguments(args);
        return fragment;
    }

    public OkDialogFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (OkDialogFragmentCallbacks) activity;
        } catch (ClassCastException ignore) {
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getString(TITLE));
        builder.setMessage(getArguments().getString(MESSAGE));
        builder.setPositiveButton(getArguments().getString(OK_BTN_TEXT), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(mCallbacks != null){
                    mCallbacks.onOkDialogFragmentClickListener(getArguments().getInt(POSITION));
                }
            }
        });

        return builder.create();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface OkDialogFragmentCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onOkDialogFragmentClickListener(int position);
    }
}