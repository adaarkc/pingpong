package janel.pingpong.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

import janel.pingpong.utils.ParseConstants;
import janel.pingpong.R;

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
        Date createdAt = message.getCreatedAt();
        long now = new Date().getTime();
        String convertedDate = DateUtils.getRelativeTimeSpanString(
                createdAt.getTime(),
                now,
                DateUtils.SECOND_IN_MILLIS)
                .toString();

        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
            holder.nameLabel.setText(messageText);
            holder.timeLabel = (TextView) convertView.findViewById(R.id.timeLabel);
            holder.timeLabel.setText(convertedDate);
            convertView.setTag(holder);
            return convertView;
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {
            holder.iconImageView.setImageResource(R.mipmap.ic_picture);
        } else {
            holder.iconImageView.setImageResource(R.mipmap.ic_video);
        }
        holder.nameLabel.setText(messageText);

        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        TextView timeLabel;
    }

    public void refill(List<ParseObject> messages) {
        mMessages.clear();
        mMessages.addAll(messages);
        notifyDataSetChanged();
    }
}
