package com.krlsedu.cths.mediacontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CarlosEduardo on 14/01/2016.
 */
public class DbManager extends SQLiteOpenHelper {
    private static final String DBNAME = "mediaControl";
    private static final int VERSAODB = 5;
    public DbManager(Context context) {
        super(context, DBNAME, null, VERSAODB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table config(" +
                "id integer not null primary key autoincrement," +
                "usar_na_tela_bloquei integer not null default 0)";
        db.execSQL(sql);
        Config config = new Config();
        config.setNaTelaBloqueio(1);
        insertConfig(db,config);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion==1 && newVersion == 2){
//            String sql ="drop table pontuacao";
//            db.execSQL(sql);
//            sql = "create table pontuacao(" +
//                    "id integer not null primary key autoincrement," +
//                    "id_user integer not null," +
//                    "total_pontos integer not null," +
//                    "nivel_fim integer not null," +
//                    "fase_fim integer not null)";
//            db.execSQL(sql);
        }
    }

    public void insertConfig(SQLiteDatabase db,Config config){
        ContentValues configs = new ContentValues();
        configs.put("usar_na_tela_bloqueio",config.getNaTelaBloqueio());
        db.insert("pontuacao",null,configs);
    }

    public List<Config> getConfiguracoes(int nResult){
        SQLiteDatabase db = getReadableDatabase();
        List<Config> ranking = new ArrayList<>();
        String sql = "select * from config order by id desc limit "+nResult;
        Cursor cr = db.rawQuery(sql,null);
        if(cr.moveToFirst()){
            do {
                Config pts = new Config();
                pts.setNaTelaBloqueio(cr.getInt(1));
                ranking.add(pts);
            }while (
                    cr.moveToNext()
                    );
        }
        cr.close();
        return ranking;
    }
}
