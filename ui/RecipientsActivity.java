package janel.pingpong.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import janel.pingpong.adapters.UserAdapter;
import janel.pingpong.utils.FileHelper;
import janel.pingpong.utils.ParseConstants;
import janel.pingpong.R;

public class RecipientsActivity extends AppCompatActivity {

    private static final String TAG = RecipientsActivity.class.getSimpleName();
    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected Toolbar mToolbar;
    protected ViewPager mViewPager;
    protected MenuItem mSendMenuItem;
    protected ImageView mImage;
    private GridView mGridView;
    protected Uri mMediaUri;
    protected String mFileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mImage = (ImageView) findViewById(R.id.send_icon);
        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);

        mGridView = (GridView)findViewById(R.id.friendsGrid);

        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        mToolbar.setNavigationIcon(ContextCompat.getDrawable(RecipientsActivity.this, R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipientsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {

                if (mGridView.getCheckedItemCount() > 0) {
                    mImage.setVisibility(View.VISIBLE);
                } else {
                    mImage.setVisibility(View.INVISIBLE);
                }

                //Toast.makeText(RecipientsActivity.this, "Friends selected: " + mListView.getCheckedItemCount(), Toast.LENGTH_LONG).show();

/*                if (mSendMenuItem == null) {
                    Toast.makeText(RecipientsActivity.this, "Menu item is null.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RecipientsActivity.this, "It worked!!!!", Toast.LENGTH_LONG).show();

                    if (mListView.getCheckedItemCount() > 0) {
                        mSendMenuItem.setVisible(true);
                    } else {
                        mSendMenuItem.setVisible(false);
                    }
                }*/
            }
        });

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject message = createMessage();

                if (message == null) {
                    // error
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(R.string.file_error)
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    send(message);
                }
            }
        });
    }

    private void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(RecipientsActivity.this, R.string.message_sent_success, Toast.LENGTH_LONG).show();
                    finish(); //kills activity
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(R.string.error_sending_message)
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        setProgressBarIndeterminateVisibility(true);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    mUsers = friends;

                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(RecipientsActivity.this, mUsers);
                        mGridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter)mGridView.getAdapter()).refill(mUsers);
                    }

                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        Log.i(TAG, "In onCreateOptionsMenu.................................");

        mSendMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        Log.i(TAG, "In onPrepareOptionsMenu.................................");

        mSendMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home: {
                NavUtils.navigateUpFromSameTask(this);
                return true;
            }
            case R.id.action_send: {
                ParseObject message = createMessage();
                //send(message);
                return true;
            }
        }


        return super.onOptionsItemSelected(item);
    }

    protected ParseObject createMessage() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
        if (fileBytes == null) {
            return null;
        } else {
            if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }
            String filename = FileHelper.getFileName(this, mMediaUri, mFileType);
            ParseFile file = new ParseFile(filename, fileBytes);
            message.put(ParseConstants.KEY_FILE, file);
            return message;
        }
    }

    protected ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<>();
        for (int i = 0; i < mGridView.getCount(); i++) {
            if (mGridView.isItemChecked(i)) {
                recipientIds.add(mUsers.get(i).getObjectId());
            }
        }
        return recipientIds;
    }
}
