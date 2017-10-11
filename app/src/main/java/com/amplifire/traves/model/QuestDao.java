package com.amplifire.traves.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestDao {
    public ItemsDao items;
    public double latitude;
    public double longitude;
    public int active_user;
    public int maximum_user;
    public PictureDao picture;
    public QuizDao quiz;
    public TreasureDao treasure;

    public ItemsDao getItems() {
        return items;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getActive_user() {
        return active_user;
    }

    public void setActive_user(int active_user) {
        this.active_user = active_user;
    }

    public int getMaximum_user() {
        return maximum_user;
    }

    public PictureDao getPicture() {
        return picture;
    }

    public QuizDao getQuiz() {
        return quiz;
    }

    public TreasureDao getTreasure() {
        return treasure;
    }


    public class ItemsDao {
        public Map<String, ItemDao> quest;
        public int point;
        public String validation_code;

        public Map<String, ItemDao> getQuest() {
            return quest;
        }

        public int getPoint() {
            return point;
        }

        public String getValidation_code() {
            return validation_code;
        }

        public class ItemDao {
            private int min;
            private String name;

            public int getMin() {
                return min;
            }

            public String getName() {
                return name;
            }
        }

    }

    public class PictureDao {
        private int min;
        public int point;

        public int getMin() {
            return min;
        }

        public int getPoint() {
            return point;
        }
    }

    public class QuizDao {
        public Map<String, QuizItemDao> quiz;
        public int point;

        public Map<String, QuizItemDao> getQuiz() {
            return quiz;
        }

        public int getPoint() {
            return point;
        }

        public class QuizItemDao {
            private String answer;
            private String question;
            public Map<String, String> choice;

            public String getAnswer() {
                return answer;
            }

            public String getQuestion() {
                return question;
            }

            public Map<String, String> getChoice() {
                return choice;
            }
        }

    }

    public class TreasureDao {
        public Map<String, TreasureItemDao> treasure;

        public Map<String, TreasureItemDao> getTreasure() {
            return treasure;
        }

        public class TreasureItemDao {
            private String barcode;
            private String desc;
            private int point;

            public String getBarcode() {
                return barcode;
            }

            public String getDesc() {
                return desc;
            }

            public int getPoint() {
                return point;
            }
        }

    }

}


