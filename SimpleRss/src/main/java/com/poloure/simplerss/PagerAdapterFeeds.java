package com.poloure.simplerss;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

class PagerAdapterFeeds extends FragmentPagerAdapter
{
   private static final Pattern     SPLIT_COMMA = Pattern.compile(",");
   private static       Set<String> s_tagSet    = Collections.synchronizedSet(
         new LinkedHashSet<String>(0));

   PagerAdapterFeeds(FragmentManager fm)
   {
      super(fm);
   }

   static
   Set<String> getTagsFromDisk(String applicationFolder, String allTag)
   {
      Set<String> tagSet = Collections.synchronizedSet(new LinkedHashSet<String>(0));
      String[] tagArray = Read.csvFile(Read.INDEX, applicationFolder, 't')[0];

      tagSet.add(allTag);

      for(String tag : tagArray)
      {
         String[] tags = SPLIT_COMMA.split(tag);
         for(String singleTag : tags)
         {
            String trimmedTag = singleTag.trim();
            tagSet.add(trimmedTag);
         }
      }
      s_tagSet = tagSet;
      return tagSet;
   }

   static
   String[] getTagsArray()
   {
      int size = s_tagSet.size();
      return s_tagSet.toArray(new String[size]);
   }

   static
   int getSize()
   {
      return s_tagSet.size();
   }
   /* END */

   @Override
   public
   Fragment getItem(int position)
   {
      return FragmentTag.newInstance(position);
   }

   @Override
   public
   int getCount()
   {
      return s_tagSet.size();
   }

   @Override
   public
   String getPageTitle(int position)
   {
      int size = getCount();
      return s_tagSet.toArray(new String[size])[position];
   }
}