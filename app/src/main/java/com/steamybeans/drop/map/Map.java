package com.steamybeans.drop.map;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.steamybeans.drop.R;
import com.steamybeans.drop.firebase.Drop;
import com.steamybeans.drop.firebase.Vote;

public class Map extends AppCompatActivity {
    private final Context context;

    public Map(Context context) {
        this.context = context;
    }

    public void setUpMarkerClickListener(GoogleMap mMap) {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(final Marker marker) {
                Drop drop = new Drop();
                final Vote vote = new Vote();

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialogue_view_drop);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                //find text view on dialog
                TextView dropDialogTitle = dialog.findViewById(R.id.TVviewDialogTitle);
                drop.setDropContent(marker.getTitle(), marker.getSnippet(), dropDialogTitle);

                Button BTNupvote = dialog.findViewById(R.id.BTNupvote);
                Button BTNdownvote = dialog.findViewById(R.id.BTNdownvote);

                BTNupvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vote.makeAVote(1,marker.getTitle(), marker.getSnippet());
                    }
                });
                BTNdownvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vote.makeAVote(-1,marker.getTitle(), marker.getSnippet());
                    }
                });


                dialog.show();
                return true;
            }
        });
    }
}
