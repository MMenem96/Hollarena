package com.creation.hollarena.UsersInfo.navigationdrawer.marketplace;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.creation.hollarena.R;
import com.creation.hollarena.UsersInfo.logincredentials.LoginActivity;
import com.creation.hollarena.UsersInfo.navigationdrawer.aboutusandhelp.AboutUsActivity;
import com.creation.hollarena.UsersInfo.navigationdrawer.magazine.CheckAge;
import com.creation.hollarena.UsersInfo.navigationdrawer.hollarenaalterego.HollarenaAlteregoActivity;
import com.creation.hollarena.UsersInfo.navigationdrawer.profile.ProfileActivity;


public class MarketPlaceActivity extends AppCompatActivity {

    private DrawerLayout marketPlacedrawerLayout;
    private ActionBarDrawerToggle mpdToggle;
    private Toolbar toolbar;
    private SharedPreferences prefs,categories;
    private String previouslyStarted;
    private String fashion,sports,books,politics;
    private Intent mainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_place);

        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        marketPlacedrawerLayout = (DrawerLayout) findViewById(R.id.drowerLayoutMarketPlace);
        mpdToggle = new ActionBarDrawerToggle(this, marketPlacedrawerLayout, R.string.open, R.string.close);

        marketPlacedrawerLayout.addDrawerListener(mpdToggle);
        mpdToggle.syncState();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation_view_market_plae);
        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case (R.id.Profile_Item_id):
                        Intent accountActivity = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(accountActivity);
                        break;


                    case (R.id.Forum_Item_id):
                        prefs=getSharedPreferences("previouslyStarted",MODE_PRIVATE);
                        prefs=getSharedPreferences("previouslyStarted",MODE_PRIVATE);
                        previouslyStarted=prefs.getString("previouslyStarted","");

                        categories = getSharedPreferences("categories", MODE_PRIVATE);
                        fashion = categories.getString("fashion", "");
                        books = categories.getString("books", "");
                        politics = categories.getString("politics", "");
                        sports = categories.getString("sports", "");

                        if (previouslyStarted=="no") {
                            mainIntent = new Intent(getApplicationContext(), CheckAge.class);
                            startActivity(mainIntent);

                        }

                        else if (fashion==null&&books==null&&politics==null&& sports==null) {
                            mainIntent = new Intent(getApplicationContext(), CheckAge.class);
                            startActivity(mainIntent);
                        } else {
                            mainIntent = new Intent(getApplicationContext(), HollarenaAlteregoActivity.class);
                            startActivity(mainIntent);
                        }
                        break;


                    case (R.id.Magazine_Item_id):
                        Intent magazineActivity = new Intent(getApplicationContext(), HollarenaAlteregoActivity.class);
                        startActivity(magazineActivity);
                        break;


                    case (R.id.about_us):
                        Intent about_usActivity = new Intent(getApplicationContext(), AboutUsActivity.class);
                        startActivity(about_usActivity);
                        break;


                    case (R.id.logout):
                        Intent logoutActivity = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(logoutActivity);

                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mpdToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
