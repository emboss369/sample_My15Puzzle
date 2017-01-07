package org.example.username.my15puzzle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.GridLayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements CompleteDialog.CompleteDialogListener,GiveUpDialog.GiveUpDialogListener{
    protected static final int IC_EGG = 15;
    private GridView mGridView;

    private TextView mTimeView;

    // CompleteDialogでOKを押した時の処理
    @Override
    public void onDialogPositiveClick() {
        int time = Integer.parseInt(mTimeView.getText().toString());
        // 成績画面を開く
        // 成績画面には、タイムを渡す。
        Intent intent = new Intent(this, RankingActivity.class);
        intent.putExtra(RankingActivity.RESULT_TIME,time);
        startActivity(intent);

    }

    @Override
    public void onDialogPositiveClick(GiveUpDialog dialog) {
        finish();
    }

    @Override
    public void onDialogNegativeClick(GiveUpDialog dialog) {

    }

    public class MainTimerTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateScore();
                    if(isComplete()) {
                        mTimer.cancel();
                        showDialog();
                    }
                }
            });
        }
    }
    Timer mTimer;
    TimerTask mTimerTask;
    Handler mHandler = new Handler();

    class Panel {
        int drawResource;
        int number;
        Panel(int drawResource, int number){
            this.drawResource = drawResource;
            this.number = number;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mTimeView = (TextView) findViewById(R.id.time);

        final ArrayList<Panel> list = new ArrayList<Panel>();
        list.add(new Panel(R.drawable.ic_piyo_01,0));
        list.add(new Panel(R.drawable.ic_piyo_02,1));
        list.add(new Panel(R.drawable.ic_piyo_03,2));
        list.add(new Panel(R.drawable.ic_piyo_04,3));

        list.add(new Panel(R.drawable.ic_piyo_05,4));
        list.add(new Panel(R.drawable.ic_piyo_06,5));
        list.add(new Panel(R.drawable.ic_piyo_07,6));
        list.add(new Panel(R.drawable.ic_piyo_08,7));

        list.add(new Panel(R.drawable.ic_piyo_09,8));
        list.add(new Panel(R.drawable.ic_piyo_10,9));
        list.add(new Panel(R.drawable.ic_piyo_11,10));
        list.add(new Panel(R.drawable.ic_piyo_12,11));

        list.add(new Panel(R.drawable.ic_piyo_13,12));
        list.add(new Panel(R.drawable.ic_piyo_14,13));
        list.add(new Panel(R.drawable.ic_piyo_15,14));
        list.add(new Panel(R.drawable.ic_egg, IC_EGG));

        // パネルをシャッフルする
        for (int i = 0; i< 10 ; i++) {
            swapRandom(list);
        }

        BitmapAdapter adapter = new BitmapAdapter(getApplicationContext(), R.layout.grid_cell,list);
        mGridView = (GridView) findViewById(R.id.grid_view);
        mGridView.setAdapter(adapter);

        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        GridLayoutAnimationController controller = new GridLayoutAnimationController(animation, .2f, .2f);
        mGridView.setLayoutAnimation(controller);

        // タイムを０に設定
        mTimeView.setText("0");
        mTimerTask = new MainTimerTask();

        // giveup
        ImageView giveup = (ImageView) findViewById(R.id.giveup);
        giveup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GiveUpDialog fragment =
                        GiveUpDialog.newInstance(R.drawable.piyo,
                                getString(R.string.giveup), getString(R.string.do_you_give_up));
                android.app.FragmentManager manager = getFragmentManager();
                fragment.show(manager, "InputCategoryDialog");

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTimer = new Timer();
        mTimer.schedule(mTimerTask,1000,1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }

    // パネルが並んでいるかチェックします。
    private boolean isComplete() {
        boolean isCompleted = true;
        int count = mGridView.getCount() - 1;
        for (int i = 0; i < count; i++) {
            Panel p = (Panel)mGridView.getItemAtPosition(i);
            if (p.number != i) {
                isCompleted = false;
                break;
            }
        }
        return isCompleted;
    }

    private void updateScore(){
        int time =  Integer.parseInt(mTimeView.getText().toString());
        time++;
        mTimeView.setText(String.valueOf(time));
    }

    private void showDialog(){
        CompleteDialog fragment =
                CompleteDialog.newInstance();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment.show(manager, "CompleteDialog");
    }

    // ランダムにパネルを入れ替えます
    private void swapRandom(ArrayList<Panel> list) {
        for (int i = 0 ; i < 16; i++ ) {
            Panel p = list.get(i);
            if(p.number == IC_EGG) {
                boolean canUp = i < 4 ? false : true;
                boolean canDown = i > 11 ? false : true;
                boolean canLeft = i % 4 == 0 ? false : true;
                boolean canRight = i % 4 == 3 ? false : true;
                int ran = (int)(Math.random()*4);   // 0〜3の乱数を得る
                int panelNo = i;
                if (ran == 0 && canUp) panelNo = i - 4;
                if (ran == 1 && canDown) panelNo = i + 4;
                if (ran == 2 && canRight) panelNo = i + 1;
                if (ran == 3 && canLeft) panelNo = i - 1;

                if (panelNo != i) { // パネルが移動可能であれば
                    Panel panel = list.get(panelNo);
                    list.remove(panel);
                    list.add(i, panel);
                    list.remove(p);
                    list.add(panelNo, p);
                }
            }
        }
    }

    public class BitmapAdapter extends ArrayAdapter<Panel> {

        private int resourceId;
        private Context context;

        class ViewHolder {
            ImageView imageView;
            int number;
            int position;
        }

        public BitmapAdapter(Context context, int resource, List<Panel> objects) {
            super(context, resource, objects);
            this.context = context;
            resourceId = resource;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(resourceId, null);
                final ViewHolder holder = new ViewHolder();
                holder.imageView = (ImageView) convertView;
                convertView.setTag(holder);
            }
            convertView.setAnimation(null);
            final ViewHolder holder = (ViewHolder)convertView.getTag();

            holder.position = position;
            holder.number = getItem(position).number;
            holder.imageView.setMaxWidth(parent.getWidth()/4);
            holder.imageView.setMaxHeight(parent.getHeight()/4);
            holder.imageView.setMinimumWidth(parent.getWidth()/4);
            holder.imageView.setMinimumHeight(parent.getHeight()/4);
            holder.imageView.setImageResource(getItem(position).drawResource);

            holder.imageView.setOnTouchListener(new View.OnTouchListener() {

                float meX;
                float meY;
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            boolean canUp = true;
                            boolean canDown = true;
                            boolean canLeft = true;
                            boolean canRight = true;

                            if (holder.position < 4) canUp = false;
                            if (holder.position > 11) canDown = false;
                            if (holder.position%4 == 0) canLeft = false;
                            if (holder.position%4 == 3) canRight = false;

                            if(canUp ){
                                final int panelNo = holder.position - 4;
                                final Panel panel = getItem(panelNo);
                                final int animationX = 0;
                                final int animationY= -view.getHeight();

                                if (panel.number == IC_EGG) {
                                    swapPanel(view, panelNo, panel, animationX, animationY);
                                }
                            }
                            if(canDown ){
                                final int panelNo = holder.position + 4;
                                final Panel panel = getItem(panelNo);
                                final int animationX = 0;
                                final int animationY= view.getHeight();

                                if (panel.number == IC_EGG) {
                                    swapPanel(view, panelNo, panel, animationX, animationY);
                                }
                            }
                            if(canRight ){
                                final int panelNo = holder.position + 1;
                                final Panel panel = getItem(panelNo);
                                final int animationX = view.getWidth();
                                final int animationY= 0;

                                if (panel.number == IC_EGG) {
                                    swapPanel(view, panelNo, panel, animationX, animationY);
                                }
                            }
                            if(canLeft ){
                                final int panelNo = holder.position - 1;
                                final Panel panel = getItem(panelNo);
                                final int animationX = -view.getWidth();
                                final int animationY= 0;

                                if (panel.number == IC_EGG) {
                                    swapPanel(view, panelNo, panel, animationX, animationY);
                                }
                            }
                            break;
                    }
                    return true;
                }

                private void swapPanel(View view, final int panelNo, final Panel panel, int animationX, int animationY) {
                    final ViewPropertyAnimator animator = view.animate();
                    animator.setDuration(100);
                    animator.setInterpolator(new DecelerateInterpolator());
                    animator.translationX(animationX);
                    animator.translationY(animationY);
                    animator.withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            animator.setDuration(300);
                            animator.translationX(0);
                            animator.translationY(0);
                            Panel temp = getItem(holder.position);
                            remove(panel);
                            insert(panel, holder.position);
                            remove(temp);
                            insert(temp,panelNo);
                            notifyDataSetChanged();
                        }
                    });
                }
            });
            return convertView;
        }
    }
}
