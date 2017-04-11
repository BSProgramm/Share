package ru.badsprogramm.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKPhotoArray;
import com.vk.sdk.api.model.VKWallPostResult;
import com.vk.sdk.api.photo.VKImageParameters;
import com.vk.sdk.api.photo.VKUploadImage;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CardView vkBtn;
    LinearLayout vkWall;
    AppCompatEditText messageView;
    AppCompatImageView imageLoad;
    Bitmap bitmap = null;
    Uri selectedImage;

    public static final String TOKEN_VK_OUTH = "tokenVk";
    static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vkBtn = (CardView) findViewById(R.id.vkBtn);
        vkWall= (LinearLayout) findViewById(R.id.vkWall);
        messageView = (AppCompatEditText) findViewById(R.id.messageView);
        imageLoad = (AppCompatImageView) findViewById(R.id.imageLoad);

        vkBtn.setOnClickListener(this);
        vkWall.setOnClickListener(this);
        imageLoad.setOnClickListener(this); //Загрузка картинки из галереи
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.vkBtn:
                Intent intent = new Intent(getApplicationContext(), VkOuth.class);
                startActivityForResult(intent, 0);
            break;
            case R.id.vkWall:
                if(bitmap == null )
                    makePost(null, messageView.getText().toString());
                else
                    makePostWithPhoto(bitmap, messageView.getText().toString());
            break;
            case R.id.imageLoad: //Загрузка картинки из галереи
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                break;
        }
    }

    //Постинг с фоткой
    void makePostWithPhoto (final Bitmap photo, final String message) {
        VKRequest request = VKApi.uploadWallPhotoRequest(new VKUploadImage(photo, VKImageParameters.jpgImage(1f)), getMyId(), 0);
        request.executeWithListener( new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                VKApiPhoto photoModel = ((VKPhotoArray) response.parsedModel).get(0);
                makePost(new VKAttachments(photoModel), message);
            }
            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(), "Трабл в загрузке картинки", Toast.LENGTH_SHORT)
                        .show();
            }
        });}

    int getMyId() {
        final VKAccessToken vkAccessToken = VKAccessToken.currentToken();
        return vkAccessToken != null ? Integer.parseInt(vkAccessToken.userId) : 0;
    }


    private void makePost(VKAttachments attachments, String message) {
        VKRequest post = VKApi.wall().post(VKParameters.from(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId, VKApiConst.ATTACHMENTS, attachments, VKApiConst.MESSAGE, message));
        post.setModelClass(VKWallPostResult.class);
        post.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                bitmap = null;
                imageLoad.setImageResource(R.drawable.image_load);
                messageView.setText("");
                Toast.makeText(getApplicationContext(),"Успешно",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(), "трабл в постинге", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageLoad.setImageBitmap(bitmap);
                }
        }
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    }
}
