package com.example.wtz.learntosing.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wtz.learntosing.R;
import com.example.wtz.learntosing.adapter.CommonBaseAdapter;
import com.example.wtz.learntosing.circle.CircleProgress;
import com.example.wtz.learntosing.media.LoadMp3Task;
import com.example.wtz.learntosing.media.MediaManager;
import com.example.wtz.learntosing.media.RecordTask;
import com.example.wtz.learntosing.media.WriteToFileTask;
import com.example.wtz.learntosing.utils.AnalysisTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    @InjectView(R.id.learn_list)
    ListView mListView;
    @InjectView(R.id.test)
    Button mButton;
    @InjectView(R.id.load_mp3_progressBar)
    public ProgressBar mLoadBar;

    private String SDPathString = Environment.getExternalStorageDirectory().
            getAbsolutePath() + "/learntosing/exercise/";
    public String filepath =
            Environment.getExternalStorageDirectory() + "/learntosing";
    private TimeCount time;
    private LearnAdapter mAdapter;
    private RecordTask recordTask;
    private List<Integer> integerList;
    private int[] colornums;//0代表原来，1代表错误
    private Map<Integer, Integer> timeMap;
    public static Map<Integer, List<Integer>> colorMap;
    public Map<Integer, Integer> scoreMap;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case 0:
                    int[] colorlist=msg.getData().getIntArray("colorlist");
                    int index = msg.arg1;
                    int score = msg.arg2;
                    int firstVisible = mListView.getFirstVisiblePosition();
                    int lastVisible = mListView.getLastVisiblePosition();
                    if (index >= firstVisible && index <= lastVisible) {
                        ViewHolder holder = (ViewHolder) (mListView
                                .getChildAt(index - firstVisible).getTag());//index- firstVisible
                        updateListViewItem(holder, index, score,colorlist);
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };
    public static String[] srcStrings = {

            "如果那两个字没有颤抖",
            "我不会发现我难受",
            "怎么说出口",
            "也不过是分手",
            "怀抱既然不能逗留",
            "何不在离开的时候",
            "一边享受一边泪流",
            "十年之前我不认识你",
            "你不属于我",
            "我们还是一样",
            "陪在一个陌生人左右",
            "走过渐渐熟悉的街头"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mAdapter = new LearnAdapter(MainActivity.this);
        colorMap = new HashMap<Integer, List<Integer>>();
        scoreMap = new HashMap<Integer, Integer>();
        mAdapter.setDataList(getData());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
//        mButton.setVisibility(View.GONE);
        File path1 = new File(filepath + "/tenyears");
        if (!path1.exists()) {
            //若不存在
            mListView.setVisibility(View.GONE);
            mButton.setVisibility(View.VISIBLE);
        }
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadBar.setVisibility(View.VISIBLE);
                for (int i = 1; i < 13; i++) {
                    LoadMp3Task task = new LoadMp3Task(MainActivity.this);
                    task.execute(i + "", "tenyears", "shinian_part" + i + "_std");

                }

            }
        });

    }

    //下载完文件之后的刷新
    public void refresh() {

        mListView.setVisibility(View.VISIBLE);
        mButton.setVisibility(View.GONE);
        getData();
    }

    public void refreshScore(int n, int score,int a[]) {
        Message message = new Message();
        message.what = 0;
        message.arg1 = n;
        message.arg2 = score;
        Bundle bundle = new Bundle();
        bundle.putIntArray("colorlist",a);  //往Bundle中存放数据
        message.setData(bundle);//mes利用Bundle传递数据
        mHandler.sendMessage(message);
    }

    private List<String> getData() {
        List<String> mdata = new ArrayList<String>();
        for (int j = 1; j < 13; j++) {
            mdata.add(filepath + "/tenyears/shinian_part" + j + "_std.mp3");
        }
        return mdata;
    }

    //录音结束后更新listview Item
    private void updateListViewItem(ViewHolder holder, int position, int socre,int []colorlist) {
        // Log.d(TAG, "updateListViewItem "+task);

        holder.record.setBackgroundResource(R.drawable.layer_record_music_gray);
        holder.score.setVisibility(View.VISIBLE);
        holder.score.setText(socre + "");

        scoreMap.put(position, socre);
        colornums = new int[holder.name.getText().toString().length()];
        integerList = new ArrayList<Integer>();

//        integerList.clear();
        for (int i = 0; i < holder.name.getText().toString().length(); i++) {
            if (colorlist[i]==1) {
                colornums[i] = 1;
                integerList.add(i);
                Log.i("index", i + "ind" + position);
            } else {
                colornums[i] = 0;
            }

        }
        if (integerList.size() != 0) {
            Log.i("indexposition", position + "");
            colorMap.put(position, integerList);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(holder.name.getText().toString());

        for (int i = 0; i < integerList.size(); i++) {
            System.out.println(integerList.get(i) + ", ");
            //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
            builder.setSpan(
                    new ForegroundColorSpan(Color.RED),
                    integerList.get(i),
                    integerList.get(i) + 1,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);     //设置指定位置文字的颜色
        }
        holder.name.setText(builder);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mAdapter.changeImageVisable(view, position);

    }

    class LearnAdapter extends CommonBaseAdapter<String> {
        private Context mContext;
        private View mLastView;
        private int mLastPosition;
        private int mLastVisibility;

        protected LearnAdapter(Context context) {
            super(context);
            mLastPosition = -1;
            timeMap = new HashMap<Integer, Integer>();


        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            mLoadBar.setVisibility(View.GONE);
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.lear_list_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Log.i("colormap", colorMap.containsKey(position) + "po" + position);
            if (scoreMap.containsKey(position)) {
                holder.score.setVisibility(View.VISIBLE);
                holder.score.setText(scoreMap.get(position) + "");
            } else {
                holder.score.setVisibility(View.GONE);

            }
            if (colorMap.containsKey(position)) {
                SpannableStringBuilder builder1 = new SpannableStringBuilder(srcStrings[position]);

                for (int i = 0; i < colorMap.get(position).size(); i++) {
                    Log.i("colormap_list", colorMap.get(position).get(i) + "");
                    //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                    builder1.setSpan(
                            new ForegroundColorSpan(Color.RED),
                            colorMap.get(position).get(i),
                            colorMap.get(position).get(i) + 1,
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);     //设置指定位置文字的颜色
                }
                holder.name.setText(builder1);

            } else {
//                holder.name.setText(mDataList.get(position));
                holder.name.setText(srcStrings[position]);

            }
            final String[] lianxis = new String[getCount()];
            if (mLastPosition == position) {
                holder.hint.setVisibility(mLastVisibility);

            } else {
                holder.hint.setVisibility(View.GONE);
            }
            holder.playmusic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.playmusic.setBackgroundResource(R.drawable.selector_btn_pause);
                    holder.playrecord.stopCartoom();
                    holder.record.stopCartoom();
                    Toast.makeText(MainActivity.this, "播放音乐", Toast.LENGTH_SHORT).show();
                    timeMap.put(position,
                            MediaManager.playSound(
                                    mDataList.get(position),
                                    new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            holder.playmusic.setBackgroundResource(R.drawable.selector_btn_play);
                                        }
                                    }));
                    holder.playmusic.startCartoom(timeMap.get(position));

                }
            });

            holder.record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaManager.release();
                    holder.playmusic.stopCartoom();
                    holder.playrecord.stopCartoom();
                    holder.record.setBackgroundResource(R.drawable.layer_record_music);
                    int recordtime = 3;
                    Log.i("----timelong--->>", timeMap.get(position) + "");
                    if (timeMap.containsKey(position)) {
                        recordtime = timeMap.get(position);
                    }
                    holder.record.startCartoom((int) Math.ceil(recordtime * 1.5));
                    Toast.makeText(MainActivity.this, "录音", Toast.LENGTH_SHORT).show();
                    time = new TimeCount((int) Math.ceil(recordtime * 1500), 1000, position,holder.name.getText().length());//构造CountDownTimer对象
                    time.start();
                    // startRecord(position + "lianxi");
                    //start record
                    recordTask = new RecordTask(MainActivity.this);
                    recordTask.execute(position + "lianxi");
                    lianxis[position] = position + "lianxi";

                }
            });
            holder.playrecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaManager.release();
                    holder.playmusic.stopCartoom();
                    holder.record.stopCartoom();
                    int x = MediaManager.playSound(
                            SDPathString + lianxis[position] + ".wav",
                            new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {

                                }
                            });
                    holder.playrecord.startCartoom(x);//(int) Math.round(timeMap.get(position) * 1.5));

                    Toast.makeText(MainActivity.this, "播放录音", Toast.LENGTH_SHORT).show();
                    Log.i("---recordpath-->", SDPathString + lianxis[position] + ".wav");
