package jimenchr.washington.edu.awty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private AlarmManager am;
    private PendingIntent pi;
    private int frequency;
    private String text;
    private String phoneNumber;
    private boolean start;
    private BroadcastReceiver alarmReciever;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = false;
        frequency = -1;

        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(MainActivity.this, phoneNumber + " : " + text, Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(alarmReciever, new IntentFilter("jimenchr.washington.edu.alarms"));

        Intent i = new Intent();
        i.setAction("jimenchr.washington.edu.alarms");
        pi = PendingIntent.getBroadcast(this, 0, i, 0);

        final Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!start) {
                    validatedFields();
                    if(text != null && phoneNumber != null && frequency > 0) {
                        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis() - frequency, frequency, pi);
                        button.setText("Stop");
                        start = true;
                    }
                } else {
                    button.setText("Start");
                    start = false;
                    am.cancel(pi);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void validatedFields() {
        EditText message = (EditText) findViewById(R.id.editMessage);
        EditText phone = (EditText) findViewById(R.id.editPhone);
        EditText frequency = (EditText) findViewById(R.id.editFreq);

        this.text = message.getText().toString();
        this.phoneNumber = phone.getText().toString();
        try {
            this.frequency = Integer.parseInt(frequency.getText().toString()) * 60 * 1000;
        } catch (NumberFormatException e) {
            this.frequency = -1;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        am.cancel(pi);
        pi.cancel();
    }
}
