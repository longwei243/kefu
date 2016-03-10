package com.m7.imkfsdk.chat.chatrow;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.holder.BaseHolder;
import com.m7.imkfsdk.chat.holder.TextViewHolder;
import com.m7.imkfsdk.utils.FaceConversionUtil;
import com.moor.imkf.model.entity.FromToMessage;
import com.moor.imkf.utils.AnimatedGifDrawable;
import com.moor.imkf.utils.AnimatedImageSpan;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by longwei on 2016/3/9.
 */
public class TextRxChatRow extends BaseChatRow{

    private Context context;

    public TextRxChatRow(int type) {
        super(type);
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, FromToMessage detail) {
        return false;
    }

    @Override
    protected void buildChattingData(Context context, BaseHolder baseHolder, FromToMessage detail, int position) {
        this.context = context;
        TextViewHolder holder = (TextViewHolder) baseHolder;
        FromToMessage message = detail;
        if(message != null) {
            if(message.showHtml) {
                // 给对话内容赋值
                URLImageParser p = new URLImageParser(holder.getDescTextView(), context);
                holder.getDescTextView().setText(Html.fromHtml(message.message, p, null));
                holder.getDescTextView().setMovementMethod(LinkMovementMethod.getInstance());

            }else {
                SpannableStringBuilder content = handler(holder.getDescTextView(),
                        message.message);
                SpannableString spannableString = FaceConversionUtil.getInstace()
                        .getExpressionString(context, content + "");
                holder.getDescTextView().setText(spannableString);// 给对话内容赋值
            }


        }
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.chat_row_text_rx, null);
            TextViewHolder holder = new TextViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, true));
        }
        return convertView;
    }

    @Override
    public int getChatViewType() {
        return ChatRowType.TEXT_ROW_RECEIVED.ordinal();
    }

    private SpannableStringBuilder handler(final TextView gifTextView,
                                           String content) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();
            try {
                String num = tempText.substring(
                        "#[face/png/f_static_".length(), tempText.length()
                                - ".png]#".length());
                String gif = "face/gif/f" + num + ".gif";
                /**
                 * 如果open这里不抛异常说明存在gif，则显示对应的gif 否则说明gif找不到，则显示png
                 * */
                InputStream is = context.getAssets().open(gif);
                sb.setSpan(new AnimatedImageSpan(new AnimatedGifDrawable(is,
                                new AnimatedGifDrawable.UpdateListener() {
                                    @Override
                                    public void update() {
                                        gifTextView.postInvalidate();
                                    }
                                })), m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                is.close();
            } catch (Exception e) {
                String png = tempText.substring("#[".length(),
                        tempText.length() - "]#".length());
                try {
                    sb.setSpan(
                            new ImageSpan(context,
                                    BitmapFactory.decodeStream(context
                                            .getAssets().open(png))),
                            m.start(), m.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        return sb;
    }

    public class URLImageParser implements Html.ImageGetter {
        Context c;
        View container;

        /***
         * Construct the URLImageParser which will execute AsyncTask and refresh the container
         * @param t
         * @param c
         */
        public URLImageParser(View t, Context c) {
            this.c = c;
            this.container = t;
        }

        public Drawable getDrawable(String source) {
            URLDrawable urlDrawable = new URLDrawable();

            // get the actual source
            ImageGetterAsyncTask asyncTask =
                    new ImageGetterAsyncTask( urlDrawable);

            asyncTask.execute(source);

            // return reference to URLDrawable where I will change with actual image from
            // the src tag
            return urlDrawable;
        }

        public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
            URLDrawable urlDrawable;

            public ImageGetterAsyncTask(URLDrawable d) {
                this.urlDrawable = d;
            }

            @Override
            protected Drawable doInBackground(String... params) {
                String source = params[0];
                return fetchDrawable(source);
            }

            @Override
            protected void onPostExecute(Drawable result) {
                // set the correct bound according to the result from HTTP call
                urlDrawable.setBounds(0, 0, result.getIntrinsicWidth(), result.getIntrinsicHeight());

                // change the reference of the current drawable to the result
                // from the HTTP call
                urlDrawable.drawable = result;

                // redraw the image by invalidating the container
                URLImageParser.this.container.invalidate();
            }

            /***
             * Get the Drawable from URL
             * @param urlString
             * @return
             */
            public Drawable fetchDrawable(String urlString) {
                try {
                    InputStream is = fetch(urlString);
                    Drawable drawable = Drawable.createFromStream(is, "src");
                    drawable.setBounds(0, 0, 25 + drawable.getIntrinsicWidth(), 25
                            + drawable.getIntrinsicHeight());
                    return drawable;
                } catch (Exception e) {
                    return null;
                }
            }

            private InputStream fetch(String urlString) throws MalformedURLException, IOException {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet request = new HttpGet(urlString);
                HttpResponse response = httpClient.execute(request);
                return response.getEntity().getContent();
            }
        }
    }

    public class URLDrawable extends BitmapDrawable {
        // the drawable that you need to set, you could set the initial drawing
        // with the loading image if you need to
        protected Drawable drawable;

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if(drawable != null) {
                drawable.draw(canvas);
            }
        }
    }
}
