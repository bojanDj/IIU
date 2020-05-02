package com.example.iot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Element adsElement = new Element();
        adsElement.setTitle("Kontaktirajte nas");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.logo3)
                .setDescription("Punch the clock aplikacija za pracenje dolazaka i odlazaka zaposlenih sa posla, napravljena kao projektni rad na Fakultetu " +
                        "Organizacionih nauka.")
                .addItem(adsElement)
                .addEmail("bojan.djekic97@yahoo.com")
                .addGitHub("")
                .addFacebook("")
                .addItem(createCopyrigth())
                .create();
        setContentView(aboutPage);
    }

    private Element createCopyrigth() {
        Element copyRight = new Element();
        final String string = String.format("© Copyrigtht by Bojan, Ane and Luka");
        copyRight.setTitle(string);
        //copyRight.setIcon(R.drawable.copy2);
        copyRight.setGravity(Gravity.CENTER);
        copyRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(About.this,string, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRight;
    }

}
