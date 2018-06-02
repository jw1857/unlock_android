package com.example.jameswinters.unlock_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import de.codecrafters.tableview.TableDataAdapter;
import de.codecrafters.tableview.TableHeaderAdapter;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class ProgressTableActivity extends AppCompatActivity {

    private ArrayList<POI> POIList;
    private ArrayList<sPOI> sPOIList;
    private ArrayList<hPOI> hPOIList;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    TableView<String[]> table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_progress_table);

        POIList = MainActivity.readPOIsFromSD(POIList, currentUser);
        sPOIList = MainActivity.readsPOIsFromSD(sPOIList, currentUser);
        hPOIList = MainActivity.readhPOIsFromSD(hPOIList, currentUser);

        int progressCountPOI = 0;
        for (POI p : POIList) {
            boolean lsp = p.getLockStatus();
            if (!lsp) {
                progressCountPOI = progressCountPOI + 1;
            }
        }
        int progressCountsPOI = 0;
        for (sPOI s : sPOIList) {
            boolean lss = s.getLockStatus();
            if (!lss) {
                progressCountsPOI = progressCountsPOI + 1;
            }
        }
        int progressCounthPOI = 0;
        for (hPOI h : hPOIList) {
            boolean lsh = h.getVisibility();
            if (lsh) {
                progressCounthPOI = progressCounthPOI + 1;
            }
        }

        int totalPOI = 0;
        int totalsPOI = 0;
        int totalhPOI = 0;

        totalPOI = POIList.size();
        totalsPOI = sPOIList.size();
        totalhPOI = hPOIList.size();

        int size = POIList.size()+hPOIList.size()+sPOIList.size();
        int poisunlocked = progressCounthPOI+progressCountPOI+progressCountsPOI;
        float poisunlockedpercent = ((float)poisunlocked/(float)size)*100;
        int poisint = (int)poisunlockedpercent;
        String poiout = Integer.toString(poisint);

        String[][] myData = new String[][]  {{currentUser.getDisplayName(),Integer.toString(progressCountPOI),Integer.toString(progressCountsPOI),Integer.toString(progressCounthPOI),poiout},
                {"Total", Integer.toString(totalPOI), Integer.toString(totalsPOI), Integer.toString(totalhPOI), "100"}
                };
        table = findViewById(R.id.progresstable);
        TableDataAdapter<String[]> myDataAdapter;
        myDataAdapter = new SimpleTableDataAdapter(this, myData);
        TableHeaderAdapter myHeaderAdapter =
                new SimpleTableHeaderAdapter(this, "", "POI", "sPOI", "hPOI", "%");
        table.setDataAdapter(myDataAdapter);
        table.setHeaderAdapter(myHeaderAdapter);


    }
}
