package janel.pingpong.adapters;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import janel.pingpong.utils.MD5util;
import janel.pingpong.utils.ParseConstants;
import janel.pingpong.R;

public class UserAdapter extends ArrayAdapter<ParseUser> {

    protected Context mContext;
    protected List<ParseUser> mUsers;
    private static final String TAG = MessageAdapter.class.getSimpleName();

    public UserAdapter(Context context, List<ParseUser> users) {
        super(context, R.layout.message_item, users);
        mContext = context;
        mUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ParseUser user = mUsers.get(position);
        String userText = user.getUsername();

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            holder = new ViewHolder();
            holder.userImageView = (ImageView) convertView.findViewById(R.id.avatar);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            holder.nameLabel.setText(userText);
            holder.userImageViewChecked = (ImageView)convertView.findViewById(R.id.checkMark);
            convertView.setTag(holder);
            return convertView;
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        String email = user.getEmail().toLowerCase();

        if (email.equals("")) {
            holder.userImageView.setImageResource(R.mipmap.avatar_empty);
        } else {
            String hash = MD5util.md5Hex(email);
            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";
            Picasso.with(mContext)
                    .load(gravatarUrl)
                    .placeholder(R.mipmap.avatar_empty)
                    .into(holder.userImageView);
        }

        holder.nameLabel.setText(userText);

        GridView gridView = (GridView)parent;
        if (gridView.isItemChecked(position)) {
            holder.userImageViewChecked.setVisibility(View.VISIBLE);
        } else {
            holder.userImageViewChecked.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView userImageView;
        ImageView userImageViewChecked;
        TextView nameLabel;
    }

    public void refill(List<ParseUser> users) {
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }
}
