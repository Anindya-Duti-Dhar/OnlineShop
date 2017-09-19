package demoapp.com.woocommerce.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import demoapp.com.woocommerce.R;

public class HomeFragment extends Fragment {

    //Defining Variables
    private TabLayout tabLayout;
    private ViewPager viewPager;
    String CurntStatus;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //view initialize and functionality declare

        // initialize tab layout with tab icon
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.offer_icon).setText("Offer"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.men_icon).setText("Men"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.women_icon).setText("Women"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.kids_icon).setText("Kids"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.homedecor_icon).setText("Home Decor"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.electronics_icon).setText("Electronics"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // initialize view pager
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(6);         // for smooth transition between tabs
        // initialize view pager adapter and setting that adapter
        final PagerAdapter adapter = new PageAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        // view pager listener
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        // tab listener
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // get the position which tab is selected
                viewPager.setCurrentItem(tab.getPosition());
                int Status = tab.getPosition();
                CurntStatus = String.valueOf(Status);
                Log.d("Home: ", CurntStatus);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // get the position which tab is unselected
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // get the position which tab is reselected
            }
        });

    }

    // view pager adapter class
    class PageAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PageAdapter(FragmentManager fm, int numTabs) {
            super(fm);
            this.mNumOfTabs = numTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    OfferFragment LatestFragment = new OfferFragment();
                    return LatestFragment;
                case 1:
                    MenFragment MenFragment = new MenFragment();
                    return MenFragment;
                case 2:
                    WomenFragment WomenFragment = new WomenFragment();
                    return WomenFragment;
                case 3:
                    KidsFragment KidsFragment = new KidsFragment();
                    return KidsFragment;
                case 4:
                    ElectronicsFragament ElectronicsFragament = new ElectronicsFragament();
                    return ElectronicsFragament;
                case 5:
                    HomeDecorFragment HomeDecorFragment = new HomeDecorFragment();
                    return HomeDecorFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

}
