package com.pretolesi.easydomotic;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pretolesi.SQL.SQLContract;

/**
 * Created by RPRETOLESI on 25/03/2015.
 */
public class RoomListFragment extends ListFragment {
    private static final String TAG = "ListRoomFragment";
    private ListRoomFragmentCallbacks mCallbacks;
    private SimpleCursorAdapter mAdapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RoomListFragment newInstance(int sectionNumber, int position) {
        RoomListFragment fragment = new RoomListFragment();
        Bundle args = new Bundle();
        args.putInt(BaseFragment.ARG_SECTION_NUMBER, sectionNumber);
        args.putInt(BaseFragment.POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public RoomListFragment() {
    }

    @Override
    public void  onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_2,
                null,
                new String[] {SQLContract.RoomEntry.COLUMN_NAME_TAG},
                new int[] {android.R.id.text1}, 0);

        setListAdapter(mAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((SettingsActivity) activity).onSectionAttached(getTag());
        ((SettingsActivity) activity).restoreActionBar();
        try {
            mCallbacks = (ListRoomFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement ListRoomFragmentCallbacks.");
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mAdapter.swapCursor(SQLContract.RoomEntry.load(getActivity()));

        Log.d(TAG, this.toString() + ": " + "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.swapCursor(null);

        Log.d(TAG, this.toString() + ": " + "onPause()");
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallbacks.onListRoomFragmentClickListener(getArguments().getInt(BaseFragment.ARG_SECTION_NUMBER), getArguments().getInt(BaseFragment.POSITION), id);
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface ListRoomFragmentCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onListRoomFragmentClickListener(int sectionNumber, int position, long id);
    }
}
