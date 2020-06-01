package com.example.blackcoffertask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class TagsActivity extends AppCompatActivity {

    BottomNavigationView navView;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.grey_color));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setSubtitle(Html.fromHtml("<font color='#969696'>\uD83D\uDCCDGolf Course Road, Delhi</font>"));
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#f3f3f3"));
//        actionBar.setIcon(R.drawable.ic_menu_black_24dp);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);



        navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(TagFragment.newInstance("", ""));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_btn_signout) {
            if (currentUser != null) {
                mAuth.signOut();
                if(LoginManager.getInstance() != null) {
                    LoginManager.getInstance().logOut();
                }
                Toast.makeText(getApplicationContext(), "Signed Out", Toast.LENGTH_LONG).show();
                startActivity(new Intent(TagsActivity.this, MainActivity.class));
                finish();
            }
            else {
                Toast.makeText(getApplicationContext(), "You aren't logged In", Toast.LENGTH_LONG).show();
            }
        }
        else if(id == android.R.id.home)
        {
            DrawerLayout navDrawer = findViewById(R.id.drawer_layout);
            // If the navigation drawer is not open then open it, if its already open then close it.
            if(!navDrawer.isDrawerOpen(GravityCompat.START))
                navDrawer.openDrawer(GravityCompat.START);
            else
                navDrawer.closeDrawer(GravityCompat.END);
        }
        else if(id == R.id.menu_btn_notification)
        {
            Snackbar.make(getWindow().getDecorView(), "No new Notifications", Snackbar.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int selectedItemId = navView.getSelectedItemId();
                    switch (item.getItemId()) {
                        case R.id.navigation_tags:
                            if(R.id.navigation_tags != selectedItemId)
                            {
                                openFragment(TagFragment.newInstance("", ""));
                            }
                            return true;
                        case R.id.navigation_trending:
                            if(R.id.navigation_trending != selectedItemId)
                            {
                                openFragment(TrendingFragment.newInstance("", ""));
                            }
                            return true;
                        case R.id.navigation_explore:
                            if(R.id.navigation_explore != selectedItemId)
                            {
                                openFragment(ExploreFragment.newInstance("", ""));
                            }
                            return true;
                        case R.id.navigation_saved:
                            if(R.id.navigation_saved != selectedItemId)
                            {
                                openFragment(SavedFragment.newInstance("", ""));
                            }
                            return true;
                    }
                    return false;
                }
            };

    @Override
    public void onBackPressed() {

        int selectedItemId = navView.getSelectedItemId();
        if(R.id.navigation_tags != selectedItemId)
        {
            setTagItem(TagsActivity.this);
        }
        else
        {
            super.onBackPressed();
        }
    }

    public static void setTagItem(Activity activity){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) activity.findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_tags);
    }
}
