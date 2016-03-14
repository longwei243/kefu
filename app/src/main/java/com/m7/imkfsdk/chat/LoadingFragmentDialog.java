package com.m7.imkfsdk.chat;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.m7.imkfsdk.R;

/**
 * Created by long on 2015/7/6.
 */
public class LoadingFragmentDialog extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_loading, null);
        TextView title = (TextView) view
                .findViewById(R.id.id_dialog_loading_msg);
        title.setText("请稍等...");
        Dialog dialog = new Dialog(getActivity(), R.style.dialog);
        dialog.setContentView(view);
        return dialog;
    }


}
