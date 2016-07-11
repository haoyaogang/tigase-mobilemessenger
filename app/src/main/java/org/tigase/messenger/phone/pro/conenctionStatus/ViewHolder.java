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

package org.tigase.messenger.phone.pro.conenctionStatus;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
//import butterknife.Bind;
//import butterknife.ButterKnife;
import org.tigase.messenger.phone.pro.R;

public class ViewHolder extends RecyclerView.ViewHolder {

//	@Bind(R.id.server_name)
	TextView mServerName;

//	@Bind(R.id.server_status_stage)
	TextView mStage;

//	@Bind(R.id.server_status_connected)
	TextView mConnected;

//	@Bind(R.id.server_status_sessionresumption)
	TextView mResumption;

	public ViewHolder(final View itemView) {
		super(itemView);
//		ButterKnife.bind(this, itemView);
		mServerName = (TextView) itemView.findViewById(R.id.server_name);
		mStage = (TextView) itemView.findViewById(R.id.server_status_stage);
		mConnected = (TextView) itemView.findViewById(R.id.server_status_connected);
		mResumption = (TextView) itemView.findViewById(R.id.server_status_sessionresumption);
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
