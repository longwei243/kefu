package com.m7.imkfsdk.chat.chatrow;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.holder.BaseHolder;
import com.m7.imkfsdk.chat.holder.InvestigateViewHolder;
import com.m7.imkfsdk.chat.holder.TextViewHolder;
import com.m7.imkfsdk.utils.FaceConversionUtil;
import com.moor.imkf.IMChatManager;
import com.moor.imkf.SubmitInvestigateListener;
import com.moor.imkf.model.entity.FromToMessage;
import com.moor.imkf.model.entity.Investigate;
import com.moor.imkf.utils.AnimatedGifDrawable;
import com.moor.imkf.utils.AnimatedImageSpan;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by longwei on 2016/3/9.
 */
public class InvestigateChatRow extends BaseChatRow{

    private Context context;

    public InvestigateChatRow(int type) {
        super(type);
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, FromToMessage detail) {
        return false;
    }

    @Override
    protected void buildChattingData(final Context context, BaseHolder baseHolder, FromToMessage detail, int position) {
        this.context = context;
        InvestigateViewHolder holder = (InvestigateViewHolder) baseHolder;
        FromToMessage message = detail;
        LinearLayout linearLayout = holder.getChat_investigate_ll();
        linearLayout.removeAllViews();
        if(message != null) {
            final List<Investigate> investigates = message.investigates;
            for (int i=0; i<investigates.size(); i++) {
                System.out.println("name is:"+investigates.get(i).name);
                LinearLayout investigateItem = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.investigate_item, null);
                TextView tv = (TextView) investigateItem.findViewById(R.id.investigate_item_tv_name);
                final Investigate investigate = investigates.get(i);
                tv.setText(investigate.name);
                investigateItem.setTag(investigate);
                investigateItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "点击了:"+investigate.name, Toast.LENGTH_SHORT).show();
                        IMChatManager.getInstance().submitInvestigate(investigate, new SubmitInvestigateListener() {
                            @Override
                            public void onSuccess() {
//                                Toast.makeText(context, "评价成功", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onFailed() {

                            }
                        });
                    }
                });
                linearLayout.addView(investigateItem);
            }
        }
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.chat_row_investigate, null);
            InvestigateViewHolder holder = new InvestigateViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, false));
        }
        return convertView;
    }

    @Override
    public int getChatViewType() {
        return ChatRowType.INVESTIGATE_ROW_TRANSMIT.ordinal();
    }
}
