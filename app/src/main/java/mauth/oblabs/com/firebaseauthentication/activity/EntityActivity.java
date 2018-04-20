package mauth.oblabs.com.firebaseauthentication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import mauth.oblabs.com.firebaseauthentication.R;
import mauth.oblabs.com.firebaseauthentication.fragment.EntityDetailCompleteFragment;
import mauth.oblabs.com.firebaseauthentication.fragment.EntityDetailFragment;
import mauth.oblabs.com.firebaseauthentication.utils.ItemClicked;
import mauth.oblabs.com.firebaseauthentication.utils.SharedPreference;


public class EntityActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    TabLayout tabLayout;
    FloatingActionButton fab;


    private ViewPager mViewPager;

    public ItemClicked fabAddItemCallback;


    public static String titleName , key , type;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Bundle bundle = getIntent().getExtras();

        titleName = bundle.getString("name");
        key = bundle.getString("key");
        type = bundle.getString("type");


        toolbar.setTitle("".concat(String.valueOf(titleName.charAt(0)).toUpperCase()).concat(titleName.substring(1)));


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());




        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.addOnPageChangeListener(addPageChangeListener());
        tabLayout = findViewById(R.id.tabLayout);


        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);



                fabAddItemCallback.clicked(0);




            }
        });






    }

    private ViewPager.OnPageChangeListener addPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position){
                    case 0:


                        fab.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        fab.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        fab.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        fab.setVisibility(View.VISIBLE);
                            break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

           FirebaseAuth mAuth = new FirebaseAuth(FirebaseApp.initializeApp(this));
           mAuth.signOut();

            new SharedPreference().clearSharedPreference(this);
            startActivity(new Intent(this , MainActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position){
                case 0:
                    EntityDetailFragment fragment = EntityDetailFragment.createInstance(key , type );
                    fabAddItemCallback =  fragment;
                    return fragment;
                case 1:
                    EntityDetailCompleteFragment fragmentComplete = EntityDetailCompleteFragment.createInstance(key );

                    return fragmentComplete;
                default:
                        EntityDetailFragment fragment1 = EntityDetailFragment.createInstance(key, type);
                        fabAddItemCallback =  fragment1;
                        return fragment1;
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "New Task";

                case 1:
                    return "Completed";

                case 2:
                    return "Urgent";

                default:
                    return "New Task";


            }
        }
    }
}
