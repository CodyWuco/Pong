package com.gamecodeschool.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class PongGame extends SurfaceView implements Runnable {

    // Are we debugging?
    private final boolean DEBUGGING = true;


    private Resolution resolution;
    private TextSizes textSizes;
    private Sounds sounds;

    // How many frames per second did we get?
    private long mFPS;
    // The number of milliseconds in a second
    private final int MILLIS_IN_SECOND = 1000;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    class Resolution {
        protected int mScreenX;
        protected int mScreenY;

        Resolution(int x, int y) { mScreenX = x; mScreenY = y;}
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // The game objects
    private Bat mBat;
    private Ball mBall;

    // The current score and lives remaining
    private int mScore = 0;
    private int mLives = 3;

    // Here is the Thread and two control variables
    private Thread mGameThread = null;
    // This volatile variable can be accessed
    // from inside and outside the thread
    private volatile boolean mPlaying;
    private boolean mPaused = true;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public PongGame(Context context, int x, int y) {
        // constructor of SurfaceView
        super(context);

        initFields(context, x, y);
        initDraw();
        initGameObject();
        startNewGame();
    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    private void initFields(Context context, int x, int y){
        resolution = new Resolution(x , y);
        textSizes = new TextSizes(resolution.mScreenX);
        sounds = new Sounds(context);
    }

    // Initialize the objects ready for drawing with
    // getHolder is a method of SurfaceView
    private void initDraw(){
        mOurHolder = getHolder();
        mPaint = new Paint();
    }

    private void initGameObject(){
        // Initialize the bat and ball
        mBall = new Ball(resolution.mScreenX);
        mBat = new Bat(resolution.mScreenX, resolution.mScreenY);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void startNewGame(){
        resetBall();
        resetStartingStats();
    }

    private void resetBall(){
        mBall.reset(resolution.mScreenX, resolution.mScreenY);
    }

    private void resetStartingStats(){
        mScore = 0;
        mLives = 3;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // When we start the thread with:
    // mGameThread.start();
    // the run method is continuously called by Android
    // because we implemented the Runnable interface
    // Calling mGameThread.join();
    // will stop the thread
    @Override
    public void run() {
        // mPlaying gives us finer control
        // rather than just relying on the calls to run
        // mPlaying must be true AND
        // the thread running for the main loop to execute
        while (mPlaying) {
            long frameStartTime = System.currentTimeMillis();

            // Provided the game isn't paused call the update method
            NotPaused();
            draw();
            avoidZeroTimeFrame(frameStartTime);
        }

    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    private void NotPaused(){
        if(!mPaused){
            update();
            detectCollisions();
        }
    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    private void avoidZeroTimeFrame(long frameStartTime){
        if (notZeroFrameTime(frameStartTime)) {
            // ready to pass to the update methods
            mFPS = currFrameRate(frameStartTime) ;
        }
    }

    private boolean notZeroFrameTime(long frameStartTime) {
        return timeThisFrameTook(frameStartTime) > 0;
    }

    private long currFrameRate(long frameStartTime){
        return MILLIS_IN_SECOND / timeThisFrameTook(frameStartTime);
    }

    private long timeThisFrameTook(long frameStartTime) {
        return System.currentTimeMillis() - frameStartTime;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void update() {
        // Update the bat and the ball
        mBall.update(mFPS);
        mBat.update(mFPS);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void detectCollisions(){
        batCollisions();
        edgeCollisions();
    }
    // `````````````````````````````````````````````````````````````````````````````````````````````
    private void batCollisions(){
        if(RectF.intersects(mBat.getRect(), mBall.getRect())) {
            // Realistic-ish bounce
            mBall.batBounce(mBat.getRect());
            mBall.increaseVelocity();
            mScore++;
            sounds.PlaySoundBeep();
        }
    }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    private void edgeCollisions(){
        detectBottomCollisions();
        detectTopCollisions();
        detectLeftCollisions();
        detectRightCollisions();

    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    private void detectBottomCollisions(){
        if(mBall.getRect().bottom > resolution.mScreenY){
            doOnBottomCollide();
            isOutOfLives();
        }
    }

    private void doOnBottomCollide(){
        mBall.reverseYVelocity();
        reduceLives();
        sounds.PlaySoundMiss();
    }

    private void reduceLives(){ mLives--; }
    private void isOutOfLives(){
        if(mLives == 0){
            mPaused = true;
            startNewGame();
        }
    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    private void detectTopCollisions(){ if(mBall.getRect().top < 0){ doOnTopCollide();} }
    private void doOnTopCollide(){
        mBall.reverseYVelocity();
        sounds.PlaySoundBoop();
    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    private void detectLeftCollisions(){ if(mBall.getRect().left < 0){ doOnLeftCollide(); } }
    private void doOnLeftCollide(){
        mBall.reverseXVelocity();
        sounds.PlaySoundBop();
    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    private void detectRightCollisions(){
        if(mBall.getRect().right > resolution.mScreenX){ doOnRightCollide(); }
    }
    private void doOnRightCollide(){
        mBall.reverseXVelocity();
        sounds.PlaySoundBop();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // These objects are needed to do the drawing
    private SurfaceHolder mOurHolder;
    private Canvas mCanvas;
    private Paint mPaint;

    // Draw the game objects and the HUD
    // should be turned into a class
    void draw() {
        if (mOurHolder.getSurface().isValid()) {
            lockCanvasMem();

            drawBackground();
            drawBatAndBall();

            setFontSize();
            drawHUD();

            drawDebugText();
            displayAndUnlockCanvas();
        }

    }
    // `````````````````````````````````````````````````````````````````````````````````````````````
    private void lockCanvasMem() {
        mCanvas = mOurHolder.lockCanvas();
    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    private void drawBackground(){
        // Fill the screen with a solid color
        mCanvas.drawColor(Color.argb
                (255, 26, 128, 182));
    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    private void drawBatAndBall(){
        selectDrawColor(Color.WHITE);
        drawBall();
        drawBat();
    }

    private void selectDrawColor(int color){
        mPaint.setColor(color);
    }

    private void drawBall(){
        mCanvas.drawRect(mBall.getRect(), mPaint);
    }
    private void drawBat(){
        mCanvas.drawRect(mBat.getRect(), mPaint);
    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    private void setFontSize(){
        mPaint.setTextSize(textSizes.mFontSize);
    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    private void drawHUD() {
        mCanvas.drawText("Score: " + mScore +
                        "   Lives: " + mLives +
                        "                             " +
                        "Cody Wuco",
                textSizes.mFontMargin , textSizes.mFontSize, mPaint);
    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    private void drawDebugText(){ if(DEBUGGING){ printDebuggingText(); } }

    private void printDebuggingText(){
        int debugSize = textSizes.mFontSize / 2;
        int debugStart = 150;
        mPaint.setTextSize(debugSize);
        mCanvas.drawText("FPS: " + mFPS ,
                10, debugStart + debugSize, mPaint);

    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    private void displayAndUnlockCanvas(){ mOurHolder.unlockCanvasAndPost(mCanvas); }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Handle all the screen touches
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        // This switch block replaces the
        // if statement from the Sub Hunter game
        switch (motionEvent.getAction() &
                MotionEvent.ACTION_MASK) {

            // The player has put their finger on the screen
            case MotionEvent.ACTION_DOWN:

                // If the game was paused unpause
                mPaused = false;

                // Where did the touch happen
                if(motionEvent.getX() > resolution.mScreenX / 2){
                    // On the right hand side
                    mBat.setMovementState(mBat.RIGHT);
                }
                else{
                    // On the left hand side
                    mBat.setMovementState(mBat.LEFT);
                }

                break;

            // The player lifted their finger
            // from anywhere on screen.
            // It is possible to create bugs by using
            // multiple fingers. We will use more
            // complicated and robust touch handling
            // in later projects
            case MotionEvent.ACTION_UP:

                // Stop the bat moving
                mBat.setMovementState(mBat.STOPPED);
                break;
        }
        return true;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // This method is called by PongActivity
    // when the player quits the game
    public void pause() {

        // Set mPlaying to false
        // Stopping the thread isn't
        // always instant
        mPlaying = false;
        try {
            // Stop the thread
            mGameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    // This method is called by PongActivity
    // when the player starts the game
    public void resume() {
        mPlaying = true;
        // Initialize the instance of Thread
        mGameThread = new Thread(this);

        // Start the thread
        mGameThread.start();
    }
}