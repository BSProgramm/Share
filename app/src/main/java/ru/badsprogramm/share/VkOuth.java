package ru.badsprogramm.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

public class VkOuth extends AppCompatActivity {

    String[] scope = new String[] {VKScope.WALL, VKScope.GROUPS, VKScope.PHOTOS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vkouth);

        VKSdk.login(this,scope);
    }

   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                res.saveTokenToSharedPreferences(getApplicationContext(), MainActivity.TOKEN_VK_OUTH);
                Toast.makeText(getApplicationContext(),"GOOD",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(),"SO BAD ;(",Toast.LENGTH_SHORT).show();
            }
        })){
            super.onActivityResult(requestCode, resultCode, data);
        }
       finish();
    }
}