package org.spoofer.cockpit;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.spoofer.cockpit.dashboards.DashFragment;
import org.spoofer.cockpit.events.AndroidGPSFactory;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_DASH_INDEX = "cockpit.dash_index";

    private ViewPager mPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPager = findViewById(R.id.viewPager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), 0);
        mPager.setAdapter(pagerAdapter);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,

                    },
                    AndroidGPSFactory.MY_PERMISSION_ACCESS_COURSE_LOCATION);
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();
    }

    @Override
    protected void onPause() {
        savePreferences();
        super.onPause();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private ScreenSlidePagerAdapter(FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @Override
        @NonNull
        public Fragment getItem(int position) {
            return DashFragment.NewDashFragment(dash_layouts[position]);
        }

        @Override
        public int getCount() {
            return dash_layouts.length;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_dashboard_select);
        menuItem.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_dashboard_select:
                    openSelectDashboard();
                    return true;

                case R.id.menu_dashboard_settings:
                    showSettings();
                    return true;

                default:
                    return false;
            }
        });
        return true;
    }

    private void loadDashLayout(String fileName) throws IOException {
        XmlResourceParser parser = getApplicationContext().getAssets().openXmlResourceParser(fileName);

    }

    private void loadPreferences() {
        SharedPreferences sp = getApplicationContext().getSharedPreferences("cockpit", Context.MODE_PRIVATE);
        int dashIndex = sp.getInt(PREF_DASH_INDEX, -1);
        if (dashIndex >= 0 && dashIndex < mPager.getAdapter().getCount())
            mPager.setCurrentItem(dashIndex, false);
    }

    private void savePreferences() {
        SharedPreferences sp = getApplicationContext().getSharedPreferences("cockpit", Context.MODE_PRIVATE);
        int dashIndex = sp.getInt(PREF_DASH_INDEX, -1);
        if (dashIndex == mPager.getCurrentItem())
            return;

        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(PREF_DASH_INDEX, mPager.getCurrentItem()).commit();
    }

}