//                    WriteToFileTask writeToFileTask = new WriteToFileTask();
//                    writeToFileTask.execute(lianxis[position]);
                }
            });

            return convertView;
        }

        public void changeImageVisable(View view, int position) {
            MediaManager.release();
            if (mLastView != null && mLastPosition != position) {
                ViewHolder holder = (ViewHolder) mLastView.getTag();
                switch (holder.hint.getVisibility()) {
                    case View.VISIBLE:
                        holder.hint.setVisibility(View.GONE);
                        mLastVisibility = View.GONE;
                        break;
                    default:
                        break;
                }
            }
            mLastPosition = position;
            mLastView = view;
            final ViewHolder holder = (ViewHolder) view.getTag();
            switch (holder.hint.getVisibility()) {
                case View.GONE:
                    holder.hint.setVisibility(View.VISIBLE);
                    mLastVisibility = View.VISIBLE;
                    holder.playmusic.setBackgroundResource(R.drawable.selector_btn_pause);
                    timeMap.put(
                            position,
                            MediaManager.playSound(
                                    mDataList.get(position),
                                    new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            holder.playmusic.setBackgroundResource(R.drawable.selector_btn_play);

                                        }
                                    }));
                    holder.playmusic.startCartoom(timeMap.get(position));

                    break;
                case View.VISIBLE:
                    holder.hint.setVisibility(View.GONE);
                    mLastVisibility = View.GONE;
                    break;
            }
        }
    }

    static class ViewHolder {
        TextView name;
        TextView score;
        LinearLayout item;
        CircleProgress playmusic;
        CircleProgress record;
        CircleProgress playrecord;
        View hint;

        public ViewHolder(View convertView) {
            name = (TextView) convertView.findViewById(R.id.learn_list_src);
            score = (TextView) convertView.findViewById(R.id.score_num);
            item = (LinearLayout) convertView.findViewById(R.id.learn_list_item);
            hint = convertView.findViewById(R.id.hint_image);
            record = (CircleProgress) convertView.findViewById(R.id.record);
            playrecord = (CircleProgress) convertView.findViewById(R.id.play_record);
            playmusic = (CircleProgress) convertView.findViewById(R.id.play_music);

        }

    }


    /**
     * 倒计时
     */
    class TimeCount extends CountDownTimer {
        int n;
        int wordlens;

        public TimeCount(long millisInFuture, long countDownInterval, int postion,int wordlen) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
            n = postion;
            wordlens=wordlen;
        }

        @Override
        public void onFinish() {
            //计时完毕时触发.t
            recordTask.stopRecord();

//            WriteToFileTask writeToFileTask = new WriteToFileTask();
//            writeToFileTask.execute(n + "lianxi");
            //你要的接口
            AnalysisTask analysisTask = new AnalysisTask(MainActivity.this);
            analysisTask.execute(n + "lianxi", n + "",wordlens+"");


        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
        }
    }

}
