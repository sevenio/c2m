package com.tvisha.click2magic.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;
    private List<String> titleList;

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titleList) {
            super(fm);
            this.fragmentList = fragmentList;
            this.titleList = titleList;
        }

   /* @Override
    public int getItemPosition(@NonNull Object object) {
       // return super.getItemPosition(object);
        return POSITION_NONE;
    }
*/


    // this is for fragment tabs
        @Override
        public Fragment getItem(int position) {
            if (position >= fragmentList.size())
                return null;

            return fragmentList.get(position);
        }
// this counts total number of tabs
        @Override
        public int getCount() {
            return fragmentList.size();
        }

   /* @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
*/
        /*public void clear(){
            notifyDataSetChanged();
        }*/
    }

