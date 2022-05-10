package com.gamecodeschool.pong;

import android.graphics.RectF;

class Ball {
    private RectF mRect;
    private float mXVelocity;
    private float mYVelocity;
    private float mBallWidth;
    private float mBallHeight;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // It is called in PongGame class
    Ball(int screenX){
        setBallSize(screenX);
        InitBallDraw();
    }

    private void setBallSize(int screenX){
        // Make the ball square and 1% of screen width
        mBallWidth = screenX / 100;
        mBallHeight = screenX / 100;
    }

    private void InitBallDraw(){ mRect = new RectF(); }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    // Return a reference to mRect to PongGame
    // should create a point class and draw class
    // should change rect to a point and send point to draw class to draw ball
    RectF getRect(){ return mRect; }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Update the ball position.
    // Called each frame/loop
    void update(long fps){
        moveBallDrawUpdate(fps);
    }

    // Move the ball
    private void moveBallDrawUpdate(long fps){
        updateTopLeftCorner(fps);
        updateBottomRightCorner(fps);
    }
    private void updateTopLeftCorner(long fps){
        mRect.left = mRect.left + (mXVelocity / fps);
        mRect.top = mRect.top + (mYVelocity / fps);
    }

    // Match up the bottom right corner
    // based on the size of the ball
    private void updateBottomRightCorner(long fps){
        mRect.right = mRect.left + mBallWidth;
        mRect.bottom = mRect.top + mBallHeight;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Reverse the vertical direction of travel
    void reverseYVelocity(){
        mYVelocity = -mYVelocity;
    }

    // Reverse the horizontal direction of travel
    void reverseXVelocity(){
        mXVelocity = -mXVelocity;
    }

    void increaseVelocity(){
        // increase the speed by 10%
        mXVelocity *= 1.1f;
        mYVelocity *= 1.1f;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    void reset(int x, int y){
        setStartingPosition(x);
        initBallVelocity(y);
    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    // Initialise the four points of
    // the rectangle which defines the ball
    private void setStartingPosition(int x){
        mRect.left = x / 2;
        mRect.top = 0;
        mRect.right = x / 2 + mBallWidth;
        mRect.bottom = mBallHeight;
    }

    // How fast will the ball travel
    private void initBallVelocity(int y){
        mYVelocity = -(y / 3);
        mXVelocity = (y / 3);
    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    // Bounce the ball back based upon
    // whether it hits the left or right hand side
    void batBounce(RectF batPosition){
        // Pick a bounce direction
        PickDirection(DetectLocationOfBatHit(batPosition));

        // bounce off of bat
        reverseYVelocity();
    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    // Detect center of bat
    // detect the center of the ball
    // Where on the bat did the ball hit?
    private float DetectLocationOfBatHit(RectF batPosition){
        return ((batPosition.left + (batPosition.width() / 2)) - (mRect.left + (mBallWidth / 2)));
    }

    // `````````````````````````````````````````````````````````````````````````````````````````````
    // send the ball right if it hit the right side of the bat and left if left
    private void PickDirection(float relativeIntersect){
        if(IsRightOfBat(relativeIntersect)){
            GoRight();
        }else{
            GoLeft();
        }
    }

    // figures out which half of the bat that the ball hit
    private boolean IsRightOfBat(float relativeIntersect){return (relativeIntersect < 0); }

    // uses Math.abs so it can keep the velocity and still guarantee the direction
    private void GoRight(){ mXVelocity = Math.abs(mXVelocity); }
    private void GoLeft(){ mXVelocity = -Math.abs(mXVelocity); }
}