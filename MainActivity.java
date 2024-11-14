package edu.gmu.cs477.fall2020.lab4_lstreet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView numGoats, numWolves, numGoatBoats, numWolfBoats, numMixedBoats;
    public final static int UPDATEGOATS = 1;
    public final static int UPDATEWOLVES = 2;
    public static final int UPDATEGOATBOATS = 3;
    public static final int UPDATEWOLFBOATS = 4;
    public static final int UPDATEMIXEDBOATS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        numGoats = findViewById(R.id.text1);
        numGoats.setText("0");
        numWolves = findViewById(R.id.text2);
        numWolves.setText("0");
        numGoatBoats = findViewById(R.id.num_goatboats);
        numGoatBoats.setText("0");
        numWolfBoats = findViewById(R.id.num_wolfboats);
        numWolfBoats.setText("0");
        numMixedBoats = findViewById(R.id.num_mixedboats);
        numMixedBoats.setText("0");
    }

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            int newVal;

            switch(msg.what){
                case UPDATEGOATS:{
                    newVal = msg.arg1;
                    numGoats.setText(Integer.toString(newVal));
                    break;}
                case UPDATEWOLVES:{
                    newVal = msg.arg1;
                    numWolves.setText(Integer.toString(newVal));
                    break;}
                case UPDATEGOATBOATS:{
                    newVal = msg.arg1;
                    numGoatBoats.setText(Integer.toString(newVal));
                    numGoats.setText(Integer.toString(0));
                    break;}
                case UPDATEWOLFBOATS:{
                    newVal = msg.arg1;
                    numWolfBoats.setText(Integer.toString(newVal));
                    numWolves.setText(Integer.toString(0));
                    break;}
                case UPDATEMIXEDBOATS:{
                    newVal = msg.arg1;
                    numMixedBoats.setText(Integer.toString(newVal));
                    numGoats.setText(Integer.toString(0));
                    numWolves.setText(Integer.toString(0));
                    break;}
            }
        }
    };

    public void go(View v) { // code for Go button
        Button go = (Button)findViewById(R.id.go);
        go.setEnabled(false); // disable Go button for safety
        Crossing c = new Crossing();
        c.start();
    }

    class Crossing extends Thread {
        Crossing() {
        }

        public void run() {
            int total = 0;
            Boat boat = new Boat(handler);
            Random r = new Random();
            while (total < 100) { // generate 100 animals
                try {
                    Thread.sleep(500); // half second
                } catch (Exception e) {
                }
                int choice = r.nextInt(2);
                if (choice == 0) {
                    Wolf wolf = new Wolf(total, boat);
                    wolf.start();
                } else {
                    Goat goat = new Goat(total, boat);
                    goat.start();
                }
                total++;
            }
        }
    }

    public class Goat extends Thread{
        int goat_num;
        Boat b;
    //    Handler handler;

     //   Goat(int num, Boat b, Handler h) {
        Goat(int num, Boat b){
            this.goat_num = num;
            this.b = b;
       //     this.handler = h;
        }

        public void run(){
            System.out.println("Generated Goat" + goat_num);

            b.board_goat();
            System.out.println("Goat" + goat_num + " crossed");
        }
    }
    public class Wolf extends Thread{
        int wolf_num;
        Boat b;
    //    Handler handler;

      //  Wolf(int num, Boat b, Handler h) {
        Wolf(int num, Boat b){
            this.wolf_num = num;
            this.b = b;
       //     this.handler = h;
        }

        public void run(){
            System.out.println("Generated Wolf" + wolf_num);//update UI @string/goats++ here

            b.board_wolf();
            System.out.println("Wolf" + wolf_num + " crossed");

        }
    }

    public class Boat {
        int num_goats;
        int num_wolves;
        int goat_slots;
        int wolf_slots;

        int num_goat_boats = 0;
        int num_wolf_boats = 0;
        int num_mixed_boats = 0;
        Handler handler;

         Boat(Handler h){
       // Boat() {
            num_goats = 0;
            //    num_wolves = 0;};
            num_wolves = 0;
            handler = h;
        };

        //critical section
        public synchronized void board_goat() {
            Message msg;
            num_goats++;
            //update UI goat count here
            msg = handler.obtainMessage(UPDATEGOATS, num_goats, 0);
            handler.sendMessage(msg);

            if (num_goats == 4) {
                num_goats = 0;
                goat_slots = 3;
                wolf_slots = 0;
                num_goat_boats++;
                //update UI goat boat count here
                msg = handler.obtainMessage(UPDATEGOATBOATS, num_goat_boats, 0);
                handler.sendMessage(msg);
                notifyAll();
            } else if ((num_goats == 2) && (num_wolves >= 2)) {
                num_goats = 0;
                num_wolves -= 2;
                goat_slots = 1;
                wolf_slots = 2;
                num_mixed_boats++;
                //update UI mixed boat count here
                msg = handler.obtainMessage(UPDATEMIXEDBOATS, num_mixed_boats, 0);
                handler.sendMessage(msg);
                notifyAll();
            } else try {
                while (goat_slots == 0)
                    wait();
                goat_slots--;
            } catch (Exception e) {
            }
        }

        //critical section
        public synchronized void board_wolf() {
            Message msg;
            num_wolves++;
            //update UI wolf count here
            msg = handler.obtainMessage(UPDATEWOLVES, num_wolves, 0);
            handler.sendMessage(msg);

            if (num_wolves == 4) {
                num_wolves = 0;
                goat_slots = 0;
                wolf_slots = 3;
                num_wolf_boats++;
                //update wolf boat UI here
                msg = handler.obtainMessage(UPDATEWOLFBOATS, num_wolf_boats, 0);
                handler.sendMessage(msg);
                notifyAll();
            } else if ((num_goats >= 2) && (num_wolves == 2)) {
                num_goats -= 2;
                num_wolves = 0;
                goat_slots = 2;
                wolf_slots = 1;
                num_mixed_boats++;
                //update mixed boat UI here
                msg = handler.obtainMessage(UPDATEMIXEDBOATS, num_mixed_boats, 0);
                handler.sendMessage(msg);
                notifyAll();
            } else try {
                while (wolf_slots == 0)
                    wait();
                wolf_slots--;
            } catch (Exception e) {
            }
        }
    }
}