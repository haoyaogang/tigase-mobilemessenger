package org.tigase.messenger.phone.pro.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.tigase.messenger.phone.pro.utils.AvatarHelper;

import java.security.MessageDigest;
import java.util.Date;

import tigase.jaxmpp.android.roster.RosterItemsCacheTableMetaData;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule;
import tigase.jaxmpp.core.client.xmpp.stanzas.Presence;

public class RosterProviderExt extends tigase.jaxmpp.android.roster.RosterProvider {

    public RosterProviderExt(Context context, SQLiteOpenHelper dbHelper, Listener listener,
                             String versionKeyPrefix) {
        super(context, dbHelper, listener, versionKeyPrefix);
    }

    public void updateStatus(SessionObject sessionObject, JID jid) {
        long id = createId(sessionObject, jid.getBareJid());
        int status = 0;
        try {
            Presence p = PresenceModule.getPresenceStore(sessionObject).getBestPresence(jid.getBareJid());
            if (p != null) {
                status = CPresence.getStatusFromPresence(p);
            }
        } catch (XMLException ex) {
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.RosterItemsCache.FIELD_STATUS, status);
        db.update(RosterItemsCacheTableMetaData.TABLE_NAME, values, RosterItemsCacheTableMetaData.FIELD_ID + " = ?",
                new String[]{String.valueOf(id)});
        if (listener != null) {
            listener.onChange(id);
        }
    }

    public void resetStatus() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.RosterItemsCache.FIELD_STATUS, CPresence.OFFLINE);
        db.update(RosterItemsCacheTableMetaData.TABLE_NAME, values, null, null);
        if (listener != null) {
            listener.onChange(null);
        }
    }

    public void resetStatus(SessionObject sessionObject) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.RosterItemsCache.FIELD_STATUS, CPresence.OFFLINE);
        db.update(RosterItemsCacheTableMetaData.TABLE_NAME, values, RosterItemsCacheTableMetaData.FIELD_ACCOUNT + " = ?",
                new String[]{sessionObject.getUserBareJid().toString()});
        if (listener != null) {
            listener.onChange(null);
        }
    }

    public boolean checkVCardHash(SessionObject sessionObject, BareJID jid, String hash) {
        boolean ok = false;
        Cursor c = dbHelper.getReadableDatabase().query(
                DatabaseContract.VCardsCache.TABLE_NAME,
                new String[]{DatabaseContract.VCardsCache.FIELD_JID, DatabaseContract.VCardsCache.FIELD_DATA,
                        DatabaseContract.VCardsCache.FIELD_HASH}, DatabaseContract.VCardsCache.FIELD_JID + "=? AND " + DatabaseContract.VCardsCache.FIELD_HASH + "=?",
                new String[]{jid.toString(), hash}, null, null, null);
        ok = c.moveToNext();
        c.close();
        return ok;
    }

    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String encodeHex(byte[] data) {

        int l = data.length;

        char[] out = new char[l << 1];

        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }

        return new String(out);
    }

    public void updateVCardHash(SessionObject sessionObject, BareJID bareJid, byte[] data) {
        String jid = bareJid.toString();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.VCardsCache.FIELD_DATA, data);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("DELETE FROM " + DatabaseContract.VCardsCache.TABLE_NAME + " WHERE "
                    + DatabaseContract.VCardsCache.FIELD_JID + "='" + jid + "'");
            try {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update(values.getAsByteArray(DatabaseContract.VCardsCache.FIELD_DATA));
                String md5 = encodeHex(md.digest());
                values.put(DatabaseContract.VCardsCache.FIELD_HASH, md5);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            values.put(DatabaseContract.VCardsCache.FIELD_JID, jid);
            values.put(DatabaseContract.VCardsCache.FIELD_TIMESTAMP, (new Date()).getTime());
            db.insert(DatabaseContract.VCardsCache.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        if (listener != null) {
            listener.onChange(null);
        }
        AvatarHelper.clearAvatar(BareJID.bareJIDInstance(jid));
    }

    public static long createId(SessionObject sessionObject, BareJID jid) {
        return (sessionObject.getUserBareJid() + "::" + jid).hashCode();
    }

    public static long createId(BareJID account, BareJID jid) {
        return (account + "::" + jid).hashCode();
    }
}
