package com.example.tapiwa.collegebuddy.miscellaneous;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.Main.ChooseClass;
import com.example.tapiwa.collegebuddy.Main.MainFrontPage;
import com.example.tapiwa.collegebuddy.authentication.LoginActivity;
import com.example.tapiwa.collegebuddy.authentication.WelcomeActivity;
import com.example.tapiwa.collegebuddy.classContents.DOCS.DOC;
import com.example.tapiwa.collegebuddy.classContents.DOCS.DocsFragment;
import com.example.tapiwa.collegebuddy.classContents.classContentsMain.ClassContentsMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Header;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.dmoral.toasty.Toasty;

/**
 * Created by tapiwa on 8/3/17.
 */

public class GenericServices {

    public static Context context;
    public static Boolean foregroundStatus = false;
    public static String thisUserName = "Your friend ";
    public static String thisUid;

    public GenericServices(Context cxt) {
        this.context = cxt;

    }


    public static boolean isConnectingToInternet() {


        int unicode = 0x1F64A;
        String emoji = new String(Character.toChars(unicode));

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return true;
        } else {

            Toast toast = Toast.makeText(context, "No internet Connection..." + emoji, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }
    }


    public static String timeStamp() {

        DateTime dt = new DateTime();
        String time  = dt.toLocalTime().toString().substring(0,5);

        return time;
    }

    public static String date() {
        DateTime dt = new DateTime();
        String year = dt.year().getAsShortText().toString();
        String month = dt.monthOfYear().getAsShortText().toString();
        String day = dt.dayOfMonth().getAsShortText().toString();

        return day + " " + month + ", " + year;
    }

    public static String dayOfTheWeek() {

        DateTime dt = new DateTime();
        String dayOfWeek = dt.dayOfWeek().getAsShortText().toString();

        return dayOfWeek;
    }


    public boolean isInForeGround() {

        return this.foregroundStatus;

    }

    public static void activateCodeMode(TextView title, TextView noteContents, CardView cardview, Context context) {

            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/sourcecodeproregular.ttf");
            noteContents.setTypeface(typeface);
            noteContents.setTextSize(15);
            noteContents.setBackgroundColor(Color.DKGRAY);
            title.setBackgroundColor(Color.DKGRAY);
            noteContents.setTextColor(Color.rgb(0,206,0));
            cardview.setCardBackgroundColor(Color.DKGRAY);

        String [] keywordsOrange = {"String","string","Boolean", "boolean", "if", "for", "int", "new", "public", "private", ";",
                "return", "static", "while", "else", "catch", "try", "null", "case", "switch", ",", "Char", "char", "Integer",
                "Character", "HashMap", "HashSet", "Set", "throw", "long", "Double"};

        for(String y:keywordsOrange)
        {
            fontcolor(y,Color.rgb(255,165,0), noteContents);
        }

        String [] keywordsPurple = {"true", "false", "=", "+" , "<", "<", "&", "%", "[", "]"};
        for(String y:keywordsPurple)
        {
            fontcolor(y,Color.rgb(214,82,148), noteContents);
        }

            Toasty.info(context, "Code mode activated", Toast.LENGTH_SHORT).show();
    }


    public static void fontcolor(String text,int color, TextView noteContents) {

        Spannable raw=new SpannableString(noteContents.getText());

        int index= TextUtils.indexOf(raw, text);
        while (index >= 0) {
            raw.setSpan(new ForegroundColorSpan(color), index, index
                    + text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            index=TextUtils.indexOf(raw, text, index + text.length());
        }
        noteContents.setText(raw);
    }

    public static void deactivateCodeMode(TextView title, TextView noteContents, CardView cardView, Context context) {

        Typeface typeface = Typeface.DEFAULT;
        noteContents.setTypeface(typeface);

        cardView.setCardBackgroundColor(Color.WHITE);
        title.setTextColor(Color.BLACK);

        title.setBackgroundColor(Color.WHITE);
        noteContents.setBackgroundColor(Color.WHITE);

        noteContents.setTextColor(Color.BLACK);


        Toasty.info(context, "Code mode deactivated", Toast.LENGTH_SHORT).show();
    }




    public static String getCurrentUserName() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid().toString();

        //getCurrentUserName
        ref.child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                thisUserName = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return thisUserName;
    }

    public static void sendInvitation(Activity context) {
        Intent Text = new Intent(Intent.ACTION_SEND);
        Text.setType("text/email");
        Text.putExtra(Intent.EXTRA_TEXT, "Hi, I joined Collabo and would like to invite you to be my contact. " +
                "Click on the link below " +
                "to download it on playstore" + "\n\n" + "https://play.google.com/store/apps/details?id=com.myprojects." +
                "tapiwa.collegebuddy&hl=en");
        context.startActivity(Intent.createChooser(Text, "Select contact"));
    }

    public static void hideKeyboard(EditText editText, Context context) {
        //Hide the keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static String getCurrentUid() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users");
        FirebaseAuth auth = FirebaseAuth.getInstance();

        return  auth.getCurrentUser().getUid().toString();

    }

