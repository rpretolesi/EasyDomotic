package com.pretolesi.easydomotic;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

/**
 * Created by RPRETOLESI on 20/03/2015.
 */
public class SetNameDialogFragment extends DialogFragment {

    private SetNameDialogFragmentCallbacks mCallbacks;
    private AlertDialog m_AD;
    private EditText m_et;
    private RadioButton m_rl;
    private RadioButton m_rp;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String POSITION = "position";
    private static final String TITLE = "title";
    private static final String DEFAULT_ORIENTATION = "default_orientation";

    public static SetNameDialogFragment newInstance(int position, String strTitle, boolean bLandscape) {
        SetNameDialogFragment fragment = new SetNameDialogFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putString(TITLE, strTitle);
        args.putBoolean(DEFAULT_ORIENTATION, bLandscape);
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
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = getActivity().getLayoutInflater().inflate(R.layout.set_name_dialog_fragment, null);
        m_et = (EditText)view.findViewById(R.id.id_et_room_name);
        m_rl = (RadioButton)view.findViewById(R.id.radioButtonLandscape);
        m_rp = (RadioButton)view.findViewById(R.id.radioButtonPortrait);
        if(getArguments().getBoolean(DEFAULT_ORIENTATION)){
            if (m_rl != null) {
                m_rl.setChecked(true);
            }
        } else {
            if (m_rp != null) {
                m_rp.setChecked(true);
            }
        }
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle(getArguments().getString(TITLE));
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (mCallbacks != null) {
                            String str = "";
                            boolean bLandscape = false;
                             if (m_et != null) {
                                str = m_et.getText().toString();
                            }
                            if (m_rl != null) {
                                bLandscape = m_rl.isChecked();;
                            }
                            mCallbacks.onSetNameDialogFragmentClickListener(SetNameDialogFragment.this, getArguments().getInt(POSITION), getArguments().getString(TITLE), str, bLandscape);
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
        void onSetNameDialogFragmentClickListener(DialogFragment dialog, int position, String strTitle,  String strName, boolean bLandscape);
    }
}
