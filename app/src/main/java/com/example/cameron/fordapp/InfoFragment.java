package com.example.cameron.fordapp;

/**
 * Created by anita13benita on 12/4/16.
 */
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.*;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import java.util.List;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class InfoFragment extends Fragment {
    private CommentsDataSource datasource;
    private TextView mQuoteView = null;
    private int mCurrIdx = ViewPastDrives.UNSELECTED;
    private int mQuoteArrLen = 0;

    // Returns currently selected item
    public int getShownIndex() {
        return mCurrIdx;
    }

    // Show the Quote string at position newIndex
    public void showQuoteAtIndex(int newIndex) {
        if (newIndex < 0 || newIndex >= mQuoteArrLen)
            return;
        datasource = new CommentsDataSource(getActivity());
        datasource.open();
        List<Comment> values = datasource.getAllComments();

        mCurrIdx = newIndex;

        mQuoteView.setText(values.get(newIndex).toString());
    }

    // Called to create the content view for this Fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout defined in quote_fragment.xml
        // The last parameter is false because the returned view does not need to be attached to the container ViewGroup
        return inflater.inflate(R.layout.detail_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Don't destroy Fragment on reconfiguration
       setRetainInstance(true);

        // This Fragment adds options to the ActionBar
        setHasOptionsMenu(true);
    }

    // Set up some information about the mQuoteView TextView
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mQuoteView = (TextView) getActivity().findViewById(R.id.quoteView);
        mQuoteArrLen = ViewPastDrives.DatesArray.length;

        mQuoteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {
                try {
                   // Toast.makeText(getActivity(),
                     //       "driving was selected", Toast.LENGTH_SHORT).show();

                    getActivity().getFragmentManager().popBackStack();

                } catch (Exception except) {
                   // android.util.Log.e(TAG, "problem with driving button " + except.getMessage());
                }
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCurrIdx = ViewPastDrives.UNSELECTED;
    }





}
