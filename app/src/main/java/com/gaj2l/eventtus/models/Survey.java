package com.gaj2l.eventtus.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by artur on 6/3/17.
 */

public class Survey
{
    private List<Question> questions;
    private String name;
    private int index = 0;
    private HashMap<Integer, Answer> answers = new HashMap();

    public Survey( String name, List questions )
    {
        this.name = name;
        this.questions = questions;
    }

    public void name( String name ) { this.name = name; }

    public String name() { return  name; }

    public boolean hasQuestions() { return questions != null && ! questions.isEmpty(); }

    public List<Question> questions() { return questions; }

    public Question question() { return questions.get(index); }

    public boolean hasNext() { return index < questions.size() - 1; }

    public void next() { index++; }

    public boolean hasPrevious() { return index > 0; }

    public void previous() { index--; }

    public String title() { return ( index + 1 ) + "/" +  questions().size(); }

    public boolean canFinish() { return questions().size() == answers().size(); }

    public HashMap<Integer, Answer> answers() { return answers; }

    public Answer answer() {
        if ( answers != null ) return answers.get( question().id() );

        return null;
    }

    public void answer( Answer a ) {
        if ( answers == null ) answers = new HashMap();

        answers.put( question().id(), a );
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof Survey )
        {
            return Survey.class.cast( obj ).name().equals( name() );
        }

        return false;
    }


    @Override
    public String toString()
    {
        return name;
    }
}
