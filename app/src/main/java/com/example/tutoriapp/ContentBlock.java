package com.example.tutoriapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContentBlock implements Parcelable {
    String header;
    String content;

    public ContentBlock(String header, String content) {
        this.header = header;
        this.content = content;
    }

    protected ContentBlock(Parcel in) {
        header = in.readString();
        content = in.readString();
    }

    public static final Creator<ContentBlock> CREATOR = new Creator<ContentBlock>() {
        @Override
        public ContentBlock createFromParcel(Parcel in) {
            return new ContentBlock(in);
        }

        @Override
        public ContentBlock[] newArray(int size) {
            return new ContentBlock[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(header);
        parcel.writeString(content);
    }
}

class Question {
    String questionId;
    String questionText;
    String questionType;
    List<String> options;
    String correctAnswer;
    String fromModule;
    String audio;
    float scoreThreshold;
}

class Module {
    List<Question> conceptual;
    List<Question> listening;
    List<Question> speaking;
    List<Page> pages;
}

class Page {
    String pageId;
    String pageType;
    String subtopic;
    String subtopicTitle;
    ArrayList<ContentBlock> contentBlocks;
}

class ModulesContainer {
    Map<String, Module> modules;
}
