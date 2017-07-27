package com.example.cameron.fordapp;

/**
 * Created by anita13benita on 12/3/16.
 *  http://www.vogella.com/tutorials/AndroidSQLite/article.html#sqlite-and-android
 */

public class Comment {

        private long id;
        private String comment;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        // Will be used by the ArrayAdapter in the ListView
        @Override
        public String toString() {
            return comment;
        }
}
