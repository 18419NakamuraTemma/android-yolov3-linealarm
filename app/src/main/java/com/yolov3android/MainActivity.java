package com.yolov3android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.IconCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.*;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

import org.opencv.dnn.Dnn;
import org.opencv.utils.Converters;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    boolean startYolo = false;
    boolean firstTimeYolo = false;

    private TestOpenHelper helper;
    private SQLiteDatabase db;
    List<Integer> setting_nums = new ArrayList<>();
    List<Integer> alarms = new ArrayList<>();


    Net Yolo;
    List<String> vegnames = Arrays.asList("じゃが芋","紅はるか","シャドウクイーン","里芋","人参","大根","紅心大根","ミックスキャロット","玉ねぎ","マッシュルーム","フルーツかぶ","スティックブロッコリー","ネギ","葉ニンニク","セロリ","ほうれん草","ディル","わさび菜","菜花","ベビーリーフ","ラディッシュ","パクチー","赤からし菜","リーフレタス・赤","リーフレタス・緑","小松菜","べか菜","春菊","サラダ水菜","赤サラダほうれん草","レモン","ルッコラ","オークレタス・赤","オークレタス・緑","リンゴ","王林","菊芋","サラダセット","ゴルゴ","シイタケ","こかぶ","白菜","ニンニク","赤大根","レンコン");
    HashMap<String,Integer> sum_obj = new HashMap<String,Integer>();
    List<Integer> vegetableID = new ArrayList<>();
    List<Integer> time = new ArrayList<>();

    List<Integer> temp = new ArrayList<>();
    List<String> cocoNames = Arrays.asList("jagaimo","beniharuka","shadowqueen","satoimo","ninjin","daikon","koushindaikon","mixcarrot","tamanegi","mushroom","fruitkabu","stickbroccoli","negi","haninniku","serori","hourensou","dill","wasabina","nabana","babyleaf","radish","pakuchi","akakarashina","leaflettuce_red","leaflettuce_green","komatsuma","bekana","shungiku","saladmizuna","akasaladhourensou","lemon","rukkora","oaklettuce_red","oaklettuce_green","ringo","ourin","kikuimo","saladset","gorugo","shitakke","kokabu","hakusai","ninniku","akadaikon","renkon");

    public void YOLO(View Button){

        if (startYolo == false){
            startYolo = true;
            if (firstTimeYolo == false){

                firstTimeYolo = true;
            	String YoloCfg = "" ; //yolov3 cfg file path here
            	String YoloWeights = ""; //yolov3 weights file path here

                Yolo = Dnn.readNetFromDarknet(YoloCfg, YoloWeights);

            }
        }
        else{
            startYolo = false;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        helper = new TestOpenHelper(getApplicationContext());
        db = helper.getWritableDatabase();

        for(int tmp=0;tmp<cocoNames.size();tmp++) {

            String where_name = "name = ?";
            String name[] = {cocoNames.get(tmp)};

            Cursor cursor = db.query(
                    "testdb",
                    new String[] { "name", "quantity","alarm" },
                    where_name,
                    name,
                    null,
                    null,
                    null
            );

            cursor.moveToFirst();
            int setting_num = cursor.getInt(1);
            int alarm = cursor.getInt(2);
            setting_nums.add(setting_num);
            alarms.add(alarm);
            cursor.close();
        }



        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.CameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);

        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);

                switch(status){

                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };

        Button returnButton = findViewById(R.id.button_back);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        int py_temp = 0;
        if(py_temp == 0) {

            if (!Python.isStarted())
                Python.start(new AndroidPlatform(this));
            py_temp = 1;
        }
        final Python py = Python.getInstance();



        PyObject pyobj = py.getModule("line");
        Mat frame = inputFrame.rgba();
        if (startYolo == true) {

            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB);

            Mat imageBlob = Dnn.blobFromImage(frame, 0.00392, new Size(416, 416), new Scalar(0, 0, 0),/*swapRB*/false, /*crop*/false);

            Yolo.setInput(imageBlob);

            List<Mat> result = new ArrayList<Mat>(2);

            List<String> outBlobNames = new ArrayList<>();
            outBlobNames.add(0, "yolo_82");
            outBlobNames.add(1, "yolo_94");
            outBlobNames.add(2, "yolo_106");

            Yolo.forward(result, outBlobNames);
            float confThreshold = 0.3f;

            List<Integer> clsIds = new ArrayList<>();
            List<Float> confs = new ArrayList<>();
            List<Rect> rects = new ArrayList<>();

            for (int i = 0; i < result.size(); ++i) {
                Mat level = result.get(i);
                for (int j = 0; j < level.rows(); ++j) {
                    Mat row = level.row(j);
                    Mat scores = row.colRange(5, level.cols());

                    Core.MinMaxLocResult mm = Core.minMaxLoc(scores);

                    float confidence = (float) mm.maxVal;
                    Point classIdPoint = mm.maxLoc;

                    if (confidence > confThreshold) {
                        int centerX = (int) (row.get(0, 0)[0] * frame.cols());
                        int centerY = (int) (row.get(0, 1)[0] * frame.rows());
                        int width = (int) (row.get(0, 2)[0] * frame.cols());
                        int height = (int) (row.get(0, 3)[0] * frame.rows());
                        int left = centerX - width / 2;
                        int top = centerY - height / 2;

                        clsIds.add((int) classIdPoint.x);
                        confs.add((float) confidence);

                        rects.add(new Rect(left, top, width, height));
                    }
                }
            }
            int ArrayLength = confs.size();

            if (ArrayLength >= 1) {
                // Apply non-maximum suppression procedure.
                float nmsThresh = 0.2f;

                MatOfFloat confidences = new MatOfFloat(Converters.vector_float_to_Mat(confs));

                Rect[] boxesArray = rects.toArray(new Rect[0]);
                MatOfRect boxes = new MatOfRect(boxesArray);
                MatOfInt indices = new MatOfInt();

                Dnn.NMSBoxes(boxes, confidences, confThreshold, nmsThresh, indices);
                // Draw result boxes:
                int[] ind = indices.toArray();
                for (int i = 0; i < ind.length; ++i) {

                    int idx = ind[i];
                    Rect box = boxesArray[idx];

                    int idGuy = clsIds.get(idx);

                    float conf = confs.get(idx);


                    int intConf = (int) (conf * 100);

                    if (sum_obj.containsKey(cocoNames.get(idGuy)) == false) {
                        sum_obj.put(cocoNames.get(idGuy), 0);
                        vegetableID.add(idGuy);
                        time.add(0);
                        temp.add(0);


                    }

                    if (sum_obj.containsKey(cocoNames.get(idGuy)) == true) {
                        int sum = sum_obj.get(cocoNames.get(idGuy));
                        sum += 1;
                        sum_obj.put(cocoNames.get(idGuy), sum);
                    }

                    Imgproc.putText(frame, cocoNames.get(idGuy) + " " + intConf + "%", box.tl(), Core.FONT_HERSHEY_SIMPLEX, 2, new Scalar(255, 255, 0), 2);
                    Imgproc.rectangle(frame, box.tl(), box.br(), new Scalar(255, 0, 0), 2);
                }

            }
            int temp_num = 0;
            Set<String> keySet_sum = sum_obj.keySet();
            Iterator<String> iteratorForKey_sum = keySet_sum.iterator();
            Collection<Integer> values_sum = sum_obj.values();
            Iterator<Integer> iteratorForValues_sum = values_sum.iterator();

            for(temp_num=0;temp_num<vegetableID.size();temp_num++) {
                int key_num = vegetableID.get(temp_num);
                String key_eng = cocoNames.get(key_num);
                String key = vegnames.get(key_num);
                int setting_num = setting_nums.get(key_num);
                int setting_alarm = alarms.get(key_num);
                int values = sum_obj.get(key_eng);
                if(setting_alarm == 1) {
                    switch (temp.get(temp_num)) {
                        case 0:
                            if(values > setting_num){
                                time.set(temp_num,0);
                            }
                            if (values <= setting_num) {
                                if(time.get(temp_num) == 15){
                                    String message = key + "の在庫が少なくなっています";
                                    PyObject send_message_0 = pyobj.callAttr("send_message", message);
                                    time.set(temp_num,0);
                                    temp.set(temp_num, 1);
                                }
                                else {
                                    int temp_time = time.get(temp_num) + 1;
                                    time.set(temp_num, temp_time);
                                }
                            }
                            break;


                        case 1:

                            if (values > setting_num) {
                                if(time.get(temp_num) == 15) {
                                    temp.set(temp_num, 0);
                                }
                                else{
                                    int num = time.get(temp_num) + 1;
                                    time.set(temp_num,num);
                                }
                            }
                            else{
                                time.set(temp_num,0);
                            }

                            break;
                    }
                }
            }

            while(iteratorForKey_sum.hasNext()) {
                String key = iteratorForKey_sum.next();
                sum_obj.put(key,0);
            }
        }
        return frame;
    }
    @Override
    public void onCameraViewStarted(int width, int height) {
        if (startYolo == true){

            String YoloCfg = "" ; //yolov3 cfg file path here
            String YoloWeights = ""; //yolov3 weights file path here

            Yolo = Dnn.readNetFromDarknet(YoloCfg, YoloWeights);
        }
    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"There's a problem, yo!", Toast.LENGTH_SHORT).show();
        }

        else
        {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraBridgeViewBase!=null){

            cameraBridgeViewBase.disableView();
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }
}
