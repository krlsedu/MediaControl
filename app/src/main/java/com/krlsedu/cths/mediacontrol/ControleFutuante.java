package com.krlsedu.cths.mediacontrol;

import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * Created by CarlosEduardo on 20/06/2016.
 */
public class ControleFutuante extends Service {

    private WindowManager windowManager;
    private LayoutInflater inflater;
    private Boolean visible = false;
    private Boolean pressed = false;
    private Boolean moved = false;
    private Boolean configAtiva = false;



    View controlerFecha;
    View controlerConfig;
    View controler;

    FloatingActionButton fabShow;
    //FloatingActionButton fabHide;
    FloatingActionButton fabVolta;
    FloatingActionButton fabPlayPause;
    FloatingActionButton fabAvanca;

    FloatingActionButton fabImageButtonFecha;
    FloatingActionButton fabConfig;

    Animation hideFabShow;
    Animation showFabShow;
    Animation showFabVolta;
    Animation showFabPlayPause;
    Animation showFabAvanca;
    Animation hideFabVolta;
    Animation hideFabPlayPause;
    Animation hideFabAvanca;
    Animation clickFab;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        inflater = LayoutInflater.from(this);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        loadComponents();

        DbManager db = new DbManager(this);
        List<Config> configList=db.getConfiguracoes(1);
        Config config;
        if (!configList.isEmpty()){
            config = configList.get(0);
        }else{
            config = new Config();
            config.setNaTelaBloqueio(0);
        }
        final WindowManager.LayoutParams paramsControler = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.TRANSLUCENT);
        paramsControler.gravity = Gravity.START|Gravity.TOP;



        //final FloatingActionButton fabHide = (FloatingActionButton) controler.findViewById(R.id.hide);
        fabImageButtonFecha =  controlerFecha.findViewById(R.id.imageButtonFecha);
        fabConfig = controlerConfig.findViewById(R.id.configControler);

        fabPlayPause.hide();
        fabAvanca.hide();

        final AudioManager manager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        if(manager.isMusicActive())
        {
            fabPlayPause.setImageResource(R.drawable.pause);
        }else{
            fabPlayPause.setImageResource(R.drawable.play);
        }

        final Handler handlerMus = new Handler();
        final Runnable delay = new Runnable() {
            public void run() {
                Log.i(String.valueOf(manager.isMusicActive()),String.valueOf(manager.isMusicActive()));
                if(manager.isMusicActive())
                {
                    fabPlayPause.setImageResource(R.drawable.pause);
                }else{
                    fabPlayPause.setImageResource(R.drawable.play);
                }
            }
        };


        fabAvanca.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fabAvanca.startAnimation(clickFab);
                        long eventtime = SystemClock.uptimeMillis();
                        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                        KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT, 0);
                        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                        sendOrderedBroadcast(downIntent, null);
                        handlerMus.postDelayed(delay, 500);
                        return true;
                }
                return false;
            }

        });



        fabVolta.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fabVolta.startAnimation(clickFab);
                        long eventtime = SystemClock.uptimeMillis();
                        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                        KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
                        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                        sendOrderedBroadcast(downIntent, null);
                        handlerMus.postDelayed(delay, 500);
                        return true;
                }
                return false;
            }
        });

        fabPlayPause.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fabPlayPause.startAnimation(clickFab);
                        long eventtime = SystemClock.uptimeMillis();
                        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                        KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                        sendOrderedBroadcast(downIntent, null);
                        handlerMus.postDelayed(delay, 500);
                        Log.i(String.valueOf(manager.isMusicActive()),String.valueOf(manager.isMusicActive()));
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                }
                return false;
            }
        });

        final Handler handlerLongPress = new Handler();
        final Runnable delayLongPress = new Runnable() {
            public void run() {
                pressed =true;
            }
        };

        fabShow.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = paramsControler.x;
                        initialY = paramsControler.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        moved = false;
                        pressed = false;
                        handlerLongPress.postDelayed(delayLongPress, 100);
                        return true;

                    case MotionEvent.ACTION_UP:
                        if(!moved) {
                            if (visible) {
                                escondeFABs();

                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) fabShow.getLayoutParams();
                                layoutParams.topMargin = 5;
                                fabShow.setLayoutParams(layoutParams);
                                fabShow.startAnimation(showFabShow);
                            } else {

                                controler.findViewById(R.id.playPause).setVisibility(View.INVISIBLE);
                                controler.findViewById(R.id.volta).setVisibility(View.INVISIBLE);
                                controler.findViewById(R.id.avanca).setVisibility(View.INVISIBLE);
                                mostraFABs();

                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) fabShow.getLayoutParams();
                                layoutParams.topMargin += fabShow.getHeight() -15;
                                fabShow.setLayoutParams(layoutParams);

                                fabShow.startAnimation(hideFabShow);
                            }
                            visible = !visible;
                        }else{

                            int xy[] = new int[2];
                            controler.findViewById(R.id.show).getLocationOnScreen(xy);
                            int x = xy[0] + (controler.findViewById(R.id.show).getHeight()/2);
                            int y = xy[1] + (controler.findViewById(R.id.show).getHeight()/2);

                            controlerFecha.findViewById(R.id.imageButtonFecha).getLocationOnScreen(xy);
                            int l = xy[0];
                            int r = xy[0] + controlerFecha.findViewById(R.id.imageButtonFecha).getHeight();
                            int t = xy[1];
                            int b = xy[1]+ controlerFecha.findViewById(R.id.imageButtonFecha).getHeight();

                            Log.i("X "+x,"X "+x);
                            Log.i("Y "+y,"Y "+y);
                            Log.i("l "+l,"l "+l);
                            Log.i("r "+r,"r "+r);
                            Log.i("t "+t,"t "+t);
                            Log.i("b "+b,"b "+b);

                            if (l<=x &&
                                    r>=x &&
                                    t<=y &&
                                    b>=y) {
                                removeChatHead(controler);
                            }

                            controlerConfig.findViewById(R.id.configControler).getLocationOnScreen(xy);
                            l = xy[0];
                            r = xy[0] + controlerConfig.findViewById(R.id.configControler).getHeight();

                            t = xy[1];
                            b = xy[1]+ controlerConfig.findViewById(R.id.configControler).getHeight();
                            Log.i("X "+x,"X "+x);
                            Log.i("Y "+y,"Y "+y);
                            Log.i("l "+l,"l "+l);
                            Log.i("r "+r,"r "+r);
                            Log.i("t "+t,"t "+t);
                            Log.i("b "+b,"b "+b);

                            if ((l<=x &&
                                    r>=x &&
                                    t<=y &&
                                    b>=y)&&configAtiva) {
                                Log.i("Config","aqui");
                                Intent intent = new Intent(ControleFutuante.this, Configs.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("EXIT", true);
                                startActivity(intent);
                            }
                            escondeCOnfigFecha();
                        }
                        pressed = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        if(pressed) {
                            mostraConfigFecha();
                            moved = true;
                            paramsControler.x = initialX + (int) (event.getRawX() - initialTouchX);
                            paramsControler.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(controler, paramsControler);
                            return true;
                        }
                        return false;

                }
                return false;
            }
        });

        final WindowManager.LayoutParams paramsControlerFecha = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.TRANSLUCENT);
        paramsControlerFecha.gravity = Gravity.BOTTOM;
        paramsControlerFecha.x = 0;
        paramsControlerFecha.y = 0;
        addControlerFecha(controlerFecha,paramsControlerFecha);

        final WindowManager.LayoutParams paramsControlerConfig = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.TRANSLUCENT);
        paramsControlerConfig.gravity = Gravity.TOP;
        paramsControlerConfig.x = 0;
        paramsControlerConfig.y = 0;

        addControlerFecha(controlerConfig,paramsControlerConfig);
        addControler(controler, paramsControler);
        return super.onStartCommand(intent, flags, startId);
    }

    private void mostraFABs() {

        fabVolta.startAnimation(showFabVolta);

        fabPlayPause.startAnimation(showFabPlayPause);

        fabAvanca.startAnimation(showFabAvanca);
    }

    private void escondeFABs() {
        fabVolta.startAnimation(hideFabVolta);
        fabVolta.setClickable(false);

        fabPlayPause.startAnimation(hideFabPlayPause);
        fabPlayPause.setClickable(false);

        fabAvanca.startAnimation(hideFabAvanca);
        fabAvanca.setClickable(false);
    }

    public void addControlerFecha(View controlerFecha, WindowManager.LayoutParams params) {
        windowManager.addView(controlerFecha, params);
    }

    public void addControler(View controler, WindowManager.LayoutParams params) {
        windowManager.addView(controler, params);
        controler.findViewById(R.id.show).setVisibility(View.INVISIBLE);
        final FloatingActionButton fab = (FloatingActionButton) controler.findViewById(R.id.show);
        //CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        //int fab_bottomMargin = layoutParams.bottomMargin;
        fab.setX(fab.getX()-200);
        fab.show();
        fab.animate().translationX(0).setInterpolator(new LinearInterpolator()).start();
    }

    public void removeChatHead(View chatHead) {
        FloatingActionButton floatingActionButton = (FloatingActionButton)chatHead.findViewById(R.id.show);
        floatingActionButton.hide();
        windowManager.removeView(chatHead);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                stopSelf();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        }, 200);
    }

    public void escondeCOnfigFecha(){

        fabImageButtonFecha.hide();
        if (configAtiva)fabConfig.hide();
    }

    public void mostraConfigFecha(){

        fabImageButtonFecha.show();
        if (configAtiva) fabConfig.show();

    }

    @Override
    public void onDestroy() {
        removeChatHead(controler);
        removeChatHead(controlerConfig);
        removeChatHead(controlerFecha);
        super.onDestroy();
    }

    public void loadComponents(){

        controlerFecha = inflater.inflate(R.layout.close_media, null);
        controlerConfig = inflater.inflate(R.layout.config_media, null);
        controler = inflater.inflate(R.layout.controle_flutuante, null);

        fabShow = (FloatingActionButton) controler.findViewById(R.id.show);
        final int[] ids = {R.drawable.show,R.drawable.hide};
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(0, ids.length - 1).setDuration(500);
        valueAnimator.setInterpolator( new LinearInterpolator());

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int i = -1;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                if(i!=animatedValue) {
                    fabShow.setImageDrawable(getResources().getDrawable(ids[animatedValue]));
                    i = animatedValue;
                }
            }
        });

        fabAvanca = controler.findViewById(R.id.avanca);
        fabVolta = controler.findViewById(R.id.volta);
        fabPlayPause = controler.findViewById(R.id.playPause);

        hideFabShow = AnimationUtils.loadAnimation(getApplication(),R.anim.fab_show_diminui);
        showFabShow = AnimationUtils.loadAnimation(getApplication(),R.anim.fab_show_aumenta);

        showFabVolta = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_volta_aparece);
        showFabPlayPause = AnimationUtils.loadAnimation(getApplication(),R.anim.fab_play_pause_aparece);
        showFabAvanca = AnimationUtils.loadAnimation(getApplication(),R.anim.fab_avanca_aparece);

        Animation.AnimationListener animationListenerShow = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                controler.findViewById(R.id.playPause).setVisibility(View.INVISIBLE);
                controler.findViewById(R.id.volta).setVisibility(View.INVISIBLE);
                controler.findViewById(R.id.avanca).setVisibility(View.INVISIBLE);
                fabVolta.show();
                fabPlayPause.show();
                fabAvanca.show();
                fabVolta.setClickable(true);
                fabPlayPause.setClickable(true);
                fabAvanca.setClickable(true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        Animation.AnimationListener animationListenerHide = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                controler.findViewById(R.id.playPause).setVisibility(View.GONE);
                controler.findViewById(R.id.volta).setVisibility(View.GONE);
                controler.findViewById(R.id.avanca).setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        Animation.AnimationListener animationListenerHideFabSHow = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                valueAnimator.start();

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        Animation.AnimationListener animationListenerShowFabShow = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) fabShow.getLayoutParams();
                layoutParams.topMargin = 5;
                fabShow.setLayoutParams(layoutParams);
                valueAnimator.reverse();
                }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        hideFabShow.setAnimationListener(animationListenerHideFabSHow);
        showFabShow.setAnimationListener(animationListenerShowFabShow);
        showFabVolta.setAnimationListener(animationListenerShow);
        showFabPlayPause.setAnimationListener(animationListenerShow);
        showFabPlayPause.setAnimationListener(animationListenerShow);

        hideFabVolta = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_volta_esconde);
        hideFabPlayPause = AnimationUtils.loadAnimation(getApplication(),R.anim.fab_play_pause_esconde);
        hideFabAvanca = AnimationUtils.loadAnimation(getApplication(),R.anim.fab_avanca_esconde);
        clickFab = AnimationUtils.loadAnimation(getApplication(),R.anim.click);
        hideFabAvanca.setAnimationListener(animationListenerHide);
        hideFabPlayPause.setAnimationListener(animationListenerHide);
        hideFabVolta.setAnimationListener(animationListenerHide);

        controler.findViewById(R.id.playPause).setVisibility(View.GONE);
        controler.findViewById(R.id.volta).setVisibility(View.GONE);
        controler.findViewById(R.id.avanca).setVisibility(View.GONE);
        controlerFecha.findViewById(R.id.imageButtonFecha).setVisibility(View.GONE);
        controlerConfig.findViewById(R.id.configControler).setVisibility(View.GONE);
    }
}