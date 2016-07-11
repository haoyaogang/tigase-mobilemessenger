/*
 * ViewHolder.java
 *
 * Tigase Android Messenger
 * Copyright (C) 2011-2016 "Tigase, Inc." <office@tigase.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */

package org.tigase.messenger.phone.pro.openchats;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
//import butterknife.Bind;
//import butterknife.ButterKnife;
import org.tigase.messenger.phone.pro.R;

public class ViewHolder extends RecyclerView.ViewHolder {

//	@Bind(R.id.contact_display_name)
	TextView mContactName;

//	@Bind(R.id.last_message)
	TextView mLastMessage;

//	@Bind(R.id.contact_avatar)
	ImageView mContactAvatar;

//	@Bind(R.id.contact_presence)
	ImageView mStatus;

//	@Bind(R.id.chat_delivery_status)
	ImageView mDeliveryStatus;

	public ViewHolder(View itemView) {
		super(itemView);
//		ButterKnife.bind(this, itemView);
		mContactName = (TextView)itemView.findViewById(R.id.contact_display_name);
		mLastMessage = (TextView)itemView.findViewById(R.id.last_message);
		mContactAvatar = (ImageView)itemView.findViewById(R.id.contact_avatar);
		mStatus = (ImageView)itemView.findViewById(R.id.contact_presence);
		mDeliveryStatus = (ImageView)itemView.findViewById(R.id.chat_delivery_status);

	}


	public void setContextMenu(final int menuId, final PopupMenu.OnMenuItemClickListener menuClick) {
		itemView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				PopupMenu popup = new PopupMenu(itemView.getContext(), itemView);
				popup.inflate(menuId);
				popup.setOnMenuItemClickListener(menuClick);
				popup.show();
				return true;
			}
		});

	}

}
