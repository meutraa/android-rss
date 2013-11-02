package com.poloure.simplerss;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;

class OnClickNavDrawerItem implements AdapterView.OnItemClickListener
{
   private final FragmentManager m_fragmentManager;
   private final ActionBar       m_actionBar;
   private final DrawerLayout    m_drawerLayout;
   private final ListAdapter     m_navigationAdapter;
   private final ViewPager       m_tagsViewPager;
   private final String[]        m_navigationTitles;

   OnClickNavDrawerItem(FragmentManager fragmentManager, ActionBar actionBar,
         DrawerLayout drawerLayout, ListAdapter navigationAdapter, ViewPager tagsViewPager,
         String[] navigationTitles)
   {
      m_fragmentManager = fragmentManager;
      m_actionBar = actionBar;
      m_drawerLayout = drawerLayout;
      m_navigationAdapter = navigationAdapter;
      m_tagsViewPager = tagsViewPager;

      /* This is finished with at the end of the Activity's onCreate so it is safe to share. */
      //noinspection AssignmentToCollectionOrArrayFieldFromParameter
      m_navigationTitles = navigationTitles;
   }

   @Override
   public
   void onItemClick(AdapterView parent, View view, int position, long id)
   {
      /* Close the drawer on any click. This will call the OnDrawerClose of the DrawerToggle. */
      m_drawerLayout.closeDrawers();

      boolean tagWasClicked = 3 < position;
      boolean feedsWasClicked = 0 == position;
      String feedTitle = m_navigationTitles[0];

      /* Determine the new title based on the position of the item clicked. */
      String selectedTitle = tagWasClicked ? feedTitle : m_navigationTitles[position];

      /* If the item selected was a tag, change the FragmentFeeds ViewPager to that page. */
      if(tagWasClicked)
      {
         m_tagsViewPager.setCurrentItem(position - 4);
      }

      /* Set the ActionBar title without saving the previous title (not changing to navTitle). */
      m_actionBar.setTitle(selectedTitle);

      /* Set the ActionBar subtitle accordingly. */
      if(feedsWasClicked)
      {
         int currentPage = m_tagsViewPager.getCurrentItem();

         String unread = (String) m_navigationAdapter.getItem(currentPage);
         m_actionBar.setSubtitle("Unread: " + unread);
      }
      else
      {
         m_actionBar.setSubtitle(null);
      }

      /* Get the selected fragment. */
      Fragment selectedFragment = m_fragmentManager.findFragmentByTag(selectedTitle);

      /* Hide the current fragment and display the selected one. */
      FragmentTransaction transaction = m_fragmentManager.beginTransaction();
      for(String navigationTitle : m_navigationTitles)
      {
         Fragment frag = m_fragmentManager.findFragmentByTag(navigationTitle);
         if(null != frag && !frag.equals(selectedFragment))
         {
            transaction.hide(frag);
         }
      }
      transaction.show(selectedFragment);
      transaction.commit();
   }
}