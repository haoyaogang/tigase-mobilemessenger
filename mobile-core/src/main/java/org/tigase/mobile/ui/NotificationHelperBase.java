package org.tigase.mobile.ui;

import org.tigase.mobile.Preferences;
import org.tigase.mobile.filetransfer.FileTransferUtility;
import org.tigase.mobile.service.FileTransferFeature;

import tigase.jaxmpp.core.client.JaxmppCore;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.chat.MessageModule.MessageEvent;
import tigase.jaxmpp.core.client.xmpp.modules.filetransfer.FileTransfer;
import tigase.jaxmpp.core.client.xmpp.modules.muc.MucModule.MucEvent;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;

public class NotificationHelperBase extends NotificationHelper {

	protected NotificationHelperBase(Context context) {
		super(context);
	}

	@Override
	protected Notification prepareChatNotification(int ico, String title, String text, PendingIntent pendingIntent,
			MessageEvent event) throws XMLException {
		long whenNotify = System.currentTimeMillis();
		Notification notification = new Notification(ico, title, whenNotify);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		// notification.flags |= Notification.FLAG_ONGOING_EVENT;
		updateSound(notification, Preferences.NOTIFICATION_CHAT_KEY);
		updateLight(notification, Preferences.NOTIFICATION_CHAT_KEY);
		updateVibrate(notification, Preferences.NOTIFICATION_CHAT_KEY);

		notification.setLatestEventInfo(context, title, text, pendingIntent);

		return notification;
	}

	@Override
	protected Notification prepareChatNotification(int ico, String title, String text, PendingIntent pendingIntent,
			MucEvent event) throws XMLException {
		long whenNotify = System.currentTimeMillis();
		Notification notification = new Notification(ico, title, whenNotify);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		// notification.flags |= Notification.FLAG_ONGOING_EVENT;

		updateSound(notification, Preferences.NOTIFICATION_MUC_MENTIONED_KEY);
		updateLight(notification, Preferences.NOTIFICATION_MUC_MENTIONED_KEY);
		updateVibrate(notification, Preferences.NOTIFICATION_MUC_MENTIONED_KEY);

		notification.setLatestEventInfo(context, title, text, pendingIntent);

		return notification;
	}

	@Override
	protected Notification prepareFileTransferProgressNotification(int ico, String title, String text, FileTransfer ft, FileTransferFeature.State state) {
		long whenNotify = System.currentTimeMillis();
		int flags = 0;

		switch (state) {
		case error:
			flags |= Notification.FLAG_AUTO_CANCEL;
			ico = android.R.drawable.stat_notify_error;
			break;

		case negotiating:
			flags |= Notification.FLAG_ONGOING_EVENT;
			flags |= Notification.FLAG_NO_CLEAR;
			break;

		case connecting:
			flags |= Notification.FLAG_ONGOING_EVENT;
			flags |= Notification.FLAG_NO_CLEAR;
			break;

		case active:
			flags |= Notification.FLAG_ONGOING_EVENT;
			flags |= Notification.FLAG_NO_CLEAR;
			break;

		case finished:
			boolean outgoing = !ft.isIncoming();
			ico = (outgoing) ? android.R.drawable.stat_sys_upload_done : android.R.drawable.stat_sys_download_done;
			flags |= Notification.FLAG_AUTO_CANCEL;
			if (!outgoing) {				
				FileTransferUtility.refreshMediaScanner(context.getApplicationContext(), ((tigase.jaxmpp.j2se.filetransfer.FileTransfer)ft).getFile());
			}
			break;
		default:
			break;
		}

		Notification notification = new Notification(ico, title, whenNotify);
		notification.flags = flags;

		PendingIntent pendingIntent = createFileTransferProgressPendingIntent(ft, state);

		notification.setLatestEventInfo(context, title, text, pendingIntent);

		return notification;
	}

	@Override
	protected Notification prepareFileTransferRequestNotification(int ico, String title, String text,
			FileTransfer ft, JaxmppCore jaxmpp, String tag) {
		long whenNotify = System.currentTimeMillis();
		Notification notification = new Notification(ico, title, whenNotify);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;

		updateSound(notification, Preferences.NOTIFICATION_FILE_KEY);
		updateLight(notification, Preferences.NOTIFICATION_FILE_KEY);
		updateVibrate(notification, Preferences.NOTIFICATION_FILE_KEY);

		PendingIntent pendingIntent = createFileTransferRequestPendingIntent(ft, jaxmpp, tag);
		notification.setLatestEventInfo(context, title, text, pendingIntent);

		return notification;
	}

}
