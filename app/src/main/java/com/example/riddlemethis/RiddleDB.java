package com.example.riddlemethis;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
public class RiddleDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "riddles.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    public RiddleDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                RiddleC.QuestionsTable.TABLE_NAME + " ( " +
                RiddleC.QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RiddleC.QuestionsTable.COLUMN_QUESTION + " TEXT, " +
                RiddleC.QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
                RiddleC.QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
                RiddleC.QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
                RiddleC.QuestionsTable.COLUMN_OPTION4 + " TEXT, " +
                RiddleC.QuestionsTable.COLUMN_ANSWER + " INTEGER" +
                ")";
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        fillQuestionsTable();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RiddleC.QuestionsTable.TABLE_NAME);
        onCreate(db);
    }
    private void fillQuestionsTable() {
        Question q1 = new Question("What has to be broken before it can be used?",
                "Egg", "Vase", "Heart", "Promise", 1);
        addQuestion(q1);
        Question q2 = new Question("Jimmy’s mother had three children. The first was called April, the second was called May. What was the name of the third?",
                "Jimmy", "June", "James", "Jake", 1);
        addQuestion(q2);
        Question q3 = new Question("What begins with T, ends with T, and has T in it?",
                "Teapot", "Lion", "Dog", "Snail", 1);
        addQuestion(q3);
        Question q4 = new Question("It belongs to you, but other people use it more than you do. What is it?",
                "Your name", "Your phone", "Your book", "Your bag", 1);
        addQuestion(q4);
        Question q5 = new Question("Which month has 28 days in it?",
                "All Months", "February", "July", "September",1);
        addQuestion(q5);
        Question q6 = new Question("Poor people have it. RIch people need it. If you eat it, you die. What is it?",
                "Nothing", "Money", "Food", "Faith",1);
        addQuestion(q6);
        Question q7 = new Question("What word in the dictionary is spelled incorrectly?",
                "Incorrectly", "Dictionary", "Difficult", "Strength",1);
        addQuestion(q7);
        Question q8 = new Question("What goes up but never goes down?",
                "Rain", "Age", "Clouds", "Grass",1);
        addQuestion(q8);
        Question q9 = new Question("Take of my skin, i won't cry. But you will! What am i?",
                "Onion", "Pepper", "Egg", "Orange",1);
        addQuestion(q9);
        Question q10 = new Question("He has married many women, but has never been married. Who is he?",
                "A Priest", "A King", "A Prince", "A Slave",1);
        addQuestion(q10);
        Question q11 = new Question("I fly without wings, i cry without eyes. What am i?",
                "Cloud", "Airplane", "Hot Air Balloon", "Kite",1);
        addQuestion(q11);
        Question q12 = new Question("What gets bigger every time you take from it?",
                "A Hole", "A clock", "Money", "Paper",1);
        addQuestion(q12);
        Question q13 = new Question("What always goes to bed with its shoes on?",
                "A Horse", "A Pig", "A Cow", "Children",1);
        addQuestion(q13);
        Question q14 = new Question("What is black and white and read all over?",
                "A newspaper", "A panda", "A note", "A clock",1);
        addQuestion(q14);
        Question q15 = new Question("What is it that's always coming but never arrives?",
                "Tomorrow", "Love", "Death", "Rain",1);
        addQuestion(q15);
        Question q16 = new Question("Feed me and i live, yet give me drink i die. What am i?",
                "Fire", "Rain", "Darkness", "Love",1);
        addQuestion(q16);
        Question q17 = new Question("What goes up when rain comes down?",
                "Umbrella", "Tension", "Electricity", "Steam",1);
        addQuestion(q17);
        Question q18 = new Question("What has 6 faces and 21 eyes but cannot see?",
                "Dice", "Spider", "Clock", "Watch",1);
        addQuestion(q18);
        Question q19 = new Question("I travel all over the world, but always stay in my corner. What am i?",
                "A stamp", "An airplane", "A passenger", "A boat",1);
        addQuestion(q19);
        Question q20 = new Question("I run all around the field but never move. What am i?",
                "Fence", "Grass", "A sheep", "A cow",1);
        addQuestion(q20);
        Question q21 = new Question("What can you catch but cannot throw?",
                "A cold", "A baseball", "A football", "A grenade",1);
        addQuestion(q21);
        Question q22 = new Question("What goes up and never goes back down?",
                "Your age", "A ball", "Gravity", "Rain",1);
        addQuestion(q22);
        Question q23 = new Question("Your are my brother, but i am not your brother. What am i?",
                "Your sister", "Your brother", "Your father", "Your mother",1);
        addQuestion(q23);
        Question q24 = new Question("If you share me, you haven't got me. What am i?",
                "A secret", "A Truth", "A lie", "Nothing",1);
        addQuestion(q24);
        Question q25 = new Question("The more i appear the less you see. What am i?",
                "Darkness", "Sunglasses", "X-ray", "Shadow",1);
        addQuestion(q25);
    }

    private void addQuestion(Question question) {
        ContentValues cv = new ContentValues();
        cv.put(RiddleC.QuestionsTable.COLUMN_QUESTION, question.getQuestion());
        cv.put(RiddleC.QuestionsTable.COLUMN_OPTION1, question.getOption1());
        cv.put(RiddleC.QuestionsTable.COLUMN_OPTION2, question.getOption2());
        cv.put(RiddleC.QuestionsTable.COLUMN_OPTION3, question.getOption3());
        cv.put(RiddleC.QuestionsTable.COLUMN_OPTION4, question.getOption4());
        cv.put(RiddleC.QuestionsTable.COLUMN_ANSWER, question.getAnswer());
        db.insert(RiddleC.QuestionsTable.TABLE_NAME, null, cv);
    }

    public List<Question> getAllQuestions() {
        List<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + RiddleC.QuestionsTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                Question question = new Question();
                question.setQuestion(c.getString(c.getColumnIndex(RiddleC.QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(RiddleC.QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(RiddleC.QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(RiddleC.QuestionsTable.COLUMN_OPTION3)));
                question.setOption4(c.getString(c.getColumnIndex(RiddleC.QuestionsTable.COLUMN_OPTION4)));
                question.setAnswer(c.getInt(c.getColumnIndex(RiddleC.QuestionsTable.COLUMN_ANSWER)));
                questionList.add(question);
            } while (c.moveToNext());
        }

        c.close();
        return questionList;
    }
}
