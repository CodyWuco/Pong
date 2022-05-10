package com.gamecodeschool.pong;

import android.graphics.RectF;

class Bat {
    private RectF mRect;
    private float mLength;
    private float mXCoord;
    private float mBatSpeed;
    private int   mScreenX;

    final int STOPPED = 0;
    final int LEFT = 1;
    final int RIGHT = 2;

    // Keeps track of if and how the ball is moving
    // Starting with STOPPED
    private int mBatMoving = STOPPED;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // passes in screen width and height
    Bat(int sx, int sy){
        initScreenSizeWidth(sx);
        initBatXDraw();
        initBatYDraw(sy);
        initBatXSpeed();
    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    // Bat needs to know the screen horizontal resolution Outside of this method
    private void initScreenSizeWidth(int sx){ mScreenX = sx; }

    // Configure the speed of the bat This code means the bat can cover the width of the screen
    // in 1 second
    private void initBatXSpeed() { mBatSpeed = mScreenX; }

    private void initBatXDraw(){
        setBatWidth();
        setBatStartingX();
    }

    // Configure the size of the bat based on the screen resolution One eighth the screen width
    private void setBatWidth(){ mLength = mScreenX / 8; }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    // Configure the starting location of the bat Roughly the middle horizontally
    private void setBatStartingX(){ mXCoord = mScreenX / 2; }

    // Initialize mRect based on the size and position
    private void initBatYDraw(int sy){
        mRect = new RectF(mXCoord, calcBatStartingY(sy),
                mXCoord + mLength,
                calcBatStartingY(sy) + calcBatHeight(sy));
    }

    private float calcBatStartingY(int sy){ return sy - calcBatHeight(sy);}
    private float calcBatHeight(int sy){ return sy / 40; }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Return a reference to mRect to PongGame
    // should create a point class and draw class
    // should change rect to a point and send point to draw class to draw ball
    RectF getRect(){ return mRect; }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Update the movement state passed
    // in by the onTouchEvent method
    void setMovementState(int state){ mBatMoving = state; }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Update the bat Called each frame/loop
    void update(long fps){
        MoveBat(fps);
        KeepBatOnScreen();
        UpdateBatDraw();
    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    // Update mRect based on the results from
    // the previous code in update
    private void UpdateBatDraw(){ mRect.left = mXCoord; mRect.right = mXCoord + mLength; }

    // Move the bat based on the mBatMoving variable
    // and the speed of the previous frame
    private void MoveBat(long fps){
        if(IsMovingLeft()){ mXCoord = mXCoord - mBatSpeed / fps; }
        if(IsMovingRight()){ mXCoord = mXCoord + mBatSpeed / fps; }
    }

    private boolean IsMovingLeft(){ return mBatMoving == LEFT; }
    private boolean IsMovingRight(){ return mBatMoving == RIGHT; }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    // Stop the bat going off the screen
    private void KeepBatOnScreen(){
        if(AtLeftEdge()){ mXCoord = 0; }
        if(AtRightEdge()){ mXCoord = mScreenX - mLength; }
    }

    private boolean AtLeftEdge(){ return mXCoord < 0; }
    private boolean AtRightEdge(){ return mXCoord + mLength > mScreenX; }

}