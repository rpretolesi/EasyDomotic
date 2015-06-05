package com.pretolesi.easydomotic.TcpIpClient;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import com.pretolesi.SQL.SQLContract;
import com.pretolesi.easydomotic.BaseFragment;
import com.pretolesi.easydomotic.LoadersUtils.Loaders;
import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.SettingsActivity;

/**
 *
 */
public class TcpIpClientListFragment extends ListFragment implements
//        SearchView.OnQueryTextListener,
//        SearchView.OnCloseListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "ListRoomFragment";
    private ListTcpIpClientFragmentCallbacks mCallbacks;
    private SimpleCursorAdapter mAdapter;
    // The SearchView for doing filtering.
//    SearchView mSearchView;

    // If non-null, this is the current filter the user has provided.
//    String mCurFilter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TcpIpClientListFragment newInstance(int sectionNumber, int position) {
        TcpIpClientListFragment fragment = new TcpIpClientListFragment();
        Bundle args = new Bundle();
        args.putInt(BaseFragment.ARG_SECTION_NUMBER, sectionNumber);
        args.putInt(BaseFragment.POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public TcpIpClientListFragment() {
    }

    @Override
    public void  onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
        setEmptyText("No Tcp Ip Server");

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);
        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_2,
                null,
                new String[] {SQLContract.TcpIpClientEntry.COLUMN_NAME_NAME},
                new int[] {android.R.id.text1}, 0);

        setListAdapter(mAdapter);

        // Start out with a progress indicator.
        setListShown(false);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
       // getLoaderManager().initLoader(Loaders.TCP_IP_CLIENT_LOADER_ID, null, this);
    }
/*
    public static class ListSearchView extends SearchView {
        public ListSearchView(Context context) {
            super(context);
        }

        // The normal SearchView doesn't clear its search text when
        // collapsed, so we will do this for it.
        @Override
        public void onActionViewCollapsed() {
            setQuery("", false);
            super.onActionViewCollapsed();
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Place an action bar item for searching.
        MenuItem item = menu.add("Search");
        item.setIcon(android.R.drawable.ic_menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        mSearchView = new ListSearchView(getActivity());
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
        mSearchView.setIconifiedByDefault(true);
        item.setActionView(mSearchView);
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((SettingsActivity) activity).onSectionAttached(getArguments().getInt(BaseFragment.ARG_SECTION_NUMBER));
//        ((SettingsActivity) activity).restoreActionBar();
        try {
            mCallbacks = (ListTcpIpClientFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement ListTcpIpClientFragmentCallbacks.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(Loaders.BASE_VALUE_COMM_CLIENT_LOADER_ID, null, this);
        // Log.d(TAG, this.toString() + ": " + "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        getLoaderManager().destroyLoader(Loaders.BASE_VALUE_COMM_CLIENT_LOADER_ID);
        // Log.d(TAG, this.toString() + ": " + "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        // Log.d(TAG, this.toString() + ": " + "onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Log.d(TAG, this.toString() + ": " + "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Log.d(TAG, this.toString() + ": " + "onDestroy()");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallbacks.onListTcpIpClientFragmentCallbacksListener(getArguments().getInt(BaseFragment.ARG_SECTION_NUMBER), getArguments().getInt(BaseFragment.POSITION), id);
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }
/*
    @Override
    public boolean onClose() {
        if (!TextUtils.isEmpty(mSearchView.getQuery())) {
            mSearchView.setQuery(null, true);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        // Called when the action bar search text has changed.  Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        String newFilter = !TextUtils.isEmpty(s) ? s : null;
        // Don't do anything if the filter hasn't actually changed.
        // Prevents restarting the loader when restoring state.
        if (mCurFilter == null && newFilter == null) {
            return true;
        }
        if (mCurFilter != null && mCurFilter.equals(newFilter)) {
            return true;
        }
        mCurFilter = newFilter;
        getLoaderManager().restartLoader(Loaders.TCP_IP_CLIENT_LOADER_ID, null, this);

        // Log.d(TAG, this.toString() + ": " + "onQueryTextChange()");

        return true;
    }
*/
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        // Log.d(TAG, this.toString() + ": " + "onCreateLoader()");
        if(id == Loaders.BASE_VALUE_COMM_CLIENT_LOADER_ID){
            return new CursorLoader(getActivity()){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.TcpIpClientEntry.load();
                }
            };
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        if(loader.getId() == Loaders.BASE_VALUE_COMM_CLIENT_LOADER_ID) {
            mAdapter.swapCursor(cursor);

            // The list should now be shown.
            if (isResumed()) {
                setListShown(true);
            } else {
                setListShownNoAnimation(true);
            }
        }

        // Log.d(TAG, this.toString() + ": " + "onLoadFinished()");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        if(loader.getId() == Loaders.BASE_VALUE_COMM_CLIENT_LOADER_ID) {
            mAdapter.swapCursor(null);
        }

        // Log.d(TAG, this.toString() + ": " + "onLoaderReset()");
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface ListTcpIpClientFragmentCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onListTcpIpClientFragmentCallbacksListener(int sectionNumber, int position, long id);
    }
}
