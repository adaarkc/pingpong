package janel.pingpong;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    protected List<ParseObject> mMessages;
    private static final String TAG = MessageAdapter.class.getSimpleName();

    public MessageAdapter(Context context, List<ParseObject> messages) {
        super(context, R.layout.message_item, messages);
        mContext = context;
        mMessages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ParseObject message = mMessages.get(position);
        String messageText = message.getString(ParseConstants.KEY_SENDER_NAME);

        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
            holder.nameLabel.setText(messageText);
            convertView.setTag(holder);
            return convertView;
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {
            holder.iconImageView.setImageResource(R.mipmap.ic_image_black_24dp);
        } else {
            holder.iconImageView.setImageResource(R.mipmap.ic_play_circle_outline_black_24dp);
        }
        holder.nameLabel.setText(messageText);
        Log.d(TAG, "logging.....................................");

        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
    }

    public void refill(List<ParseObject> messages) {
        mMessages.clear();
        mMessages.addAll(messages);
        notifyDataSetChanged();
    }
}
