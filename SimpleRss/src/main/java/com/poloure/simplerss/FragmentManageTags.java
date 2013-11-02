package com.poloure.simplerss;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

class FragmentManageTags extends ListFragment
{
   static
   ListFragment newInstance()
   {
      return new FragmentManageTags();
   }

   @Override
   public
   void onActivityCreated(Bundle savedInstanceState)
   {
      super.onActivityCreated(savedInstanceState);

      ListView listView = getListView();
      Context context = getActivity();
      ListAdapter listAdapter = new AdapterManageTags(context);
      setListAdapter(listAdapter);

      String allTag = context.getString(R.string.all_tag);
      String applicationFolder = FeedsActivity.getApplicationFolder(context);

      AsyncManageTagsRefresh.newInstance(listView, applicationFolder, allTag);
   }

   @Override
   public
   View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {
      super.onCreateView(inflater, container, savedInstanceState);

      return inflater.inflate(R.layout.listview_cards, container, false);
   }

}