package mauth.oblabs.com.firebaseauthentication.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import mauth.oblabs.com.firebaseauthentication.R;
import mauth.oblabs.com.firebaseauthentication.fragment.EntityListFragment;
import mauth.oblabs.com.firebaseauthentication.utils.Helper;
import mauth.oblabs.com.firebaseauthentication.utils.ItemClicked;
import mauth.oblabs.com.firebaseauthentication.utils.SharedPreference;


public class ShoppingActivity extends AppCompatActivity {





    FloatingActionButton fab;




    public ItemClicked fabAddItemCallback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        fab = (FloatingActionButton) findViewById(R.id.fab);


        EntityListFragment fragment = EntityListFragment.createInstance();
        fabAddItemCallback =  fragment;


        getSupportFragmentManager().beginTransaction().add(R.id.container , fragment).commit();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




        fabAddItemCallback.clicked(0);







            }
        });






    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2)
        {
            boolean status= data.getBooleanExtra("status" , false);

            if(status){
                Helper.showToast(this , "Success");
            }else{
                Helper.showToast(this , "Failed");
            }

        }
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

            EntityListFragment fragment = EntityListFragment.createInstance();
            fabAddItemCallback =  fragment;
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
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
