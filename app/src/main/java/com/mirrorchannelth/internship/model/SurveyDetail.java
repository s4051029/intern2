package com.mirrorchannelth.internship.model;

import java.util.ArrayList;

/**
 * Created by rooney on 5/18/2016.
 */
public class SurveyDetail {
    private String id = null;
    private String type = null;
    private String no = null;
    private String title = null;
    private ArrayList<Image> imageList = null;
    private ArrayList<Choice> choiceList = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Image> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<Image> picture) {
        this.imageList = picture;
    }

    public void addImage(Image image) {
        if (getImageList() == null) {
            setImageList(new ArrayList<Image>());
        }
        getImageList().add(image);
    }

    public ArrayList<Choice> getChoiceList() {
        return choiceList;
    }

    public void setChoiceList(ArrayList<Choice> choiceList) {
        this.choiceList = choiceList;
    }

    public void addChoice(Choice choice) {
        if (getChoiceList() == null) {
            setChoiceList(new ArrayList<Choice>());
        }
        getChoiceList().add(choice);
    }
}
