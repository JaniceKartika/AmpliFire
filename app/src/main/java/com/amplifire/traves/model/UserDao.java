package com.amplifire.traves.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDao {
    public String key;
    public String name;
    public int point;
    public String current_location;
    public String current_quest;
    public String email;
    public Map<String, locationStatusDao> location;
    public Map<String, questStatusDao> quest;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getCurrent_location() {
        return current_location;
    }

    public void setCurrent_location(String current_location) {
        this.current_location = current_location;
    }

    public String getCurrent_quest() {
        return current_quest;
    }

    public void setCurrent_quest(String current_quest) {
        this.current_quest = current_quest;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, locationStatusDao> getLocation() {
        return location;
    }

    public void setLocations(Map<String, locationStatusDao> location) {
        this.location = location;
    }

    public class locationStatusDao {
        public Map<String, StatusDao> status;

        public Map<String, StatusDao> getStatus() {
            return status;
        }

        public void setStatus(Map<String, StatusDao> status) {
            this.status = status;
        }

        public class StatusDao {
            public int status;

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }
        }
    }

    public class questStatusDao {
        public Map<String, StatusDao> status;
        public Map<String, Boolean> treasures;

        public Map<String, StatusDao> getStatus() {
            return status;
        }

        public void setStatus(Map<String, StatusDao> status) {
            this.status = status;
        }

        public Map<String, Boolean> getTreasures() {
            return treasures;
        }

        public void setTreasures(Map<String, Boolean> treasures) {
            this.treasures = treasures;
        }

        public class StatusDao {
            public int status;

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }
        }
    }

}


