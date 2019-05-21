package sunmoonuniv.opensource.offender_location;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class menu2Activity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2);

        Button button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("name","BYE");

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(this);

        loadFragment(new HomeFragment());
    }

    private  Boolean loadFragment(Fragment fragment){
        if (fragment != null){

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_containerbox, fragment)
                    .commit();

            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        switch (item.getItemId()){

            case R.id.navigation_dashboard:
                fragment = new HomeFragment();
                break;

            case R.id.navigation_location:
                fragment = new LocationFragment();
                break;

            case R.id.navigation_notifications:
                fragment = new DashboardFragment();
                break;
        }

        return loadFragment(fragment);

    }
}
