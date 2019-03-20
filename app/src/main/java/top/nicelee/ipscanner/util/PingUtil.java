package top.nicelee.ipscanner.util;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class PingUtil extends Thread{
    Process process;
    InputStream inputStream;
    ProgressDialog dialog;
    private Handler mHandler = new Handler(Looper.getMainLooper());//获取主线程的Looper

    public PingUtil(ProgressDialog dialog, Process process, InputStream inputStream){
        this.dialog = dialog;
        this.process = process;
        this.inputStream = inputStream;
    }

    public void run () {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = null;
        try {
            while((line = bufferedReader.readLine()) !=null ) {
                if(line.contains("0 received") || line.contains("Network is unreachable")){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.setMessage("该设备暂时无法PING通");
                        }
                    });
                    sleep(1000);
                    dialog.dismiss();
                }else if(line.contains("1 received")){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.setMessage("该设备可以PING通");
                        }
                    });
                    sleep(1000);
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //dialog.dismiss();
        //process.destroy();
    }

    public static void ping(final ProgressDialog dialog, String ip){
        Process process = null;
        dialog.setMessage("正在ping");
        dialog.show();
        try {
            process = Runtime.getRuntime().exec("ping -c 1 " + ip);
            PingUtil errorStream = new PingUtil(dialog, process, process.getErrorStream());
            PingUtil outputStream = new PingUtil(dialog, process, process.getInputStream());
            errorStream.start();
            outputStream.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
