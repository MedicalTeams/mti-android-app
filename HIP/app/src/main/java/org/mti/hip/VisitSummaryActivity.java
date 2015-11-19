package org.mti.hip;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.mti.hip.model.Visit;
import org.mti.hip.utils.HttpClient;

import java.io.IOException;

public class VisitSummaryActivity extends SuperActivity {

    private Button submit;
    private String visitJson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_summary);
        Visit visit = getStorageManagerInstance().currentVisit();
        visitJson = getJsonManagerInstance().writeValueAsString(visit);
        Log.d("visit json", visitJson);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    Exception e;
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            getHttpClientInstance().post(HttpClient.visitEndpoint, visitJson);
                        } catch (IOException e) {
                            this.e = e;
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if (e == null) {
                            Intent i = new Intent(VisitSummaryActivity.this, DashboardActivity.class);
                            i.putExtra(EXTRA_MSG, "Visit submitted");
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } else {
                            alert.showAlert("Visit send failed", "Sad failure is sad");
                        }
                        super.onPostExecute(aVoid);
                    }
                }.execute();

            }
        });
    }


}
