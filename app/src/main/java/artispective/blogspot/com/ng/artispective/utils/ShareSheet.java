package artispective.blogspot.com.ng.artispective.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.adapters.ShareAdapter;
import artispective.blogspot.com.ng.artispective.interfaces.ShareItemClickListener;
import artispective.blogspot.com.ng.artispective.models.ShareItem;

public class ShareSheet extends BottomSheetDialog {
    private Context context;
    private ShareItemClickListener shareItemClickListener;

    public ShareSheet(Context context, ShareItemClickListener shareItemClickListener) {
        super(context);
        this.shareItemClickListener = shareItemClickListener;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        setContentView(view);

        ArrayList<ShareItem> shareItems = new ArrayList<>(2);
        shareItems.add(new ShareItem(R.drawable.facebook, "Facebook"));
        shareItems.add(new ShareItem(R.drawable.twitter, "Twitter"));
//        shareItems.add(new ShareItem(R.drawable.google_plus, "Google+"));

        ShareAdapter adapter = new ShareAdapter(context, shareItems);
        ListView listView = (ListView) findViewById(R.id.share_items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        shareItemClickListener.onFacebookClicked();
                        dismiss();
                        break;
                    case 1:
                        shareItemClickListener.onTwitterClicked();
                        dismiss();
                        break;
//                    case 2:
//                        shareItemClickListener.onGooglePlusClicked();
//                        dismiss();
//                        break;
                    default:
                        dismiss();
                        break;
                }
            }
        });
    }
}
