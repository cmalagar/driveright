package com.example.cameron.fordapp;

/**This is from examples in class
 * Created by anita13benita on 12/4/16.
 */
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.app.ListActivity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.*;
import android.view.View;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import java.util.List;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class DataFragment extends ListFragment {
    // Callback interface that allows this Fragment to notify the QuoteViewerActivity when
    // user clicks on a List Item
    private CommentsDataSource datasource;
    TextView g_button;
    ListSelectionListener mListener = null;
    int mCurrIdx = -1;
    public interface ListSelectionListener {
        public void onListSelection(int index);
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        try {

            // Set the ListSelectionListener for communicating with the QuoteViewerActivity
            mListener = (ListSelectionListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArticleSelectedListener");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This Fragment will add items to the ActionBar
        setHasOptionsMenu(true);
        datasource = new CommentsDataSource(getActivity());
        datasource.open();

        List<Comment> values = datasource.getAllComments();
        // Retain this Fragment across Activity Reconfigurations
        setRetainInstance(true);


    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        datasource = new CommentsDataSource(getActivity());
        datasource.open();

        List<Comment> values = datasource.getAllComments();
        // Set the list adapter for the ListView
        // Discussed in more detail in the user interface classes lesson
        setListAdapter(new ArrayAdapter<String>(getActivity(),
              R.layout.list_item, ViewPastDrives.DatesArray));
        //setListAdapter(new ArrayAdapter<Comment>(getActivity(),
             //      R.layout.list_item, values));

        // If a title has already been selected in the past, reset the selection state now
        if (mCurrIdx != ViewPastDrives.UNSELECTED) {
            setSelection(mCurrIdx);
        }
    }

    // Called when the user selects an item from the List
    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        mCurrIdx = pos;

        // Indicates the selected item has been checked
        getListView().setItemChecked(pos, true);

        // Inform the QuoteViewerActivity that the item in position pos has been selected
        mListener.onListSelection(pos);
    }





}
