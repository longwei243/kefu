package com.m7.imkfsdk.chat;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.m7.imkfsdk.R;
import com.moor.imkf.model.entity.Peer;

import java.util.ArrayList;
import java.util.List;

/**
 * 技能组列表界面
 */
public class PeerDialog extends DialogFragment {

    private ListView investigateListView;

    private List<Peer> peers = new ArrayList<Peer>();

    private PeerAdapter adapter;

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setTitle("选择技能组");

        // Get the layout inflater
        View view = inflater.inflate(R.layout.dialog_investigate, null);
        investigateListView = (ListView) view.findViewById(R.id.investigate_list);

        Bundle bundle = getArguments();
        peers = (List<Peer>) bundle.getSerializable("Peers");

        adapter = new PeerAdapter(getActivity(), peers);

        investigateListView.setAdapter(adapter);

        investigateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                Peer peer = (Peer) parent.getAdapter().getItem(position);
                Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                chatIntent.putExtra("PeerId", peer.getId());
                startActivity(chatIntent);
            }
        });

        return view;
    }



}
