package com.example.myapplication;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CONTACT_PICK = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 101;
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 102;
    private static final int SMS_PERMISSION_REQUEST_CODE = 300;
    private PreviewView previewView;
    private ExecutorService cameraExecutor;

    // Store the selected contact's phone number
    private String selectedContactNumber = null;
    private String pendingSMSPhoneNumber = null;
    private String pendingSMSMessage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.preview_view);
        cameraExecutor = Executors.newSingleThreadExecutor();
        Button btnSelectContact = findViewById(R.id.btnViewContact);
        Button btnCallContact = findViewById(R.id.btnCallContact);
        Button btnQR = findViewById(R.id.btnQR);

        btnQR.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                previewView.setVisibility(View.VISIBLE);  // Show the camera preview
                startCamera();
            }
        });


        // Handle "View Contact" button click
        btnSelectContact.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {
                openContactPicker();
            }
        });

        // Handle "Call Contact" button click
        btnCallContact.setOnClickListener(v -> {
            if (selectedContactNumber != null) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            PERMISSIONS_REQUEST_CALL_PHONE);
                } else {
                    callContact(selectedContactNumber);
                }
            } else {
                Toast.makeText(MainActivity.this, "No contact selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @androidx.camera.core.ExperimentalGetImage
    @OptIn(markerClass = ExperimentalGetImage.class)
    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // Setup preview
        androidx.camera.core.Preview preview = new androidx.camera.core.Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Setup image analysis for barcode scanning
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        // Barcode Scanner
        BarcodeScanner scanner = BarcodeScanning.getClient();

        imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
            @androidx.camera.core.ExperimentalGetImage
            @Nullable
            android.media.Image mediaImage = imageProxy.getImage();
            if (mediaImage != null) {
                InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                scanner.process(image)
                        .addOnSuccessListener(barcodes -> {
                            if (!barcodes.isEmpty()) {
                                Barcode barcode = barcodes.get(0);
                                handleBarcode(barcode);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(MainActivity.this, "Failed to scan QR code", Toast.LENGTH_SHORT).show();
                        })
                        .addOnCompleteListener(task -> imageProxy.close());
            } else {
                imageProxy.close();
            }
        });

        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview);
    }

    private void handleBarcode(Barcode barcode) {
        switch (barcode.getValueType()) {
            case Barcode.TYPE_URL:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(barcode.getUrl().getUrl()));
                startActivity(browserIntent);
                break;
            case Barcode.TYPE_EMAIL:
                // Extract email details
                String email = barcode.getEmail().getAddress();
                String subject = barcode.getEmail().getSubject();
                String body = barcode.getEmail().getBody();

                // Intent to open email app
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject != null ? subject : "QR Code Email");
                emailIntent.putExtra(Intent.EXTRA_TEXT, body != null ? body : "");

                    startActivity(emailIntent);

                break;

            case Barcode.TYPE_PHONE:
                // Use ACTION_VIEW to open SMS app to send a message
                Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + barcode.getPhone().getNumber()));
                smsIntent.putExtra("sms_body", "Hello from QR code!");
                startActivity(smsIntent);
                break;
            case Barcode.TYPE_SMS:
                // Handle SMS QR Code
                pendingSMSPhoneNumber = barcode.getSms().getPhoneNumber();
                pendingSMSMessage = barcode.getSms().getMessage();

                // Check and request SMS permission before proceeding
                requestSMSPermission();
                break;
            case Barcode.TYPE_TEXT:
                Toast.makeText(this, "Text: " + barcode.getDisplayValue(), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Unsupported QR code format", Toast.LENGTH_SHORT).show();
                break;
        }
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    // Check for SMS permission and request it if necessary
    private void requestSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, send SMS
            sendSMS(pendingSMSPhoneNumber, pendingSMSMessage);
        }
    }

    private void sendSMS(String phoneNumber, String message) {
        if (phoneNumber != null && message != null) {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setData(Uri.parse("smsto:" + phoneNumber)); // Use "smsto:" instead of "sms:"
            smsIntent.putExtra("sms_body", message);

            // Check if there is an app to handle the SMS intent
            if (smsIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(smsIntent);
            } else {
                Toast.makeText(this, "No SMS app found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid QR code for SMS", Toast.LENGTH_SHORT).show();
        }
    }


    private void openContactPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_CONTACT_PICK);
    }

    private void callContact(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CONTACT_PICK && resultCode == RESULT_OK) {
            if (data != null) {
                Uri contactUri = data.getData();
                String[] projection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};

                // Fetch the selected contact
                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                    int hasPhoneNumberIndex = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                    String contactId = cursor.getString(idIndex);
                    int hasPhoneNumber = cursor.getInt(hasPhoneNumberIndex);

                    if (hasPhoneNumber > 0) {
                        Cursor phonesCursor = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{contactId},
                                null);

                        if (phonesCursor != null && phonesCursor.moveToFirst()) {
                            int phoneNumberIndex = phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            selectedContactNumber = phonesCursor.getString(phoneNumberIndex);
                            phonesCursor.close();

                            Toast.makeText(this, "Selected contact: " + selectedContactNumber, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Contact has no phone number", Toast.LENGTH_SHORT).show();
                    }
                    cursor.close();
                }
            }
        }
    }

    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openContactPicker();
            } else {
                Toast.makeText(this, "Permission required to access contacts", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (selectedContactNumber != null) {
                    callContact(selectedContactNumber);
                }
            } else {
                Toast.makeText(this, "Permission required to make calls", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send SMS
                if (pendingSMSPhoneNumber != null && pendingSMSMessage != null) {
                    sendSMS(pendingSMSPhoneNumber, pendingSMSMessage);
                }
            } else {
                Toast.makeText(this, "SMS permission is required to send messages", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
