package yay.poloure.simplerss;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class service_update extends IntentService
{
   static Context service_context;
   static String storage;

   public service_update()
   {
      super("service_update");
   }

   @Override
   protected void onHandleIntent(Intent intent)
   {
      service_context = this;

      PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
      PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SIMPLERSS");
      wakelock.acquire();

      Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

      String UNREAD_ITEM    = getString(R.string.notification_title_singular);
      String UNREAD_ITEMS   = getString(R.string.notification_title_plural);
      String GROUP_UNREAD   = getString(R.string.notification_content_tag_item);
      String GROUP_UNREADS  = getString(R.string.notification_content_tag_items);
      String GROUPS_UNREADS = getString(R.string.notification_content_tags);

      int page     = intent.getIntExtra("GROUP_NUMBER", 0);

      storage  = util.get_storage();

      String[] all_tags  = read.file(main.GROUP_LIST);
      String tag         = all_tags[page];

      String[][] content = read.csv(main.INDEX);
      String[] names     = content[0];
      String[] urls      = content[1];
      String[] tags      = content[2];

      /* Download and parse each feed in the tag. */
      boolean success;
      for(int i = 0; i < names.length; i++)
      {
         if(tags[i].equals(tag) || tag.equals(main.ALL))
         {
            success = write.dl(urls[i], names[i] + main.STORE);
            if(success)
               new parser(names[i]);
            else
               util.post("Download of " + urls[i] + " failed.");
         }
      }

      int[] unread_counts = util.get_unread_counts(all_tags);

      /* If activity is running. */
      if(main.service_handler != null)
      {
         Message m = new Message();
         Bundle b = new Bundle();
         b.putInt("page_number", page);
         m.setData(b);
         main.service_handler.sendMessage(m);
      }
      else if(unread_counts[0] != 0 && intent.getBooleanExtra("NOTIFICATIONS", false))
      {
         /* Calculate the number of tags with new items. */
         int tag_items = 1, count;
         int sizes       = unread_counts.length;

         for(int i = 1 ; i < sizes; i++)
         {
            count = unread_counts[i];
            if(count != 0)
               tag_items++;
         }

         String not_title;
         not_title = (unread_counts[0] == 1) ? String.format(UNREAD_ITEM, 1) :
                      String.format(UNREAD_ITEMS, unread_counts[0]);

         String not_content;
         if(unread_counts[0] == 1 && (tag_items - 1) == 1)
            not_content = String.format(GROUP_UNREAD, 1);
         else if(unread_counts[0] > 1 && (tag_items - 1) == 1)
            not_content = String.format(GROUP_UNREADS, 1);
         else
            not_content = String.format(GROUPS_UNREADS, (tag_items - 1));

         NotificationCompat.Builder not_builder = new NotificationCompat.Builder(this)
               .setSmallIcon(R.drawable.rss_icon)
               .setContentTitle(not_title)
               .setContentText(not_content)
               .setAutoCancel(true);

         Intent result_intent = new Intent(this, main.class);

         TaskStackBuilder stack_builder = TaskStackBuilder.create(this);

         stack_builder.addParentStack(main.class);
         stack_builder.addNextIntent(result_intent);
         PendingIntent result_pending_intent = stack_builder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
         not_builder.setContentIntent(result_pending_intent);
         NotificationManager notification_manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
         notification_manager.notify(1, not_builder.build());
      }

      wakelock.release();
      stopSelf();
   }

   public static boolean check_service_running(Activity activity)
   {
      ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
      for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
      {
         if(service_update.class.getName().equals(service.service.getClassName()))
            return true;
      }
      return false;
   }
}
