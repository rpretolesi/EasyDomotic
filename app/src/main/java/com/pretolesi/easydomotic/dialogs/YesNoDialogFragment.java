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
public class YesNoDialogFragment extends DialogFragment {

    private static final String TAG = "YesNoDialogFragment";

    public static final int SAVE_CONFIRM_ID = 100;
    public static final int SAVE_CONFIRM_FROM_BACK_BUTTON_ID = 101;
    public static final int SAVE_CONFIRM_ITEM_ALREADY_EXSIST_ID = 103;
    public static final int DELETE_CONFIRM_ID = 110;

    private YesNoDialogFragmentCallbacks mCallbacks;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String DLG_ID = "dlg_id";
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String YES_BTN_TEXT = "yes_btn_text";
    private static final String NO_BTN_TEXT = "no_btn_text";

    public static YesNoDialogFragment newInstance(int dlgID, String strTitle, String strMessage, String strYesButtonText, String strNoButtonText) {
        YesNoDialogFragment fragment = new YesNoDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DLG_ID, dlgID);
        args.putString(TITLE, strTitle);
        args.putString(MESSAGE, strMessage);
        args.putString(YES_BTN_TEXT, strYesButtonText);
        args.putString(NO_BTN_TEXT, strNoButtonText);
        fragment.setArguments(args);
        return fragment;
    }

    public YesNoDialogFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (YesNoDialogFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement YesNoDialogFragmentCallbacks.");
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
        builder.setPositiveButton(getArguments().getString(YES_BTN_TEXT), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mCallbacks.onYesNoDialogFragmentClickListener(getArguments().getInt(DLG_ID), true, false);
            }
        });
        builder.setNegativeButton(getArguments().getString(NO_BTN_TEXT), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mCallbacks.onYesNoDialogFragmentClickListener(getArguments().getInt(DLG_ID), false, true);
            }
        });

        return builder.create();
    }
    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface YesNoDialogFragmentCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onYesNoDialogFragmentClickListener(int dlgID, boolean bYes, boolean bNo);
    }
}
