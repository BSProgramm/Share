package ru.badsprogramm.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKWallPostResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatButton vkBtn, vkWall;
    AppCompatEditText messageView;
    public static final String TOKEN_VK_OUTH = "tokenVk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vkBtn = (AppCompatButton) findViewById(R.id.vkBtn);
        vkWall= (AppCompatButton) findViewById(R.id.vkWall);
        messageView = (AppCompatEditText) findViewById(R.id.messageView);

        vkBtn.setOnClickListener(this);
        vkWall.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.vkBtn:
                Intent intent = new Intent(getApplicationContext(), VkOuth.class);
                startActivityForResult(intent, 0);
            break;
            case R.id.vkWall:
                makePost(null, messageView.getText().toString());
            break;
        }
    }

    private void makePost(VKAttachments attachments, String message) {
        VKRequest post = VKApi.wall().post(VKParameters.from(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId, VKApiConst.ATTACHMENTS, attachments, VKApiConst.MESSAGE, message));
        post.setModelClass(VKWallPostResult.class);
        post.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(), error.errorReason, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
