package com.erwanhesry.connectedclub.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.erwanhesry.connectedclub.LocatedInsideActivity;
import com.erwanhesry.connectedclub.LocatedInsideFragment;
import com.erwanhesry.connectedclub.MainActivity;
import com.erwanhesry.connectedclub.R;
import com.erwanhesry.connectedclub.R.string;
import com.viewpagerindicator.IconPagerAdapter;

public class LocatedInsideFragmentAdapter extends FragmentPagerAdapter {
	
    private static String[] CONTENT = new String[] { MainActivity.ctx.getString(R.string.profile), MainActivity.ctx.getString(R.string.activity), MainActivity.ctx.getString(R.string.preOrder), MainActivity.ctx.getString(R.string.suggestions)};

    private int mCount = CONTENT.length;

    public LocatedInsideFragmentAdapter(FragmentManager fm) {
        super(fm);
        if(LocatedInsideActivity.currentClub.twitterAccount == null  || !MainActivity.isConnected()){
        	CONTENT = new String[] { MainActivity.ctx.getString(R.string.profile), MainActivity.ctx.getString(R.string.preOrder), MainActivity.ctx.getString(R.string.suggestions)};
        	mCount = CONTENT.length;
        } else if(LocatedInsideActivity.currentClub.orderable == 0){
        	CONTENT = new String[] { MainActivity.ctx.getString(R.string.profile), MainActivity.ctx.getString(R.string.activity), MainActivity.ctx.getString(R.string.lacarte), MainActivity.ctx.getString(R.string.suggestions)};
        	mCount = CONTENT.length;
        } else if(LocatedInsideActivity.currentClub.twitterAccount == null && LocatedInsideActivity.currentClub.orderable == 0){
        	CONTENT = new String[] { MainActivity.ctx.getString(R.string.profile), MainActivity.ctx.getString(R.string.lacarte), MainActivity.ctx.getString(R.string.suggestions)};
        	mCount = CONTENT.length;
        }
    }

    @Override
    public Fragment getItem(int position) {
        return LocatedInsideFragment.newInstance(CONTENT[position % CONTENT.length]);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return LocatedInsideFragmentAdapter.CONTENT[position % CONTENT.length];
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }

}
