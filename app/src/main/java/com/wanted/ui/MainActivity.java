package com.wanted.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wanted.R;
import com.wanted.entities.Role;
import com.wanted.entities.User;
import com.wanted.util.ResizeUtil;

import java.util.ArrayList;
import java.util.List;
import com.wanted.R;

/**
 * Created by xlin2
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView avatar;

    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_main);

        getDataFromActivity();
        findViews();
        initViews();
        addListeners();
    }

    public void getDataFromActivity() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = (User) extras.get("User");
            Toast.makeText(this, "Id is " + user.getId(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Get ui objects
     */
    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.people_detail_toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        avatar = (ImageView) findViewById(R.id.avatarImageView);
    }

    /**
     * Do some initializations on ui objects
     */
    private void initViews() {
        // tool bar
        setSupportActionBar(toolbar);

        // drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // navigation view
        setNavMenu();
        navigationView.setNavigationItemSelectedListener(this);

        // view pager
        setupViewPager(viewPager);

        // tab layout
        tabLayout.setupWithViewPager(viewPager);

        // avatar
        avatar.setImageDrawable(new ResizeUtil(this).resizeAvatar(R.drawable.avatar));

        // image loader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
    }

    private void setNavMenu() {
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.activity_recruiter_drawer);
        if (user != null) {
            if (user.getRole() == Role.RECRUITER) {
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.activity_recruiter_drawer);
            }
        }
    }

    /**
     * Add listeners to ui objects
     */
    private void addListeners() {
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_resume) {
            Intent intent = new Intent(this, ResumeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_following) {
            Intent intent = new Intent(this, FollowingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_follower) {
            Intent intent = new Intent(this, FollowerActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_favorite) {
            Intent intent = new Intent(this, FavoriteActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_posts) {
            Intent intent = new Intent(this, PostActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addItem(new JobFragment(), "Jobs");
        adapter.addItem(new PeopleFragment(), "People");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addItem(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}
