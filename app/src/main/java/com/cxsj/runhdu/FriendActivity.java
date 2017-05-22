package com.cxsj.runhdu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class FriendActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        setToolbar(R.id.toolbar_friend, true);
        //TODO:查找好友列表，并显示。
        //设置recyclerView的点击事件，启动动画，跳转到新activity
        //封装TodayRecyclerView的设置列表方法
        //根据服务（接口）的返回值，提示操作。
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friend_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.friend_menu2:
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
