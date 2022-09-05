package com.babiev.aoneapp.web_view;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.babiev.aoneapp.web_view.settings.SettingsAdapter;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * этот класс созданя для работы с NFC, при этом можно получить информацию об NFC карте с помощью модели NFC карты
 *
 * в случае если прилоежние сворачивается и разворачивается используются методы onResume и onPause,
 * которые вызываются в активити
 */
public class NfcAdapterCustom {

    private Context mContext;

    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;


    /**
     * пользователь не должен прикладывать карточка чаще чем 1 раз в 5 секунд
     * для авторизации
     *
     * этот метод борется с прикладыванием карт чаще чем 1 раз в 5 секунд
     */
    private boolean checkDoubleLoginNfc(){

        String appPath = SettingsAdapter.getAppPath();

        // каждый раз прикладывая карту - приложение запускается повторно и все переменные не сохраняются
        // поэтому создаем файлик и наличие файлика говорит о том, что был запуск
        File lastLoginDateTime = new File(appPath + "/lastLoginDateTime.txt");

        if(lastLoginDateTime.exists()){

            long diff = (new Date()).getTime() - lastLoginDateTime.lastModified();

            if(diff < 5000){
                System.out.println("diff sec меньше 5сек: " + diff);

                return true;

            } else {

                System.out.println("diff sec больше 5 сек: " + diff);

                lastLoginDateTime.delete();

                try {
                    lastLoginDateTime.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        } else {
            try {
                lastLoginDateTime.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return false;

    }

    /**
     * вызывается в onCreate активите в котором необходим доступ к NFC
     * @param context
     */
    public NfcAdapterCustom(Context context) {
        this.mContext = context;
    }

    public void onResume(){
        if (mNfcAdapter != null && mNfcAdapter.isEnabled())
            mNfcAdapter.enableForegroundDispatch((Activity)mContext, mNfcPendingIntent, null,
                    null);
        else
            mNfcAdapter = NfcAdapter.getDefaultAdapter(mContext);
    }

    public void onPause() {
        if (mNfcAdapter != null && mNfcAdapter.isEnabled())
            mNfcAdapter.disableForegroundDispatch((Activity) mContext);
        else
            mNfcAdapter = NfcAdapter.getDefaultAdapter(mContext);
    }

    //todo допиливаем НФС
    public boolean checkNFC(){
        mNfcAdapter = NfcAdapter.getDefaultAdapter(mContext);

        if (mNfcAdapter == null) {
            return false;
        } else {
            if (!mNfcAdapter.isEnabled()) {
                return false;
            } else {
                return true;
            }
        }

    }

    /**
     * модель объекта карты
     * сейчас храню только id карты,
     * на карте есть какие-то еще даные и эти данные со временем сюда попадут тоже
     */
    public class NfcCard{
        public String cardId;

        /**
         * хранит в себе Tag с нфц карты
         */
        private Tag mNfcTag;
    }
}
