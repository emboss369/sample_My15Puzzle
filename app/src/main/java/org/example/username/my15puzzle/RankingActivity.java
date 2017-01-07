package org.example.username.my15puzzle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankingActivity extends AppCompatActivity {

    public static final String RESULT_TIME = "RESULT_TIME";
    protected static final String RESULT_TIME_STR = "RESULT_TIME_";

    private List<Integer> mList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        // 共有プリファレンスに保存してある過去のタイムをArrayListに読み込みます
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        for(int i = 0 ; i < 10; i++) {
            int result_time = pref.getInt(RESULT_TIME_STR + String.valueOf(i), 9999999);
            mList.add(Integer.valueOf(result_time));
        }
        // 繊維元から渡されたTimeを取り出す
        final Intent intent = getIntent();

        int time = intent.getIntExtra(RESULT_TIME,9999999);

        // タイムをリストに追加します。
        mList.add(Integer.valueOf(time));
        // 並び替えます。
        Collections.sort(mList);
        // 最後のタイムを削除します。
        mList.remove(mList.size() - 1);

        // 共有プリファレンスに新しいタイム一覧を保存します。
        SharedPreferences.Editor edit = pref.edit();
        for(int i = 0 ; i < 10; i++) {
            edit.putInt(RESULT_TIME_STR + String.valueOf(i),mList.get(i));
        }
        edit.apply();   // 非同期書き込み。同期書き込みはcommit();

        ScoreAdapter adapter = new ScoreAdapter(getApplicationContext(), R.layout.score_cell,time,mList);
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(adapter);

        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        GridLayoutAnimationController controller = new GridLayoutAnimationController(animation, .2f, .2f);
        gridView.setLayoutAnimation(controller);

        // OKボタンを押して閉じる
        // 今まではfinish()を押してActivityを閉じて、前のアクティビティに戻っていました。
        // 最初の画面へ一気に戻りたい場合は、インテントにフラグを指定してstartActivityを呼びます。
        Button okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMain = new Intent(RankingActivity.this,MainActivity.class);
                // スタックをクリアしてメインへ戻るフラグです。
                goToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(goToMain);
            }
        });

    }



    public class ScoreAdapter extends ArrayAdapter<Integer> {
        private int resourceId;
        private int current;

        class ViewHolder {
            ImageView icon;
            TextView time;
        }

        public ScoreAdapter(Context context, int resource, int time ,List<Integer> objects) {
            super(context, resource, objects);
            resourceId = resource;
            current = time;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(resourceId, null);
                final ViewHolder holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                convertView.setTag(holder);

            }
            final ViewHolder holder = (ViewHolder)convertView.getTag();
            if (position == 0) {
                holder.icon.setImageResource(R.drawable.ic_piyo_01);
                convertView.setBackgroundResource(R.color.score_gold);
            } else
            if (position == 1) {
                holder.icon.setImageResource(R.drawable.ic_piyo_02);
                convertView.setBackgroundResource(R.color.score_silver);
            } else
            if (position == 2) {
                holder.icon.setImageResource(R.drawable.ic_piyo_03);
                convertView.setBackgroundResource(R.color.score_bronze);
            } else {
                holder.icon.setImageBitmap(null);   // ４位以下はアイコンなし
                convertView.setBackgroundColor(0);  // ４位以下は色なし
            }

            holder.time.setText(getItem(position).toString() + " 秒");

            if (current == getItem(position)){
                holder.time.setTextColor(getColor(R.color.score_current));
            } else {
                holder.time.setTextColor(Color.BLACK);
            }

            return convertView;
        }


    }

}
