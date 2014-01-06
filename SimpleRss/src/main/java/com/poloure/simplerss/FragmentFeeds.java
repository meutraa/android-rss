package com.poloure.simplerss;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

/* Must be public for rotation. */
public
class FragmentFeeds extends Fragment
{
   static final int VIEW_PAGER_ID = 10000;
   static final String FRAGMENT_ID_PREFIX = "android:switcher:10000:";

   static
   Fragment newInstance()
   {
      return new FragmentFeeds();
   }

   @Override
   public
   View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {
      Activity activity = getActivity();

      if(null == container)
      {
         return new View(activity);
      }

      setHasOptionsMenu(true);

      ListView navigationList = (ListView) activity.findViewById(R.id.navigation_drawer);
      BaseAdapter navigationAdapter = (BaseAdapter) navigationList.getAdapter();

      String applicationFolder = FeedsActivity.getApplicationFolder(activity);
      ViewPager.OnPageChangeListener onTagPageChange = new OnPageChangeTags(activity,
            navigationAdapter, applicationFolder);

      FragmentManager fragmentManager = activity.getFragmentManager();
      PagerAdapterFeeds adapter = new PagerAdapterFeeds(fragmentManager);
      adapter.updateTags(applicationFolder, activity);

      /* Create the ViewPager. */
      ViewPager viewPager = ViewPagerStrip.newInstance(activity);
      viewPager.setAdapter(adapter);
      viewPager.setOnPageChangeListener(onTagPageChange);
      viewPager.setId(VIEW_PAGER_ID);

      return viewPager;
   }

   @Override
   public
   void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
   {
      MenuItem refreshMenu = menu.findItem(R.id.refresh);
      MenuItem unreadMenu = menu.findItem(R.id.unread);
      MenuItem addFeedMenu = menu.findItem(R.id.add_feed);

      if(null != refreshMenu && null != unreadMenu && null != addFeedMenu)
      {
         refreshMenu.setVisible(true);
         unreadMenu.setVisible(true);
         addFeedMenu.setVisible(true);
      }
   }
}
