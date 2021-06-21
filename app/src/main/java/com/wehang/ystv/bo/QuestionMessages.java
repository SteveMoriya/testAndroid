package com.wehang.ystv.bo;

/**
 * Created by lenovo on 2017/8/9.
 */

public class QuestionMessages extends YsMessage{


    /**
     * answerContent : null
     * questionTime : 2017-09-19 16:16:11
     * pics : b22c8123d7cb4e8facb3d96ca282a6bd.jpg,8ddd6014c0234725822af6a594d5dda3.jpg,fff65027e4d242cda92facb3022773b6.jpg
     * questionUserId : 600012
     * answerTime : null
     * questionContent : 我了吗
     * questionIconUrl : bbf2e4e26ddd44d4b2a96825915a1b97.jpg
     * question_id : 6970e4d362ef448fb6a55a5cf30d3399
     * questionUserName : 早睡
     */

    public String answerContent;
    public String questionTime;
    public String pics;
    public String questionUserId;
    public String answerTime;
    public String questionContent;
    public String questionIconUrl;
    public String questionId;
    public String questionUserName;
    public int isVip=0;
    public String answerUserName;
    public String sourceId;
    public String name;
    public String title;
    public String iconUrl;

    @Override
    public String toString() {
        return "QuestionMessages{" +
                "answerContent='" + answerContent + '\'' +
                ", questionTime='" + questionTime + '\'' +
                ", pics='" + pics + '\'' +
                ", questionUserId='" + questionUserId + '\'' +
                ", answerTime='" + answerTime + '\'' +
                ", questionContent='" + questionContent + '\'' +
                ", questionIconUrl='" + questionIconUrl + '\'' +
                ", question_id='" + questionId + '\'' +
                ", questionUserName='" + questionUserName + '\'' +
                '}';
    }
}
