package top.nicelee.ipscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import top.nicelee.ipscanner.util.PingUtil;
import top.nicelee.purehost.R;

public class MainActivity extends Activity {
    public static String path;
    HashMap<String, String> macNoteMap= new HashMap<String, String>();
    ListView listView; //邻居ListView
    TextView textView; //选中的邻居
    ProgressDialog dialog; //显示正在扫描
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化
        path = getFilesDir().getAbsolutePath();
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在扫描");
        dialog.setCanceledOnTouchOutside(false);
        listView = (ListView) findViewById(R.id.listview);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView)view;
                editNote(textView);
            }
        });
        //加载notes
        Config.load(macNoteMap);
        //刷新邻居信息
        refreshNeighbors();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ViewGroup vp = (ViewGroup) v;

        String content =  listView.getItemAtPosition(((AdapterView.AdapterContextMenuInfo) menuInfo).position).toString();//获取listview的item对象
        for(int i=0; i<vp.getChildCount(); i++){
            TextView txtView = (TextView)vp.getChildAt(i);
            if(txtView.getText().equals(content)){
                textView = txtView;
                break;
            }
        }
        getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_copy) {
            ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("",textView.getText());
            cmb.setPrimaryClip(clipData);
            //cmb.setText(content.trim()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
            Toast.makeText(this, "邻居信息已复制", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.menu_note) {
            editNote(textView);
        }else if (item.getItemId() == R.id.menu_ping) {
            //ip : matcher.group(1)
            final Matcher matcher = infoPattern.matcher(textView.getText());
            if(matcher.find()){
                PingUtil.ping( dialog, matcher.group(1));
            }
        }else if (item.getItemId() == R.id.menu_refresh) {
            refreshNeighbors();
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 刷新邻居信息
     */
    void refreshNeighbors(){

        //扫描局域网
        IpScanner ipScanner = new IpScanner();
        ipScanner.setOnScanListener(new IpScanner.OnScanListener() {
            @Override
            public void scan(Map<String, String> resultMap) {
                List<String> list = new ArrayList<String>();
                try{
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
                }catch (Exception e){
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        try{
            ipScanner.startScan();
            dialog.show();
        }catch (Exception e){
            updateView(new ArrayList<String>());
        }

    }

    /**
     * 根据 邻居信息 更新视图
     * @param list
     */
    void updateView(List<String> list){
        if(list.isEmpty()){
            list.add("当前没有检测到邻居");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, list);//新建并配置ArrayAapeter
        listView.setAdapter(adapter);
    }

    /**
     * 弹出输入框, 用于输入相应备注
     */
    Pattern infoPattern = Pattern.compile("^([^ ]+) ([^ ]+)(.*)$");
    void editNote(final TextView textView){
        //提取信息
        //ip : matcher.group(1)
        //mac : matcher.group(2)
        //备注 : matcher.group(3)
        final Matcher matcher = infoPattern.matcher(textView.getText());
        if(!matcher.find()){
            return;
        }

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
