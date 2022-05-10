package com.gamecodeschool.pong;

class TextSizes {
    int mFontSize;
    int mFontMargin;

    // it is suggested to pass in screen width
    TextSizes(int mScreen){
        setmFontSize(mScreen);
        setmFontMargin(mScreen);
    }

    void setmFontSize(int mScreen){
        // Font is 5% (1/20th) of screen
        mFontSize = mScreen / 20;
    }

    void setmFontMargin(int mScreenX){
        // Margin is 1.5% (1/75th) of screen
        mFontMargin = mScreenX / 75;
    }
}