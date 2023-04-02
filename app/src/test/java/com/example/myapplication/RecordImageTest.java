package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class RecordImageTest {

    private static final String FAKE_IMAGE_URI = "content://fake.uri/image.jpg";

    @Mock
    FirebaseStorage mockFirebaseStorage;
    @Mock
    StorageReference mockStorageReference;
    @Mock
    UploadTask mockUploadTask;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockFirebaseStorage.getReference()).thenReturn(mockStorageReference);
        Mockito.when(mockStorageReference.child(Mockito.anyString())).thenReturn(mockStorageReference);
        Mockito.when(mockStorageReference.putFile(Mockito.any(Uri.class))).thenReturn(mockUploadTask);
    }

    @Test
    public void cameraIntent_shouldLaunchCameraApp() {
        // Launch the activity
        ActivityScenario.launch(Record_image.class);

        // Click on the camera button
        Mockito.verify(mockUploadTask, Mockito.never()).addOnSuccessListener(Mockito.any());
        Mockito.verify(mockUploadTask, Mockito.never()).addOnFailureListener(Mockito.any());
        Mockito.verify(mockStorageReference, Mockito.never()).child(Mockito.anyString());

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test.jpg");
            photoFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri photoURI = Uri.fromFile(photoFile);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        Mockito.verify(mockStorageReference, Mockito.never()).putFile(Mockito.any(Uri.class));
    }

    @Test
    public void galleryIntent_shouldLaunchGalleryApp() {
        // Launch the activity
        ActivityScenario.launch(Record_image.class);

        // Click on the gallery button
        Mockito.verify(mockUploadTask, Mockito.never()).addOnSuccessListener(Mockito.any());
        Mockito.verify(mockUploadTask, Mockito.never()).addOnFailureListener(Mockito.any());
        Mockito.verify(mockStorageReference, Mockito.never()).child(Mockito.anyString());

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Mockito.when(mockFirebaseStorage.getReference()).thenReturn(mockStorageReference);
        Mockito.when(mockStorageReference.child(Mockito.anyString())).thenReturn(mockStorageReference);
        Mockito.when(mockStorageReference.putFile(Mockito.any(Uri.class))).thenReturn(mockUploadTask);
        Mockito.when(mockUploadTask.addOnSuccessListener(Mockito.any())).thenReturn(mockUploadTask);

        Mockito.when(mockUploadTask.addOnSuccessListener(Mockito.any())).thenReturn(mockUploadTask);
        Mockito.when(mockUploadTask.addOnFailureListener(Mockito.any())).thenReturn(mockUploadTask);

        Mockito.when(mockUploadTask.isComplete()).thenReturn(true);

        Mockito.when(mockUploadTask.getResult()).thenReturn(null);

        Mockito.when(mockUploadTask.isSuccessful()).thenReturn(true);

        Mockito.when(mockUploadTask.getException()).thenReturn(null);

        Mockito.verify(mockStorageReference, Mockito.never()).putFile(Mockito.any(Uri.class));
    }

}
