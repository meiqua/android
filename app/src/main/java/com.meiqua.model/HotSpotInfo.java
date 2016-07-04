package com.meiqua.model;              //this model is for mapFragment
                                       //when you click one shelf in the map,actually you click hotSpotInfo
public class HotSpotInfo {
    private int xLeft;
    private int xRight;
    private int yTop;
    private int yBottom;
    private int MarkerX;
    private int MarkerY;
    private String tag;
    public HotSpotInfo(int xl,int yt,int xr,int yb,String ta){
        this.xLeft=xl;
        this.xRight=xr;
        this.yBottom=yb;
        this.yTop=yt;
        this.tag=ta;
        this.MarkerX=(xl+xr)/2;
        this.MarkerY=yt;
    }
    public int getLeft(){return this.xLeft;}
    public int getRight(){return this.xRight;}
    public int getTop(){return this.yTop;}
    public int getBottom(){return this.yBottom;}
    public String getTag(){return this.tag;}
    public int getMarkerX(){return this.MarkerX;}
    public int getMarkerY(){return this.MarkerY;}

}
