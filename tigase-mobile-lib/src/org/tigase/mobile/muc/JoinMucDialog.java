package org.tigase.mobile.muc;

import java.util.ArrayList;

import org.tigase.mobile.Constants;
import org.tigase.mobile.MessengerApplication;
import org.tigase.mobile.R;

import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.xmpp.modules.muc.MucModule;
import tigase.jaxmpp.j2se.Jaxmpp;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class JoinMucDialog extends DialogFragment {

	public static JoinMucDialog newInstance() {
		JoinMucDialog frag = new JoinMucDialog();
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity());
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);

		dialog.setContentView(R.layout.join_room_dialog);
		dialog.setTitle(getString(R.string.aboutButton));

		ArrayList<String> accounts = new ArrayList<String>();
		for (Account account : AccountManager.get(getActivity()).getAccountsByType(Constants.ACCOUNT_TYPE)) {
			accounts.add(account.name);
		}

		final Spinner accountSelector = (Spinner) dialog.findViewById(R.id.muc_accountSelector);
		final Button button = (Button) dialog.findViewById(R.id.muc_joinButton);
		final TextView roomName = (TextView) dialog.findViewById(R.id.muc_roomName);
		final TextView mucServer = (TextView) dialog.findViewById(R.id.muc_server);
		final TextView nickname = (TextView) dialog.findViewById(R.id.muc_nickname);
		final TextView password = (TextView) dialog.findViewById(R.id.muc_password);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
				accounts.toArray(new String[] {}));
		accountSelector.setAdapter(adapter);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BareJID account = BareJID.bareJIDInstance(accountSelector.getSelectedItem().toString());
				final Jaxmpp jaxmpp = ((MessengerApplication) getActivity().getApplicationContext()).getMultiJaxmpp().get(
						account);

				Runnable r = new Runnable() {

					@Override
					public void run() {
						try {
							jaxmpp.getModulesManager().getModule(MucModule.class).join(roomName.getEditableText().toString(),
									mucServer.getEditableText().toString(), nickname.getEditableText().toString(),
									password.getEditableText().toString());
						} catch (Exception e) {
							Log.w("MUC", "", e);
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
				(new Thread(r)).start();
				dialog.hide();
			}
		});

		return dialog;
	}
}
