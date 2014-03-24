/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.poloure.simplerss;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public
class AdapterTags extends ArrayAdapter<FeedItem>
{
    static final Set<Long> READ_ITEM_TIMES = Collections.synchronizedSet(new HashSet<Long>(0));
    static final int TYPE_PLAIN = 0;
    static final int TYPE_IMAGE = 1;
    static final int TYPE_IMAGE_SANS_DESCRIPTION = 2;
    static final int TYPE_PLAIN_SANS_DESCRIPTION = 3;
    static final float READ_OPACITY = 0.5F;
    /* We use indexOf on this Long List so it can not be a Set. */
    public final List<Long> m_itemTimes = new ArrayList<Long>(0);
    private final Context m_context;

    public
    AdapterTags(Context context)
    {
        super(context, android.R.id.list);
        m_context = context;
    }

    @Override
    public
    long getItemId(int position)
    {
        return position;
    }

    @Override
    public
    View getView(int position, View convertView, ViewGroup parent)
    {
        int viewType = getItemViewType(position);
        FeedItem item = getItem(position);

        ViewFeedItem view = null != convertView ? (ViewFeedItem) convertView : new ViewFeedItem(m_context, viewType);

        // Apply the read effect.
        boolean isRead = READ_ITEM_TIMES.contains(item.m_time);
        view.setAlpha(isRead ? READ_OPACITY : 1.0F);
        view.setBackgroundResource(isRead ? R.drawable.selector_transparent : R.drawable.selector_white);

        // If the recycled view is the view we want, keep it.
        if(null != convertView && item.m_time.equals(view.m_item.m_time))
        {
            return view;
        }

        // Set the information.
        view.m_item = item;
        view.m_hasImage = TYPE_IMAGE == viewType || TYPE_IMAGE_SANS_DESCRIPTION == viewType;

        // If the view was an image, load the image.
        if(view.m_hasImage)
        {
            view.setBitmap(null);
            view.setTag(item.m_time);
            AsyncLoadImage.newInstance(view, item.m_imageName, item.m_time);
        }

        return view;
    }

    @Override
    public
    int getItemViewType(int position)
    {
        FeedItem item = getItem(position);

        boolean isDes = !item.m_desLines[0].isEmpty();

        if(item.m_imageLink.isEmpty())
        {
            return isDes ? TYPE_PLAIN : TYPE_PLAIN_SANS_DESCRIPTION;
        }
        else
        {
            return isDes ? TYPE_IMAGE : TYPE_IMAGE_SANS_DESCRIPTION;
        }
    }

    @Override
    public
    int getViewTypeCount()
    {
        return 4;
    }
}
