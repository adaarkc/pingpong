package janel.pingpong;

import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class InboxTab extends android.support.v4.app.ListFragment {

    protected List<ParseObject> mMessages;
    private static final String TAG = InboxTab.class.getSimpleName();
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.inbox, container, false);
    }

    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static InboxTab newInstance(int sectionNumber) {
        InboxTab fragment = new InboxTab();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListView = getListView();

        getActivity().setProgressBarIndeterminateVisibility(true);
        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addAscendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    mMessages = messages;

                    if (getListView().getAdapter() == null) {
                        MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                        setListAdapter(adapter);
                    } else {
                        ((MessageAdapter)getListView().getAdapter()).refill(mMessages);
                    }
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        Uri fileUri = Uri.parse(file.getUrl());

        if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
            //view image
            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            intent.setData(fileUri);
            startActivity(intent);
        } else {
            //view video
            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.setDataAndType(fileUri, "video/*");
            startActivity(intent);
        }

        List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);
        if (ids.size() == 1) {
            message.deleteInBackground();
        } else {
            ids.remove(ParseUser.getCurrentUser().getObjectId());
        }

        ArrayList<String> idsToRemove = new ArrayList<>();
        idsToRemove.add(ParseUser.getCurrentUser().getObjectId());
        message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
        message.saveInBackground();
    }

}