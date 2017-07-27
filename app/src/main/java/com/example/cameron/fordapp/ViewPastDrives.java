package com.example.cameron.fordapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cameron.fordapp.DataFragment.ListSelectionListener;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Anita
 */

public class ViewPastDrives extends Activity implements ListSelectionListener {

    public static String[]  DatesArray;
    public static String[] QuoteArray;
    private final DataFragment mTitlesFragment = new DataFragment();
    private final InfoFragment mDetailsFragment = new InfoFragment();

    public static final int UNSELECTED = -1;
    private FragmentManager mFragmentManager;
    private CommentsDataSource datasource;
    TextView g_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("View All Rides");
        datasource = new CommentsDataSource(this);
        datasource.open();
        List<Comment> values = datasource.getAllComments();
        ArrayList<String>temp = new ArrayList<>();
        DatesArray  = new String[values.size()];

        for(Comment n : values){
          temp.add( n.toString().substring(0,17));
        }
        String[] QuoteArray;
        for(int i = 0 ; i < temp.size() ;i++){
            DatesArray[i] = temp.get(i);
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();
        datasource.close();
        // Get the string arrays with the titles and qutoes
      // DatesArray = temp.toArray(DatesArray);
      //  QuoteArray = getResources().getStringArray(R.array.Quotes);

        setContentView(R.layout.activity_view_past_drives);

        // Get a reference to the FragmentManager
        mFragmentManager = getFragmentManager();

        // Start a new FragmentTransaction
        FragmentTransaction fragmentTransaction = mFragmentManager
                .beginTransaction();

        // Add the TitleFragment to the layout
        fragmentTransaction.add(R.id.title_fragment_container, mTitlesFragment);

        // Commit the FragmentTransaction
        fragmentTransaction.commit();


    }

    // Called when the user selects an item in the TitlesFragment
    @Override
    public void onListSelection(int index) {

        // If the QuoteFragment has not been added, add it now
        if (!mDetailsFragment.isAdded()) {

            // Start a new FragmentTransaction
            FragmentTransaction fragmentTransaction = mFragmentManager
                    .beginTransaction();

            // Add the QuoteFragment to the layout
            fragmentTransaction.remove(mTitlesFragment);
            fragmentTransaction.add(R.id.quote_fragment_container, mDetailsFragment);

            // Add this FragmentTransaction to the backstack
            fragmentTransaction.addToBackStack(null);

            // Commit the FragmentTransaction
            fragmentTransaction.commit();

            // Force Android to execute the committed FragmentTransaction
            mFragmentManager.executePendingTransactions();
        }

        if (mDetailsFragment.getShownIndex() != index) {

            // Tell the QuoteFragment to show the quote string at position index
            mDetailsFragment.showQuoteAtIndex(index);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Get a reference to the MenuInflater
        MenuInflater inflater = getMenuInflater();

        // Inflate the menu using activity_menu.xml
        inflater.inflate(R.menu.top_menu, menu);

        // Return true to display the menu
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settingsMenu:

                // Show a Toast Message. Toast Messages are discussed in the lesson on user interface classes
              //  Toast.makeText(getApplicationContext(),
                //        "This action provided by the Drive", Toast.LENGTH_SHORT)
                  //      .show();
                datasource.open();
                List<Comment> values = datasource.getAllComments();
                for(int i = 0; i < values.size(); i++){
                    datasource.deleteComment(values.get(i));
                }

                datasource.close();
                NavUtils.navigateUpFromSameTask(this);
                // return value true indicates that the menu click has been handled
             case android.R.id.home:

                Intent intent = NavUtils.getParentActivityIntent(this);
                NavUtils.navigateUpFromSameTask(this);

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}