    public static void createPDF (String title, String content, Activity context) throws IOException, DocumentException {

        //Code snippet taken from
//https://www.codeproject.com/Articles/986574/Android-iText-Pdf-Example

        //Create time stamp
        Date date = new Date() ;

        File myFile = createNewPDFFile(context, title);

        if(!myFile.exists()) {
            myFile.createNewFile();
        }

        OutputStream output = new FileOutputStream(myFile);

        //Step 1
        Document document = new Document();

        //Step 2
        PdfWriter.getInstance(document, output);

        //Step 3
        document.open();

        //Step 4 Add content

        Font f = new Font(Font.FontFamily.HELVETICA,30.0f,Font.BOLD, BaseColor.BLUE);
        Font f2 = new Font(Font.FontFamily.HELVETICA,20.0f,Font.BOLD, BaseColor.BLACK);

        Paragraph collaboHeading = new Paragraph("Collabo Notes", f);
        collaboHeading.setAlignment(Element.ALIGN_CENTER);
        collaboHeading.setSpacingAfter(5f);

        Paragraph heading = new Paragraph(title, f2);
        heading.setAlignment(Element.ALIGN_LEFT);
        heading.setSpacingAfter(3f);

        document.add(collaboHeading);
        document.add(heading);
        document.add(new Paragraph(content));

        //Step 5: Close the document
        document.close();

         AppUsageAnalytics.incrementPageVisitCount("Pdf_docs_created");
        Intent email = new Intent(Intent.ACTION_SEND);
        Uri contentUri = FileProvider.getUriForFile(context, "com.example.android.fileprovider", myFile);
        email.putExtra(Intent.EXTRA_STREAM, contentUri);
        email.setType("message/rfc822");
        context.startActivity(email);

    }

    public static void saveNotePdf (final String title, String content, final Activity context, final String projectKey) throws IOException, DocumentException {

        //Code snippet taken from
//https://www.codeproject.com/Articles/986574/Android-iText-Pdf-Example

        //Create time stamp
        File myFile = createNewPDFFile(context, title);

        if(!myFile.exists()) {
            myFile.createNewFile();
        }

        OutputStream output = new FileOutputStream(myFile);

        //Step 1
        Document document = new Document();

        //Step 2
        PdfWriter.getInstance(document, output);

        //Step 3
        document.open();

        //Step 4 Add content

        Font f = new Font(Font.FontFamily.HELVETICA,30.0f,Font.BOLD, BaseColor.BLUE);
        Font f2 = new Font(Font.FontFamily.HELVETICA,20.0f,Font.BOLD, BaseColor.BLACK);

        Paragraph collaboHeading = new Paragraph("Collabo Notes", f);
        collaboHeading.setAlignment(Element.ALIGN_CENTER);
        collaboHeading.setSpacingAfter(5f);

        Paragraph heading = new Paragraph(title, f2);
        heading.setAlignment(Element.ALIGN_LEFT);
        heading.setSpacingAfter(3f);

        document.add(collaboHeading);
        document.add(heading);
        document.add(new Paragraph(content));

        //Step 5: Close the document
        document.close();

        // Creating image from a URL


        final Uri contentUri = FileProvider.getUriForFile(context, "com.example.android.fileprovider", myFile);

        StorageReference noteStorage = FirebaseStorage
                            .getInstance()
                            .getReference(DocsFragment.DOCS_STORAGE_REF).child(MainFrontPage.user)
                            .child(ClassContentsMainActivity.projectKey)
                            .child(contentUri.getLastPathSegment());


                    final DatabaseReference noteDatabaseRef = FirebaseDatabase
                            .getInstance()
                            .getReference(DocsFragment.DOCS_DATABASE_REF);



                    noteStorage.putFile(contentUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            String uploadKey = noteDatabaseRef.push().getKey();

                            @SuppressWarnings("VisibleForTests") String downloadUri = task.getResult()
                                    .getDownloadUrl().toString();

                            DOC doc1 = new DOC(title, GenericServices.date(), "pdf", downloadUri, uploadKey);

                            noteDatabaseRef.child(MainFrontPage.user).child(projectKey).child(uploadKey).setValue(doc1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toasty.success(context, "Saved to Docs", Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    });

                    AppUsageAnalytics.incrementPageVisitCount("Note_PDF_Saved");

    }


    public static File createNewPDFFile(Context context, String name) throws IOException {
        // Create an image file name
        String imageFileName = name + "_PDF" + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File pdfFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".pdf",         /* suffix */
                storageDir      /* directory */
        );

        return pdfFile;
    }

    public static File createNewPDFFileOpening(Context context, String name) throws IOException {
        // Create an image file name
        String imageFileName = name + "_PDF" + "_";
        File storageDir = Environment.getExternalStorageDirectory();
        File pdfFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".pdf",         /* suffix */
                storageDir      /* directory */
        );

