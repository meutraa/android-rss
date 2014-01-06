package com.poloure.simplerss;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

/* Must be public for rotation. */
public
class ListFragmentTag extends ListFragment
{
   private static final String POSITION_KEY = "POSITION";

   static
   ListFragment newInstance(int position)
   {
      ListFragment listFragment = new ListFragmentTag();
      Bundle bundle = new Bundle();
      bundle.putInt(POSITION_KEY, position);
      listFragment.setArguments(bundle);
      return listFragment;
   }

   @Override
   public
   void onActivityCreated(Bundle savedInstanceState)
   {
      super.onActivityCreated(savedInstanceState);

      ListView listView = getListView();
      Activity activity = getActivity();
      ActionBar actionBar = activity.getActionBar();
      String applicationFolder = FeedsActivity.getApplicationFolder(activity);

      ListView navigationList = (ListView) activity.findViewById(R.id.navigation_drawer);
      AdapterNavigationDrawer adapterNavigationDrawer = (AdapterNavigationDrawer) navigationList
            .getAdapter();

      ListAdapter listAdapter = new AdapterTags(activity, applicationFolder);
      setListAdapter(listAdapter);

      Bundle bundle = getArguments();
      int position = bundle.getInt(POSITION_KEY);

      /* Get what the listViewTopPadding is. */
      int listViewTopPadding = listView.getPaddingTop();

      AbsListView.OnScrollListener scrollListener = new OnScrollFeedListener(
            adapterNavigationDrawer, actionBar, applicationFolder, position, listViewTopPadding);

      listView.setOnScrollListener(scrollListener);
      listView.setDividerHeight(0);
      listView.setOnItemLongClickListener(new OnFeedItemLongClick(activity));

      if(0 == position)
      {
         AsyncTagPage.newInstance(0, listView, applicationFolder, true);
      }
   }

   @Override
   public
   View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {
      ListView listView = new ListView(getActivity());

      /* Set to android.R.id.list so that the ListFragment knows to use this list. */
      listView.setId(android.R.id.list);
      listView.setBackgroundColor(Color.WHITE);
      listView.setFadingEdgeLength(0);

      return listView;
   }
}
