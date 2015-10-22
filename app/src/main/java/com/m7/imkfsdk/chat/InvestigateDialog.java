package com.m7.imkfsdk.chat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.m7.imkfsdk.R;
import com.moor.im.IMChatManager;
import com.moor.im.SubmitInvestigateListener;
import com.moor.im.model.entity.Investigate;

import java.util.ArrayList;
import java.util.List;

/**
 * 评价列表界面
 */
public class InvestigateDialog extends DialogFragment {

    private ListView investigateListView;

    private List<Investigate> investigates = new ArrayList<Investigate>();

    private InvestigateAdapter adapter;

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setTitle("提交评价");

        // Get the layout inflater
        View view = inflater.inflate(R.layout.dialog_investigate, null);
        investigateListView = (ListView) view.findViewById(R.id.investigate_list);

        investigates = IMChatManager.getInstance().getInvestigate();

        adapter = new InvestigateAdapter(getActivity(), investigates);

        investigateListView.setAdapter(adapter);

        investigateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Investigate investigate = (Investigate) parent.getAdapter().getItem(position);
                IMChatManager.getInstance().submitInvestigate(investigate, new SubmitInvestigateListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getActivity(), "评价提交成功", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }

                    @Override
                    public void onFailed() {
                        System.out.println("评价提交失败");
                        dismiss();
                    }
                });
            }
        });

        return view;
    }



}