        return pdfFile;
    }

    public static File createNewPDFFile2(Context context, String name) throws IOException {
        // Create an image file name
        String imageFileName = name + "_PDF" + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File pdfFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return pdfFile;
    }

    public static void sendImagePdf(final Context context, String url) throws IOException, DocumentException {

        final File myFile = createNewPDFFile(context, "Collabo Image");

        if(!myFile.exists()) {
            myFile.createNewFile();
        }

        OutputStream output = new FileOutputStream(myFile);

        //Step 1
        final Document doc = new Document();

        //Step 2
        PdfWriter.getInstance(doc, output);

        //Step 3
        doc.open();


            doc.open();

            // Creating image from a URL
            FirebaseStorage storage = FirebaseStorage.getInstance();
//
            StorageReference httpsReference = storage.getReferenceFromUrl(url);

            httpsReference.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    Image image = null;
                    try {
                        image = Image.getInstance(myFile.getAbsolutePath());
                    } catch (BadElementException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());

                    try {
                        doc.add(image);
                        doc.close();


                        Intent email = new Intent(Intent.ACTION_SEND);
                        Uri contentUri = FileProvider.getUriForFile(context, "com.example.android.fileprovider", myFile);
                        email.putExtra(Intent.EXTRA_STREAM, contentUri);
                        email.setType("message/rfc822");
                        context.startActivity(email);

                        AppUsageAnalytics.incrementPageVisitCount("Image_Pdfs_created");

                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toasty.error(context, "Failed to produce pdf, please try again", Toast.LENGTH_SHORT).show();
                }
            });



    }


    public static void saveImagePdf(final Context context, String url, final String imageName, final String projectKey) throws IOException, DocumentException {

        final File myFile = createNewPDFFile(context, imageName);

        if(!myFile.exists()) {
            myFile.createNewFile();
        }

        OutputStream output = new FileOutputStream(myFile);

        //Step 1
        final Document doc = new Document();

        //Step 2
        PdfWriter.getInstance(doc, output);

        //Step 3
        doc.open();


        doc.open();

        // Creating image from a URL
        FirebaseStorage storage = FirebaseStorage.getInstance();
//
        StorageReference httpsReference = storage.getReferenceFromUrl(url);

        httpsReference.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                Image image = null;
                try {
                    image = Image.getInstance(myFile.getAbsolutePath());
                } catch (BadElementException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());

                try {
                    doc.add(image);
                    doc.close();

                    final Uri contentUri = FileProvider.getUriForFile(context, "com.example.android.fileprovider", myFile);

                   StorageReference pdfStorage = FirebaseStorage
                           .getInstance()
                           .getReference(DocsFragment.DOCS_STORAGE_REF);

                    final DatabaseReference pdfDatabaseRef = FirebaseDatabase
                            .getInstance()
                            .getReference(DocsFragment.DOCS_DATABASE_REF);

                    pdfStorage.child(MainFrontPage.user).putFile(contentUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            String uploadKey = pdfDatabaseRef.push().getKey();

                            @SuppressWarnings("VisibleForTests") String downloadUri = task.getResult()
                                    .getDownloadUrl().toString();

                            DOC doc1 = new DOC(imageName, GenericServices.date(), "pdf", downloadUri, uploadKey);

                            pdfDatabaseRef.child(MainFrontPage.user).child(projectKey).child(uploadKey).setValue(doc1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toasty.success(context, "Saved to Docs", Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    });


                    AppUsageAnalytics.incrementPageVisitCount("PDF_Saved");

                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toasty.error(context, "Failed to save PDF, please try again", Toast.LENGTH_SHORT).show();

            }
        });



    }


    public static void uploadFiletoFireBase(Uri uri, final String projectKey, final Context context) {

        Cursor returnCursor =
                context.getContentResolver().query(uri, null, null, null, null);
    /*
     * Get the column indexes of the data in the Cursor,
     * move to the first row in the Cursor, get the data,
     * and display it.
     */int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();

       final String filename = returnCursor.getString(nameIndex);

        StringBuilder builder = new StringBuilder();

        int i = 0;
        while(filename.charAt(i) != '.') {
            builder.append(filename.charAt(i));
            i++;
        }

        final  String trimmedFileName = builder.toString();

                    StorageReference pdfStorage = FirebaseStorage
                            .getInstance()
                            .getReference(DocsFragment.DOCS_STORAGE_REF);

                    final DatabaseReference pdfDatabaseRef = FirebaseDatabase
                            .getInstance()
                            .getReference(DocsFragment.DOCS_DATABASE_REF);

                    pdfStorage.child(MainFrontPage.user).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            String uploadKey = pdfDatabaseRef.push().getKey();

                            @SuppressWarnings("VisibleForTests") String downloadUri = task.getResult()
                                    .getDownloadUrl().toString();

                            DOC doc1 = new DOC(trimmedFileName, GenericServices.date(), "pdf", downloadUri, uploadKey);

                            pdfDatabaseRef.child(MainFrontPage.user).child(projectKey).child(uploadKey).setValue(doc1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toasty.success(context, "Saved to Docs", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
    }

}
