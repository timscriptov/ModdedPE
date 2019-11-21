package com.microsoft.onlineid.internal.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.microsoft.onlineid.internal.Resources;
import com.microsoft.onlineid.internal.profile.DownloadProfileImageTask;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.ui.AbstractListAdapter;
import java.util.HashSet;
import java.util.Set;

public class AccountListAdapter extends AbstractListAdapter<AuthenticatorUserAccount> {
    protected final Context _applicationContext;
    private final int _imageUserTileResId;
    protected final Resources _resources;
    private final int _textEmailResId;
    private final int _textFirstLastResId;
    protected final Set<TextView> _visibleAccounts;

    public AccountListAdapter(Activity activityContext) {
        this._visibleAccounts = new HashSet();
        this._applicationContext = activityContext.getApplicationContext();
        this._resources = new Resources(this._applicationContext);
        this._imageUserTileResId = this._resources.getId("imageUserTile");
        this._textFirstLastResId = this._resources.getId("textFirstLast");
        this._textEmailResId = this._resources.getId("textEmail");
    }

    public AccountListAdapter() {
        this._visibleAccounts = new HashSet();
        this._applicationContext = null;
        this._resources = null;
        this._imageUserTileResId = 0;
        this._textFirstLastResId = 0;
        this._textEmailResId = 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AuthenticatorUserAccount account = (AuthenticatorUserAccount) this._items.get(position);
        if (convertView == null) {
            convertView = ((LayoutInflater) parent.getContext().getSystemService("layout_inflater")).inflate(this._resources.getLayout("account_picker_tile"), null);
        }
        setupBasicAccountViews(convertView, account);
        return convertView;
    }

    protected void setupBasicAccountViews(View inflatedView, AuthenticatorUserAccount account) {
        ImageView imageUserTile = (ImageView) inflatedView.findViewById(this._imageUserTileResId);
        TextView textFirstLast = (TextView) inflatedView.findViewById(this._textFirstLastResId);
        TextView textEmail = (TextView) inflatedView.findViewById(this._textEmailResId);
        account.getDisplayName();
        textEmail.setText(account.getUsername());
        if (textFirstLast != null) {
            textFirstLast.setText(account.getDisplayName());
        }
        new DownloadProfileImageTask(this._applicationContext, account, imageUserTile).execute(new Void[0]);
    }

    public long getItemId(int position) {
        return (long) ((AuthenticatorUserAccount) this._items.get(position)).hashCode();
    }

    protected void onChanged() {
        super.onChanged();
        this._visibleAccounts.clear();
    }

    public int getItemViewType(int position) {
        return 0;
    }

    public int getViewTypeCount() {
        return 1;
    }
}
