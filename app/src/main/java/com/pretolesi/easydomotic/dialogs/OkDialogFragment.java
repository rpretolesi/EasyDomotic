package com.pretolesi.easydomotic.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;

/**
 *
 */
public class OkDialogFragment extends DialogFragment {

    private static final String TAG = "OkDialogFragment";


    public static final int SAVING_OK_ID = 100;
    public static final int SAVING_ERROR_ID = 101;
    public static final int DELETING_OK_ID = 102;
    public static final int DELETING_ERROR_ID = 103;

    public static final int ROOM_ERROR_ID = 110;
    public static final int LIGHT_SWITCH_NAME_ERROR = 112;
    public static final int ORIENTATION_ERROR_ID = 113;
    public static final int POSITION_ERROR_ID = 114;
    private OkDialogFragmentCallbacks mCallbacks;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String DLG_ID = "dlg_id";
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String OK_BTN_TEXT = "ok_btn_text";

    public static OkDialogFragment newInstance(int dlgID, String strTitle, String strMessage, String strOkButtonText) {
        OkDialogFragment fragment = new OkDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DLG_ID, dlgID);
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
                    mCallbacks.onOkDialogFragmentClickListener(getArguments().getInt(DLG_ID));
                }
            }
        });
        builder.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    // Back button must not close the Dialog.
                }
                return true;
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
        void onOkDialogFragmentClickListener(int dlgID);
    }
}