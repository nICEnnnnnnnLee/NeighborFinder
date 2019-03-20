package top.nicelee.ipscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.nicelee.ipscanner.util.Config;
import top.nicelee.ipscanner.util.IpScanner;
import top.nicelee.purehost.R;

public class MainActivity extends Activity {
    private TextView mHello;

    HashMap<String, String> macNoteMap= new HashMap<String, String>();
    public static String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        path = getFilesDir().getAbsolutePath();
        //加载notes
        Config.load(macNoteMap);

        //显示正在扫描
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("正在扫描");
        dialog.setCanceledOnTouchOutside(false);

        //扫描局域网
        IpScanner ipScanner = new IpScanner();
        ipScanner.setOnScanListener(new IpScanner.OnScanListener() {
            @Override
            public void scan(Map<String, String> resultMap) {
                dialog.dismiss();
                List<String> list = new ArrayList<String>();
                for(Map.Entry entry: resultMap.entrySet()){
                    StringBuilder sb = new StringBuilder(entry.getValue().toString());
                    sb.append(" ").append(entry.getKey().toString());
                    String note = macNoteMap.get(entry.getKey().toString());
                    if(note != null){
                        sb.append(" ").append(note);
                    }
                    list.add(sb.toString());
                }
                updateView(list);
            }
        });
        ipScanner.startScan();
        dialog.show();
    }


    void updateView(List<String> list){
        ListView listView = (ListView) findViewById(R.id.listview);//在视图中找到ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, list);//新建并配置ArrayAapeter
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView)view;
                //Toast.makeText(MainActivity.this,textView.getText() + "你点击了"+i+"按钮",Toast.LENGTH_SHORT).show();
                input(textView);
            }
        });
    }
    String result;
    Pattern infoPattern = Pattern.compile("^([^ ]+) ([^ ]+)(.*)$");
    void input(final TextView textView){
        //提取信息
        //ip : matcher.group(1)
        //mac : matcher.group(2)
        //备注 : matcher.group(3)
        final Matcher matcher = infoPattern.matcher(textView.getText());
        matcher.find();

        //初始化弹窗
        final EditText inputServer = new EditText(this);
        inputServer.setText(matcher.group(3).trim());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入备注")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(inputServer)
                .setNegativeButton("Cancel", null);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //显示备注
                textView.setText(matcher.group(1) + " " + matcher.group(2) + " ");
                textView.append(inputServer.getText().toString());
                //保存备注
                macNoteMap.put(matcher.group(2), inputServer.getText().toString());
                Config.save(macNoteMap);
            }
        });
        builder.show();
    }

}
